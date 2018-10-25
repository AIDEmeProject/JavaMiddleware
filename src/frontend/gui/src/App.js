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

function sendChosenColumns(event, onSuccess){

    var endPoint = backend + "/choose-options"
    var formData = new FormData(document.querySelector('#choose-columns'));
    var object = new FormData();
    formData.forEach(function(value, key){
        object[key] = value;

        
    });


    $.ajax({
        type: "POST",
        url: endPoint,
        data: $('#choose-columns').serialize(),
        success: onSuccess,
        
      });

      return

/* 
    axios.post(endPoint,
               json, 
                {
                    headers: {
                        
                    }
                }
     ).then(response  => {
        
        onSuccess(response.data)
     }).catch(e => {
         alert(e)
     }) */
}

class NewSession extends Component{

  handleSubmit(event){

      event.preventDefault()
      uploadFile(event, this.props.fileUploaded)
  }

  render(){

        return (

            <div>

                <p>
                    New Session
                </p>
                <form 
                    onSubmit={this.handleSubmit.bind(this)}     
                >   

                    <label htmlFor="dataset">Dataset to run model on </label>
                    <input id="dataset" name="dataset" type="file" />

                    <input type="submit" value="learn model on this dataset" />
                    
                </form> 
        
            </div>
        )
    }
}

class SessionOptions extends Component{
    

    onChosenColumns(e){
        e.preventDefault()
        sendChosenColumns(e, this.props.sessionWasStarted)
    }

    render(){
        
        return (
            <div>
                <div>
                    Session Option. Pick the columns which will be used as feature: 
                </div>
                <form 
                
                    id="choose-columns"
                    onSubmit={this.onChosenColumns.bind(this)}>
            
                    {
                                                
                        this.props.columns.map((column, key) => (

                            <div key={key}>

                                
                                <label htmlFor={"column-" + column}>
                                    {column}
                                </label>
                                <input
                                    type="checkbox"
                                    name={"column" + key }
                                    value={key} id={"column-" + column }  />
                            </div>    
                            
                        ))
                    }

                    <input type="submit" value="Start session" />
                
                </form>
            </div>

        )

    }
}

class Exploration extends Component{

    render(){

        return (

            <div>

                <p> label this sample</p>

            </div>
        )
    }
}

class App extends Component {


  constructor(props){
      super(props)

      this.state = {step : NEW_SESSION}
  }

  sessionWasStarted(response){
    this.setState({
        step: EXPLORATION,
        labeledExamples: [],
        
    })

  }


  fileUploaded(response){

    this.setState({
      step: SESSION_OPTIONS,
      columns: response
    })
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
      <div className="App">


        <section>
            <View 
                fileUploaded={this.fileUploaded.bind(this)} 
                sessionWasStarted={this.sessionWasStarted.bind(this)}  
                {...this.state}
            />
        </section>
      </div>
    );
  }

}

export default App;
