import React, { Component } from 'react';

import TSMModelVisualization from './TSMModelVisualization'
import SpecificPointToLabel from '../InitialSampling/SpecificPointToLabel'
import DataPoints from './DataPoints'
import GroupedPointTableHead from './GroupedPointTableHead'
import HeatMap from '../visualisation/HeatMap'

import $ from 'jquery'
import {backend, webplatformApi} from '../../constants/constants'



class TSMExploration extends Component{

    constructor(props){
        
        super(props)

        this.state = {
            pointsToLabel: this.props.pointsToLabel.map(e => e),
            noPoints: [],
            allLabeledPoints: [],
            labeledPoints: [],   
            initialLabelingSession: true,    
            hasYes: false,
            hasNo: false,
            showModelPerformance: false,
            showLabelView: true,
            showLabelHistory: false,
            visualizationData: {
                TSMBound: null
            }
        }        
    }

    render(){
        
        return (
            
            <div>
                

                <h4>
                    Labeleling phase
                </h4>

                {
                    ! this.state.initialLabelingSession &&
                
                    <ul className="nav nav-tabs">
                    
                        <li className="nav-item">

                            <a 
                            className="nav-link" 
                            href="#basic-options"
                            onClick={() => this.setState({
                                'showModelPerformance': false, 
                                'showLabelView': true,
                                'showLabelHistory': false
                                })}
                            >
                                Label view
                            </a>
                        </li>

                        <li className="nav-item">
                            <a 
                                className="nav-link" 
                                href="#advanced-options"
                                onClick={() => this.setState({
                                    'showModelPerformance': false, 
                                    'showLabelView': false,  
                                    'showLabelHistory': true
                                })}
                            >
                                View labeled points
                            </a>
                        </li>   


                        <li className="nav-item">
                            <a 
                                className="nav-link" 
                                href="#advanced-options"
                                onClick={() => this.setState({
                                    'showModelPerformance': true, 
                                    'showLabelView': false,  
                                    'showLabelHistory': false
                                })}
                            >
                                Assess model Performance
                            </a>
                        </li>                       
                    </ul>
                }
                { 
                    this.state.initialLabelingSession && 
                    
                    <div>
                        The first phase of labeling continues until we obtain 
                        a positive example and a negative example.        
 

                        <p>
                            Grouped variable exploration. If you chose no, 
                            you will be asked to label each subgroups
                            independantly                    
                        </p>

                        <SpecificPointToLabel 
                            onNewPointsToLabel={this.newPointsToLabel.bind(this)}                            
                        />            
                    </div>                
                }
            
                {
                    this.state.showLabelView && 
                
                    <div>

                       <p>
                            Grouped variable exploration. If you chose no, 
                            you will be asked to label each subgroups
                            independantly                    
                        </p>

                    <table className="group-variable">

                        <GroupedPointTableHead 
                            groups={this.props.groups}
                        />

                            <tbody>                
                            {
                            this.state.pointsToLabel.map((point, i) => {
                                
                                return (

                                    <tr 
                                        key={i}
                                        className="variable-group">

                                        <td>
                                            {point.id}
                                        </td>
                                        {
                                            this.props.groups.map((g, iGroup) => {
                                                
                                                var pointIds = g.map(e => e.finalIdx)    
                                                
                                                var dataAsGroups = []

                                                pointIds.forEach(realId => {
                                                    var value = point.data[realId]
                                                
                                                    dataAsGroups.push(value)
                                                })
                                                                                                                                
                                                var values = dataAsGroups.join(", ")
                                                                                                                                                                         
                                                if ( typeof point.labels !== "undefined"){
                                                    var L = () => {
                                                        return <button
                                                                    data-point={i}
                                                                    data-subgroup={iGroup}
                                                                    className="btn btn-primary btn-raised"
                                                                    onClick = {this.onSubGroupNo.bind(this)}
                                                                >
                                                                    No
                                                                </button>
                                                    }
                                                }
                                                else{
                                                    var L = () => {return <span></span>}
                                                }                                
                                                
                                                return (
                                                    <td 
                                                        colSpan={g.length}
                                                        key={iGroup}
                                                    >
                                                        {values} <L />
                                                    </td>
                                                )
                                            })
                                        }
                                        <td>
                                        
                                            <button 
                                                style={{display: typeof point.labels === "undefined" ? "inherit": "none"}}
                                                className="btn btn-primary btn-raised"
                                                data-point={i}
                                                onClick={this.groupWasLabeledAsYes.bind(this)}
                                            >
                                                Yes
                                            </button>
                                                                
                                            <button 
                                                style={{display: typeof point.labels === "undefined" ? "inherit": "none"}}
                                                className="btn btn-primary btn-raised"
                                                data-point={i}
                                                onClick={this.groupWasLabeledAsNo.bind(this)}
                                            >
                                                No
                                            </button>

                                            <button
                                                className="btn btn-primary btn-raised"
                                                style={{display: typeof point.labels === "undefined" ? "none": "inherit"}}
                                                data-point={i}
                                                onClick={this.groupSubLabelisationFinished.bind(this)}
                                            >
                                                Validate Subgroup labels
                                            </button>
                                        </td>
                                    </tr>
                                )
                            })
                        }
                            </tbody>
                        </table>
                        { 
                            ! this.state.initialLabelingSession && 

                            <HeatMap                    
                                labeledPoints={this.state.allLabeledPoints}
                                datasetInfos={this.props.datasetInfos}
                                availableVariables={this.props.chosenColumns}
                            />
                        }                                            
                    </div>

                }                           
                {
                    this.state.showModelPerformance && 
                    <TSMModelVisualization 
                        TSMBound={this.state.visualizationData.TSMBound}
                    />
                }
                {
                    this.state.showLabelHistory && 

                    <DataPoints                             
                        availableVariables={this.props.chosenColumns}
                        labeledPoints={this.state.allLabeledPoints}
                        chosenColumns={this.props.chosenColumns}
                        groups={this.props.groups}
                    />
                }                
            </div>
        )
    }

