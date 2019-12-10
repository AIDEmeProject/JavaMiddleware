import {backend, webplatformApi, defaultConfiguration} from '../constants/constants'

import $ from "jquery";

function sendFilters(filters, onSuccess){

    var url = backend + "/get-points-by-filtering"

    $.ajax({
        type: "POST",
        dataType: 'JSON',
        url: url,
        data: {
            filters: JSON.stringify(filters)
        },
        
        success: onSuccess
    })
}

export default sendFilters

