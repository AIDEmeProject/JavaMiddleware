/*
 * Copyright (c) 2019 École Polytechnique
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, you can obtain one at http://mozilla.org/MPL/2.0
 *
 * Authors:
 *       Luciano Di Palma <luciano.di-palma@polytechnique.edu>
 *       Enhui Huang <enhui.huang@polytechnique.edu>
 *       Laurent Cetinsoy <laurent.cetinsoy@gmail.com>
 *
 * Description:
 * AIDEme is a large-scale interactive data exploration system that is cast in a principled active learning (AL) framework: in this context,
 * we consider the data content as a large set of records in a data source, and the user is interested in some of them but not all.
 * In the data exploration process, the system allows the user to label a record as “interesting” or “not interesting” in each iteration,
 * so that it can construct an increasingly-more-accurate model of the user interest. Active learning techniques are employed to select
 * a new record from the unlabeled data source in each iteration for the user to label next in order to improve the model accuracy.
 * Upon convergence, the model is run through the entire data source to retrieve all relevant records.
 */

import React, { Component } from 'react';


class TSMLabel extends Component{
    render(){
        return (
            <div>
                <p>
                    We ran the current model of several example of the dataset so that you can check
                    if the model     
                </p>


                <table className="group-variable">
                    <thead>
                        <tr>

                            <td>Row id</td>
                            {
                                this.props.groups.map((g, i) => {
                                    
                                    return (
                                        <th 
                                            key ={i}
                                            colSpan={g.length}
                                        >
                                            {g.map(e => e.name).join(", ")}
                                        </th>
                                    )
                                })
                            }
                            <th>
                                Label    
                            </th>                
                        </tr>
                    </thead>

                    <tbody>                
                    {
                    this.props.labeledPoints.map((point, i) => {
                        
                        return (

                            <tr 
                                key={i}
                                className="variable-group">

                                <td>
                                    {point.id}
                                </td>
                                {
                                    this.props.groups.map((g, iGroup) => {
                                        
                                        var values = g.map( variable => {
                                            
                                            return point.data[variable.i]

                                        }).join(", ")
                                                                                                                         
                                        if ( typeof point.labels !== "undefined"){
                                            var L = () => {
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
                                            var L = () => {return <span></span>}
                                        }                                
                                        
                                        return (
                                            <td 
                                                colSpan={g.length}
                                                key={iGroup}
                                            >
                                                {values} <L />
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
                    })
                }
                </tbody>

                </table>
            </div>
        )
    }
}

class TSMModelVisualization extends Component{

    render(){
     
        if ( ! this.props.TSMBound){
            return (
                <div>
                    Please label more example to have an estimation of the model performance
                </div>
            )
        }
        return (
            <div>
                
                <p>                    
                    Estimated model performance >= { Math.round(this.props.TSMBound * 100) } %
                </p>

            </div>
        )
    }
}

export default TSMModelVisualization