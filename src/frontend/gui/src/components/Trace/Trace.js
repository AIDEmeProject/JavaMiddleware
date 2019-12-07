import $ from 'jquery'
import * as d3 from 'd3'
import React, { Component } from 'react';

import loadFileFromInputFile from '../../lib/data_utils'
import TSMTraceDataset from '../../model/TSMTraceDataset'
import TraceDataset from '../../model/TraceDataset';
import Dataset from '../../model/Dataset'

import LabelInfos from '../visualisation/LabelInfos'
import LearnerOption from '../options/LearnerOption'

import DataPoints from '../DataPoints'
import ModelBehavior from '../visualisation/ModelBehavior'
import initializeBackend from '../../actions/trace/initializeBackend'
import sendPointBatch from '../../actions/trace/sendPointBatch'

import carColumns from './carColumns'

import buildTSMConfiguration from '../../lib/buildTSMConfiguration'
import {simpleMarginConfiguration, 
        versionSpaceConfiguration, 
        factorizedVersionSpaceConfiguration} from '../../constants/constants'


class QueryTrace extends Component{

    render(){

        const algorithm = this.state.algorithm
        return (
            <div>
                { 
                    this.state.showLoading && 
                    <div className="row">
                    <div className="col col-lg-6 offset-3 card">

                    <h1>
                        Trace module
                    </h1>
                
                    <div className="form-group ">
                    
                        <label htmlFor="dataset">
                            1. Choose the dataset to be labeled
                        </label>
                        <input
                            required
                            className="form-control-file"
                            id="dataset" name="dataset" type="file" 
                        />


                        <label htmlFor="trace">
                            2. Choose the trace
                        </label>
                        <input
                            className="form-control-file"
                            id="trace" name="trace"
                            type="file"
                        >                           
                        </input>


                        <label htmlFor="trace-columns">
                            3. Choose the trace columns
                        </label>
                        <input
                            className="form-control-file"
                            id="trace-columns" name="trace-columns"
                            type="file"
                        >                           
                        </input>

                        { false && 
                        
                        <div>              
                            <label htmlFor="use-tsm">
                                Use TSM ?
                            </label>              
                            <input 
                                name="use-tsm"
                                id="use-tsm"
                                className="checkbox"
                                type="checkbox" 
                                onChange={e => this.setState({useTSM: e.target.checked})}
                                checked={this.state.useTSM}
                            />
                        </div>
                        }
                     
                        <LearnerOption                         
                            learnerChanged={this.learnerChanged.bind(this)}
                        />

                        <button                    
                            className="btn btn-raised"
                            onClick={this.onValidateTrace.bind(this)}
                        >
                            Validate
                        </button>


                        <button
                            className="btn btn-raised"
                            onClick={this.computeFullTrace.bind(this)}
                        >
                            Compute full trace
                        </button>

                    </div>
                </div>
                </div>
                }
                        
                    {
                        ! this.state.showLoading && 

                        <div>

                            <div>
                                <div>
                                    <button 
                                        className="btn btn-primary btn-raised"
                                        onClick={this.sendLabelDataForComputation.bind(this)}
                                        disabled={this.state.isComputing ? true:false}
                                    >
                                        Compute next iteration
                                    </button>

                                    <button
                                        className="btn btn-primary btn-raised"
                                        onClick={e => this.setState({
                                            showModelBehavior: false,
                                            showDataPoints: true
                                        })}
                                        
                                    >
                                        Show labeled points
                                    </button>

                                    <button
                                        className="btn btn-primary btn-raised"
                                        onClick={e => this.setState({
                                            showModelBehavior: true,
                                            showDataPoints: false
                                        })}
                                    >
                                        Show Model Behavior
                                    </button>

                                </div>

                                { 
                                    this.state.isComputing &&
                                    <p>Backend is computing please wait</p>
                                }
                            </div>
                        
                            { 
                                this.state.showModelBehavior && 

                                <div>
                                    <p>                                       
                                        Algorithm {algorithm} <br />

                                        {
                                            this.state.useTSM && 

                                            <span>TSM is enabled  <br /></span>
                                        }

                                        Number of positive predictions :
                                         {this.state.positivePoints}

                                    </p>
                                    

                                    <LabelInfos
                                        iteration={this.state.lastIndice}
                                        labeledPoints={this.state.allLabeledPoints}
                                    />

                                    <ModelBehavior                     
                                        labeledPoints={this.state.allLabeledPoints}                        
                                        availableVariables={this.state.availableVariables}
                                        projectionHistory={this.state.projectionHistory}
                                        fakePointGrid={this.state.fakePointGrid}
                                        modelPredictionHistory={this.state.modelPredictionHistory}
                                        hasTSM={this.state.useTSM}     
                                        realDataset={true}     
                                        TSMPredictionHistory={this.state.TSMPredictionHistory}              
                                    />

                                </div>
                            }
                    
                            {
                                this.state.showDataPoints && 
                    
                                <DataPoints                            
                                    points={this.state.allLabeledPoints}
                                    chosenColumns={this.state.availableVariables}
                                    show={true}
                                    dataset={this.state.dataset}
                                />
                            }
                    </div>
                }
            </div>           
        )
    }

