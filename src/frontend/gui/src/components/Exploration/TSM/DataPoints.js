import React, { Component } from 'react';
import GroupedPointTableHead from './GroupedPointTableHead'


class DataPoints extends Component{

    constructor(props){
        super(props)        
    }
  
    render(){         
        
        return (

            <div>
                
                <h3>
                    Labeled Points
                </h3>
            
                <table className="table-label">
                
                    <GroupedPointTableHead 
                        groups={this.props.groups}
                    />  

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
                                            this.props.groups.map((g, iGroup) => {
                                                
                                                var pointIds = g.map(e => e.finalIdx)    
                                                
                                                var dataAsGroups = []

                                                pointIds.forEach(realId => {
                                                    var value = point.data[realId]
                                                
                                                    dataAsGroups.push(value)
                                                })
                                                                                                                                
                                                var values = dataAsGroups.join(", ")
                                                
                                                return (
                                                    <td 
                                                        colSpan={g.length}
                                                        key={iGroup}
                                                    >
                                                        {values} 
                                                    </td>
                                                )
                                            })
                                        }       

                                        <td>
                                            {point.label}    
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

export default DataPoints
