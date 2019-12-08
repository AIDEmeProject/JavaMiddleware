

function buildTSMConfiguration(baseConfiguration, groupsWithIds, usedColumnNames, datasetMetadata){
    
    var tsmJson = {
        hasTsm: true,                
        searchUnknownRegionProbability: 0.5,                
        columns: usedColumnNames,
        decompose: true
    }
        
    var groups = buildGroupsFromJson(groupsWithIds, datasetMetadata.columnNames)
    //var flags =  groups.map(g => {return [true, false]})        
    var flags = buildFlagsFromColumnTypes(groupsWithIds,  datasetMetadata.types)
    
    Object.assign(tsmJson, {
        flags: flags,
        featureGroups: groups,                   
    })

    baseConfiguration["multiTSM"] = tsmJson

    return baseConfiguration
}

function isGroupCategorical(group, columnTypes){
    var result = true
    group.forEach( variableId => {
                
        var isColCategorical = columnTypes[variableId]
        
        if ( ! isColCategorical){
            
            result = false
        }
    })

    return result
}

function buildFlagsFromColumnTypes(groups, columnTypes){
    
    return groups.map(group => {
        const isCategorical = isGroupCategorical(group, columnTypes)
        return [true, isCategorical]
    })
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