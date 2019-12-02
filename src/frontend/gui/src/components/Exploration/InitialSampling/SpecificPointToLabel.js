import React, { Component } from 'react';

import $ from 'jquery'
import {backend} from '../../../constants/constants'


class SpecificPointToLabel extends Component{

    constructor(props){

        super(props)
        this.state = {
            labelId: null
        }
    }
       
    render(){
                       
        return (
            <div>
                <p>
                    You can help the search of the positive and the negative example by providing the row id of 
                    an example you know
                </p>
                <div className="form-inline">
                    <div className="form-group">
                    
                    <label htmlFor="rowid">
                        Row id </label>
                    <input 
                        className="form-control"
                        type="integer" onChange={e => this.setState({labelId: e.target.value})}
                        id="rowid"
                    />
                
                <input 
                    onClick={this.onSpecificPointRequest.bind(this)}
                    type="submit" value="get"/>
                </div>
                </div>
            </div>
        )        
    }            

    onSpecificPointRequest(){

        getSpecificPoint(this.state.labelId, this.props.onNewPointsToLabel)

    }
}

function getSpecificPoint(id, onSuccess){

    var url = backend + "/get-specific-point-to-label"

    $.get(url, {id: id}, onSuccess)
}

export default SpecificPointToLabel