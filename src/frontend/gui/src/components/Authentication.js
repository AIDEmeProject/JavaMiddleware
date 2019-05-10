import React, { Component } from 'react';

import axios from "axios" 
import $ from 'jquery'

import {webplatformApi} from '../constants/constants'


function uploadFile(data, onSuccess){

    var endPoint = webplatformApi + "/session/new"
    var formData = new FormData();
               
    axios.post(endPoint,
               formData,     
               {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    'Authorization': 'Token ' + data.token
                        
                }}).then( response  => {
        
        onSuccess(response.data)
     }).catch(e => {
         alert(e)
     })    
}

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
                    Please enter your token in order to you use
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
export default Authentication