import {backend, webplatformApi, defaultConfiguration} from '../../constants/constants'

import $ from "jquery";

function initializeBackend(traceOptions, onSuccess){

    var url = backend + "/start-trace"



    $.ajax({

        type: "POST",
        dataType: 'JSON',
        url: url,
        data: {
            "configuration": JSON.stringify(traceOptions.configuration),
            'dataLoading': JSON.stringify({
                'columnIds': traceOptions.columnIds
            }),
            'encodedDatasetName': traceOptions.encodedDatasetName
        },
        success: onSuccess
    })
}


export default initializeBackend

