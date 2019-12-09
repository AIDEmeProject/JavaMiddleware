
import {backend, webplatformApi, defaultConfiguration} from '../constants/constants'

import $ from "jquery";


function sendFakePoint(dataPoint, onSuccess){

    var url = backend + "/fake-point-initial-sampling"
    
    $.ajax({
        type: "POST",
        dataType: 'JSON',
        url: url,
        data: {
            fakePoint: JSON.stringify(dataPoint)
        },
       
        success: onSuccess
    })
}

export default sendFakePoint