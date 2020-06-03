import React, { Component } from 'react';

import $ from 'jquery'
import {backend, webplatformApi} from '../../../constants/constants'

import SpecificPointToLabel from './SpecificPointToLabel'
import FakePointSampling from './FakePointSampling'
import FilteringPoints from './FilteringPoints'

import PointLabelisation from '../../PointLabelisation'
import sendFakePoint from '../../../actions/sendFakePoint'

import robot from '../../../resources/robot.png'

class InitialSampling extends Component{

    constructor(props){
        
        super(props)       

        this.state = {
            showLabeling: false,
            showFilterBasedSampling: false
        }
    }
    
    render(){
        
        return (
            <div className="card">
                <div>
                    <div className="row">
                        <div className="col col-lg-8 offset-lg-2">


                        <p className="card">   

                            <span className="chatbot-talk">
                                <img src={robot} width="70" />
                                <q>
                                    The first phase of labeling continues until we obtain 
                                    a positive example and a negative example. <br />

                                    To get the initial samples, would you like to go through initial sampling or attribute filtering
                                </q>
                            </span>
                        </p>

                      
                            
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
                                        Initial sampling
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
                                        Faceted search
                                    </a>
                                </li>    

                                { 
                                    false && 
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
                                }               
                            </ul>
                        
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
                                    dataset={this.props.dataset}
                                    onPositiveLabel={this.props.onPositiveLabel}
                                    onNegativeLabel={this.props.onNegativeLabel}
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