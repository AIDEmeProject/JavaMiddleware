
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

module.exports = {
    backend: "http://localhost:7060",
    defaultConfiguration: defautConfiguration
}