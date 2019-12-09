import $ from 'jquery'
import {backend, webplatformApi} from '../constants/constants'

function getTSMPredictionsOverGridPoints(dataWasReceived){

    var url = backend + "/get-tsm-predictions-over-grid-point"

    $.get(url, rawLabels =>{

        var predictedLabels = rawLabels.map(e => {
            return {
                'id': e.dataPoint.id,
                'label': e.label.label
            }
        })

        dataWasReceived(predictedLabels)
    })    
}

export default getTSMPredictionsOverGridPoints