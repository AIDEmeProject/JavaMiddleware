import React, { Component } from 'react';


import {backend, webplatformApi, defaultConfiguration} from '../../constants/constants'
import GroupVariables from '../GroupVariables'
import AdvancedOptions from './AdvancedOptions'
import sendChosenColumns from '../../actions/sendChosenColumns'

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
            finalVariables: [],

            learner: "Uncertainty sampling",
            classifier: "SVM",
            showColumns: true,
        }
    }

    render(){

        return (
            <div>        
                <ul className="nav nav-tabs">
                    <li className="nav-item">
                        <a 
                           className="nav-link active" 
                           href="#basic-options"
                           onClick={this.onBasicOptionClick.bind(this)}
                        >
                            Basic Options
                        </a>
                    </li>

                    <li className="nav-item">
                        <a 
                           className="nav-link active" 
                           href="#basic-options"
                           onClick={this.onVariableGrouping.bind(this)}
                        >
                            Variable Grouping
                        </a>
                    </li>


                    <li className="nav-item">
                        <a 
                            className="nav-link" 
                            href="#advanced-options"
                            onClick={this.onAdvancedOptionClick.bind(this)}
                        >
                            Advanced options
                        </a>
                    </li>                    
                </ul>


                <form                 
                    id="choose-columns"                    
                    
                >        
                {
                    
                    <div style={{ "display": this.state.showColumns ? "initial": "none"}}>

                        <p>
                            The following columns were found. Pick the one you want to use for this session.
                        </p>

                                {                
                                    this.props.columns.map((column, key) => (
                                            
                                        <div key={key} className="row" >
                                            <div                                     
                                                className="checkbox col s4"
                                            >                                    
                                                <label>
                                                                                
                                                    <input        
                                                        id={"column-" + column }  
                                                        name={"column" + key }
                                                        type="checkbox"
                                                        className="form-control"                                                                                                                                
                                                        value={key}                                             
                                                        onChange={this.onCheckedColumn.bind(this)}
                                                        checked={this.state.checkboxes[key]}
                                                    /> {column}

                                                </label>
                                            </div>      
                                            <div className="col s4">
                                                <select 
                                                    data-key={key}
                                                    onChange={this.onColumnTypeChange.bind(this)}
                                                    ref={"column-type-" + key}
                                                >
                                                    <option value="numerical">Numerical</option>
                                                    <option value="categorical">Categorical</option>
                                                </select>
                                            </div>
                                        </div>                                                    
                                    ))
                                }
                        
                                <label>Use fake point sampler</label>
                                <input 
                                    type="checkbox"
                                    onClick={this.onFakePointClick.bind(this)}
                                />

                                <input 
                                    id="conf" 
                                    name="configuration"
                                    type="text"
                                    style={{visibility: "hidden"}}
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
                                
                        </div>
                    }

                                            
                    <AdvancedOptions {...this.state} />

                    <GroupVariables 
                        
                        showVariableGroups={this.state.showVariableGroups}
                        availableVariables={this.state.availableVariables}
                        groupWasAdded={this.groupWasAdded.bind(this)}
                        variableWasAddedToGroup={this.variableWasAddedToGroup.bind(this)}
                        groupsWereValidated={this.groupsWereValidated.bind(this)}
                        variableGroupsChanged= {this.variableGroupsChanged.bind(this)}
                    />

                    <button 
                        className="btn btn-success btn-raised"
                        
                        onClick={this.onSessionStartClick.bind(this)}
                    >        
                        Start session
                    </button>
                           
                </form>
            </div>
        )
    }

    componentDidMount(){

        window.$('form').bootstrapMaterialDesign()     
        window.$('select').select();   
    }
    
    onVariableGrouping(){

        this.setState({
            showAdvancedOptions: false,
            showColumns: false,
            showVariableGroups: true
        })
    }

    onBasicOptionClick(){
        this.setState({
            showAdvancedOptions: false,
            showColumns: true,
            showVariableGroups: false
        })
    }

    onAdvancedOptionClick(){

        this.setState({
            showAdvancedOptions: true,
            showColumns: false,
            showVariableGroups: false
        })
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
                    i: i,
                    type: this.refs["column-type-" + i].value
                })
            }
        })
        
                
        this.setState({
            chosenColumns: chosenColumns,
            checkboxes: checkboxes,
            availableVariables: availableVariables,
            finalVariables: availableVariables
        })
    }

    onColumnTypeChange(e){
        var iColumn = e.target.dataset.key
        var variable = Object.assign({}, this.state.availableVariables[iColumn])
        variable.type = e.target.value

        var availableVariables = this.state.availableVariables.map(e=>e)
        availableVariables[iColumn] = variable
        this.setState({availableVariables: availableVariables})                
    }
    
    onSessionStartClick(e){
        
        e.preventDefault()
        
        sendChosenColumns(this.props.tokens, this.state, this.props.sessionWasStarted)        

        this.props.sessionOptionsWereChosen({
            
            useFakePoint: this.state.useFakePoint,
            chosenColumns: this.state.availableVariables
        })
    }

    variableGroupsChanged(groups){
        this.setState({
            variableGroups: groups
        })
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
}



SessionOptions.defaultProps = {
    "classifiers": [
        {value: "svm", label:"SVM"},
        {value: "majorityVote", label:"Majority vote"},
    ]
}

export default SessionOptions