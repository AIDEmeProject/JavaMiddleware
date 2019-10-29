import React, { Component } from 'react';

import * as  d3 from "d3"

class VectorStatistics extends Component{
    
    constructor(props){

        super(props)
        this.state = {
            
        }
    }

    render(){
        const data = this.props.data
        const min = d3.min(data),
              max = d3.max(data),
              std = d3.deviation(data),
              mean = d3.mean(data),
              median = d3.median(data)

        return (
            <div>
                <h5>Descriptive statistics</h5>
                        
                <table className="table">
                    <thead>
                        <tr>
                            <th>
                                Min
                            </th>
                            <th>
                                Max
                            </th>
                            <th>
                                Mean
                            </th>
                            <th>
                                Median
                            </th>
                            <th>
                                Standard deviation
                            </th>                            
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>
                                {max}
                            </td>
                            <td>
                                {min}
                            </td>
                            <td>
                                {mean}
                            </td>
                            <td>
                                {median}
                            </td>
                            <td>
                                {std}
                            </td>                            
                        </tr>
                    </tbody>
                </table>    
            </div>
        )
    }
}

export default VectorStatistics