import React, { Component } from 'react';

import $ from "jquery";

import Authentication from './components/Authentication'
import NewSession from './components/options/NewSession'
import SessionOptions from './components/options/SessionOptions'
import Exploration from './components/Exploration'
import TSMExploration from './components/TSM/TSMExploration'
import BreadCrumb from './components/BreadCrumb'

import MicroModal from 'micromodal'
import ModelBehavior from './components/visualisation/ModelBehavior'

import getDecisionBoundaryData from './actions/getDecisionBoundaryData'

import {backend, webplatformApi} from './constants/constants'
import './App.css';
import logo from './AIDEME.png'

import * as d3 from "d3"
import Dataset from './model/Dataset';

import animate_html_element from './lib/animate_text'

const EXPLORATION = "Exploration"
const NEW_SESSION = "NewSession"
const SESSION_OPTIONS = "SessionOptions"
const TSM_EXPLORATION = "TSMExploration"
const AUTHENTICATION = "Authentication"


class AnimatedText extends Component{

    constructor(props){
        super(props)
    }
    render(){
        return (
            <div>
                
                <img 
                    src={logo}
                    height={30}
                    className="speaking-bot"
                ></img>
                <span 
                    id={this.props.id} 
                    className="animated-text"
                    
                >{this.props.text}</span>
            </div>
        )
    }

    componentDidMount(){
        const elem = document.getElementById(this.props.id)
        animate_html_element(elem, 10)
    }
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
            hasFalse: false,
            availableVariables: [],
            finalVariables: [],
            bread: this.getBreadCrum(NEW_SESSION),
            allLabeledPoints: [],
            history: []
        }
    }

  getBreadCrum(step){
      var breads = {
          
          [AUTHENTICATION]: [
              {
                name: 'Authentication'
              }
          ],

          [NEW_SESSION]: [
            {
               name: 'Authentication'
            },
            {
               name: 'New session',
               active: true
            }
          ],

          [SESSION_OPTIONS]: [
            {
                name: 'Authentication'
            },
            {
                name: 'New session',            
            },
            {
                name:'Setup',
                active:true
            }
          ],
          [EXPLORATION]: [
            {
                name: 'Authentication'
            },
            {
                name: 'New session',            
            },
            {
                name:'Session options',                
            },
            {
                name:'Interactive labeling',
                active: true
            },
            
          ]

      }
      breads[TSM_EXPLORATION] = breads[EXPLORATION]

      return breads[step] || []
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
                                     
            <ul className="navbar navbar-dark bg-dark box-shadow ">                 
                
                <li className="nav-item">
                    <a className="navbar-brand" href="/">                    
                        <img src={logo} height="50" alt="logo" /> AIDEme                    
                    </a>             
                </li>
                
                <li className="nav-item">
                
                <BreadCrumb                        
                    items={this.state.bread}
                />
                </li>
            </ul>
           
            <div className="App container-fluid">                                    
                <div className="row">

                    <div className="col col-lg-12">

                        <AnimatedText 
                            text="Hello I am aideme you data exploration assistant. Here you can visualize 
                            the model behavior at each iteration. Click on next and previous to see the evolution"
                            id="test-bla"
                        />
                        

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
                            groupsWereValidated={this.groupsWereValidated.bind(this)}
                            onDatasetLoaded={this.onDatasetLoaded.bind(this)}
                        />            
                    </div>
                </div>
            
                <div className="row">
                    <div className="col col-lg-10 offset-lg-1">                
                    </div>
                </div>

                <div id="pandas-profiling">
                </div>
            </div>
      </div>
    );
  }

  onShowModalClick(e){
      this.setState({
          showModal: true
      })
  }

  onCloseModal(e){
      this.setState({
          showModal: false
      })
  }
   
  onAuthenticationSuccess(response){
        
        this.setState({
            authorizationToken: response.authorizationToken,
            sessionToken: response.sessionToken,
            step: NEW_SESSION
        })
    }

    onDatasetLoaded(event){
        
        var fileContent = event.target.result        
        var csv = d3.csvParse(fileContent)        
        var dataset = new Dataset(csv)
        
        this.setState({
            dataset: dataset
        })
    }

    fileUploaded(response){

  
        this.setState({
            step: SESSION_OPTIONS,
            datasetInfos: response,
            bread: this.getBreadCrum(SESSION_OPTIONS)
        })
    }

    sessionOptionsWereChosen(options){
        
        var newOptions = Object.assign({}, this.state.options, options)
        this.setState({
            options: newOptions
        })
    }

    groupsWereValidated(chosenColumns, groups, callback){

        var options = {
            chosenColumns: chosenColumns,
            groups: groups
        }
        var newOptions = Object.assign({}, this.state.options, options)
        
        this.setState({options: newOptions}, callback)
    }

    sessionWasStarted(response){
        
        var options = this.state.options
                
        var pointsToLabel = response.map( pointToLabel => {
            return {
                id: pointToLabel.id,
                data: pointToLabel.data.array
            }
        })
        
        var finalVariables =  options.chosenColumns
        var onlyTwoVariables = finalVariables.length == 2
        var hasTSM = options.groups || onlyTwoVariables
                
        if (hasTSM){

            if (onlyTwoVariables){
                var groups = [[finalVariables[0]], [finalVariables[1]]]
            }
            else{
                var groups = options.groups
            }

            this.setState({
                step: TSM_EXPLORATION,
                bread: this.getBreadCrum(TSM_EXPLORATION),
                pointsToLabel: pointsToLabel,     
                groups: groups,
                chosenColumns: finalVariables   
            })    
        }
        else{            
            this.setState({
                step: EXPLORATION,
                bread: this.getBreadCrum(EXPLORATION),
                pointsToLabel: pointsToLabel,
                chosenColumns: this.state.options.chosenColumns                
            })
        }        
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

    getTokens(){

        return {
            authorizationToken: this.state.authorizationToken, 
            sessionToken: this.state.sessionToken
        }
    }

    dataWasLabeled(dataIndex, label){

        var tokens = this.getTokens()
        var labeledPoint = this.state.pointsToLabel[dataIndex]
        labeledPoint.label = label

        var allLabeledPoints = this.state.allLabeledPoints
        allLabeledPoints.push(labeledPoint)

        var labeledPoints = this.state.labeledPoints.map(e => e)
        labeledPoints.push(labeledPoint)

        var pointsToLabel = this.state.pointsToLabel.map(e => e)
        console.log(dataIndex, pointsToLabel)
        pointsToLabel.splice(dataIndex, 1)
        console.log(pointsToLabel)
                                        
        
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
                sendPointLabel({
                    data: labeledPoints,
                }, tokens, this.onNewPointsToLabel.bind(this))
            })
        }
    }


    labelForInitialSession(labeledPoints, pointsToLabel){
        
        var tokens = this.getTokens()

        if  (pointsToLabel.length === 0){

            if (this.state.hasYes && this.state.hasNo ){

                this.setState({
                    hasYesAndNo: true,
                    initialLabelingSession: false,
                    labeledPoints: []
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

    getModelBoundaries(){

        if ( ! this.state.initialLabelingSession){
            getDecisionBoundaryData(this.dataWasReceived.bind(this))
        }
        
    }

    dataWasReceived(boundaryData){
        
        let history = this.state.history
        history.push(JSON.parse(boundaryData))        
        this.setState({
            history: history
        })        
    }


    componentDidUpdate(){
        console.log(this.state)
    }

    componentDidMount(){
        MicroModal.init()
    }
}

function sendPointLabel(data, tokens, onSuccess){
    console.log(data)
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
