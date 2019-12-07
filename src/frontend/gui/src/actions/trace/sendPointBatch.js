import {backend, webplatformApi, defaultConfiguration} from '../../constants/constants'

import $ from "jquery";

function sendPointBatch(labeledPoints, onSuccess){

    var url = backend + "/get-next-traces"
    $.ajax({

        type: "POST",
        dataType: 'JSON',
        url: url,
        data: {
            "labeledPoints": JSON.stringify(labeledPoints),            
        },
        success: onSuccess
    })
}

export default sendPointBatch