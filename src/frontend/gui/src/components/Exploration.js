import React, { Component } from 'react';

import $ from "jquery";
import {backend} from '../constants/constants'

import ModelVisualization from './ModelVisualization'
import ExplorationActions from './ExplorationActions'
import SpecificPointToLabel from './SpecificPointToLabel'

function getWholedatasetLabeled(){

    var url = backend + "/get-labeled-dataset"

    $.get(url, response => {

        var blob = new Blob([response]);
        var link = document.createElement('a');
        
        link.href = window.URL.createObjectURL(blob);
        document.body.appendChild(link);
        link.download = "labeled_dataset.csv";
        link.click();
    })
}

function getVisualizationData(dataWasReceived){

    var url = backend + "/get-visualization-data"

    $.get(url, dataWasReceived)


    
}

class Exploration extends Component{

    constructor(props){
        super(props)
        this.state = {
            showModelVisualisation: false
            
        }
    }

    onPositiveLabel(e){        
        this.props.onPositiveLabel(e.target.dataset.key, this.props.onNewPointsToLabel)
    }

    onNegativeLabel(e){
        this.props.onNegativeLabel(e.target.dataset.key, this.props.onNewPointsToLabel)
    }
    
    onLabelWholeDatasetClick(){
        getWholedatasetLabeled()
    }

    onVisualizeClick(){
        getVisualizationData(this.dataWasReceived.bind(this))        
    }

    dataWasReceived(data){
        console.log('cou')
        this.setState({
            showModelVisualisation: true,
            visualizationData: data
        })
    }



    render(){

        var FirstPhase,
            Bottom,
            Viz
        
        if (this.state.showModelVisualisation){
            Viz = () => {
                return (
                    <ModelVisualization 
                        {...this.props}
                        {...this.state}
                    />
                )
            }
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
        }
        else {            
            FirstPhase = () => {return(<div></div>)}                       
        }

        return (

            <div>
                <h4>
                    Labeleling phase
                </h4>
                            
                <FirstPhase />
                
                <p>Please label the following examples</p>
            
                <table className="table-label">
                    <thead>                        
                        <tr>
                        
                            <th>
                                Row id
                            </th>

                            {
                                this.props.availableVariables.map((column, key) => {
                                    
                                    return (
                                        <th key={key} >
                                        {column.name} 
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


                <SpecificPointToLabel 
                    onNewPointsToLabel={this.props.onNewPointsToLabel}
                    show={this.props.initialLabelingSession}
                />


                <ExplorationActions
                    show={ ! this.props.initialLabelingSession}
                    onLabelWholeDatasetClick={this.onLabelWholeDatasetClick.bind(this)}
                    onVisualizeClick={this.onVisualizeClick.bind(this)}
                />
                

                <Viz />
                
            </div>
        )
    }
}

export default Exploration