import React, { Component } from 'react';

class LearnerOption extends Component{

    render(){
        return (    
            <div className="form-group">
                <label htmlFor="algorithm-selection">Learner</label>
                    <select 
                        onChange={this.onLearnerChange.bind(this)}
                        className="form-control" 
                        id="algorithm-selection"
                        name="active-learner"
                    >                    
                        <option 
                            value="simplemargin"
                            defaultValue
                        >
                            Simple margin (SVM)
                        </option>
                        
                        <option 
                            value="simplemargintsm"
                            defaultValue
                        >
                            Simple margin (SVM) + TSM
                        </option>


                        <option value="versionspace">
                            Version Space
                        </option>              

                        <option value="factorizedversionspace">
                            Factorized Version Space
                        </option>                
                    </select>
            </div>
        )
    }

    onLearnerChange(e){
        
        this.props.learnerChanged(e.target.value)
    }
}

export default LearnerOption