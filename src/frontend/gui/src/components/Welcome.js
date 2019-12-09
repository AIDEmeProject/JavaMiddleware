import React, { Component } from 'react';

class Welcome extends Component{

  render(){

    return (
        <div className="row">

            <div className="col col-lg-6 offset-lg-3">

                <h1>Welcome to Aideme</h1>


                <p>
                    Content from the poster
                    You can edit the file Wecome.js if you want                    
                </p>


                <p>
                    Content from the poster
                    You can edit the file Wecome.js if you want                    
                </p>


                <p>
                    Content from the poster
                    You can edit the file Wecome.js if you want                    
                </p>


                <p>
                    Content from the poster
                    You can edit the file Wecome.js if you want                    
                </p>

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
            </div>

        </div>
    )

  }
}

export default Welcome