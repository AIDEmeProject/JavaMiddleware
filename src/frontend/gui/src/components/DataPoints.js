import React, { Component } from 'react';
import Dataset from '../model/Dataset'


class DataPoints extends Component{

    constructor(props){
        super(props)
        this.state = {
            labelId: null
        }
    }
  
    render(){

            if ( ! this.props.show){
                return (<div></div>)
            }

            const dataset = this.props.dataset
        
            return (

                <div className="row">
                    <div className="col col-lg-12">

                    
                    <hr />
                    <h3>
                        Labeled Points
                    </h3>
                
                    <table className={this.props.normal ? "table": "table-label"}>
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

                                <th>
                                    Label 
                                </th>                                                    
                            </tr>
                        </thead>

                        <tbody>
                        
                        {
                            this.props.points.map((point, key) => {
                                
                                const data = dataset.get_selected_columns_point(point.id)
                                
                                
                                return (

                                    <tr key={key}>

                                        <td >
                                                {point.id}
                                        </td>
                            
                                        {

                                            data.map((value, valueKey) => {


                                                return (
                                                    
                                                    <td 
                                                        key={valueKey}
                                                        data-toggle="tooltip"
                                                        data-placement="top" 
                                                        title={value}
                                                        key={valueKey}
                                                    >
                                                        {Dataset.displayValue(value)}
                                                    </td>
                                                )
                                            })
                                        }

                                        <td>
                                            { point.label }
                                        </td>
                                    </tr>
                                )
                            })                    
                        }
                    </tbody>
                
                </table>
                </div>
            </div>
        )     
    }            
}

export default DataPoints