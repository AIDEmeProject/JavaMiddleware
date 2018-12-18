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

class GroupVariables extends Component {
        
    constructor(props){

        super(props)        

        this.state = {
            groups: [
                [],
                []
            ],            
            
            usedVariables: []
        }
    }

    componentDidUpdate(){
        window.$('input').bootstrapMaterialDesign()    
    }
    
    render(){

        if  (typeof this.state.availableVariables !== "undefined"){
            var availableVariables = this.state.availableVariables
        }
        else {
            var availableVariables = []
        }

        
        
        return (

            <div style={{ display: this.props.showVariableGroups ? "block": "none"}}>                

                <h4>
                    Variable subgroups
                </h4>

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
                                <div                                    
                                    className="checkbox"
                                >
                                    {                                
                                        availableVariables.map((variable, i) => {
                                            
                                            var isVariableInGroup = containsObject(variable, this.state.groups[iGroup])
                                            
                                            var showCheckbox = ( ! variableIsUsed(variable, this.state.usedVariables) || 
                                            isVariableInGroup)

                                            var Variable
                                            
                                            if ( showCheckbox)
                                                {
                                                Variable = () => {
                                                        
                                                    return (
                                                        <div>{/* required because bs theme removes inner div */}
                                                            <div                                                            
                                                                className="checkbox"
                                                            >
                                                                <label>
                                                                    <input
                                                                        value={i}    
                                                                        className="form-control"
                                                                        data-group={iGroup}   
                                                                        data-variableid={variable.i}                                                                                                            
                                                                        type="checkbox"
                                                                        defaultChecked={isVariableInGroup}                                                                    
                                                                        onChange={this.onVariableCheckboxClick.bind(this)}
                                                                    /> {variable.name}
                                                                </label>
                                                            </div>        
                                                        </div>                                                
                                                    )                                                    
                                                }
                                            }
                                            else{
                                                Variable = () => { return (<div></div>)}
                                            }

                                            return (
                                                <Variable key={i} />
                                            )                                           
                                        })
                                    }
                                </div>                                
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
            availableVariables: nextProps.availableVariables.map (e => e)
        }, this.forceUpdate)

    }

    validateGroups(){
        this.props.groupsWereValidated(this.state)
    }

    onVariableCheckboxClick(e){
        
        var target = e.target
        var vId = parseInt(target.dataset.variableid)
        
        var groups = this.state.groups.map(e=>e)
        
        var usedVariables = this.state.usedVariables.map(e => e)     

        var variable = this.state.availableVariables.filter( v => {

            return v.i === vId
        })[0]

                
        var iGroup = e.target.dataset.group
        

        if (target.checked){
            
            usedVariables.push(variable)
            groups[iGroup].push(variable)

        }
        else{

            usedVariables = usedVariables.filter( v => {
                return v.i !== vId
            })

            var groupVariables = groups[iGroup].filter( v => {
                return v.i !== vId
            })        

            groups[iGroup] = groupVariables 
        }
        
        this.setState({
            usedVariables: usedVariables,
            groups: groups
        })                
    }


    addVariableGroup(){
        var groups = this.state.groups.map(e=>e)

        groups.push([])
        this.setState({
            groups: groups
        })
    }
}

GroupVariables.defaultProps = {
    showVariableGroups: true,
    availableVariables: [        
      
    ],
    groups: [
        [

        ],
        [

        ]
    ],
    
}

export default GroupVariables