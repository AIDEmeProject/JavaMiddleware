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
        
        const negativeSamples = labeledPoints.filter(e => e.label === 0)
        const positiveSamples = labeledPoints.filter(e => e.label === 1)
        
        return (

            <div id="iteration-labels">
                <div>
                    Labeled sample {
                        labeledPoints.length + 2
                    }
                </div>

                <div>
                    Positive labels {
                        positiveSamples.length + 1
                    }
                </div>

                <div>
                    Negative labels {
                        negativeSamples.length + 1
                    }
                </div>
            </div>
        )
    }
}



export default LabelInfos