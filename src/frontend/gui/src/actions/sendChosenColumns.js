import {backend, webplatformApi, defaultConfiguration} from '../constants/constants'

import $ from "jquery";

function sendVariableGroups(tokens, chosenVariables, groups, onSuccess){

    var endPoint = backend + "/choose-options"    
    var configuration = defaultConfiguration
    configuration['useFakePoint'] = false

    var tsmJson = {
        hasTSM: true,                
        searchUnknownRegionProbability: 0.5,                
        columns: chosenVariables.map( e => e.name)
    }
     
    if (chosenVariables == 2){
            
        var flags = chosenVariables.map(g => { return [true, false]}) 
        var groups = chosenVariables.map( v => [v.name] ) //array because each variable is a group
        
    }
    else{
        
        var flags =  groups.map(g => {return [true, false]})        
        var groups = groups.map( g => { return g.map(v => v.name)})
        
    }
   
    Object.assign(tsmJson, {
        flags: flags,
        featureGroups: groups,                   
    })

    configuration["multiTSM"] = tsmJson


    $('#conf').val(JSON.stringify(configuration))
      
    var payload = $('#choose-columns').serialize()

    $.ajax({

        type: "POST",
        url: endPoint,
        data: payload,        
        success: (response) =>  {onSuccess(response)},
        
    });    

}


function sendColumns(tokens, state, onSuccess){
    
    var endPoint = backend + "/choose-options"    
    var configuration = defaultConfiguration
    configuration['useFakePoint'] = state.useFakePoint || false
                      
    var hasTSM = state.chosenColumns.length == 2 || !! state.variableGroups[0].length > 0
                  
    $('#conf').val(JSON.stringify(configuration))
      
    var payload = $('#choose-columns').serialize()

    $.ajax({

        type: "POST",
        url: endPoint,
        data: payload,        
        success: (response) =>  {onSuccess(response)},
        
    });    
              
}

function sendDataToWebPlateform(availableVariables, options, hasTSM, featureGroups,  sessionOptionsUrl, tokens){


    var sessionOptionsUrl = webplatformApi + "/session/" + tokens.sessionToken + "/options"

    var numberOfNumerical = availableVariables.reduce((e, acc) => {
        return acc + 1 * e.type == "numerical"
    }, 0)

    var numberOfCategorical = availableVariables.reduce((e, acc) => {
        return acc + 1 * e.type == "categorical"
    }, 0)

    
    var statisticData = {
        column_number: availableVariables.length,
        numberOfCategorical: numberOfCategorical,
        numberOfNumerical: numberOfNumerical,
        has_tsm: hasTSM,
        learner: options.learner,
        classifier: options.classifier,
       
    }
    
    if (hasTSM){
        if  (availableVariables.length == 2){
            var nGroups = 2
        }
        else{
            var nGroups = featureGroups.length
        }     
        statisticData['number_of_variable_groups'] = nGroups
    }
    

    $.ajax({
        type: "PUT",
        url: sessionOptionsUrl,
        data: statisticData,
        headers: {
            Authorization: "Token " + tokens.authorizationToken
        }
    })
}


export default {
    sendColumns: sendColumns,
    sendVariableGroups: sendVariableGroups    
}
