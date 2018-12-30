import React, { Component } from 'react';
import $ from "jquery";

import {backend, defaultConfiguration} from '../constants/constants'
import GroupVariables from './GroupVariables'

function sendChosenColumns(state, onSuccess){

    var endPoint = backend + "/choose-options"    
    var configuration = defaultConfiguration
              
    if (state.finalGroups){

        var tsmJson = {
            multiTSM: {
                hasTSM: true,
                searchUnknownRegionProbability: 0.5,
                featureGroups: state.finalGroups.map( g => { return g.map(v => v.name)}),
                columns: state.finalVariables.map( e => e.name),
                flags: state.finalGroups.map(g => {return [true, false]})                    
            },
        }

        configuration = Object.assign(configuration, tsmJson)
    }
    else if (state.availableVariables.length == 2){
        var tsmJson = {
            multiTSM: {
                hasTSM: true,
                searchUnknownRegionProbability: 0.5,
                featureGroups: state.availableVariables.map( v => { return [v.name]}),
                columns: state.availableVariables.map( e => {return e.name}),
                flags: state.availableVariables.map(g => {return [true, false]}) 
            },
        }

        configuration = Object.assign(configuration, tsmJson)
    }
    
    var json = JSON.stringify(configuration)

    $('#conf').val(json)
    
    
    var variableData = {
        availableVariables: state.availableVariables.map((v, i) => {
            return {name: v.name, i:i}
        }),
        finalGroups: state.finalGroups,
        finalVariables: state.finalVariables
    }

    $.ajax({

        type: "POST",
        url: endPoint,
        data: $('#choose-columns').serialize(),        
        success: (response) =>  {onSuccess(response, variableData)},
        
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


    groupsWereValidated(options){

        var groups = options.groups
        
        var availableVariables = {}
        options.availableVariables.forEach((e, i) => {
            availableVariables[e.name] = i
        })
        

        var finalGroups = groups.map(g => {
            
            return g.map((v, i) => {
                return {name: v.name, i: availableVariables[v.name]}
            })    
        })

        
        var finalVariables = options.availableVariables.map ( (e,i) => {
            return {name: e.name, i:i}
        })

        
        this.setState({
            finalGroups: finalGroups,
            finalVariables: finalVariables
        })
    }

    groupWasAdded(){

        var groups = this.state.variableGroups.map(e => e)
        groups.push([])

        this.setState({
            variableGroups: groups
        })

        this.forceUpdate()
    }

    onChosenColumns(e){
        
        e.preventDefault()
        
        sendChosenColumns(this.state, this.props.sessionWasStarted)        

        this.props.sessionOptionsWereChosen({
            
            chosenColumns: this.state.chosenColumns.map( (c, i) => {
                return {
                    name: c.name,
                    i: i
                }
            })            
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
                                            id={"column-" + column }  
                                            name={"column" + key }

                                            type="checkbox"
                                            className="form-control"                                        
                                                                                        
                                            value={key} 
                                            
                                            onChange={this.onCheckedColumn.bind(this)}
                                        /> {column}

                                    </label>

                                    <input />

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
                        groupsWereValidated={this.groupsWereValidated.bind(this)}
                    />
                           
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