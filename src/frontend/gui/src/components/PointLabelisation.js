import React, { Component } from 'react';

class PointLabelisation extends Component{
    
    render(){
        
        return (
            <div className="">
                <div className="row">
                    <div className="col col-lg-8 offset-lg-2">
                        <p>
                            Please label the following examples
                        </p>
                    </div>
                </div>

                <table className="table-label">
                    <thead>                        
                        <tr>                        
                            <th>
                                Row id
                            </th>

                            {
                                this.props.chosenColumns.map((column, key) => {
                                    
                                    return (
                                        <th key={key} >
                                            {column.name} 
                                        </th>
                                    )
                                })
                            }

                                                                             
                        </tr>
                    </thead>

                    <tbody>
                    
                {
                    this.props.pointsToLabel.map((point, key) => {
                        const pointData = this.props.dataset.get_selected_columns_point(point.id)
                        return (

                            <tr key={key}>

                                <td >
                                        {point.id}
                                </td>
                      
                                {

                                    pointData.map((value, valueKey) => {
                                        return (
                                            
                                            <td  key={valueKey}>
                                                {value}
                                            </td>
                                        )
                                    })
                                }

                               
                            </tr>
                        )
                    })                    
                }
                </tbody>

                </table>

                <table className="table-control">
                    <thead>
                        <tr>
                            <th>Label</th>
                        </tr>

                    </thead>
                    <tbody>
                        {
                        this.props.pointsToLabel.map((point, key) => {
                            
                            return (

                                <tr key={key}>

                                    
                                    <td className="button-td">
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