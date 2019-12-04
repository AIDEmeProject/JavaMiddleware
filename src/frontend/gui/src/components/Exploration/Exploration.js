import React, { Component } from 'react';

import $ from "jquery";
import {backend, webplatformApi} from '../../constants/constants'

import ModelVisualization from '../visualisation/ModelVisualization'

import PointLabelisation from '../PointLabelisation'
import InitialSampling from './InitialSampling/InitialSampling'
import ModelBehavior from '../visualisation/ModelBehavior'

import DataPoints from '../DataPoints'

import wholeDatasetLabelizationWasAsked from '../../actions/statisticCollection/wholeDatasetLabelizationWasAsked'
import explorationSendLabeledPoint from '../../actions/explorationSendLabeledPoint'
import getWholedatasetLabeled from '../../actions/getWholeLabeledDataset'
import getDecisionBoundaryData from '../../actions/getDecisionBoundaryData'

import getGridPoints from '../../actions/getGridPoints'
import getGridPointLabels from '../../actions/getGridPointLabels'



class Exploration extends Component{

    constructor(props){
        
        super(props)
        this.state = {
            showModelVisualisation: false,
            showLabelView: true,
            showLabelHistory: false,
            showModelBehavior: false,
            labeledPoints: [],
            pointsToLabel: this.props.pointsToLabel.map(e => e),
            allLabeledPoints: [],
            initialLabelingSession: true,
            history: [],  
            gridHistory: {
                grid: [],
                labelHistory: []
            }
        }
    }

    render(){
                    
        if (this.state.initialLabelingSession){
            return (
                <InitialSampling 
                    pointsToLabel={this.state.pointsToLabel}
                    chosenColumns={this.props.chosenColumns}

                    onPositiveLabel={this.onPositiveLabel.bind(this)}
                    onNegativeLabel={this.onNegativeLabel.bind(this)}
                    onNewPointsToLabel={this.onNewPointsToLabel.bind(this)}
                    dataset={this.props.dataset}
                />)
        }
        
        return (

            <div>
               <div className="row">
                    <div className="col col-lg-8 offset-lg-2">                            
                        <ul className="nav nav-tabs bg-primary">
                        
                            <li className="nav-item">
                                <a 
                                    className={this.state.showLabelView ? "nav-link active": "nav-link"} 
                                    href="#basic-options"
                                    onClick={() => this.setState({
                                        'showModelVisualisation': false,
                                        'showLabelView': true,
                                        'showHeatmap': false,
                                        'showLabelHistory': false,
                                        'showModelBehavior': false
                                        })}
                                >
                                    Labeling
                                </a>
                            </li>

                            <li className="nav-item">
                                <a 
                                    className={this.state.showLabelHistory ? "nav-link active": "nav-link"} 
                                    href="#advanced-options"
                                    onClick={() => this.setState({
                                        'showModelVisualisation': false, 
                                        'showLabelView': false, 
                                        'showHeatmap': false,
                                        'showLabelHistory': true,
                                        'showModelBehavior': false
                                    })}
                                >
                                    History
                                </a>
                            </li>         

                            <li className="nav-item">
                                <a 
                                    className={this.state.showModelVisualisation ? "nav-link active": "nav-link"} 
                                    href="#advanced-options"
                                    onClick={() => this.setState({
                                        'showModelVisualisation': false, 
                                        'showLabelView': false,  
                                        'showHeatmap': false,
                                        'showLabelHistory': false,
                                        'showModelBehavior': true
                                    })}
                                >
                                    Model Behavior
                                </a>
                            </li>       


                            <li className="nav-item">
                                <a 
                                    className={this.state.showModelVisualisation ? "nav-link active": "nav-link"} 
                                    href="#advanced-options"
                                    onClick={() => this.setState({
                                        'showModelVisualisation': true, 
                                        'showLabelView': false,  
                                        'showHeatmap': false,
                                        'showLabelHistory': false,
                                        'showModelBehavior': false
                                    })}
                                >
                                    Model Performance
                                </a>
                            </li>       

                            <li className="nav-item">
                                <a
                                    className="nav-link"
                                    onClick={this.onLabelWholeDatasetClick.bind(this)}
                                >                        
                                    Auto-labeling           
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>
                                                                                     
                { 
                    this.state.showLabelView && 

                        <div>
                            <PointLabelisation                                                           
                                chosenColumns={this.props.chosenColumns}
                                pointsToLabel={this.state.pointsToLabel}
                                onPositiveLabel={this.onPositiveLabel.bind(this)}
                                onNegativeLabel={this.onNegativeLabel.bind(this)}
                                dataset={this.props.dataset}
                            />
                        </div>
                }

                { 
                    this.state.showModelBehavior && 

                    <ModelBehavior                        
                        labeledPoints={this.state.allLabeledPoints}
                        datasetInfos={this.props.datasetInfos}
                        availableVariables={this.props.chosenColumns}
                        history={this.state.history}
                        gridHistory={this.state.gridHistory}
                    />

                }

                {
                    this.state.showModelVisualisation && 
                                        
                    <ModelVisualization 
                        {...this.props}
                        {...this.state}
                    />
                }

                {
                    this.state.showLabelHistory && 

                    <DataPoints                             
                        availableVariables={this.props.finalVariables}
                        points={this.state.allLabeledPoints}
                        chosenColumns={this.props.chosenColumns}
                        show={true}
                    />
                }                
            </div>
        )
    }


