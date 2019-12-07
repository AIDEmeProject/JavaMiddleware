var factorizedVersionSpaceConfiguration ={ 
    "activeLearner": {
        "learners": {
            "name": "MajorityVote",
            "sampleSize": 8,
            "versionSpace": {
                "addIntercept": true, 
                "decompose": true,
                "hitAndRunSampler": {
                    "cache": true,
                    "rounding": true, 
                    "roundingCache": true, 
                    "selector": {
                        "name": "WarmUpAndThin",
                        "thin": 64,
                        "warmUp": 256 
                    } 
                },
                "jitter": 1e-09, 
                "kernel": {
                    "gamma": 0, 
                    "name": "gaussian" 
                },
                "solver": "gurobi" 
            } 
        }, 
        "lossFunctionId": "GREEDY", 
        "name": "SubspatialSampler", 
        "repeat": 5 
    },
    "subsampleSize": 50000, 
    "task": "sdss_overlapping_0.1%", 
    "useFactorizationInformation": true
} 

var versionSpaceConfiguration = {
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
        "name": "UncertaintySampler" //Version space = uncertainty sampler
    },
    "subsampleSize": 50000,
    "task": "sdss_Q4_0.1%"
}


var simpleMarginConfiguration = {

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

var algorithmNames = {
    'simplemargin': 'Simple Margin (SVM)',
    'simplemargintsm': 'Simple Margin (SVM) + TSM',
    'versionspace': 'Version Space',
    'factorizedversionspace': 'Factorized Version Space'
}

module.exports = {
    backend: "http://localhost:7060",
    webplatformApi: "http://localhost:8000/api",
    defaultConfiguration: versionSpaceConfiguration,

    versionSpaceConfiguration: versionSpaceConfiguration,
    
    simpleMarginConfiguration: simpleMarginConfiguration,
    
    factorizedVersionSpaceConfiguration: factorizedVersionSpaceConfiguration,

    algorithmNames: algorithmNames
}