    newPointsToLabel(points){
        
        var newPoints = points.map(e => {
            return {
                id: e.id,
                data: e.data.array
            }
        })
        
        var pointsToLabel = this.state.pointsToLabel.map(e => e)

        this.setState({
            pointsToLabel: pointsToLabel.concat(newPoints),
            labeledPoints: [],
            initialLabelingSession: ! (this.state.hasYes && this.state.hasNo)
        })        
    }
     
    groupWasLabeledAsYes(e){

        var pointId = e.target.dataset.point
        var pointsToLabel = this.state.pointsToLabel.map(e => e)

        var labeledPoint = this.state.pointsToLabel[pointId]
        
        labeledPoint.labels = this.props.groups.map( e => 1)
        labeledPoint.label= 1
        
        pointsToLabel.splice(pointId, 1)
        
        var labeledPoints = this.state.labeledPoints.map(e => e)

        labeledPoints.push(labeledPoint)

        var allLabeledPoints = this.state.allLabeledPoints.map(e => e)
        allLabeledPoints = allLabeledPoints.concat(labeledPoints)
        
        this.setState({
            allLabeledPoints: allLabeledPoints,
            pointsToLabel: pointsToLabel,
            labeledPoints: labeledPoints,
            hasYes: true
        },
        () => {
            
            if (pointsToLabel.length == 0){
                getVisualizationData(this.dataWasReceived.bind(this))
                sendLabels(labeledPoints, this.newPointsToLabel.bind(this))
            }
        })              
    }

    groupWasLabeledAsNo(e){

        var iPoint = e.target.dataset.point
        var pointsToLabel = this.state.pointsToLabel.map(e => e)

        var point = pointsToLabel[iPoint]
        point.labels = this.props.groups.map (e => 1)
        point.label= 0

        this.setState({
            pointsToLabel: pointsToLabel,  
            hasNo: true          
        })   
    }

