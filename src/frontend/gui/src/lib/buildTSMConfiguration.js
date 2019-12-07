


function buildTSMConfiguration(baseConfiguration, groupsWithIds, usedColumnNames, allColumns){
    
    var tsmJson = {
        hasTSM: true,                
        searchUnknownRegionProbability: 0.5,                
        columns: usedColumnNames,
        decompose: true
    }

    var groups = buildGroupsFromJson(groupsWithIds, allColumns)
    var flags =  groups.map(g => {return [true, false]})        
    
    Object.assign(tsmJson, {
        flags: flags,
        featureGroups: groups,                   
    })

    baseConfiguration["multiTSM"] = tsmJson

    return baseConfiguration
}


function buildGroupsFromJson(factorizationGroupByIds, encodedColumnNames){
        
    var groupWithNames = factorizationGroupByIds.map(spec => {
        return spec.map(id => {
    
            return encodedColumnNames[id]
        })
    })
    
    return groupWithNames
}





export default buildTSMConfiguration