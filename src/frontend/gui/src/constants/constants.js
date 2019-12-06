
var defautConfiguration = {
    "activeLearner": {
        "learner": {
            "name": "MajorityVote", // MajorityVote | SVM |
            "sampleSize": 8, // Only for MajorityVote : >= 1
            "versionSpace": {
                "addIntercept": true,
                "hitAndRunSampler": {
                "cache": true,
                "rounding": true,
                "selector": {
                    "name": "WarmUpAndThin", // Only For Majority Vote
                    "thin": 10,
                    "warmUp": 100
                }
                },
                "kernel": {
                    "name": "gaussian"
                },
                "solver": "ojalgo"
            }
        },
        "name": "UncertaintySampler"
    },
    "subsampleSize": 50000,
    "task": "sdss_Q4_0.1%"
}


var SimpleMarginConfiguration = {

    "activeLearner": {
        "name": "SimpleMargin",
        "svmLearner": {
            "C": 1024,
            "kernel": {
                "gamma": 0,
                "name": "gaussian"
            },
            "name": "SVM"
        }
    },
    "subsampleSize": 50000,    
    "useFactorizationInformation": false
}



module.exports = {
    backend: "http://localhost:7060",
    webplatformApi: "http://localhost:8000/api",
    defaultConfiguration: defautConfiguration
}