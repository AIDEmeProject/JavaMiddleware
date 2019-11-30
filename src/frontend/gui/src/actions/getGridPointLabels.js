import $ from 'jquery'

getGridPointLabels(dataWasReceived){

    var url = backend + "/get-label-over-grid-point"

    $.get(url, dataWasReceived)    
}

export default getGridPointLabels