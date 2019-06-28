import React, { Component } from 'react';

import $ from "jquery";

import Authentication from './components/Authentication'
import NewSession from './components/options/NewSession'
import SessionOptions from './components/options/SessionOptions'
import Exploration from './components/Exploration'
import TSMExploration from './components/TSM/TSMExploration'
import DataPoints from './components/DataPoints'


import {backend, webplatformApi} from './constants/constants'
import './App.css';

const EXPLORATION = "Exploration"
const NEW_SESSION = "NewSession"
const SESSION_OPTIONS = "SessionOptions"
const TSM_EXPLORATION = "TSMExploration"
const AUTHENTICATION = "Authentication"


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
            hasFalse: false,
            availableVariables: [],
            finalVariables: []
        }
    }

  render() {

    var View;
    switch(this.state.step){
        

        case AUTHENTICATION: 
            View = Authentication
            break

        case NEW_SESSION:
            View = NewSession
            break

        case SESSION_OPTIONS:
            View = SessionOptions
            break

        case TSM_EXPLORATION:
            View = TSMExploration
            break

        case EXPLORATION:
            View = Exploration
            break
        
        default: 
            View = NEW_SESSION
    }

    return (

        <div>            
        
            <nav className="navbar navbar-dark bg-dark box-shadow ">
                 
                <a className="navbar-brand" href="/">
                    CEDAR - Active learning labeler
                </a>
         

            </nav>
            <div className="App container">
                    
                <div className="row">

                    <div className="col col-lg-10 offset-lg-1">
            
                        <View 
                            onNewPointsToLabel={this.onNewPointsToLabel.bind(this)}
                            fileUploaded={this.fileUploaded.bind(this)} 
                            sessionWasStarted={this.sessionWasStarted.bind(this)}  
                            onPositiveLabel={this.onPositiveLabel.bind(this)}
                            onNegativeLabel={this.onNegativeLabel.bind(this)}
                            sessionOptionsWereChosen={this.sessionOptionsWereChosen.bind(this)}
                            {...this.state}
                            onAuthenticationSuccess={this.onAuthenticationSuccess.bind(this)}
                            tokens={{
                                authorizationToken: this.state.authorizationToken, 
                                sessionToken: this.state.sessionToken
                            }}
                        />            
                    </div>
                </div>
            
                <div className="row">

                    <div className="col col-lg-10 offset-lg-1">


                        <DataPoints 
                            show={this.state.step == "Exploration" || this.state.step == "TSMExploration" }
                            availableVariables={this.state.finalVariables}
                            points={this.state.labeledPoints}
                        />
                    </div>
                </div>
            </div>
      </div>
    );
  }



    onAuthenticationSuccess(response){
        
        this.setState({
            authorizationToken: response.authorizationToken,
            sessionToken: response.sessionToken,
            step: NEW_SESSION
        })
    }

    sessionOptionsWereChosen(options){

        this.setState({
            options: options
        })
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
                finalVariables: variableData.availableVariables,
                availableVariables: variableData.availableVariables
            })
        }        
    }


    fileUploaded(response){

        this.setState({
            step: SESSION_OPTIONS,
            columns: response
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
        console.log(e.target)
        var dataIndex = e.target.dataset.key
        this.dataWasLabeled(dataIndex, 1)
    }

    onNegativeLabel(e){
        var dataIndex = e.target.dataset.key
        this.dataWasLabeled(dataIndex, 0)                      
    }


    getTokens(){
        return {
            authorizationToken: this.state.authorizationToken, 
            sessionToken: this.state.sessionToken
        }
    }

    dataWasLabeled(dataIndex, label){


        var tokens = this.getTokens()

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
            }, tokens, this.onNewPointsToLabel.bind(this))    

        }
    }


    labelForInitialSession(labeledPoints, pointsToLabel){
        
        var tokens = this.getTokens()

        if  (pointsToLabel.length === 0){

            if (this.state.hasYes && this.state.hasNo ){

                this.setState({
                    hasYesAndNo: true,
                    initialLabelingSession: false
                }, ()=> {
                    sendPointLabel({
                        data: labeledPoints,
                    }, tokens, this.onNewPointsToLabel.bind(this))
                })
            }
            else{
                sendPointLabel({
                    data: labeledPoints,
                }, tokens, this.onNewPointsToLabel.bind(this))
            }
        }
    }


}


function sendPointLabel(data, tokens, onSuccess){
    
    var labeledPoints = data.data.map(e => {
        return {
            id: e.id,
            label: e.label,
            data: {
                array: e.data
            }
        }
    })
    
    var endPoint = backend + "/data-point-were-labeled"

    $.ajax({
        type: "POST",
        dataType: 'JSON',
        url: endPoint,
        data: {
            labeledPoints: JSON.stringify(labeledPoints)
        },
        
        success: onSuccess
    })
    
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


export default App;
