import React, { Component } from 'react';

import actions from '../../actions/sendChosenColumns'

import GroupVariables from './GroupVariables'
import AdvancedOptions from './AdvancedOptions'

import DataExploration from '../visualisation/DataExploration'


class SessionOptions extends Component{
    
    constructor(props){

        super(props)

        var datasetInfos = this.props.datasetInfos
       
        const columnTypes = datasetInfos.uniqueValueNumbers.map((e, i) => {
            return e > 10 || datasetInfos.hasFloats[i] ? "numerical": "categorical"
        })
        
        var chosenColumns = datasetInfos.columns.map( (col, idx) => {
            return {
                'name': col,
                'idx': idx,
                'isUsed': false,
                'type': columnTypes[idx]
            }
        })            

        this.state = {
            checkboxes: datasetInfos.columns.map (c => false),
            chosenColumns: chosenColumns,
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
            showExploration: false
        }
    }

    render(){

        const datasetInfos = this.props.datasetInfos
        const columns = datasetInfos.columns               

        return (
            <div>        
                <ul className="nav nav-tabs bg-primary">
                    <li className="nav-item">
                        <a 
                           className="nav-link" 
                           href="#basic-options"
                           onClick={this.onBasicOptionClick.bind(this)}
                        >
                            Attribute selection
                        </a>
                    </li>

                    <li className="nav-item">
                        <a 
                           className="nav-link" 
                           href="#basic-options"
                           onClick={this.onVariableGrouping.bind(this)}
                        >
                            Factorization structure
                        </a>
                    </li>

                    <li className="nav-item">
                        <a 
                            className="nav-link" 
                            href="#advanced-options"
                            onClick={this.onAdvancedOptionClick.bind(this)}
                        >
                            Algorithm selection
                        </a>
                    </li>                    
                </ul>

                <form                 
                    id="choose-columns"  
                    className="card"                  
                >                        
                    <div                         
                         style={{ "display": this.state.showColumns ? "initial": "none"}}
                    >

                        <div className="row">
                            <div className="col col-lg-12">
                                <p>
                                    Explore the dataset and pick the variables for the labeling phase
                                </p>
                            </div>
                        </div>

                        <div className="row">
                            <div className="col col-lg-3"  id="column-picker">
                                <button 
                                    className="btn btn-success btn-raised"                        
                                    onClick={this.onSessionStartClick.bind(this)}
                                >        
                                    Start session
                                </button>

                                <p>
                                    Column name
                                </p>

                            {                
                                columns.map((column, key) => (
                                        
                                    <div key={key} className="">

                                        <div className="form-check form-check-inline">
                                                                            
                                            <input        
                                                id={"column-" + column }  
                                                name={"column" + key }
                                                type="checkbox"
                                                value={key}
                                                className="form-check-input"                                                                                             
                                                onClick={this.onCheckedColumn.bind(this)}
                                                defaultChecked={this.state.checkboxes[key]}
                                            /> 
                                            
                                            <label
                                                className="column-name-label"
                                                htmlFor={"column-" + column}>
                                                {column || "Not available"}
                                            </label>
                                            

                                            <button
                                                className="btn btn-primary btn-raised btn-explore"
                                                role="button"
                                                type="button"
                                                data-key={key}
                                                onClick={this.onExploreClick.bind(this)}>
                                                Explore
                                            </button>
                                        </div>

                                    </div>                                                    
                                    ))
                                }
                        </div>
                                    
                        <div className="col col-lg-9">
                                                                                           
                            <DataExploration                                 
                                dataset={this.props.dataset}
                                firstVariable={this.state.firstVariable}
                                secondVariable={this.state.secondVariable}
                                show={this.state.showExploration}
                            />
                                                                                                                      
                        </div>
                        </div>
                    </div>
                                            
                    <AdvancedOptions {...this.state} />
                    
                    <GroupVariables                
                        show={this.state.showVariableGroups}         
                        chosenColumns={this.state.chosenColumns}                        
                        groupWasAdded={this.groupWasAdded.bind(this)}                        
                        groupsWereValidated={this.groupsWereValidated.bind(this)}                        
                    />

                    <input 
                        id="conf" 
                        name="configuration"
                        type="text"
                        style={{visibility: "hidden"}}
                    />
                           
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

    onExploreClick(e){
        
        e.preventDefault()

        var columnId = e.target.dataset.key
        
        this.setState({
            firstVariable: columnId,
            showExploration: true
        })
        
        
        return false
    }

    onCheckedColumn(e){
                    
        var checkboxes = this.state.checkboxes.map (e => e)
        var idx = e.target.value

        var newChosenColumns = this.state.chosenColumns.map(e => e)
        newChosenColumns[idx].isUsed = e.target.checked      
        var idx = 0;
        newChosenColumns.forEach((e, i) => {

            if (e.isUsed){
                newChosenColumns[i] =  Object.assign({}, e, {
                    finalIdx: idx
                })
                idx++
            }            
        })

        checkboxes[idx] = e.target.checked

              
        this.setState({
            chosenColumns: newChosenColumns,
            checkboxes: checkboxes,            
        })
    }

    onColumnTypeChange(e){

        var iColumn = e.target.dataset.key
        
        var newChosenColumnState = this.state.chosenColumns.map(e => e)
        newChosenColumnState[iColumn].type = e.target.value
                
        this.setState({
            chosenColumns: newChosenColumnState,            
        })                
    }
    
    onSessionStartClick(e){
        
        e.preventDefault()
                       
        var chosenColumns = this.state.chosenColumns.filter(e => e.isUsed)
        //const enableTSM = chosenColumns.length == 2
        const enableTSM = false
        console.log(enableTSM)
        if (enableTSM){
            var groups = [
                [chosenColumns[0]],
                [chosenColumns[1]]
            ]
            this.groupsWereValidated(groups)
        }
        else{

            actions.sendColumns(this.props.tokens, this.state, this.props.sessionWasStarted)        

            this.props.sessionOptionsWereChosen({            
                chosenColumns: this.state.chosenColumns.filter(e => e.isUsed),                
            })                
        }        
    }
   
   

    groupsWereValidated(groups){
        
        var chosenColumns = groups.flatMap( g => {
            return g.map( v => {return v} )
        })
        //var chosenColumns = this.state.chosenColumns //.filter(e => e.isUsed)      
        
        this.computeVariableColumnIndices(groups)
        
        this.props.groupsWereValidated(chosenColumns, groups, ()=> {
            actions.sendVariableGroups(this.props.tokens, chosenColumns, groups, this.props.sessionWasStarted)    
        })                                  
    }

    computeVariableColumnIndices(groups){
        
        var i = 0;

        groups.forEach( variables => {
            
            variables.forEach(variable => {
                
                variable['realId'] = i
                i++
            })
            
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