import React, { Component } from 'react';

class Group extends Component {

    render(){
        
        const availableVariables = this.props.availableVariables
        const iGroup = this.props.iGroup
        const group = this.props.group
                
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
                                {variable.name} <button 
                                                    className="btn btn-raised btn-sm"
                                                    data-variable={iVariable}
                                                    data-group={iGroup}
                                                    onClick={this.removeVariable.bind(this)}
                                                >
                                                    Remove
                                                </button>
                                </div>        
                            </div>      
                        </div>                        
                    )
                    })
            }
            </div>
        )
    }

    constructor(props){

        super(props)      
        this.state = {}
    }


    removeVariable(e){

        e.preventDefault()
        var iVariable = parseInt(e.target.dataset.variable)
        var iGroup = parseInt(e.target.dataset.group)
        this.props.onVariableRemovedFromGroup(iGroup, iVariable)
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

export default Group