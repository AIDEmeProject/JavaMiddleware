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

class ModelVisualization extends Component{

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

        if (this.props.TSMBound){
            TSMBound = () => {
                return (
                    <p>                    
                        Estimated model performance >= {this.props.TSMBound * 100} %
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
        return (
            <div>
                <p>
                    We ran the current model of several example of the dataset so that you can check
                    if the model     
                </p>

                <TSMBound />

                <table className='predicted'>
                    <thead>
                        <tr>

                            <th>
                                Id
                            </th>
                            
                        {
                            
                            this.props.availableVariables.map((c, i) => {
                                return (
                                    <th
                                        key={i}
                                    >
                                        {c.name}
                                    </th>
                                )
                            })
                            
                        }
                        <th>
                            Predicted Label
                        </th>
                            
                        </tr>
                    </thead>
                
                    {
                        predictions.map((data, i) => {
                            return (
                                <tr key={i}>

                                    <td>
                                        {data.id}
                                    </td>

                                    {
                                        data.data.map( (d, j) => {
                                            return (
                                                <td
                                                    key={j}
                                                >
                                                    {d}
                                                </td>

                                            )
                                        })
                                    }

                                    <td>
                                        {data.label}
                                    </td>
                                </tr>
                            )
                        })
                    }
                    
                    </table>
                    
                    <HeatMap />
            </div>
        )
    }
}

ModelVisualization.defaultProps = {
    

    
}

export default ModelVisualization