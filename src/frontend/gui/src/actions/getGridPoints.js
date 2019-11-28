import $ from "jquery";
import {backend, webplatformApi} from '../constants/constants'


function getGridPoints(onGridPointReception){
    
    var url = backend + "/get-fake-point-grid"

    $.get(url, response => {
        var rawPoints = response


        var points = rawPoints.map(e => {
            return e.data.array
            
        })
        onGridPointReception(points)
    })

}

export default getGridPoints