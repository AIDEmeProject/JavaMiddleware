import React, { Component } from 'react';

import $ from "jquery";
import {backend} from '../constants/constants'

import ModelVisualization from './ModelVisualization'

class Exploration extends Component{

    constructor(props){
        super(props)
        this.state = {
            showModelVisualisation: false
        }
    }

    render(){

        var FirstPhase,
            Bottom,
            Viz
        
        if (this.state.showModelVisualisation){
            Viz = () => {return(<ModelVisualization />)}
        }
        else {
            Viz = () => { return (<div></div>) }
        }        
        
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

                    <hr />
                    
                    <button 
                        className="btn btn-primary btn-raised"
                        onClick={ () => {this.setState({showModelVisualisation: ! this.state.showModelVisualisation})} }
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

                <h4>
                    Labeleling phase
                </h4>

                            
                <FirstPhase />
                
                <p>Please label the following samples</p>

                

                <table className="table-label">
                    <thead>                        
                        <tr>
                        
                            <th>
                                id
                            </th>

                            {
                                this.props.options.chosenColumns.map((column, key) => {
                                    return (
                                        <th key={key} >
                                        {column} 
                                        </th>
                                    )
                                })
                            }

                            <th>
                                Label 
                            </th>                                                    
                        </tr>
                    </thead>

                    <tbody>
                    
                {
                    this.props.pointsToLabel.map((point, key) => {

                        return (

                            <tr key={key}>

                                <td >
                                                {point.id}
                                </td>
                      
                                {

                                    point.data.map((value, valueKey) => {
                                        return (
                                            
                                            <td  key={valueKey}>
                                                {value}
                                            </td>
                                        )
                                    })
                                }

                                <td>
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
                                </td>
                            </tr>
                        )
                    })                    
                }
                </tbody>

                </table>


                <Bottom />
                

                <Viz />

                

                
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