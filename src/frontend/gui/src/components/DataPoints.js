import React, { Component } from 'react';

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
            return (

                <div>
                    <hr />
                    <h3>
                        Labeled Points
                    </h3>
                
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

                            <th>
                                Label 
                            </th>                                                    
                        </tr>
                    </thead>

                    <tbody>
                    
                    {
                        this.props.points.map((point, key) => {
                        
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
                                        { point.label }
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