import React, { Component } from 'react';

class PredictionStatistics extends Component{
  
    render(){         
    
        return (

            <div id="prediction-stats">
                
                <h5>Classifier statistics</h5>
                
                <table className="table">                
                    <tbody>
                        <tr>
                            <td>
                            Positive  
                            </td>
                            <td>
                                {this.props.stats.positive}
                            </td>                        
                        </tr>

                        <tr>
                            <td>
                            Negative  
                            </td>
                            <td>
                                {this.props.stats.negative}
                            </td>                        
                        </tr>                       
                    </tbody>
                </table>
            </div>
        )
    }

    constructor(props){
        super(props)        
    }

}

export default PredictionStatistics