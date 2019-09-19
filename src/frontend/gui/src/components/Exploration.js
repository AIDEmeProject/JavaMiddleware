import React, { Component } from 'react';

import $ from "jquery";
import {backend, webplatformApi} from '../constants/constants'

import ModelVisualization from './visualisation/ModelVisualization'

import PointLabelisation from './PointLabelisation'
import InitialSampling from './InitialSampling/InitialSampling'
import HeatMap from './visualisation/HeatMap'

import DataPoints from './DataPoints'

class Exploration extends Component{

    constructor(props){
        
        super(props)
        this.state = {
            showModelVisualisation: false,
            showLabelView: true,
            showLabelHistory: false
            
        }
    }

    render(){
                    
        if (this.props.initialLabelingSession){
            return (<InitialSampling {...this.state} {...this.props} />)
        }
        
        return (

            <div>
                <h4>
                    Labeleling phase     <button
                        className="btn btn-primary btn-raised pull-right"
                    onClick={this.onLabelWholeDatasetClick.bind(this)}
                >
                    Get the whole dataset labeled
                </button>
                </h4>
                            
                <ul className="nav nav-tabs">
                   
                    <li className="nav-item">

                        <a 
                           className={this.state.showLabelView ? "nav-link active": "nav-link"} 
                           href="#basic-options"
                           onClick={() => this.setState({
                               'showModelVisualisation': false,
                               'showLabelView': true,
                               'showHeatmap': false,
                               'showLabelHistory': false
                            })}
                        >
                            Label view
                        </a>
                    </li>

                    <li className="nav-item">
                        <a 
                            className={this.state.showLabelHistory ? "nav-link active": "nav-link"} 
                            href="#advanced-options"
                            onClick={() => this.setState({
                                'showModelVisualisation': false, 
                                'showLabelView': false, 
                                'showHeatmap': false,
                                'showLabelHistory': true
                            })}
                        >
                            View labeled points
                        </a>
                    </li>         

                    <li className="nav-item">
                        <a 
                            className={this.state.showModelVisualisation ? "nav-link active": "nav-link"} 
                            href="#advanced-options"
                            onClick={() => this.setState({
                                'showModelVisualisation': true, 
                                'showLabelView': false,  
                                'showHeatmap': false,
                                'showLabelHistory': false
                            })}
                        >
                            Assess model Performance
                        </a>
                    </li>                                                  
                </ul>
        
                {                     
                    this.state.showLabelView &&                                                             
                    
                        <div>
                            <PointLabelisation 
                                {...this.props} 
                                {...this.state}
                            />

                            <HeatMap
                                labeledPoints={this.props.labeledPoints}
                                datasetInfos={this.props.datasetInfos}
                                availableVariables={this.props.chosenColumns}
                            />
                        </div>
                }

                {
                    this.state.showModelVisualisation && 
                                    
                    <ModelVisualization 
                        {...this.props}
                        {...this.state}
                    />
                }

                {
                    this.state.showLabelHistory && 

                    <DataPoints                             
                        availableVariables={this.props.finalVariables}
                        points={this.props.labeledPoints}
                        chosenColumns={this.props.chosenColumns}
                        show={true}
                    />
                }
                
            </div>
        )
    }

   
    onLabelWholeDatasetClick(){
        
        getWholedatasetLabeled()

        notifyLabel(this.props.tokens)

    }

    onVisualizeClick(){
        getVisualizationData(this.dataWasReceived.bind(this))        
    }

    dataWasReceived(data){
        
        this.setState({
            showModelVisualisation: true,
            visualizationData: data
        })
    }
}


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

function notifyLabel(tokens){
    
    var wasAskedToLabelDatasetUrl = webplatformApi + "/session/" + tokens.sessionToken + "/label-whole-dataset"

    $.ajax({
        type: "PUT", 
        dataType: "JSON",
        url: wasAskedToLabelDatasetUrl,
        headers: {
            Authorization: "Token " + tokens.authorizationToken
        },
        data:{
            clicked_on_label_dataset: true
        }        
    })
}



export default Exploration