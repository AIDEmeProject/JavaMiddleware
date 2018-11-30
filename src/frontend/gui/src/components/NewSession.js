import React, { Component } from 'react';

import axios from "axios" ;

import {backend} from '../constants/constants'

function uploadFile(event, onSuccess){

    var endPoint = backend + "/new-session"
    var formData = new FormData();
    
    var file = document.querySelector('form input[type=file]').files[0]
    formData.append("dataset", file);
    
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

                        <label htmlFor="dataset">Choose the dataset to be labeled</label>
                        <input
                            className="form-control-file"
                            id="dataset" name="dataset" type="file" 
                        />

                        <input
                            className="btn btn-raised btn-primary"
                            type="submit" value="Confirm" 
                        />
                        
                    </form> 
                </div>        
            </div>
        )
    }
}
export default NewSession