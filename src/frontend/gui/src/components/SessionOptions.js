import React, { Component } from 'react';
import $ from "jquery";

import {backend} from '../constants/constants'

function sendChosenColumns(event, onSuccess){

    var endPoint = backend + "/choose-options"
   
    $.ajax({
        type: "POST",
        url: endPoint,
        data: $('#choose-columns').serialize(),
        success: onSuccess,
        
      });      
}

class SessionOptions extends Component{
    
    constructor(props){
        super(props)
        this.state = {
            checkboxes: this.props.columns.map (c => false),
            chosenColumns: []
        }
    }

    onChosenColumns(e){
        e.preventDefault()
        sendChosenColumns(e, this.props.sessionWasStarted)        
        this.props.sessionOptionsWereChosen({
            
            chosenColumns: this.state.chosenColumns            
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
        
        this.setState({
            chosenColumns: chosenColumns,
            checkboxes: checkboxes
        })
    }

    render(){
        
        return (
            <div>              
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
                                            className="form-control"                                        
                                            type="checkbox"
                                            name={"column" + key }
                                            value={key} 
                                            id={"column-" + column }  
                                            onChange={this.onCheckedColumn.bind(this)}
                                        /> {column}

                                    </label>

                                </div>                                                          
                        ))
                    }
                                        
                    <div className="form-group">
                        <label htmlFor="algorithm-selection">Choose the algorithm for the session</label>
                        <select className="form-control" id="algorithm-selection">
                            <option  defaultValue value="TSM">TSM</option> 
                            <option value="algo2">Algo 2</option>                            
                        </select>
                    </div>

                    <input type="submit" value="Start session" />                
                </form>
            </div>
        )
    }
}

export default SessionOptions