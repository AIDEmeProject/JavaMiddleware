import React, { Component } from 'react';


class AdvancedOptions extends Component{

    render(){

        if ( ! this.props.showAdvancedOptions){
            return (<div></div>)
        }

        return (
            <div>                      
                <div className="form-group">
                    <label htmlFor="algorithm-selection">Learner</label>
                    <select 
                        className="form-control" 
                        id="algorithm-selection"
                        name="active-learner"
                    >
                        <option 
                            value="UncertaintySampler"
                            defaultValue
                        >
                            Uncertainty Sampling
                        </option>
                        <option value="versionSpace">
                            Version Space
                        </option>                             
                    </select>
                </div>

                <div className="form-group">
                    <label htmlFor="classifier">Classifier</label>
                    <select 
                        className="form-control" 
                        id="classfier-selection"
                        name="classifier"
                    >
                        <option 
                            value="SVM"
                            defaultValue
                        >
                            SVM
                        </option>
                        <option value="Majority Vote">
                            Majority Vote
                        </option>                             
                    </select>
                </div>                        
            </div>
        )
    }

    componentDidMount(){
        
    }

}



export default AdvancedOptions