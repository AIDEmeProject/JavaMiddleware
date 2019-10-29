import React, { Component } from 'react';

import MicroModalComponent from '../MicroModalComponent'

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

                <button className="btn btn-primary">Validate</button>
            </div>
        )
    }

    onVariableAddedClick(e){
        
        const dataset = e.target.dataset
        const iVariable = dataset.variableid
        
        const iGroup = this.props.iGroup
        const isChecked = e.target.checked
        
        
        if (isChecked){
            this.props.onVariableAddedToGroup(iGroup, iVariable)
        }
        else{
            this.props.onVariableRemovedFromGroup(iGroup, iVariable)
        }        
    }
}

class Group extends Component {

    constructor(props){

        super(props)      
        this.state = {}
    }

    render(){
        
        const availableVariables = this.props.availableVariables
        const iGroup = this.props.iGroup
        const group = this.props.group
        console.log(group)
        
        return (

            <div>
            
                { 
                    this.props.group.map((variable, iVariable) => {

                   
                    return ( 
                        <div                                    
                            className=""
                            key={iVariable}
                        >
                            <div>{/* required because bs theme removes inner div */}
                            <div                                                            
                                className=""
                            >
                                
                                {variable.name}                                
                                </div>        
                            </div>      
                        </div>                        
                    )
                    })
            }
            </div>
        )
    }

    onVariableCheckboxClick(e){

        var isChecked = e.target.checked
        var iVariable = parseInt(e.target.dataset.variableorder)
            
        
        var iGroup = parseInt(e.target.dataset.groupid)
        
        if (isChecked){
            this.props.onVariableAddedToGroup(iGroup, iVariable)
        }
        else{
            this.props.onVariableRemovedFromGroup(iGroup, iVariable)
        }        
    }
    
}

class GroupVariables extends Component {
        
    constructor(props){

        super(props)        
        
        this.state = {
            groups: [
                [],
                []
            ],                        
            variablesNotAlreadyInAGivenGroup: this.props.chosenColumns.map(e => e)
        }
    }

    componentDidUpdate(){
        window.$('input').bootstrapMaterialDesign()    
    }
    
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
                    Please put the variable in subgroups (at least 2). Left out variables 
                    will be put in their own subgroups
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
                    typeof this.state.editedGroupId !== "undefined" && 


                    <MicroModalComponent
                        title={"Edition of group " + this.state.editedGroupId}
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

    onVariableAddedToGroup(groupId, variableId){
        
        var variable = this.props.chosenColumns[variableId]
        
        var newGroupsState = this.state.groups.map(e => e)

        var modifiedGroup = newGroupsState[groupId]
        
        variable['realId'] = variableId
        modifiedGroup.push(variable)
                
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

        console.log(modifiedGroup, removedColumnId)
        
        modifiedGroup = modifiedGroup.filter(variable => {
            console.log(parseInt(variable.idx) !== parseInt(removedColumnId), variable.idx)
            return parseInt(variable.idx) !== parseInt(removedColumnId)
        })

        console.log(modifiedGroup, removedColumnId)

        var variablesNotAlreadyInAGivenGroup = this.state.variablesNotAlreadyInAGivenGroup.map(e => e)
        variablesNotAlreadyInAGivenGroup.push(variable)
        
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

GroupVariables.defaultProps = {
    
    chosenColumns: [
        {
            'idx': 0,
            'isUsed': true,
            'name': 'test1',
            'type': 'numerical'
        },
        {
            'idx': 1,
            'isUsed': true,
            'name': 'test2',
            'type': 'numerical'
        },
     
        {
            'idx': 3,
            'isUsed': true,
            'name': 'test4',
            'type': 'numerical'
        },
    ]
    
}

export default GroupVariables