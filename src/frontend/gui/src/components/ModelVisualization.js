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

        var TSMBound

        if (this.props.tsmBound){
            TSMBound = () => {
                return (
                    <p>                    
                        Estimated model performance >= {this.props.tsmBound * 100} %
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
                            
                        {
                            
                            this.props.chosenColumns.map((c, i) => {
                                return (
                                    <th
                                        key={i}
                                    >
                                        {c}
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
                        this.props.predictions.map((data, i) => {
                            return (
                                <tr key={i}>

                                    {
                                        data.values.map( (d, j) => {
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
    tsmBound: 0.9,

    chosenColumns: ['age', 'sex'],

    predictions : [
        {            
            values: [1, 2],
            label: 1
        },

        {            
            values: [4, 5],
            label: 0
        },

        {            
            values: [4, 5],
            label: 0
        },
    ]
}

export default ModelVisualization