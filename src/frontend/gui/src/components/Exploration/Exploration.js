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
import getModelPredictionsOverGridPoints from '../../actions/getModelPredictionsOverGridPoints'



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
            fakePointGridabelHistory: [],
            projectionHistory: [],
            fakePointGrid: [],
            modelPredictionHistory: []

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
                                    onClick={this.onModelBehaviorClick.bind(this)}
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
                        projectionHistory={this.state.projectionHistory}
                        fakePointGrid={this.state.fakePointGrid}
                        modelPredictionHistory={this.state.modelPredictionHistory}
                        hasTSM={false}
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
                        dataset={this.props.dataset}
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


    onModelBehaviorClick(e){
        

        const hasBehaviorData = this.state.modelPredictionHistory.length > 0 && 
                                this.state.projectionHistory.length > 0 


        if (hasBehaviorData){    
            this.setState({
                'showModelVisualisation': false, 
                'showLabelView': false,  
                'showHeatmap': false,
                'showLabelHistory': false,
                'showModelBehavior': true
            })
        }
        else{
            alert('Please label at least one more point or wait for computation to finish')
        }
    }

    onPositiveLabel(e){
        
        var dataIndex = parseInt(e.target.dataset.key)
        this.dataWasLabeled(dataIndex, 1)
    }

    onNegativeLabel(e){
        var dataIndex = parseInt(e.target.dataset.key)
        this.dataWasLabeled(dataIndex, 0)                      
    }

    componentDidUpdate(){
        //console.log(this.state)
        //console.log(this.state.allLabeledPoints)
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

    
        if ( this.state.fakePointGrid.length === 0){
            getGridPoints(points => {                                                
                this.setState({
                    fakePointGrid: points
                })

            })
        }

        if ( ! this.state.initialLabelingSession){

            getDecisionBoundaryData(this.dataWasReceived.bind(this))
            getModelPredictionsOverGridPoints(predictedLabels => {
                                                            
                var history = this.state.modelPredictionHistory
                history.push(predictedLabels)                
                this.setState({
                    modelPredictionHistory: history
                })
            }, false)
        }        
    }

    labelForInitialSession(labeledPoints, pointsToLabel){
        
        var tokens = this.props.tokens

        const hasYesAndNo = this.state.hasYes && this.state.hasNo

        if (hasYesAndNo){
            this.setState({
                hasYesAndNo: true,
                initialLabelingSession: false,
                labeledPoints: [],
                pointsToLabel: []
            }, ()=> {
                explorationSendLabeledPoint({
                    data: labeledPoints,
                }, tokens, this.onNewPointsToLabel.bind(this))
            })

            return 
        }

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
        
        let history = this.state.projectionHistory
        history.push(JSON.parse(boundaryData))
        
        this.setState({
            projectionHistory: history
        })        
    }   
}


export default Exploration