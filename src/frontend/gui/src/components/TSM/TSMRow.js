import React, { Component } from 'react';

import ExplorationActions from '../ExplorationActions'
import TSMModelVisualization from './TSMModelVisualization'
import SpecificPointToLabel from '../InitialSampling/SpecificPointToLabel'

import $ from 'jquery'
import {backend, webplatformApi} from '../../constants/constants'


class TSMRow extends Component{

    constructor(props){
        
        super(props)       
    }

    render(){
        return (
                                
            <tr 
                key={i}
                className="variable-group">

                <td>
                    {point.id}
                </td>
                {
                    this.props.finalGroups.map((g, iGroup) => {
                        
                        var values = g.map( variable => {
                            
                            return point.data[variable.i]

                        }).join(", ")
                                                                                                         
                        if ( typeof point.labels !== "undefined"){
                            var LabelButton = () => {
                                return <button
                                            data-point={i}
                                            data-subgroup={iGroup}
                                            className="btn btn-primary btn-raised"
                                            onClick = {this.onSubGroupNo.bind(this)}
                                        >
                                            No
                                        </button>
                            }
                        }
                        else{
                            var LabelButton = () => {return <span></span>}
                        }                                
                        
                        return (
                            <td 
                                colSpan={g.length}
                                key={iGroup}
                            >
                                {values} <LabelButton />
                            </td>
                        )
                    })
                }
                <td>
                
                    <button 
                        style={{display: typeof point.labels === "undefined" ? "inherit": "none"}}
                        className="btn btn-primary btn-raised"
                        data-point={i}
                        onClick={this.groupWasLabeledAsYes.bind(this)}
                    >
                        Yes
                    </button>
                                        
                    <button 
                        style={{display: typeof point.labels === "undefined" ? "inherit": "none"}}
                        className="btn btn-primary btn-raised"
                        data-point={i}
                        onClick={this.groupWasLabeledAsNo.bind(this)}
                    >
                        No
                    </button>

                    <button
                        className="btn btn-primary btn-raised"
                        style={{display: typeof point.labels === "undefined" ? "none": "inherit"}}
                        data-point={i}
                        onClick={this.groupSubLabelisationFinished.bind(this)}
                    >
                        Validate Subgroup labels
                    </button>
                </td>
            </tr>
           
        )
    }

  
}


export default TSMRow