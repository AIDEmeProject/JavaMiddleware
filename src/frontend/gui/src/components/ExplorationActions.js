import React, { Component } from 'react';


class ExplorationActions extends Component{

    render(){
            if ( ! this.props.show){
                return (<div></div>)
            }
            
            return (
            <div>

                <hr />
                
                <button 
                    className="btn btn-primary btn-raised"
                    onClick={this.props.onVisualizeClick}
                >
                    Visualize model
                </button>

                <button
                    className="btn btn-primary btn-raised"
                    onClick={this.props.onLabelWholeDatasetClick}
                >
                    Get the whole dataset labeled
                </button>
            </div>
            )
        
        }        
}

export default ExplorationActions