import React, { Component } from 'react';

import axios from "axios" 
import $ from 'jquery'

import {webplatformApi} from '../constants/constants'

class Authentication extends Component{

  handleSubmit(event){

      event.preventDefault()
      var data = {
          token: $('#token').val()
      }
      uploadFile(data, this.props.onAuthenticationSuccess)
  }

  render(){

        return (

            <main>
                
                <h1>
                    Welcome to AidePlus
                </h1>

                <p>
                    Please enter your token in order to you use the application.
                </p>

                <div>                
                    <form 
                        onSubmit={this.handleSubmit.bind(this)}     
                    >   

                        <div className="input-group ">
                        
                            <div className="input-group-prepend">
                                <span className="input-group-text" id="">Enter your token</span>
                            </div>
                            <input
                                id="token"
                                required
                                className="form-control"
                                name="token"
                            />
                           <input type="submit" value="submit" />
                        </div>                      
                    </form> 
                </div>        
            </main>
        )
    }
}


function uploadFile(data, onSuccess){

    var endPoint = webplatformApi + "/session/new"
    var formData = new FormData();
               
    var data = {
        headers: {
            'Content-Type': 'multipart/form-data',
            'Authorization': 'Token ' + data.token                
        }
    }

    axios.post(endPoint, formData, data)
         .then(response  => {        
            onSuccess(response.data)
        })
        .catch(e => {
            alert(e)
        })    
}

export default Authentication