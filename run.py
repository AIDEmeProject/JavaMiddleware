import subprocess

from scripts import *

# TASK
MODE = 'EVAL'  # NEW, RESUME, EVAL
NUM_RUNS = 1  # NEW mode only
BUDGET = 20  # NEW and RESUME modes
RUNS = [1]  # RESUME and EVAL modes
METRICS = [  # EVAL mode only
    ConfusionMatrix(SVM(C=1e7, kernel='gaussian')),
    TargetSetAccuracy()
]

TASK = "sdss_Q1_0.1%"
SUBSAMPLE_SIZE = 50000

ACTIVE_LEARNER = SimpleMargin(C=1e7, kernel="gaussian", gamma=0)
ACTIVE_LEARNER = RandomSampler()
ACTIVE_LEARNER = ActiveTreeSearch(KNN(k=100, gamma=0.1), lookahead=1)
ACTIVE_LEARNER = UncertaintySampler(MajorityVote(
    num_samples=8,
    warmup=100, thin=10, chain_length=64, selector="single", rounding=True, cache=True,  # hit-and-run
    kernel='gaussian', gamma=0,  # kernel
    add_intercept=True, solver="ojalgo")  # extra
)

experiment_dir = os.path.join('experiment', TASK, ACTIVE_LEARNER.name, str(ACTIVE_LEARNER))

experiment = Experiment(task=TASK, subsample=SUBSAMPLE_SIZE, active_learner=ACTIVE_LEARNER)
experiment.dump_to_config_file(experiment_dir)

command_line = [
    "java -cp target/data_exploration-1.0-SNAPSHOT-jar-with-dependencies.jar RunExperiment",
    "--experiment_dir", experiment_dir,
    "--mode", MODE,
    "--num_runs", str(NUM_RUNS),
    "--budget", str(BUDGET),
]

assert_in_list(MODE, ['NEW', 'RESUME', 'EVAL'])
assert_positive("NUM_RUNS", NUM_RUNS)
assert_positive("BUDGET", BUDGET)
assert_positive("SUBSAMPLE_SIZE", SUBSAMPLE_SIZE)

if RUNS:
    command_line.append("--runs")
    command_line.append(' '.join(map(str, RUNS)))

if METRICS and MODE == 'EVAL':
    command_line.append("--metrics")
    for m in METRICS:
        assert_is_instance(m, Metric)
        command_line.append(str(m))
        m.dump_to_config_file(experiment_dir, add_name=True)

subprocess.run(' '.join(command_line), shell=True)