    constructor(props){

        super(props)

        this.state = {

            showModelBehavior: false,
            showLoading: true,
            isComputing: false,
            columnNames: [],
            availableVariables: [],
            fakePointGrid: [],
            TSMPredictionHistory: [],
            modelPredictionHistory: [],
            projectionHistory: [],
            allLabeledPoints: [],
            iteration: 0,
            lastIndice: 0,
            allLabeledPoints: [],
            useTSM: false,
            algorithm: 'simplemargin'
        }
    }

    computeFullTrace(){

    }

    learnerChanged(algorithm){

        var useTSM = algorithm === "simplemargintsm" || 
                     algorithm === "factorizedversionspace"
        
        console.log(useTSM)
        this.setState({
            useTSM: useTSM,
            algorithm: algorithm
        })
    }

    encodedDatasetChanged(e){
        this.setState({
            encodedDatasetChanged: e.target.value
        })
    }


    onValidateTrace(e){

        loadFileFromInputFile("trace", event => {

            var fileContent = event.target.result 
            var filePath = $("#trace").val(); 
            const ext = filePath.substr(filePath.lastIndexOf('.') + 1,filePath.length);    
            
            const isCsv = ext === "csv"
            const useTSM = this.state.useTSM
            
            if (useTSM){
                var trace = TSMTraceDataset.buildFromLoadedInput(fileContent, isCsv)
            }
            else{
                var trace = TraceDataset.buildFromLoadedInput(fileContent, isCsv)
                
            }
         
            this.setState({ 
                'traceDataset': trace                
            })
        })
        
        loadFileFromInputFile("dataset", event => {
            
            var fileContent = event.target.result 
            var dataset = Dataset.buildFromLoadedInput(fileContent)
           
            this.setState({
                'dataset': dataset
            }, this.initializeBackend)
        })

        loadFileFromInputFile('trace-columns', event => {
            
            var fileContent = event.target.result
            var traceColumns = JSON.parse(fileContent)        
                    
            this.setState({
                'traceColumns': traceColumns,               
            })
        })
    }

    getPositivePredictedPoints(modelPredictionHistory, step){
        
        var iteration = Math.min(step, modelPredictionHistory.length - 1)

        return modelPredictionHistory[iteration].filter(e => {
            return e.label === 1
        }).length
    }


 
    buildConfiguration(){
        
        var configurations = {
            'simplemargin': simpleMarginConfiguration,
            'simplemargintsm': simpleMarginConfiguration,
            'versionspace': versionSpaceConfiguration,
            'factorizedversionspace': factorizedVersionSpaceConfiguration
        }
        
        var configuration = configurations[this.state.algorithm]

        if (this.state.useTSM){
            var allColumns = this.props.carColumns
            
            const factorizationGroups = this.state.traceColumns.factorizationGroups  
            const usedColumns = this.state.traceColumns.encodedDataset.map (e => allColumns[e])
            console.log(usedColumns)
            configuration = buildTSMConfiguration(configuration, factorizationGroups, usedColumns, allColumns)
            console.log(configuration)
        }
        
        return configuration
    }

