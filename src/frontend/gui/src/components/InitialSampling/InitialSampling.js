import React, { Component } from 'react';

import $ from 'jquery'
import {backend, webplatformApi} from '../../constants/constants'

import SpecificPointToLabel from './SpecificPointToLabel'
import FakePointSampling from './FakePointSampling'
import PointLabelisation from '../PointLabelisation'

class InitialSampling extends Component{

    constructor(props){
        
        super(props)       
    }
    
    render(){

        console.log(this.props.options)
    
        if ( ! this.props.initialLabelingSession){
            return (<div></div>)
        }


        if (this.props.options.useFakePoint){
            return (
                <div>
                     <FakePointSampling 
                        pointToLabel={this.props.pointsToLabel[0].data}
                     />
                </div>
            )
        }

        return (
            <div>
                <div>
                    The first phase of labeling continues until we obtain 
                    a positive example and a negative example. <br />

                    
                    <SpecificPointToLabel 
                        onNewPointsToLabel={this.props.onNewPointsToLabel}
                        
                    />
                   
                   <PointLabelisation
                        {...this.props}   
                        {...this.state}
                    />

                </div>
                                                  
            </div>
        )
    }

}

export default InitialSampling