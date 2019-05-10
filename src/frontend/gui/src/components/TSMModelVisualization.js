import React, { Component } from 'react';


import  HeatMap from './HeatMap'

function getVisualizationData(){

}

class ModelBoundaries extends Component{

    render(){

        return (
            <div>

            </div>
        )
    }
}

class TSMModelVisualization extends Component{

    beforeMounted(){
        
    }

    render(){

        var predictions = this.props.visualizationData.predictions.map(e => {
            return {
                id: e.dataPoint.id,
                label: e.label,
                data: e.dataPoint.data.array
            }
        })
        
        var TSMBound

        if (this.props.visualizationData.TSMBound){
            TSMBound = () => {
                return (
                    <p>                    
                        Estimated model performance >= {this.props.visualizationData.TSMBound * 100} %
                    </p>
                )
            }
        }
        else{
            TSMBound = () => {
                return (
                    <span></span>
                )
            }
        }

        if( this.props.TSM){
            return (<TSMBound />)
        }

        return (
            <div>
                <p>
                    We ran the current model of several example of the dataset so that you can check
                    if the model     
                </p>

                <TSMBound />

                 <table className="group-variable">
                    <thead>
                        <tr>

                            <td>Row id</td>
                            {
                                this.props.finalGroups.map((g, i) => {
                                    
                                    return (
                                        <th 
                                            key ={i}
                                            colSpan={g.length}
                                        >
                                            {g.map(e => e.name).join(", ")}
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
                    this.props.labeledPoints.map((point, i) => {
                        
                        return (

                            <tr 
                                key={i}
                                className="variable-group">

                                <td>
                                    {point.id}
                                </td>
                                {
                                    this.props.finalGroups.map((g, iGroup) => {
                                        
                                        var values = g.map( variable => {
                                            
                                            return point.data[variable.i]

                                        }).join(", ")
                                                                                                                         
                                        if ( typeof point.labels !== "undefined"){
                                            var L = () => {
                                                return <button
                                                            data-point={i}
                                                            data-subgroup={iGroup}
                                                            className="btn btn-primary btn-raised"
                                                            onClick = {this.onSubGroupNo.bind(this)}
                                                        >
                                                            No
                                                        </button>
                                            }
                                        }
                                        else{
                                            var L = () => {return <span></span>}
                                        }                                
                                        
                                        return (
                                            <td 
                                                colSpan={g.length}
                                                key={iGroup}
                                            >
                                                {values} <L />
                                            </td>
                                        )
                                    })
                                }
                                <td>
                                
                                    <button 
                                        style={{display: typeof point.labels === "undefined" ? "inherit": "none"}}
                                        className="btn btn-primary btn-raised"
                                        data-point={i}
                                        onClick={this.groupWasLabeledAsYes.bind(this)}
                                    >
                                        Yes
                                    </button>
                                                        
                                    <button 
                                        style={{display: typeof point.labels === "undefined" ? "inherit": "none"}}
                                        className="btn btn-primary btn-raised"
                                        data-point={i}
                                        onClick={this.groupWasLabeledAsNo.bind(this)}
                                    >
                                        No
                                    </button>

                                    <button
                                        className="btn btn-primary btn-raised"
                                        style={{display: typeof point.labels === "undefined" ? "none": "inherit"}}
                                        data-point={i}
                                        onClick={this.groupSubLabelisationFinished.bind(this)}
                                    >
                                        Validate Subgroup labels
                                    </button>
                                </td>
                            </tr>
                        )
                    })
                }
                </tbody>

                </table>
                    
                    
            </div>
        )
    }
}


export default TSMModelVisualization