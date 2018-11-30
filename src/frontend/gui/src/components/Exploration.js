import React, { Component } from 'react';

import $ from "jquery";
import {backend} from '../constants/constants'

class Exploration extends Component{

    render(){

        if (this.props.initialLabelingSession){

            var FirstPhase = (

                <p>
                    The first phase of labeling keeps goind on
                    until an instance of a positive and a negative example 
                    is provided
                </p>
            )
        }
        else {
            var FirstPhase = (<div></div>)
        }

        return (

            <div>
                <p> label this sample</p>
                
                { FirstPhase }
                <div>

                    <div style={{display: "inline-block", width:10, margin: 10}}>
                        id
                    </div>

                    {
                        this.props.options.chosenColumns.map((column, key) => {
                            return (
                                <div key={key} style={{display: "inline-block", minWidth:10, margin: 10}}>
                                 {column} 
                                </div>
                            )
                        })
                    }

                    <div style={{display: "inline-block", minWidth:10, margin: 10}}>
                        Label 
                    </div>
                </div>

                {
                    this.props.pointsToLabel.map((point, key) => {

                        return (

                            <div key={key}>

                                <div style={{margin: 10, width:10, display: "inline-block"}}>
                                                {point.id}
                                </div>
                                {

                                    point.data.array.map((value, valueKey) => {
                                        return (
                                            
                                            <div style={{margin: 10, width:10, display: "inline-block"}} key={valueKey}>
                                                {value}
                                            </div>
                                        )
                                    })
                                }

                                <button
                                    className="btn btn-raised btn-primary" 
                                    data-key={key} 
                                    onClick={this.onPositiveLabel.bind(this)}>
                                    Yes
                                </button>

                                <button 
                                    className="btn btn-raised btn-primary"  
                                    data-key={key} 
                                    onClick={this.onNegativeLabel.bind(this)}
                                >
                                    No
                                </button>
                            </div>                            
                        )
                    })
                }
            </div>
        )
    }

    onPositiveLabel(e){
        
        this.props.onPositiveLabel(e.target.dataset.key, this.props.onNewPointsToLabel)
    }

    onNegativeLabel(e){
        this.props.onNegativeLabel(e.target.dataset.key, this.props.onNewPointsToLabel)
    }
}

export default Exploration