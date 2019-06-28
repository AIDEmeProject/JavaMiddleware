import React, { Component } from 'react';

import $ from 'jquery'
import {backend, webplatformApi} from '../../constants/constants'

import SpecificPointToLabel from './SpecificPointToLabel'
import FakePointSampling from './FakePointSampling'
import PointLabelisation from '../PointLabelisation'

class InitialSampling extends Component{

    constructor(props){
        
        super(props)       

        this.state = {
            labelingInitialSampling: true
        }
    }
    
    render(){
        console.log(this.props)
        return (
            <div>
                <div>

                    <ul className="nav nav-tabs">
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

                    The first phase of labeling continues until we obtain 
                    a positive example and a negative example. <br />

                    {
                        this.state.labelingInitialSampling && 

                        <div>
                            <SpecificPointToLabel 
                                onNewPointsToLabel={this.props.onNewPointsToLabel}                        
                            />
                
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
                            {...this.props}
                            variableTypes={this.props.options.variableTypes}
                            />
                       </div>
               
                    }


                </div>
                                                  
            </div>
        )
    }
}

export default InitialSampling