import React, { Component } from 'react';
import $ from "jquery";

import {backend, webplatformApi, defaultConfiguration} from '../../constants/constants'
import GroupVariables from '../GroupVariables'
import AdvancedOptions from './AdvancedOptions'


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
            learner: "Uncertainty sampling",
            classifier: "SVM"
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
        
        sendChosenColumns(this.props.tokens, this.state, this.props.sessionWasStarted)        

        this.props.sessionOptionsWereChosen({
            
            useFakePoint: this.state.useFakePoint,
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
   
    onFakePointClick(e){
         
        this.setState({
            useFakePoint: e.target.checked
        })
    }

    render(){

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

                                   

                                </div>                                                          
                        ))
                    }

                    <p>
                        By grouping variable in formal subgroups, the convergence
                        speed can be improved. 
                    </p>


                    <input 
                        id="conf" 
                        name="configuration"
                        type="text"
                        style={{visibility: "hidden"}}/>


                    <input 
                        className="btn btn-success btn-raised"
                        type="submit" value="Start session" 
                    />        

                    <div>
                        <label>Use fake point sampler</label>
                        <input 
                            type="checkbox"
                            onClick={this.onFakePointClick.bind(this)}
                        />
                    </div>

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


function sendChosenColumns(tokens, state, onSuccess){

    var endPoint = backend + "/choose-options"    
    var configuration = defaultConfiguration
    configuration['useFakePoint'] = state.useFakePoint || false
    
              
    var hasTSM = !! state.finalGroups || state.availableVariables.length == 2
    
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
          
    var sessionOptionsUrl = webplatformApi + "/session/" + tokens.sessionToken + "/options"

    var statisticData = {
        column_number: variableData.availableVariables.length,
        has_tsm: hasTSM,
        learner: state.learner,
        classifier: state.classifier
    }


    if (hasTSM){
        statisticData['number_of_variable_groups'] = tsmJson.multiTSM.featureGroups.length       
    }
    
    $.ajax({
        type: "PUT",
        url: sessionOptionsUrl,
        data: statisticData,
        headers: {
            Authorization: "Token " + tokens.authorizationToken
        }
    })
}



SessionOptions.defaultProps = {
    "classifiers": [
        {value: "svm", label:"SVM"},
        {value: "majorityVote", label:"Majority vote"},
    ]
}

export default SessionOptions