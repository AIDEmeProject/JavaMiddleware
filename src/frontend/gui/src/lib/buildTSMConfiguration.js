function buildTSMConfiguration(baseConfiguration, groups, chosenVariables){
    
    var tsmJson = {
        hasTSM: true,                
        searchUnknownRegionProbability: 0.5,                
        columns: chosenVariables.map( e => e.name),
        decompose: true
    }

    var flags =  groups.map(g => {return [true, false]})        
    var groups = groups.map( g => { return g.map(v => v.name)})


    Object.assign(tsmJson, {
        flags: flags,
        featureGroups: groups,                   
    })

    baseConfiguration["multiTSM"] = tsmJson

    return baseConfiguration
}

export default buildTSMConfiguration