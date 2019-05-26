import React, { Component } from 'react';

import $ from "jquery";
import {backend} from '../../constants/constants'

import init from '../../lib/sample_heatmap'

class HeatMap extends Component{

    render(){
        return (
            <div >
                <div id="scatterplot" style={{minWidth: 500, minHeight: 500}}>
                    
                </div>

                <div id="heatchart" style={{minWidth: 500, minHeight: 500}}>
                    
                </div>                        
            </div>
        )
    }

    componentDidMount(){
        init()
    }


}



export default HeatMap