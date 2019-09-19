import React, { Component } from 'react';

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

class Group extends Component {

    constructor(props){

        super(props)      
        this.state = {}
    }

    render(){
        
        const availableVariables = this.props.availableVariables
        const iGroup = this.props.iGroup
        
        return (

            <div>

            { 
                this.props.chosenColumns.map((variable, iVariable) => {

                var isAlreadyUsed = ! containsObject(variable, availableVariables)
                var isInGroup = containsObject(variable, this.props.group)

                if (isAlreadyUsed && ! isInGroup){
                    return (<div key={iVariable}></div>)
                }
                return ( 
                    <div                                    
                        className="checkbox"
                        key={iVariable}
                    >
                        <div>{/* required because bs theme removes inner div */}
                        <div                                                            
                            className="checkbox"
                        >
                            <label>
                                <input
                                    type="checkbox"
                                    className="form-control"
                                    
                                    data-groupid={iGroup}   
                                    data-variableid={variable.idx}  
                                    data-variableorder={iVariable}                                                                                                          
                                                                                                                                          
                                    onChange={this.onVariableCheckboxClick.bind(this)}
                                /> {variable.name}
                            </label>
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
                {
                    this.state.groups.map((group, iGroup)=> {

                        return (
                            <div                            
                                key={iGroup}
                                
                            >
                                Group {iGroup}                        
                                
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

                <button
                    role="button"
                    type="button"
                    className ="btn btn-primary btn-raised"
                    onClick={this.addVariableGroup.bind(this)}
                >
                    Add variable group
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
        )
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
        
        modifiedGroup = modifiedGroup.filter(variable => {
            return variable.idx !== removedColumnId
        })

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