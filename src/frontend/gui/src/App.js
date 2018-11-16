import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';

import axios from "axios" ;

import $ from "jquery";

const EXPLORATION = "Exploration"
const NEW_SESSION = "NewSession"
const SESSION_OPTIONS = "SessionOptions"

var backend = "http://localhost:7060"

function uploadFile(event, onSuccess){

    var endPoint = backend + "/new-session"
    var formData = new FormData();
    
    var file = document.querySelector('form input[type=file]').files[0]
    formData.append("dataset", file);
    
    axios.post(endPoint,
               formData, 
               {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
     }).then( response  => {
        
        onSuccess(response.data)
     }).catch(e => {
         alert(e)
     })    
}

class NewSession extends Component{

  handleSubmit(event){

      event.preventDefault()
      uploadFile(event, this.props.fileUploaded)
  }

  render(){

        return (

            <div>
                <h1>
                    New Session
                </h1>

                <div>                
                    <form 
                        onSubmit={this.handleSubmit.bind(this)}     
                    >   

                        <label htmlFor="dataset">Choose the dataset to be labeled</label>
                        <input
                            className="form-control-file"
                            id="dataset" name="dataset" type="file" 
                        />

                        <input
                            className="btn btn-raised btn-primary"
                            type="submit" value="Confirm" 
                        />
                        
                    </form> 
                </div>        
            </div>
        )
    }
}

function sendChosenColumns(event, onSuccess){

    var endPoint = backend + "/choose-options"
   
    $.ajax({
        type: "POST",
        url: endPoint,
        data: $('#choose-columns').serialize(),
        success: onSuccess,
        
      });      
}

class SessionOptions extends Component{
    
    constructor(props){
        super(props)
        this.state = {
            checkboxes: this.props.columns.map (c => false),
            chosenColumns: []
        }
    }

    onChosenColumns(e){
        e.preventDefault()
        sendChosenColumns(e, this.props.sessionWasStarted)        
        this.props.sessionOptionsWereChosen({
            
            chosenColumns: this.state.chosenColumns            
        })
    }

    componentDidMount(){

        window.$('form').bootstrapMaterialDesign()        
    }

    onCheckedColumn(e){
                    
        var checkboxes = this.state.checkboxes.map(e=>e);
        checkboxes[e.target.value] = e.target.checked

        var chosenColumns = this.props.columns.filter((e, k)=>{

            return checkboxes[k]
        })        
        
        this.setState({
            chosenColumns: chosenColumns,
            checkboxes: checkboxes
        })
    }

    render(){
        
        return (
            <div>              
                <form                 
                    id="choose-columns"
                    onSubmit={this.onChosenColumns.bind(this)}
                >
                                                        
                    {                
                        this.props.columns.map((column, key) => (
                                                    
                                <div 
                                    key={key} 
                                    className="checkbox"
                                >                                    
                                    <label>
                                                                    
                                        <input        
                                            className="form-control"                                        
                                            type="checkbox"
                                            name={"column" + key }
                                            value={key} 
                                            id={"column-" + column }  
                                            onChange={this.onCheckedColumn.bind(this)}
                                        /> {column}

                                    </label>

                                </div>                                                          
                        ))
                    }
                                        
                    <div className="form-group">
                        <label htmlFor="algorithm-selection">Choose the algorithm for the session</label>
                        <select className="form-control" id="algorithm-selection">
                            <option  defaultValue value="TSM">TSM</option> 
                            <option value="algo2">Algo 2</option>                            
                        </select>
                    </div>

                    <input type="submit" value="Start session" />                
                </form>
            </div>
        )
    }
}

function sendPointLabel(data, onSuccess){
    console.log(data)
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

class Exploration extends Component{

    render(){

        if (this.props.initialLabelingSession){

            var FirstPhase = (

                <p>
                    The first phase of labeling keeps goind on
                    until an instance of a positive and a negative example 
                    is provided
                </p>
            )
        }
        else {
            var FirstPhase = (<div></div>)
        }

        return (

            <div>
                <p> label this sample</p>
                
                { FirstPhase }
                <div>

                    <div style={{display: "inline-block", width:10, margin: 10}}>
                        id
                    </div>

                    {
                        this.props.options.chosenColumns.map((column, key) => {
                            return (
                                <div key={key} style={{display: "inline-block", minWidth:10, margin: 10}}>
                                 {column} 
                                </div>
                            )
                        })
                    }

                    <div style={{display: "inline-block", minWidth:10, margin: 10}}>
                        Label 
                    </div>
                </div>

                {
                    this.props.pointsToLabel.map((point, key) => {

                        return (

                            <div key={key}>

                                <div style={{margin: 10, width:10, display: "inline-block"}}>
                                                {point.id}
                                </div>
                                {

                                    point.data.array.map((value, valueKey) => {
                                        return (
                                            
                                            <div style={{margin: 10, width:10, display: "inline-block"}} key={valueKey}>
                                                {value}
                                            </div>
                                        )
                                    })
                                }

                                <button
                                    className="btn btn-raised btn-primary" 
                                    data-key={key} 
                                    onClick={this.onPositiveLabel.bind(this)}>
                                    Yes
                                </button>

                                <button 
                                    className="btn btn-raised btn-primary"  
                                    data-key={key} 
                                    onClick={this.onNegativeLabel.bind(this)}
                                >
                                    No
                                </button>
                            </div>                            
                        )
                    })
                }
            </div>
        )
    }

    onPositiveLabel(e){
        
        this.props.onPositiveLabel(e.target.dataset.key, this.props.onNewPointsToLabel)
    }

    onNegativeLabel(e){
        this.props.onNegativeLabel(e.target.dataset.key, this.props.onNewPointsToLabel)
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
            hasFalse: false

        }
    }

    sessionWasStarted(response){
        
        this.setState({
            step: EXPLORATION,
            pointsToLabel: response,        
        })
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

            if (label == 1){
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

        if  (pointsToLabel.length == 0){

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
                    <a className="nav-link active" href="#">CEDAR - Active learning labeler</a>
                </li>
                
            </ul>
        </div>

        <div className="row">

            <div className="col">
    
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

        <div className="row">
            <div className="col">
                {this.state.labeledPoints.length} points labeled                    
            </div>
        </div>
      </div>
    );
  }
}

class PointsAsTable extends Component{

    render(){
        return (
            <table>
                    <thead>
                            <tr>
                                {
                                    this.props.columns.map((col, k) => {
                                    
                                        return (
                                            <th key={k}>
                                                { col }
                                            </th>
                                        )
                                    })
                                }
                            </tr>
                        </thead>
                    <tbody>
                        {
                            this.props.rows.map((row, k) => {
                                return (

                                    <tr>
                                        <td>
                                            {row}
                                        </td>
                                    </tr>
                                )
                            })
                        }
                    </tbody>
            </table>
        )
    }
}

export default App;
