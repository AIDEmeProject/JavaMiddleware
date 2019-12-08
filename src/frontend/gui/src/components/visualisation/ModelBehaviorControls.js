import React, { Component } from 'react';

class ModelBehaviorControls extends Component{

    constructor(props){
        super(props)
    }

    render(){

        const iteration = this.props.iteration

        return (
            <div id="iteration-control">

                <div>Iteration <span className="iteration-number">{iteration + 1}</span></div>
                <button
                    className="btn btn-primary btn-raised"
                    onClick={this.props.onPreviousIteration}
                > Previous </button>
                <button
                    className="btn btn-primary btn-raised"
                    onClick={this.props.onNextIteration}
                >
                    Next
                </button>

            </div>
        )
    }
}

export default ModelBehaviorControls