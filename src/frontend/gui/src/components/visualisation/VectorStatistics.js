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
              //uniqueValues = d3.set(data).values().length

        return (
            <div>
                <h4>Descriptive statistics</h4>
                        
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
                                {this.props.uniqueValues.length}
                            </td>                          
                        </tr>
                    </tbody>
                </table>  

                { 
                    false &&
                    <div>
                        <h4>
                            Unique value counts
                        </h4>
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
                                    this.props.uniqueValues.map((d,i) => {
                                    return (
                                        <tr key={i}>
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