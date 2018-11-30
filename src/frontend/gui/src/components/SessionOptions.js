import React, { Component } from 'react';
import $ from "jquery";

import {backend} from '../constants/constants'

function sendChosenColumns(event, onSuccess){

    var endPoint = backend + "/choose-options"
   
    $.ajax({

        type: "POST",
        url: endPoint,
        data: $('#choose-columns').serialize(),
        success: onSuccess,
        
      });      
}

class GroupVariables extends Component {
    
    constructor(props){
        super(props)
        
        this.state = {
            groups: [
                [
                    {
                        name: "age",
                        i: 2
                    }
                ]
            ],
            
        }

    }

    render(){
        return (
            <div>
                {
                    this.state.groups.map((group, i)=> {
                        return (
                            <div
                                key={i}
                            >
                                Group {i}

                                {
                                    group.map((variable, j) => {
                                        return (
                                            <div
                                                key={j}
                                            >
                                            
                                                {variable.name}
                                            </div>
                                        )
                                        
                                    })
                                }   

                                <select
                                    className="form-control"        
                                >

                                    {
                                        this.props.availableVariables.map((v, i) => {
                                            return (
                                                <option
                                                    value={v.i}    
                                                >
                                                    {v.name}
                                                </option>
                                            )
                                        })
                                    }

                                </select>

                                
                            </div>
                        )
                    })
                }


                <button

                    role="button"
                    type="button"
                    className ="btn btn-primary btn-raised"
                    onClick={this.addVariableGroup.bind(this)}
                >
                    Add variable group
                </button>

            </div>
        )
    }


    addVariableGroup(){

        var groups = this.state.groups.map(e => e)
        groups.push([])

        this.setState({
            groups: groups
        })
    }
}


class SessionOptions extends Component{
    
    constructor(props){
        super(props)
        this.state = {
            checkboxes: this.props.columns.map (c => false),
            chosenColumns: [],
            showAdvancedOptions: false,
            availableVariables: []
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


        var availableVariables = []
        this.props.columns.forEach((c, i) => {
            if (checkboxes[i]){
                availableVariables.push({
                    name: c,
                    i: i
                })
            }
        })

        this.setState({
            chosenColumns: chosenColumns,
            checkboxes: checkboxes,
            availableVariables: availableVariables
        })
    }

    render(){
        
        var AdvancedOptions

        if (this.state.showAdvancedOptions){

            AdvancedOptions = () => {
                return (

                    <div>                      
                        <div className="form-group">
                            <label htmlFor="algorithm-selection">Learner</label>
                            <select 
                                    className="form-control" 
                                    id="algorithm-selection"
                            >
                                <option 
                                    value="algo2"
                                    defaultValue
                                >
                                    Uncertainty Sampling
                                </option>
                                <option value="versionSpace">
                                    Version Space
                                </option>                             
                            </select>
                        </div>

                        <div className="form-group">
                            <label htmlFor="classifier">Classifier</label>
                            <select 
                                        className="form-control" 
                                        id="classfier-selection"
                            >
                                <option 
                                    value="SVM"
                                    defaultValue
                                >
                                    SVM
                                </option>
                                <option value="Majority Vote">
                                    Majority Vote
                                </option>                             
                            </select>
                        </div>                        
                    </div>
            )}            
        }
        else{
            AdvancedOptions = () => { return(<div></div>)}
        }

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

                    <button 
                        className="btn btn-primary btn-raised"
                        onClick={() => this.setState({
                            showAdvancedOptions: ! this.state.showAdvancedOptions
                        })}    
                    >
                        Show advanced options
                    </button>

                    <AdvancedOptions  />

                    <GroupVariables availableVariables={this.state.availableVariables} />

                    <input type="submit" value="Start session" />                
                </form>
            </div>
        )
    }
}

SessionOptions.defaultProps = {
    "classifiers": [
        {value: "svm", label:"SVM"},
        {value: "majorityVote", label:"Majority vote"},
    ]
}

export default SessionOptions