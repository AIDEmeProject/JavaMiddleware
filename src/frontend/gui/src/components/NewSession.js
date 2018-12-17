import React, { Component } from 'react';

import axios from "axios" ;

import {backend} from '../constants/constants'

import TSMExploration from './TSMExploration'

import ModelVisualization from './ModelVisualization'
import GroupVariables from './GroupVariables'

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
  }

  render(){

        return (

            <div>
                
                <h1>
                    New Session
                </h1>

                <div>                
                    <form 
                        onSubmit={this.handleSubmit.bind(this)}     
                    >   
                        <div className="form-group ">
                        
                            <label htmlFor="dataset">
                                Choose the dataset to be labeled
                            </label>
                            <input
                                required
                                className="form-control-file"
                                id="dataset" name="dataset" type="file" 
                            />
                            <small className="text-muted">CSV and TSV are supported</small>
                        </div>


                        <div className="form-group ">

                            <label htmlFor="separator" className="bmd-label-floating">
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
        )
    }
}
export default NewSession