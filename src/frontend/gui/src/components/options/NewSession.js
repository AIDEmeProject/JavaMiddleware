import React, { Component } from 'react';

import axios from "axios" ;

import {backend} from '../../constants/constants'
import loadCSVFromInputFile from '../../lib/data_utils'

function uploadFile(event, onSuccess){

    var endPoint = backend + "/new-session"
    var formData = new FormData();
    
    var file = document.querySelector('form input[type=file]').files[0]

    if (! file){
        alert('Please select a file')
        return
    }
    formData.append("dataset", file);
    formData.append('separator', document.getElementById('csv-separator').value)

    axios.post(endPoint,
               formData, 
               {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
     }).then( response  => {
        
        onSuccess(response.data)
     }).catch(e => {
         alert(e)
     })    
}

class NewSession extends Component{

  
  handleSubmit(event){

      event.preventDefault()
      uploadFile(event, this.props.fileUploaded)
      
      loadCSVFromInputFile("dataset", this.props.onDatasetLoaded)
  }

  render(){

        return (

            <div className="row">
                <div className="col col-lg-6 offset-3 card">
                <h1>
                    New Session
                </h1>

                <div>                
                    <form 
                        onSubmit={this.handleSubmit.bind(this)}     
                    >   
                        <div className="form-group ">
                        
                            <label htmlFor="dataset">
                               1. Choose the dataset to be labeled
                            </label>
                            <input
                                required
                                className="form-control-file"
                                id="dataset" name="dataset" type="file" 
                            />
                           
                        </div>

                        <p>
                            2. Choose the separator.
                        </p>
                        <p>
                            CSV, TSV and Semi-colon separator are supported
                        </p>
                        <div className="form-group ">
                            
                            <label htmlFor="separator" >
                                Separator
                            </label>
                                <select
                                    className="form-control"
                                    id="csv-separator"
                                    name="separator"
                                >
                                    <option value="," >Comma ","</option>                                
                                    <option value="\t" >Tab</option>
                                    <option value=";" >Semi-colon ";"</option>                                
                                </select>
                            
                        </div>

                        <div className="form-group bmd-form-group">
                           
                            <input
                                
                                className="btn btn-raised btn-primary"
                                type="submit" value="Confirm" 
                            />
                        </div>
                    </form> 
                </div>   
                </div>
                     
            </div>
        )
    }
}
export default NewSession