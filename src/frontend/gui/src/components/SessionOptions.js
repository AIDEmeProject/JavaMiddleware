import React, { Component } from 'react';
import $ from "jquery";

import {backend, defaultConfiguration} from '../constants/constants'
import GroupVariables from './GroupVariables'

function sendChosenColumns(event, onSuccess){

    var endPoint = backend + "/choose-options"
    
    var json = JSON.stringify(defaultConfiguration)
    
    $('#conf').val(json)
    
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
            chosenColumns: [],
            showAdvancedOptions: false,
            showVariableGroups: false,
            availableVariables: [],            
            variableGroups: [
                [],
                []
            ],
            
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
                                name="active-learner"
                            >
                                <option 
                                    value="UncertaintySampler"
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
                                name="classifier"
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

                 <p>
                        The following columns were found. Pick the one you want to use for this session.
                    </p>

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

                    <p>
                        By grouping variable in formal subgroups, the convergence
                        speed can be improved. 
                    </p>


                    <input id="conf" name="configuration" type="text" style={{visibility: "hidden"}}/>


                    <input 
                        className="btn btn-success btn-raised"
                        type="submit" value="Start session" 
                    />        


                    <button 
                        type="button"
                        className="btn btn-primary btn-raised"
                        onClick={() => this.setState({
                            showVariableGroups: ! this.state.showVariableGroups
                        })}    
                    >
                        Group Variables
                    </button>


                    <button 
                        type="button"
                        className="btn btn-primary btn-raised"
                        onClick={() => this.setState({
                            showAdvancedOptions: ! this.state.showAdvancedOptions
                        })}    
                    >
                        Show advanced options
                    </button>
                    
                 
                    <AdvancedOptions {...this.state} />

                    <GroupVariables 
                        
                        showVariableGroups={this.state.showVariableGroups}
                        availableVariables={this.state.availableVariables}
                        groupWasAdded={this.groupWasAdded.bind(this)}
                        variableWasAddedToGroup={this.variableWasAddedToGroup.bind(this)}
                        
                    />
                           
                </form>
            </div>
        )
    }

  

    groupWasAdded(){
        var groups = this.state.variableGroups.map(e => e)
        groups.push([])

        this.setState({
            variableGroups: groups
        })

        this.forceUpdate()
    }

    variableWasAddedToGroup(variable, target){

        
        var usedVariables = this.state.usedVariables.map(e => e)                
        if (target.checked){

            var value = target.value
                        
            usedVariables.push(variable)
        }
        else{
            usedVariables.splice(target.value, 1)
        }
        

        this.setState({
            usedVariables: usedVariables,
            
        })
        
    }
}

SessionOptions.defaultProps = {
    "classifiers": [
        {value: "svm", label:"SVM"},
        {value: "majorityVote", label:"Majority vote"},
    ]
}

export default SessionOptions