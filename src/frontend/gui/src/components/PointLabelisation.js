import React, { Component } from 'react';
import Dataset from '../model/Dataset'

class PointLabelisation extends Component{
    
    render(){
        const dataset = this.props.dataset
        return (
            <div className="">
                <div className="row">
                    <div className="col col-lg-8 offset-lg-2">
                        <p>
                            Please label the following examples
                        </p>
                  

                <table className="table-label">
                    <thead>                        
                        <tr>                        
                            

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

                               
                      
                                {

                                    pointData.map((value, valueKey) => {
                                        return (
                                            
                                            <td
                                                key={valueKey}
                                                data-toggle="tooltip"
                                                data-placement="top" 
                                                title={value}
                                            >
                                                {Dataset.displayValue(value)}
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
                                            onClick={e => this.props.onPositiveLabel(e)}>
                                            Yes
                                        </button>

                                        <button 
                                            className="btn btn-raised btn-primary"  
                                            data-key={key} 
                                            onClick={e => this.props.onNegativeLabel(e)}
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

            </div>
                </div>
        )   
    }        
}

export default PointLabelisation