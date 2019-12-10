import React, { Component } from 'react';

import $ from 'jquery'
import {backend, webplatformApi} from '../../../constants/constants'

import SpecificPointToLabel from './SpecificPointToLabel'
import FakePointSampling from './FakePointSampling'
import FilteringPoints from './FilteringPoints'

import PointLabelisation from '../../PointLabelisation'
import sendFakePoint from '../../../actions/sendFakePoint'

class InitialSampling extends Component{

    constructor(props){
        
        super(props)       

        this.state = {
            showLabeling: true,
            showFilterBasedSampling: false
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
                                    onClick={() => this.setState({
                                        showLabeling: true,
                                        showFakePointSampling: false,
                                        showFilterBasedSampling: false
                                    })}
                                    >
                                        Labeling initial sampling
                                    </a>
                                </li>

                                <li className="nav-item">
                                    <a 
                                    className="nav-link active" 
                                    href="#"
                                    onClick={() => this.setState({
                                        showLabeling: false,
                                        showFakePointSampling: false,
                                        showFilterBasedSampling: true
                                    })}
                                    >
                                        Filter base sampling
                                    </a>
                                </li>    

                                <li className="nav-item">
                                    <a 
                                    className="nav-link active" 
                                    href="#"
                                    onClick={() => this.setState({
                                        showLabeling: false,
                                        showFakePointSampling: false
                                    })}
                                    >
                                        Fake point initial sampling
                                    </a>
                                </li>                     
                            </ul>
                        

                        <p className="card">                            
                            The first phase of labeling continues until we obtain 
                            a positive example and a negative example. 
                        </p>

                        {
                            //this.state.showLabeling && 
                            false &&
                            
                            <SpecificPointToLabel 
                                onNewPointsToLabel={this.props.onNewPointsToLabel}                        
                            />
                        }
                    
                    </div>
                    </div>
                        

                    {
                        this.state.showLabeling && 

                        <div>
                                            
                            <PointLabelisation
                                {...this.props}   
                                {...this.state}
                            />
                        </div>
                    }

                 
                    {
                        this.state.showFilterBasedSampling && 
                        <div className="row">
                            <div className="col col-lg-8 offset-lg-2">
                                <FilteringPoints
                                    chosenVariables={this.buildChosenVariableForFiltering()}
                                />
                            </div>
                        </div>

                    }

                    {
                        this.state.showFakePointSampling && 

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

    buildChosenVariableForFiltering(){
        
        var chosenVariables = this.props.chosenColumns

        chosenVariables = this.props.chosenColumns.map(e => {
            const dataset = this.props.dataset

            if (e.type == "numerical"){
                
                //compute min and max
                var min = dataset.min(e.name)
                var max = dataset.max(e.name)
                return Object.assign(e, {min:min, max:max})

            }
            else{
                var uniqueValues = Object.entries(dataset.uniqueValues(e.name)).map (e => e[0])
                return Object.assign(e, {values: uniqueValues})
            }

        })

        return chosenVariables
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