import * as d3 from 'd3'
import React, { Component } from 'react';

import loadFileFromInputFile from '../../lib/data_utils'
import TraceDataset from '../../model/TraceDataset';
import Dataset from '../../model/Dataset'

import Exploration from '../Exploration/Exploration'


import DataPoints from '../DataPoints'
import ModelBehavior from '../visualisation/ModelBehavior'
import initializeBackend from '../../actions/trace/initializeBackend'
import sendPointBatch from '../../actions/trace/sendPointBatch'

class QueryTrace extends Component{

    render(){

        return (
            <div>
                { this.state.showLoading && 
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

                        
                        <div>
                            <select
                                onChange={this.encodedDatasetChanged.bind(this)}
                            >
                                <option
                                    value="cars"
                                >Cars</option>
                                <option
                                    value="jobs"
                                >Jobs</option>
                            </select>
                        </div>

                        <button
                            onClick={this.onValidateTrace.bind(this)}
                        >
                            Validate
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

                                    <ModelBehavior                     
                                        labeledPoints={this.state.allLabeledPoints}                        
                                        availableVariables={this.props.availableVariables}
                                        projectionHistory={this.state.projectionHistory}
                                        fakePointGrid={this.state.fakePointGrid}
                                        modelPredictionHistory={this.state.modelPredictionHistory}
                                        hasTSM={false}                        
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
            columnNames: ['year', 'length', 'width'],            
            availableVariables: [
                {
                    'id': 0,
                    'name': 'year',                    
                },
                {
                    'id': 1,
                    'name': 'length',                    
                },
                {
                    'id': 2,
                    'name': 'width',                    
                }
            ],
            fakePointGrid: [],
            modelPredictionHistory: [],
            projectionHistory: [],
            allLabeledPoints: [],
            iteration: 0,
            lastIndice: 0,
            allLabeledPoints: []
        }
    }

    encodedDatasetChanged(e){
        this.setState({
            encodedDatasetChanged: e.target.value
        })
    }

    onValidateTrace(e){

        loadFileFromInputFile("dataset", event => {
            
            var fileContent = event.target.result 
            var dataset = Dataset.buildFromLoadedInput(fileContent)
            dataset.set_columns_selected_by_users(this.state.columnNames.map(e => {
                return {'name': e}
            }))

            this.setState({
                'dataset': dataset
            }, this.initializeBackend)
        })

        loadFileFromInputFile("trace", event => {

            var fileContent = event.target.result 
            var trace = TraceDataset.buildFromLoadedInput(fileContent)    
            
            this.setState({ 
                'traceDataset': trace                
            })
        })

        loadFileFromInputFile('trace-columns', event => {
            
            var fileContent = event.target.result

            this.setState({
                'traceColumns': JSON.parse(fileContent)
            })
        })
    }

    initializeBackend(){

        var options = {
            columnIds: this.state.traceColumns.encoded_dataset,
            encodedDatasetName: "cars_encoded.csv"
        }

        this.setState({
            isComputing: true,
            showLoading: false
        }, () => {
            initializeBackend(options, this.traceBackendWasInitialized.bind(this))
        })
        
    }

    traceBackendWasInitialized(fakePointGrid){
        
        var grid = fakePointGrid.map(e => {return e.data.array})
        this.setState({
            fakePointGrid: grid,            
        }, this.sendLabelDataForComputation.bind(this))        
    }

    getLabeledPointToSend(iRowTrace){

        return this.state.traceDataset.get_point(iRowTrace)
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
        return {
            id: id,
            label: sentPoint.label,        
            data: this.state.dataset.get_point(id)
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

        this.setState({
            modelPredictionHistory: modelPredictionHistory,
            projectionHistory: projectionHistory,            
            showLoading: false,
            iteration: this.state.iteration + 1,
            allLabeledPoints: allLabeledPoints,
            isComputing: false
        })
    }
}


function backendPredToFrontendFormat(rawPoints, isTSM){
    return rawPoints.map(e => {
        return {
            'id': isTSM ? e.id: e.dataPoint.id,
            'label': e.label == 'POSITIVE' ? 1: -1
        }
    })
}

export default QueryTrace