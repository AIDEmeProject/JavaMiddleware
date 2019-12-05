import React, { Component } from 'react';

import loadCSVFromInputFile from '../../lib/data_utils'
import Dataset from '../../model/Dataset';
import Exploration from '../Exploration/Exploration'


class QueryTrace extends Component{

    render(){

        return (
            
            <div className="row">
                <div className="col col-lg-6 offset-3 card">
                <h1>
                    New Session
                </h1>

                <div>                
                    <div>   
                        <div className="form-group ">
                        
                            <label htmlFor="dataset">
                               1. Choose the dataset to be labeled
                            </label>
                            <input
                                required
                                className="form-control-file"
                                id="dataset" name="dataset" type="file" 
                            />
                           
                           <button
                                onClick={this.onValidateTrace.bind(this)}
                           >
                               Validate
                            </button>
                        </div>
                    </div>
                </div>

                <div>
                                  
                </div>
            </div>

            </div>
        )
    }

    constructor(props){
        super(props)
        this.state = {
            columnNames: ['price_msrp', 'height', 'basic_year', 'body_type']
        }
    }

    onValidateTrace(e){
        loadCSVFromInputFile("dataset", event => {
            
            var fileContent = event.target.result 
            var dataset = Dataset.buildFromLoadedInput(fileContent)

            this.setState({
                'dataset': dataset
            })
        })
    }

}


export default QueryTrace