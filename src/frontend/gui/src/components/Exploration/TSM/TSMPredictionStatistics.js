import React, { Component } from 'react';

class TSMPredictionStatistics extends Component{
  
    render(){         
        

        return (

            <div id="tsm-pred-stats">
                <h5>TSM prediction Statistics</h5>
                
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

                        <tr>
                            <td>
                            Unknown  
                            </td>
                            <td>
                                {this.props.stats.unknown}
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

export default TSMPredictionStatistics