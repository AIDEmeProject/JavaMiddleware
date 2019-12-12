import React, { Component } from 'react';

import $ from "jquery";
import * as  d3 from "d3"



class LabelInfos extends Component{
    
    constructor(props){
        super(props)
    }

    render(){

        
        const iteration = this.props.iteration
        const labeledPoints = this.props.labeledPoints.filter((e, i) => { return i <= iteration})

        var nNegativeSamples = labeledPoints.filter(e => e.label === 0).length
        var nPositiveSamples = labeledPoints.filter(e => e.label === 1).length
        var nLabeledPoints = labeledPoints.length

        if (this.props.live){
            nLabeledPoints += 2
            nNegativeSamples += 1
            nPositiveSamples += 1
        }
        
        
        return (

            <div id="iteration-labels">
                <div>
                    Labeled sample {
                        nLabeledPoints
                    }
                </div>

                <div>
                    Positive labels {
                        nPositiveSamples
                    }
                </div>

                <div>
                    Negative labels {
                        nNegativeSamples
                    }
                </div>
            </div>
        )
    }
}

LabelInfos.defaultProps = {
    live:true
}



export default LabelInfos