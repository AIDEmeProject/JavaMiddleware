import React, { Component } from 'react';

import $ from "jquery";

import Authentication from './components/Authentication'
import NewSession from './components/options/NewSession'
import SessionOptions from './components/options/SessionOptions'
import Exploration from './components/Exploration/Exploration'
import TSMExploration from './components/Exploration/TSM/TSMExploration'
import BreadCrumb from './components/BreadCrumb'
import Trace from './components/Trace/Trace'

import MicroModal from 'micromodal'
import ModelBehavior from './components/visualisation/ModelBehavior'


import Welcome from './components/Welcome'

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
const TRACE = 'Trace'
const WELCOME = 'Welcome'

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
            step : WELCOME,
            columns: [],
            
            initialLabelingSession: true,
            hasYesAndNo: false,            
            hasYes: false,
            hasFalse: false,
            availableVariables: [],
            finalVariables: [],
            bread: this.getBreadCrum(NEW_SESSION),
            labeledPoints: [],
            pointsToLabel: [],
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
        
        case WELCOME:
            View = Welcome
            break

        case AUTHENTICATION: 
            View = Authentication
            break

        case TRACE:
            View = Trace
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
                                         
                        <View                        
                            {...this.state}
                            fileUploaded={this.fileUploaded.bind(this)} 
                            sessionWasStarted={this.sessionWasStarted.bind(this)}  
                            
                            sessionOptionsWereChosen={this.sessionOptionsWereChosen.bind(this)}
                            
                            onAuthenticationSuccess={this.onAuthenticationSuccess.bind(this)}
                            tokens={{
                                authorizationToken: this.state.authorizationToken, 
                                sessionToken: this.state.sessionToken
                            }}
                            groupsWereValidated={this.groupsWereValidated.bind(this)}
                            onDatasetLoaded={this.onDatasetLoaded.bind(this)}
                            onTraceClick={this.onTraceClick.bind(this)}
                            onInteractiveSessionClick={this.onInteractiveSessionClick.bind(this)}
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

  onTraceClick(e){
    this.setState({'step': TRACE}) 
  }

  onInteractiveSessionClick(){
      this.setState({'step': NEW_SESSION})
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

        const chosenColumns = options.chosenColumns
        
        this.state.dataset.set_column_names_selected_by_user(chosenColumns)
        
        this.setState({
            options: newOptions
        })
    }

    groupsWereValidated(chosenColumns, groups, callback){

        var options = {
            chosenColumns: chosenColumns,
            groups: groups,            
        }
        
        this.state.dataset.set_column_names_selected_by_user(chosenColumns)

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
        //var onlyTwoVariables = finalVariables.length == 2
        var hasTSM = options.groups
                
        if (hasTSM){
        
            var groups = options.groups
          
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
   
    getTokens(){

        return {
            authorizationToken: this.state.authorizationToken, 
            sessionToken: this.state.sessionToken
        }
    }
   
    componentDidMount(){
        MicroModal.init()
    }
}


export default App;
