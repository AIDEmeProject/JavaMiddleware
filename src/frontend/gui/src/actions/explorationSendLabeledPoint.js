import $ from "jquery";
import {backend, webplatformApi} from '../constants/constants'


function explorationSendLabeledPoint(data, tokens, onSuccess){
    console.log(data)
    var labeledPoints = data.data.map(e => {
        return {
            id: e.id,
            label: e.label,
            data: {
                array: e.data
            }
        }
    })
    
    var endPoint = backend + "/data-point-were-labeled"

    $.ajax({
        type: "POST",
        dataType: 'JSON',
        url: endPoint,
        data: {
            labeledPoints: JSON.stringify(labeledPoints)
        },
        
        success: onSuccess
    })
    
    var updateLabelData = webplatformApi + "/session/" + tokens.sessionToken + "/new-label"
    
    $.ajax({
        type: "PUT", 
        dataType: "JSON",
        url: updateLabelData,
        headers: {
            Authorization: "Token " + tokens.authorizationToken
        },
        data: {
            number_of_labeled_points: data.data.length
        }
    })
}

export default explorationSendLabeledPoint