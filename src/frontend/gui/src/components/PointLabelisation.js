import React, { Component } from 'react';

class PointLabelisation extends Component{
    
    render(){
        console.log(this.props)        
        return (
            <div>

                <p>
                    Please label the following examples
                </p>

                <table className="table-label">
                    <thead>                        
                        <tr>                        
                            <th>
                                Row id
                            </th>

                            {
                                this.props.availableVariables.map((column, key) => {
                                    
                                    return (
                                        <th key={key} >
                                        {column.name} 
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
                    this.props.pointsToLabel.map((point, key) => {
                        
                        return (

                            <tr key={key}>

                                <td >
                                        {point.id}
                                </td>
                      
                                {

                                    point.data.map((value, valueKey) => {
                                        return (
                                            
                                            <td  key={valueKey}>
                                                {value}
                                            </td>
                                        )
                                    })
                                }

                                <td>
                                    <button
                                        className="btn btn-raised btn-primary" 
                                        data-key={key} 
                                        onClick={this.props.onPositiveLabel}>
                                        Yes
                                    </button>

                                    <button 
                                        className="btn btn-raised btn-primary"  
                                        data-key={key} 
                                        onClick={this.props.onNegativeLabel}
                                    >
                                        No
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

export default PointLabelisation