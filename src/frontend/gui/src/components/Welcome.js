import React, { Component } from 'react';

import welcomeImg from  "../resources/welcome.png"

class Welcome extends Component{

  render(){

    return (
        <div className="row">

            <div className="col col-lg-6 offset-lg-3">

            <div className="center">

                <h1>Welcome to AIDEme</h1>

                <p>                
                    <img src={welcomeImg} width="600" />
                </p>
                <p className="">
                    <button
                        className="btn btn-raised"
                        onClick={this.props.onTraceClick}
                    >
                        Trace session
                    </button>
                    <button
                        className="btn btn-raised"
                        onClick={this.props.onInteractiveSessionClick}
                    >
                        Interactive session
                    </button>
                </p>
            </div>
            </div>
        </div>
    )

  }
}

export default Welcome