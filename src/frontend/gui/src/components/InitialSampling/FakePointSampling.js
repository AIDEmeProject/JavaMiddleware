import React, { Component } from 'react';

import $ from 'jquery'
import {backend, webplatformApi} from '../../constants/constants'

class FakePointSampling extends Component{

    constructor(props){
        
        super(props)

        this.state = {
            
            fakePoint: this.props.pointToLabel
        }        
    }
    
    render(){
        console.log(this.props)
        
        return (
            <div>
               
                <h4>
                    Fake point initial sampling
                </h4>
                <p>
                    We have created a fake point. It was created by taking the median value of t
                    the numerical columns and the the most common category.

                    Please change the attribute of the point until you reach a point which
                    would belong to the positive class
                </p>

                <table>
                    <thead>
                        <tr>
                            {
                                this.props.availableVariables.map((v, i) => {
                                    return (
                                        <th key={i}>
                                            { v.name }
                                        </th>
                                    )
                                })
                            }
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            {
                                this.state.fakePoint.map((col, i) => {
                                
                                return (
                                    <td
                                        key={i}
                                    >
                                        
                                        <button
                                            data-col={i}
                                            className="btn"
                                            onClick={this.increaseValue.bind(this)}                            
                                        >
                                            <i 
                                                className="material-icons"
                                                data-col={i}
                                            >
                                                keyboard_arrow_up
                                            </i> 
                                        </button>                                        

                                        <span>
                                            { col }
                                        </span>

                                        <button 
                                            
                                            className="btn"
                                            onClick={this.decreaseValue.bind(this)}     
                                            data-col={i}                                   
                                        >
                                            <i 
                                                className="material-icons"         
                                                data-col={i}                                       
                                            >
                                                keyboard_arrow_down</i>          
                                        </button>
                                    </td>
                                )
                            })
                        }

                        <td>

                            <button 
                                onClick={this.onValidatePoint.bind(this)}
                                className="btn"
                            >
                                Validate
                            </button>
                        </td>
                    </tr>        
                </tbody>
                </table>                                
            </div>
        )
    }

    increaseValue(e){

        var i = e.target.dataset.col
        console.log(i)
        
        var pointToLabel = this.state.fakePoint.map(e => e)
        
        var colType = this.props.availableVariables[i].type

        if (colType == "numerical"){
            pointToLabel[i] += pointToLabel[i] * 0.1
        }
        else{
            pointToLabel[i] += 1
        }

        this.setState({
            fakePoint: pointToLabel
        })
    }

    decreaseValue(e){
        
        var i = e.target.dataset.col
        
        console.log(i)
      
        var pointToLabel = this.state.fakePoint.map(e => e)
        
        var colType = this.props.availableVariables[i].type

        if (colType == "numerical"){
            pointToLabel[i] -= pointToLabel[i] * 0.1
        }
        else{
            pointToLabel[i] = Math.max(0, pointToLabel[i] - 1)
        }

        this.setState({
            fakePoint: pointToLabel
        })
    }

    onValidatePoint(){
        sendPoint(this.state.pointToLabel, this.props.fakePointWasValidated)
    }
}

function sendPoint(point, onSuccess){
    var url = backend + "/fake-point-initial-sampling"

    var data = {
        fakePoint: point
    }
    $.post(url, data, onSuccess)

}

FakePointSampling.defaultProps = {
    availableVariables: ['age', 'sex'],
    variableTypes: ["numerical", 'categorical'],
    pointToLabel: [22, 1],
        
}

export default FakePointSampling