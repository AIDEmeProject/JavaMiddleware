import $ from "jquery";
import {backend, webplatformApi} from '../constants/constants'


function getDecisionBoundaryData(dataWasReceived){

    var url = backend + "/get-decision-boundaries"

    $.get(url, dataWasReceived)
}
export default getDecisionBoundaryData