import {backend, webplatformApi, defaultConfiguration} from '../../constants/constants'

import $ from "jquery";

function initializeBackend(traceOptions, onSuccess){

    var url = backend + "/start-trace"

    var configuration = traceOptions.configuration

    configuration.columns = traceOptions.columnNames

    $.ajax({

        type: "POST",
        dataType: 'JSON',
        url: url,
        data: {
            'algorithm': traceOptions.algorithm,
            "configuration": JSON.stringify(traceOptions.configuration),
            'columnIds': JSON.stringify(traceOptions.columnIds),                            
            'encodedDatasetName': traceOptions.encodedDatasetName
        },
        success: onSuccess
    })
}


export default initializeBackend