    onNewPointsToLabel(points){
        
        
        var pointsToLabel = this.state.pointsToLabel.map(e=>e)

        var receivedPoints = points.map(e => {
            return {
                id: e.id,
                data: e.data.array
            }
        })

        for (var point of receivedPoints){
            pointsToLabel.push(point)
        }

        this.setState({
            pointsToLabel: pointsToLabel
        })       
    }

    onPositiveLabel(e){
        
        var dataIndex = parseInt(e.target.dataset.key)
        this.dataWasLabeled(dataIndex, 1)
    }

    onNegativeLabel(e){
        var dataIndex = parseInt(e.target.dataset.key)
        this.dataWasLabeled(dataIndex, 0)                      
    }

    dataWasLabeled(dataIndex, label){
        
        var tokens = this.props.tokens
        var labeledPoint = this.state.pointsToLabel[dataIndex]
        
        labeledPoint.label = label

        var allLabeledPoints = this.state.allLabeledPoints
        allLabeledPoints.push(labeledPoint)

        var labeledPoints = this.state.labeledPoints.map(e => e)
        labeledPoints.push(labeledPoint)

        var pointsToLabel = this.state.pointsToLabel.map(e => e)
        
        pointsToLabel.splice(dataIndex, 1)
        
        this.setState({
            allLabeledPoints: allLabeledPoints,
            pointsToLabel: pointsToLabel,
            labeledPoints: labeledPoints
        })
        
        if (this.state.initialLabelingSession){

            if (label === 1){
                this.setState({
                    hasYes: true
                }, () => {
                    this.labelForInitialSession(labeledPoints, pointsToLabel)
                })
            }
            else{
                this.setState({
                    hasNo: true
                }, () => {
                    this.labelForInitialSession(labeledPoints, pointsToLabel)
                })
            }                        
        }
        else{     
            this.setState({
                labeledPoints: []
            }, () =>{  
                explorationSendLabeledPoint({
                    data: labeledPoints,
                }, tokens, response => {
                    
                    this.onNewPointsToLabel(response)
                    
                    this.getModelBehaviorData()
                })
            })
        }
    }



    getModelBehaviorData(){

        console.log(this.state.gridHistory.grid.length)
        if ( this.state.gridHistory.grid.length === 0){
            getGridPoints(points => {
                
                var grid = this.state.gridHistory
                grid.grid = points
                this.setState({
                    gridHistory:grid
                })

            })
        }

        if ( ! this.state.initialLabelingSession){

            getDecisionBoundaryData(this.dataWasReceived.bind(this))
            getGridPointLabels(response => {
                                
                var rawLabels = response
                
                var predictedLabels = rawLabels.map(e => {
                    return {
                        'id': e.dataPoint.id,
                        'label': e.label === "NEGATIVE" ? -1: 1
                    }
                })
                var grid = this.state.gridHistory
                var labelHistory = grid.labelHistory
                labelHistory.push(predictedLabels)
                
                this.setState({
                    gridHistory: {
                        'grid': grid.grid,
                        'labelHistory': labelHistory
                    }
                })
            })
        }        
    }



    labelForInitialSession(labeledPoints, pointsToLabel){
        
        var tokens = this.props.tokens

        if  (pointsToLabel.length === 0){

            if (this.state.hasYes && this.state.hasNo ){
                
                this.setState({
                    hasYesAndNo: true,
                    initialLabelingSession: false,
                    labeledPoints: []
                }, ()=> {
                    explorationSendLabeledPoint({
                        data: labeledPoints,
                    }, tokens, this.onNewPointsToLabel.bind(this))
                })
            }
            else{

                this.setState({
                    labeledPoints: []
                }, () => {
                    explorationSendLabeledPoint({
                        data: labeledPoints,
                    }, tokens, this.onNewPointsToLabel.bind(this))
                })                            
            }
        }
    }

    onLabelWholeDatasetClick(e){

        e.preventDefault()
        
        getWholedatasetLabeled()

        wholeDatasetLabelizationWasAsked(this.props.tokens)
    }


    dataWasReceived(data){
        
        this.setState({
            showModelVisualisation: true,
            visualizationData: data
        })
    }

    onNewPointsToLabel(points){
        
        var pointsToLabel = this.state.pointsToLabel.map(e=>e)

        var receivedPoints = points.map(e => {
            return {
                id: e.id,
                data: e.data.array
            }
        })

        for (var point of receivedPoints){
            pointsToLabel.push(point)
        }

        this.setState({
            pointsToLabel: pointsToLabel
        })       
    }

    onPositiveLabel(e){
        
        var dataIndex = e.target.dataset.key
        this.dataWasLabeled(dataIndex, 1)
    }

    onNegativeLabel(e){
        var dataIndex = e.target.dataset.key
        this.dataWasLabeled(dataIndex, 0)                      
    }

    dataWasReceived(boundaryData){
        
        let history = this.state.history
        history.push(JSON.parse(boundaryData))
        
        this.setState({
            history: history
        })        
    }   
}


export default Exploration