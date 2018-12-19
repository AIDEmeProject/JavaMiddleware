function sendLabeledPoints(labeledPoints){

    labeledPoints.map (point => {
        
        return {

            "dataPoint":{
                    "id": point.id,
                    "data": point.data
                },
                "label": point.labels.map( l => {
                    return l ? "POSITIVE" : "NEGATIVE"
                })
        }
    })
}