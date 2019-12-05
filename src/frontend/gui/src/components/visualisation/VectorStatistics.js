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
              median = d3.median(data),
              uniqueValues = d3.set(data).values().length

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
                            <th>
                                Unique values
                            </th>                          
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>
                                {min}
                            </td>
                            <td>
                                {max}
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
                            <td>
                                {uniqueValues}
                            </td>                          
                        </tr>
                    </tbody>
                </table>  

                { 
                    this.props.uniqueValues.length < 50 &&
                    <div>
                        <p>
                            Unique value counts
                        </p>
                        <table className="table">
                            <thead>
                                <tr>
                                    <th>
                                        Value
                                    </th>
                                    <th>
                                        Count
                                    </th>
                                </tr>
                            </thead>

                            <tbody>
                                {
                                    this.props.uniqueValues.map(d => {
                                    return (
                                        <tr>
                                            <td>
                                                { d[0]}
                                            </td>
                                            <td>
                                                { d[1] }
                                            </td>
                                        </tr>
                                        )
                                    })
                                }
                                
                            </tbody>
                        </table>
                    </div>
                }
            </div>
        )
    }
}

export default VectorStatistics