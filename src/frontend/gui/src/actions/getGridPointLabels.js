import $ from 'jquery'
import {backend, webplatformApi} from '../constants/constants'

function getGridPointLabels(dataWasReceived){

    var url = backend + "/get-label-over-grid-point"

    $.get(url, dataWasReceived)    
}

export default getGridPointLabels