import React, { Component } from 'react';

class GroupedPointTableHead extends Component{
    
    render(){
        return (
            
            <thead>
                <tr>
                    <td>Row id</td>
                    {
                        
                        this.props.groups.map((g, i) => {
                            
                            const columnNames = g.map(v => v.name)

                            return (
                                <th 
                                    key ={i}
                                    colSpan={g.length}
                                >
                                    {columnNames.join(", ")}
                                </th>
                            )
                        })
                    }
                    <th>
                        Label    
                    </th>                
                </tr>
            </thead>
        )
    }
}

export default GroupedPointTableHead