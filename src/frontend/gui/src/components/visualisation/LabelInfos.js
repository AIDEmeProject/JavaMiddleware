import React, { Component } from 'react';

import $ from "jquery";
import * as  d3 from "d3"



class LabelInfos extends Component{
    
    constructor(props){
        super(props)
    }

    render(){

        //const iteration = this.props.iteration
        const labeledPoints = this.props.labeledPoints
        
        const negativeSamples = labeledPoints.filter(e => e.label === 0)
        const positiveSamples = labeledPoints.filter(e => e.label === 1)

        return (

            <div id="iteration-labels">
                <div>
                    Labeled sample {
                        labeledPoints.length
                    }
                </div>

                <div>
                    Positive labels {
                        positiveSamples.length
                    }
                </div>

                <div>
                    Negative labels {
                        negativeSamples.length
                    }
                </div>
            </div>
        )
    }
}


export default LabelInfos