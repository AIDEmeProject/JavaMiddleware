import React, { Component } from 'react';

import $ from "jquery";
import {backend, webplatformApi} from '../constants/constants'

import ModelVisualization from './visualisation/ModelVisualization'
import ExplorationActions from './ExplorationActions'
import PointLabelisation from './PointLabelisation'
import InitialSampling from './InitialSampling/InitialSampling'

class Exploration extends Component{

    constructor(props){

        super(props)
        this.state = {
            showModelVisualisation: false
            
        }
    }

    render(){
            
        if (this.props.initialLabelingSession){
            return (<InitialSampling {...this.state} {...this.props} />)
        }
        
        return (

            <div>
                <h4>
                    Labeleling phase
                </h4>
                            
                                                            
                <PointLabelisation 
                    {...this.props} 
                    {...this.state}
                />
    
                <ExplorationActions
                    show={ ! this.props.initialLabelingSession}
                    onLabelWholeDatasetClick={this.onLabelWholeDatasetClick.bind(this)}
                    onVisualizeClick={this.onVisualizeClick.bind(this)}
                />
                
                <ModelVisualization 
                    {...this.props}
                    {...this.state}
                />
                
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