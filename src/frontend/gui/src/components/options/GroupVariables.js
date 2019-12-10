import React, { Component } from 'react';

import MicroModalComponent from '../MicroModalComponent'
import Group from './Group'

function containsObject(obj, list) {

    var i;
    for (i = 0; i < list.length; i++) {
        if (list[i] === obj) {
            return true;
        }
    }

    return false;
}

function variableIsUsed(variable, usedVariables){
    return containsObject(variable, usedVariables)
}

class GroupEditor extends Component{

    constructor(props){

        super(props)      
        this.state = {
            includedVariables: []
        }
    }

    render(){
        const availableVariables = this.props.availableVariables
        const chosenColumns = this.props.chosenColumns
        
        return (
            <div>
                                
                {
                    chosenColumns.map((variable, i) => {
                
                        return (
                            <div
                                key={i} 
                                className="form-inline in-line"
                            >
                                <label
                                    htmlFor={"column-group-" + i}
                                >
                                    {variable.name}                                    
                                </label>                                
                                <input
                                    id={"column-group-" + i}
                                    type="checkbox"
                                    className="form-control"                                                                                                                            
                                    data-variableid={variable.idx}  
                                    data-variableorder={i}
                                    onChange={this.onVariableAddedClick.bind(this)}
                                />
                            </div>
                        )
                    })
                }                
            </div>
        )
    }

    onVariableAddedClick(e){
        
        const dataset = e.target.dataset
        const iVariable = parseInt(dataset.variableid)
        
        const iGroup = this.props.iGroup
        const isChecked = e.target.checked
        
        
        if (isChecked){
            this.props.onVariableAddedToGroup(iGroup, iVariable)
        }
        //else{
        //    this.props.onVariableRemovedFromGroup(iGroup, iVariable)
        //}        
    }
}


class GroupVariables extends Component {
                
    render(){

        var availableVariables = this.state.variablesNotAlreadyInAGivenGroup
        
        if ( ! this.props.show){
            return (<div></div>)
        }
        return (

            <div>                

                <h4>
                    Variable subgroups
                </h4>

                 <p>
                    By grouping variable in formal subgroups, the convergence
                    speed can be improved. 
                </p>
                <p>
                    Group variable by click on "edit" on a given group                    
                </p>
                
                <div>
                
                    <button
                        role="button"
                        type="button"
                        className ="btn btn-primary btn-raised"
                        onClick={this.addVariableGroup.bind(this)}
                    >
                        Add group
                    </button>

                    <button 
                        type="button"
                        role="button"
                        className ="btn btn-primary btn-raised"
                        onClick={this.validateGroups.bind(this)}
                    >
                        Validate groups
                    </button>
                </div>

                <div>
                {
                    this.state.groups.map((group, iGroup)=> {

                        return (
                            <div                            
                                key={iGroup}
                                className="card group"                                
                            >
                            <p>
                                Group {iGroup} 
                                <button
                                    type="button"
                                    role="button"
                                    data-group={iGroup}
                                    className="btn btn-primary" 
                                    onClick={this.onGroupEdit.bind(this)}
                                >Edit</button>
                            </p>
                                                                                      
                            <Group 
                                group={group} 
                                iGroup={iGroup}
                                chosenColumns={this.props.chosenColumns}
                                availableVariables={availableVariables}
                                onVariableAddedToGroup={this.onVariableAddedToGroup.bind(this)}
                                onVariableRemovedFromGroup={this.onVariableRemovedFromGroup.bind(this)}
                            />
                            </div>
                        )
                    })
                }

                </div>

                {
                    this.state.editedGroupId !== null && 


                    <MicroModalComponent
                        title={"Edition of group " + this.state.editedGroupId}
                        onClose={this.closeFactorizationGroupEdition.bind(this)}
                    >
                        
                        <GroupEditor 
                            group={this.state.groups[this.state.editedGroupId]}
                            iGroup={this.state.editedGroupId}
                            chosenColumns={this.props.chosenColumns}
                            availableVariables={availableVariables}
                            onVariableAddedToGroup={this.onVariableAddedToGroup.bind(this)}
                            onVariableRemovedFromGroup={this.onVariableRemovedFromGroup.bind(this)}
                        />

                    </MicroModalComponent>
                }

            </div>
        )
    }

    constructor(props){

        super(props)        
        
        this.state = {
            groups: [
                [],                
            ],   
            editedGroupId: null,                     
            variablesNotAlreadyInAGivenGroup: this.props.chosenColumns.map(e => e)
        }
    }

    componentDidUpdate(){
        window.$('input').bootstrapMaterialDesign()    
    }

    closeFactorizationGroupEdition(e){
        e.preventDefault()
        this.setState({editedGroupId: null})
    }

    onGroupEdit(e){

        const groupId = e.target.dataset.group

        this.setState({
            editedGroupId: groupId
        })
    }

    componentWillReceiveProps(nextProps){        
        //merge stuff
        this.setState({
            variablesNotAlreadyInAGivenGroup: nextProps.chosenColumns.filter(e => e.isUsed)
        }, this.forceUpdate)
    }


    isVariableInGroup(group, variable){
        const names = group.map(e => e.name)

        return names.includes(variable.name)
    }

    onVariableAddedToGroup(groupId, variableId){
        
        var variable = this.props.chosenColumns[variableId]
        
        var newGroupsState = this.state.groups.map(e => e)

        var modifiedGroup = newGroupsState[groupId]
        
        variable['realId'] = variableId
        variable['id'] = variableId

        if ( ! this.isVariableInGroup(modifiedGroup, variable)){

            modifiedGroup.push(variable)
        }
                    
        newGroupsState[groupId] = modifiedGroup
                                
        var variablesNotAlreadyInAGivenGroup = this.state.variablesNotAlreadyInAGivenGroup.filter(v => {
            
            return v.idx !== variable.idx
        })
    
        this.setState({
            groups: newGroupsState,
            variablesNotAlreadyInAGivenGroup: variablesNotAlreadyInAGivenGroup
        })
    }
    
    onVariableRemovedFromGroup(groupId, removedColumnId){
        
        var variable = this.props.chosenColumns[removedColumnId]
        var newGroupsState = this.state.groups.map(e => e)
        var modifiedGroup = newGroupsState[groupId]

        
        /*
        modifiedGroup = modifiedGroup.filter((variable, i) => {
            
            return i !== removedColumnId
        })
        */
        modifiedGroup.splice(removedColumnId, 1)
        

        var variablesNotAlreadyInAGivenGroup = this.state.variablesNotAlreadyInAGivenGroup.map(e => e)
        variablesNotAlreadyInAGivenGroup.push(variable)
        newGroupsState[groupId] = modifiedGroup
        this.setState({
            groups: newGroupsState,
            variablesNotAlreadyInAGivenGroup: variablesNotAlreadyInAGivenGroup
        })
    }

    addVariableGroup(){

        var groups = this.state.groups.map(e=>e)

        groups.push([])
        this.setState({
            groups: groups
        })
    }

    validateGroups(){
        
        this.props.groupsWereValidated(this.state.groups)
    }
}


export default GroupVariables