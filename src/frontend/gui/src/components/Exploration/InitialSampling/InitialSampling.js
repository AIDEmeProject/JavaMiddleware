import React, { Component } from 'react';

import $ from 'jquery'
import {backend, webplatformApi} from '../../../constants/constants'

import SpecificPointToLabel from './SpecificPointToLabel'
import FakePointSampling from './FakePointSampling'
import PointLabelisation from '../../PointLabelisation'
import sendFakePoint from '../../../actions/sendFakePoint'

class InitialSampling extends Component{

    constructor(props){
        
        super(props)       

        this.state = {
            labelingInitialSampling: true
        }
    }
    
    render(){
        
        return (
            <div className="card">
                <div>
                    <div className="row">
                        <div className="col col-lg-8 offset-lg-2">

                            
                            <ul className="nav nav-tabs bg-primary">
                                <li className="nav-item">
                                    <a 
                                    className="nav-link active" 
                                    href="#"
                                    onClick={() => this.setState({labelingInitialSampling: true})}
                                    >
                                        Labeling initial sampling
                                    </a>
                                </li>

                                <li className="nav-item">
                                    <a 
                                    className="nav-link active" 
                                    href="#"
                                    onClick={() => this.setState({labelingInitialSampling: false})}
                                    >
                                        Fake point initial sampling
                                    </a>
                                </li>                     
                            </ul>
                        

                        <p>                            
                            The first phase of labeling continues until we obtain 
                            a positive example and a negative example. 
                        </p>

                        {
                            //this.state.labelingInitialSampling && 
                            false &&
                            
                            <SpecificPointToLabel 
                                onNewPointsToLabel={this.props.onNewPointsToLabel}                        
                            />
                        }
                    
                    </div>
                    </div>
                        

                    {
                        this.state.labelingInitialSampling && 

                        <div>
                                            
                            <PointLabelisation
                                {...this.props}   
                                {...this.state}
                            />
                        </div>
                    }

                 
                    {
                        ! this.state.labelingInitialSampling && 

                        <div>
                            <FakePointSampling 
                                pointToLabel={this.props.pointsToLabel[0].data}                            
                                onFakePointValidation={this.fakePointWasValidated.bind(this)}
                                availableVariables={this.props.availableVariables}
                            />
                       </div>
               
                    }
                </div>                                                  
            </div>
        )
    }

    fakePointWasValidated(fakePointData){

        const fakePoint = {
            'data': fakePointData,
            'label':1
        }
        sendFakePoint(fakePoint, this.props.fakePointWasValidated)
    }
}

export default InitialSampling