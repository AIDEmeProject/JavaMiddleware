import React, { Component } from 'react';

import $ from "jquery";
import {backend} from '../constants/constants'

import ModelVisualization from './ModelVisualization'

class Exploration extends Component{

    render(){
        var FirstPhase,
            Bottom

        if (this.props.initialLabelingSession){

            FirstPhase = () => {
                return (
                <p>
                    The first phase of labeling continues until we obtain 
                    a positive example and a negative example.                    
                </p>
            )}

            Bottom = () => {return(
                <div></div>
            )}

        }
        else {
            
            FirstPhase = () => {return(<div></div>)}

            Bottom = () => {
                return (
                <div>
                    <button 
                        className="btn btn-primary btn-raised"
                    >

                        Visualize model

                    </button>

                    <button
                        className="btn btn-primary btn-raised"
                    >
                        Get the whole dataset labeled
                    </button>
                </div>
            )}            
        }

        return (

            <div>
                <p> label this sample</p>
                
                <FirstPhase />
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

                <Bottom />
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