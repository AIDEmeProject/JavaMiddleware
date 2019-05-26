import React, { Component } from 'react';

import $ from 'jquery'
import {backend, webplatformApi} from '../../constants/constants'

class FakePointSampling extends Component{

    constructor(props){
        
        super(props)

        this.state = {
            
            pointToLabel: this.props.pointToLabel
        }        
    }
    
    render(){
            
        return (
            <div>
               
                <h4>
                    Fake point sampling
                </h4>
                <p>
                    Here is a fake point. Would you class it as the positive class ? If yes click on
                    validate
                    Otherwise change its features until it would belong to the positive class                    
                </p>
                <tr>
                    {
                        this.state.pointToLabel.map((col, i) => {
                            
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
            </div>
        )
    }

    increaseValue(e){
        var i = e.target.dataset.col
        
        var pointToLabel = this.state.pointToLabel.map(e => e)
        pointToLabel[i] += pointToLabel[i] * 0.1

        this.setState({
            pointToLabel: pointToLabel
        })
    }

    decreaseValue(e){
        
        var i = e.target.dataset.col
        
        var pointToLabel = this.state.pointToLabel.map(e => e)
        pointToLabel[i] -= pointToLabel[i] * 0.1

        this.setState({
            pointToLabel: pointToLabel
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
    
    fakePointWasValidated: (response) => {console.log('lal')}
}

export default FakePointSampling