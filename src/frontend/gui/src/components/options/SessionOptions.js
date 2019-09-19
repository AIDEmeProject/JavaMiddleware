import React, { Component } from 'react';

import actions from '../../actions/sendChosenColumns'

import GroupVariables from './GroupVariables'
import AdvancedOptions from './AdvancedOptions'




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
        }
    }

    render(){

        const datasetInfos = this.props.datasetInfos
        const columns = datasetInfos.columns               

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
                            The following columns were found. 
                            Pick the one you want to use for this session.
                        </p>

                            <div className="row">
                                <div                                     
                                    className="col s4 vertical-center"
                                >    
                                    Name
                                </div>

                                <div                                     
                                    className="col s2 center vertical-center"
                                >    
                                    Variable type
                                </div>

                                <div>
                                    Unique values
                                </div>

                                <div className="col s2 center vertical-center">
                                    Minimum
                                </div>

                                <div className="col s2 center vertical-center">
                                    Maximum
                                </div>
                            </div>
                                {                
                                    columns.map((column, key) => (
                                            
                                        <div key={key} className="row" >
                                            <div                                     
                                                className="col s4 vertical-center"
                                            >                     
                                                <div className="checkbox inline vertical-center">
                                                    <label>
                                                                                    
                                                        <input        
                                                            id={"column-" + column }  
                                                            name={"column" + key }
                                                            type="checkbox"
                                                            className="form-control"                                                                                                                                
                                                            value={key}                                             
                                                            onClick={this.onCheckedColumn.bind(this)}
                                                            defaultChecked={this.state.checkboxes[key]}
                                                        /> {column}

                                                    </label>
                                                </div> 
                                            </div>
                                            <div className="col s2 center vertical-center">
                                                <select 
                                                    className="form-control"
                                                    data-key={key}
                                                    onChange={this.onColumnTypeChange.bind(this)}
                                                    ref={"column-type-" + key}
                                                    value={this.state.chosenColumns[key].type}
                                                >
                                                    <option value="numerical">Numerical</option>
                                                    <option value="categorical">Categorical</option>
                                                </select>
                                            </div>

                                            <div className="col s2 center vertical-center"> 
                                                {datasetInfos.uniqueValueNumbers[key]}
                                            </div>

                                            <div className="col s2 center vertical-center"> 
                                                {datasetInfos.minimums[key]}
                                            </div>

                                            <div className="col s2 center vertical-center"> 
                                                {datasetInfos.maximums[key]}
                                            </div>
                                        </div>                                                    
                                    ))
                                }
                        
                                <input 
                                    id="conf" 
                                    name="configuration"
                                    type="text"
                                    style={{visibility: "hidden"}}
                                />
                                                                
                        </div>
                    }
                                            
                    <AdvancedOptions {...this.state} />
                    
                    <GroupVariables                
                        show={this.state.showVariableGroups}         
                        chosenColumns={this.state.chosenColumns}                        
                        groupWasAdded={this.groupWasAdded.bind(this)}                        
                        groupsWereValidated={this.groupsWereValidated.bind(this)}                        
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
        const enableTSM = chosenColumns.length == 2

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

        var chosenColumns = this.state.chosenColumns.filter(e => e.isUsed)      
        
        this.props.groupsWereValidated(chosenColumns, groups, ()=> {
            actions.sendVariableGroups(this.props.tokens, chosenColumns, groups, this.props.sessionWasStarted)    
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