    initializeBackend(){

        var options = {
            algorithm: this.state.algorithm,
            columnIds: this.state.traceColumns.encodedDataset,
            encodedDatasetName: "./cars_encoded.csv",
            configuration: this.buildConfiguration(),            
        }

        this.state.dataset.set_columns_selected_by_users(this.state.columnNames)
            
        this.setState({
            isComputing: true,
            showLoading: false
        }, () => {
            initializeBackend(options, this.traceBackendWasInitialized.bind(this))
        })        
    }

    traceBackendWasInitialized(fakePointGrid){
        
        const usedColumnIds = this.state.traceColumns.rawDataset
        
        var columnNames = this.state.dataset.get_column_names_from_ids(usedColumnIds)
        var availableVariables =  columnNames.map((e, i) => {
                return {name: e, realId: i}
        })
        console.log(usedColumnIds, columnNames)
        //var grid = fakePointGrid.map(e => {return e.data.array})
                
        var grid = this.state.dataset.get_parsed_columns_by_names(columnNames)
      
        this.setState({
            fakePointGrid: grid, 
            availableVariables: availableVariables,
            columnNames: columnNames
        }, this.sendLabelDataForComputation.bind(this))        
    }

    getLabeledPointToSend(iRowTrace){

        var point = this.state.traceDataset.get_point(iRowTrace)
        point.data = {
            array: [2]
        }
        return point
    }
    
    sendLabelDataForComputation(){
        
        var lastIndice = this.state.lastIndice
        var pointsToSend = d3.range(2).map((e, i) => {
            return this.getLabeledPointToSend(i + lastIndice)
        })

        this.setState({
            isComputing: true,
            lastIndice: lastIndice + 2
        }, () => {
            
            sendPointBatch(pointsToSend, response => {
                this.dataReceived(response, pointsToSend)
            })    
        })        
    }

    getDataPointFromId(sentPoint){

        const id = sentPoint.id
        var data = this.state.dataset.get_point(id)

        return {
            id: id,
            label: sentPoint.label,        
            data: data.map(e => parseFloat(e))
        } 
    }

    dataReceived(response, sentPoints){
                
        var projectionHistory = this.state.projectionHistory
        var projectionData = JSON.parse(response.jsonProjectionPredictions)
        
        projectionHistory.push(
            projectionData
        )
    
        var modelPredictionHistory = this.state.modelPredictionHistory
        var modelPredictions = backendPredToFrontendFormat(response.labeledPointsOverGrid, false)
        modelPredictionHistory.push(modelPredictions)

        var allLabeledPoints = this.state.allLabeledPoints
        allLabeledPoints = allLabeledPoints.concat(sentPoints.map(this.getDataPointFromId.bind(this)))
        

        var newState = {
            modelPredictionHistory: modelPredictionHistory,
            projectionHistory: projectionHistory,            
            showLoading: false,
            iteration: this.state.iteration + 1,
            allLabeledPoints: allLabeledPoints,
            isComputing: false,
            positivePoints: this.getPositivePredictedPoints(modelPredictionHistory, this.state.lastIndice / 2)
        }
        
        if (this.state.useTSM){
            var TSMPredictionHistory = this.state.TSMPredictionHistory
            var TSMPredictionsOverGrid = response.TSMPredictionsOverGrid.map(e => {
                return {
                    'id': e.dataPoint.id,
                    'label': e.label.label
                }
            })
            TSMPredictionHistory.push(TSMPredictionsOverGrid)
            newState['TSMPredictionHistory'] = TSMPredictionHistory
            
        }

        this.setState(newState)
    }
}


function backendPredToFrontendFormat(rawPoints, useTSM){

    return rawPoints.map(e => {
        return {
            'id': useTSM ? e.id: e.dataPoint.id,
            'label': e.label == 'POSITIVE' ? 1: -1
        }
    })
}

QueryTrace.defaultProps = {
    carColumns: carColumns
}

export default QueryTrace