import $ from 'jquery'
import {backend, webplatformApi} from '../constants/constants'

function getModelPredictionsOverGridPoints(dataWasReceived, isTSM){

    var url = backend + "/get-model-predictions-over-grid-point"

    $.get(url, rawPoints => {

        const predictions = rawPoints.map(e => {
            return {
                'id': isTSM ? e.id: e.dataPoint.id,
                'label': e.label == 'POSITIVE' ? 1: -1
            }
        })
        dataWasReceived(predictions)
    })    
}

export default getModelPredictionsOverGridPoints