    groupSubLabelisationFinished(e){
        
        var iPoint = e.target.dataset.point
        var pointsToLabel = this.state.pointsToLabel.map(e => e)
        var point = pointsToLabel[iPoint]
        

        if ( point.labels.reduce( (acc, v) => acc + v) == point.labels.length ){
            alert('please label at least one subgroup')
            return
        }

        
        pointsToLabel.splice(iPoint, 1)

        var labeledPoints = this.state.labeledPoints.map(e => e)
        labeledPoints.push(point)

        var allLabeledPoints = this.state.allLabeledPoints.map(e => e)
        allLabeledPoints = allLabeledPoints.concat(labeledPoints)
        
        this.setState({
            pointsToLabel: pointsToLabel,
            labeledPoints: labeledPoints,
            allLabeledPoints: allLabeledPoints
        }, ()=> {
            
            if (pointsToLabel.length == 0){
                getVisualizationData(this.dataWasReceived.bind(this))
                sendLabels(labeledPoints, this.newPointsToLabel.bind(this))             
            }
        })       
    }

    onSubGroupNo(e){

        var data = e.target.dataset
        var iPoint = data.point
        var iSubgroup = data.subgroup

        var pointsToLabel = this.state.pointsToLabel.map(e => e)
        var point = pointsToLabel[iPoint]
       
        point.labels[iSubgroup] = 0
            
        pointsToLabel[iPoint] = point
        this.setState({
            pointsToLabel: pointsToLabel,
          
        })
    }
    
    dataWasReceived(data){
        
        this.setState({
            
            visualizationData: data
        })
    }

    onLabelWholeDatasetClick(){

        getWholedatasetLabeled()

        notifyLabelWholeDataset(this.props.tokens)
    }    
}


function getWholedatasetLabeled(){

    var url = backend + "/get-labeled-dataset"

    $.get(url, response => {

        var blob = new Blob([response]);
        var link = document.createElement('a');
        
        link.href = window.URL.createObjectURL(blob);
        document.body.appendChild(link);
        link.download = "labeled_dataset.csv";
        link.click();
    })
}

function getVisualizationData(dataWasReceived){

    var url = backend + "/get-visualization-data"

    $.get(url, dataWasReceived)    
}

function dataWasLabeledNotification(tokens, data){
    var updateLabelData = webplatformApi + "/session/" + tokens.sessionToken + "/new-label"
    
    $.ajax({
        type: "PUT", 
        dataType: "JSON",
        url: updateLabelData,
        headers: {
            Authorization: "Token " + tokens.authorizationToken
        },
        data: {
            number_of_labeled_points: data.data.length
        }
    })
}


function notifyLabelWholeDataset(tokens){
    
    var wasAskedToLabelDatasetUrl = webplatformApi + "/session/" + tokens.sessionToken + "/label-whole-dataset"

    $.ajax({
        type: "PUT", 
        dataType: "JSON",
        url: wasAskedToLabelDatasetUrl,
        headers: {
            Authorization: "Token " + tokens.authorizationToken
        },
        data:{
            clicked_on_label_dataset: true
        }        
    })
}

function sendLabels(labeledPoints, onSuccess){
    
    var labeledPoints = labeledPoints.map(e => {
        return {
            id: e.id,
            labels: e.labels,
            data: {
                array: e.data
            }
        }
    })
    
    var endPoint = backend + "/tsm-data-point-were-labeled"
    $.ajax({
        type: "POST",
        dataType: 'JSON',
        url: endPoint,
        data: {
            labeledPoints: JSON.stringify(labeledPoints)
        },
       
        success: onSuccess
    })
}



TSMExploration.defaultProps = {
    
    pointsToLabel: [
        {
            'id': 4,
            'data': {
                'array': [1, 7, 2]
            }
        }
    ]
}

export default TSMExploration