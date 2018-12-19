import React, { Component } from 'react';

import $ from "jquery";

import NewSession from './components/NewSession'
import SessionOptions from './components/SessionOptions'
import Exploration from './components/Exploration'
import TSMExploration from './components/TSMExploration'

import './App.css';

import {backend} from './constants/constants'

const EXPLORATION = "Exploration"
const NEW_SESSION = "NewSession"
const SESSION_OPTIONS = "SessionOptions"
const TSM_EXPLORATION = "TSMExploration"

function sendPointLabel(data, onSuccess){
    
    var endPoint = backend + "/data-point-were-labeled"
    $.ajax({
        type: "POST",
        dataType: 'JSON',
        url: endPoint,
        data: {
            labeledPoints: JSON.stringify(data.data)
        },
        header: {
            "Content-Type":"applications/json"
        },
        success: onSuccess
    })
}


class App extends Component {

    constructor(props){
        super(props)

        this.state = {
            step : NEW_SESSION,
            columns: [],
            labeledPoints: [],
            pointsToLabel: [],
            initialLabelingSession: true,
            hasYesAndNo: false,            
            hasYes: false,
            hasFalse: false

        }
    }

    sessionWasStarted(response, variableData){
        

        var pointsToLabel = response.map( pointToLabel => {
            return {
                id: pointToLabel.id,
                data: pointToLabel.data.array
            }
        })
        
        if (variableData.finalGroups){
            this.setState({
                step: TSM_EXPLORATION,
                pointsToLabel: pointsToLabel,     
                finalGroups: variableData.finalGroups,
                finalVariables: variableData.finalVariables   
            })    
        }
        else{
            this.setState({
                step: EXPLORATION,
                pointsToLabel: pointsToLabel,        
            })
        }

        
    }

    sessionOptionsWereChosen(options){

        this.setState({
            options: options
        })
    }

    fileUploaded(response){

        this.setState({
            step: SESSION_OPTIONS,
            columns: response
        })
    }

    onNewPointsToLabel(points){


        var pointsToLabel = this.state.pointsToLabel.map(e=>e)
        
        for (var point of points){
            pointsToLabel.push(point)
        }

        this.setState({
            pointsToLabel: pointsToLabel
        })
    }

    onPositiveLabel(dataIndex){
        
       this.dataWasLabeled(dataIndex, 1)
    }
    
    onNegativeLabel(dataIndex, label){
           
        this.dataWasLabeled(dataIndex, 0)                      
    }

    dataWasLabeled(dataIndex, label){

        var point = this.state.pointsToLabel[dataIndex]

        point.label = label
                
        var labeledPoints = this.state.labeledPoints.map(e=>e)
        labeledPoints.push(point)

        var pointsToLabel = this.state.pointsToLabel.map(e => e)
        pointsToLabel.splice(dataIndex, 1)
                                        
        this.setState({
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
            sendPointLabel({
                data: labeledPoints,
            }, this.onNewPointsToLabel.bind(this))    

        }
    }


    labelForInitialSession(labeledPoints, pointsToLabel){

        if  (pointsToLabel.length === 0){

            if (this.state.hasYes && this.state.hasNo ){

                this.setState({
                    hasYesAndNo: true,
                    initialLabelingSession: false
                }, ()=> {
                    sendPointLabel({
                        data: labeledPoints,
                    }, this.onNewPointsToLabel.bind(this))
                })
            }
            else{
                sendPointLabel({
                    data: labeledPoints,
                }, this.onNewPointsToLabel.bind(this))
            }
        }
    }

    

  render() {

    var View;
    switch(this.state.step){
        
        case SESSION_OPTIONS:
            View = SessionOptions
            break

        case TSM_EXPLORATION:
            View = TSMExploration
            break

        case EXPLORATION:
            View = Exploration
            break

        case NEW_SESSION:
            View = NewSession
            break

        default: 
            View = NewSession
    }

    return (

      <div className="App container">

        <div>
            <ul className="nav nav-tabs">
                <li className="nav-item">                  
                    <a className="nav-link active" href="/">
                    CEDAR - Active learning labeler
                    </a>
                </li>
                
            </ul>
        </div>

        <div className="row">

            <div className="col col-lg-8 offset-lg-2">
    
                <View 
                    fileUploaded={this.fileUploaded.bind(this)} 
                    sessionWasStarted={this.sessionWasStarted.bind(this)}  
                    onPositiveLabel={this.onPositiveLabel.bind(this)}
                    onNegativeLabel={this.onNegativeLabel.bind(this)}
                    sessionOptionsWereChosen={this.sessionOptionsWereChosen.bind(this)}
                    {...this.state}
                />            
            </div>
        </div>
      
        
      </div>
    );
  }
}


export default App;
