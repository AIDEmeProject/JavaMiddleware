import React, { Component } from 'react';

import ExplorationActions from './ExplorationActions'

import $ from 'jquery'
import {backend} from '../constants/constants'

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
        header: {
            "Content-Type":"applications/json"
        },
        success: onSuccess
    })
}

class TSMExploration extends Component{

    constructor(props){
        
        super(props)

        this.state = {
            pointsToLabel: this.props.pointsToLabel,
            noPoints: [],
            labeledPoints: [],            
        }        
    }

    newPointsToLabel(points){
        
        var pointsToLabels = points.map(e => {
            return {
                id: e.id,
                data: e.data.array
            }
        })
        this.setState({
            pointsToLabel: pointsToLabels,
            labeledPoints: []
        })
    }
     
    groupWasLabeledAsYes(e){

        var pointId = e.target.dataset.point
        var pointsToLabel = this.state.pointsToLabel.map(e => e)

        var labeledPoint = this.state.pointsToLabel[pointId]
        
        labeledPoint.labels = this.props.finalGroups.map( e => 1)
        console.log(labeledPoint)
        pointsToLabel.splice(pointId, 1)
        
        var labeledPoints = this.state.labeledPoints.map(e => e)

        labeledPoints.push(labeledPoint)
        console.log(labeledPoints)
        this.setState({
            pointsToLabel: pointsToLabel,
            labeledPoints: labeledPoints
        })
      
        if (pointsToLabel.length == 0){
            sendLabels(labeledPoints, this.newPointsToLabel.bind(this))
        }
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

        this.setState({
            pointsToLabel: pointsToLabel,
            labeledPoints: labeledPoints
        })

        if (pointsToLabel.length == 0){
            sendLabels(labeledPoints, this.newPointsToLabel.bind(this))
        }
    }

    onSubGroupNo(e){

        var data = e.target.dataset
        var iPoint = data.point
        var iSubgroup = data.subgroup

        var pointsToLabel = this.state.pointsToLabel.map(e => e)
        var point = pointsToLabel[iPoint]
       
        point.labels[iSubgroup] = 0
            
        console.log(point)

        pointsToLabel[iPoint] = point
        this.setState({
            pointsToLabel: pointsToLabel
        })
    }
    
    groupWasLabeledAsNo(e){

        var iPoint = e.target.dataset.point
        var pointsToLabel = this.state.pointsToLabel.map(e => e)


        var point = pointsToLabel[iPoint]
        point.labels = this.props.finalGroups.map (e => 1)

        this.setState({
            pointsToLabel: pointsToLabel
        })   
    }

    explorationActions(){
        return (
            <ExplorationActions show={ ! this.props.initialLabelingSession}/>
        )
    }

    render(){
        var FirstPhase
        if (this.props.initialLabelingSession){

            FirstPhase = () => {
                return (
                <p>
                    The first phase of labeling continues until we obtain 
                    a positive example and a negative example.                    
                </p>
            )}
        }
        else {            
            FirstPhase = () => {return(<div></div>)}                       
        }

        return (
            <div>

                <p>
                    Grouped variable exploration. If you chose no, you will be asked to label each subgroups
                    independantly                    
                </p>

                <h4>
                    Labeleling phase
                </h4>
                            
                <FirstPhase />
                
                <p>Please label the following samples</p>

                <table className="group-variable">
                    <thead>
                        <tr>
                            {
                                this.props.finalGroups.map((g, i) => {
                                    
                                    return (
                                        <th 
                                            key ={i}
                                            colSpan={g.length}
                                        >
                                            {g.map(e => e.name).join(", ")}
                                        </th>
                                    )
                                })
                            }
                            <th>
                                Label    
                            </th>                
                        </tr>
                    </thead>

                    <tbody>                
                    {
                    this.state.pointsToLabel.map((point, i) => {
                        
                        return (

                            <tr 
                                key={i}
                                className="variable-group">
                                {
                                    this.props.finalGroups.map((g, iGroup) => {
                                        
                                        var values = g.map( variable => {
                                            
                                            return point.data[variable.i]

                                        }).join(", ")
                                                                                                                         
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

                {this.explorationActions()}
            </div>
        )
    }
 
}

TSMExploration.defaultProps = {

    
    pointsToLabel:[

        {
            data: [0, 0 , 22, 0]
        },

        {
            data: [0, 1, 33, 1]
        },
    ]
}

export default TSMExploration