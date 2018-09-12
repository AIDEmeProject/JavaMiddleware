import subprocess

from scripts import *


#############################
# EXPERIMENT CONFIGURATIONS
#############################
# task id, as defined in the tasks.ini file
TASK = "sdss_Q1_0.1%"

# size of random, unlabeled sample . Use float('inf') if no sub-sampling is to be performed
SUBSAMPLE_SIZE = 50000

# Run modes to perform. There are four kinds: NEW, RESUME, EVAL, and AVERAGE
MODES = [
    #'NEW',       # run new exploration
    #'RESUME',    # resume a previous exploration
    'EVAL',      # run evaluation procedure over finished runs
    'AVERAGE'    # average all evaluation file for a given metric
]

# Number of new explorations to run. Necessary for the NEW mode only
NUM_RUNS = 1

# Maximum number of labeled points to be labeled by the user. Necessary for NEW and RESUME modes
BUDGET = 25

# Runs to perform evaluation. Necessary for RESUME and EVAL modes
RUNS = [2]

# Evaluation metrics. Necessary for EVAL and AVERAGE modes.
# Check the scripts/metrics.py file for all possibilities
METRICS = [
    ConfusionMatrix(SVM(C=1e7, kernel='gaussian')),
    TargetSetAccuracy()
]

# Active Learning algorithm to run. Necessary for RUN and RESUME modes.
# Check the scripts/active_learners.py file for all possibilities
# ACTIVE_LEARNER = SimpleMargin(C=1e7, kernel="gaussian", gamma=0)
# ACTIVE_LEARNER = RandomSampler()
# ACTIVE_LEARNER = ActiveTreeSearch(KNN(k=100, gamma=0.1), lookahead=1)
ACTIVE_LEARNER = UncertaintySampler(MajorityVote(
    num_samples=8,
    warmup=100, thin=10, chain_length=64, selector="single", rounding=True, cache=True,  # hit-and-run
    kernel='gaussian', gamma=0,  # kernel
    add_intercept=True, solver="ojalgo")  # extra
)


#############################
# DO NOT CHANGE
#############################

# PARAMETER VALIDATION
for mode in MODES:
    assert_in_list(mode, ['NEW', 'RESUME', 'EVAL', 'AVERAGE'])
assert_positive("NUM_RUNS", NUM_RUNS)
assert_positive("BUDGET", BUDGET)
assert_positive("SUBSAMPLE_SIZE", SUBSAMPLE_SIZE)

# BUILD EXPERIMENT
experiment_dir = os.path.join('experiment', TASK, ACTIVE_LEARNER.name, str(ACTIVE_LEARNER))
experiment = Experiment(task=TASK, subsample=SUBSAMPLE_SIZE, active_learner=ACTIVE_LEARNER)
experiment.dump_to_config_file(os.path.join(experiment_dir, 'Runs'))

# BUILD COMMAND LINE ARGUMENTS
command_line = [
    "java -cp target/data_exploration-1.0-SNAPSHOT-jar-with-dependencies.jar RunExperiment",
    "--experiment_dir", experiment_dir,
    "--mode", ' '.join(MODES),
    "--num_runs", str(NUM_RUNS),
    "--budget", str(BUDGET),
]

if RUNS:
    command_line.append("--runs")
    command_line.append(' '.join(map(str, RUNS)))

if METRICS and ('EVAL' in MODES or 'AVERAGE' in MODES):
    command_line.append("--metrics")
    for m in METRICS:
        assert_is_instance(m, Metric)
        command_line.append(str(m))
        m.dump_to_config_file(experiment_dir, add_name=True)

subprocess.run(' '.join(command_line), shell=True)
