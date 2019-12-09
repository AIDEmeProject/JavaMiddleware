import React, { Component } from 'react';


class ClassifierOption extends Component{

    render(){
        return(
            <div>
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
}





class AdvancedOptions extends Component{

    render(){

        if ( ! this.props.showAdvancedOptions){
            return (<div></div>)
        }

        return (
            
            <div className="row">                      
                <div className="col col-lg-6 offset-lg-3">
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
                                Simple Margin
                            </option>
                            <option value="versionSpace">
                                Version Space
                            </option>                             
                        </select>
                    </div>

                                        
            </div>
        </div>
            
        )
    }

    componentDidMount(){
        
    }

}



export default AdvancedOptions