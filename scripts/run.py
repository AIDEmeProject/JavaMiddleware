import subprocess

from experiment import *
from active_learners import *
from metrics import *


# TASK
MODE = 'NEW'  # NEW, RESUME, EVAL
NUM_RUNS = 1
RUNS = range(10)
BUDGET = 20
METRICS = [
    ConfusionMatrix(SVM(C=1e4, kernel='gaussian')),
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

base_dir = os.path.join('../experiment', TASK, ACTIVE_LEARNER.name)
experiment_dir = create_config_file(base_dir, ACTIVE_LEARNER)


experiment = Experiment(task=TASK, subsample=SUBSAMPLE_SIZE, active_learner=ACTIVE_LEARNER)
command_line = [
    "java RunExperiment ../META-INF/MANIFEST.MF ../out/artifacts/data_exploration_jar/data_exploration.jar",
    "--experiment_dir", str(experiment),
    "--mode", MODE,
    "--numRuns", str(NUM_RUNS),
    "--budget", str(BUDGET),
]

if RUNS:
    command_line.append("--runs")
    command_line.append(' '.join(map(str, RUNS)))

if METRICS:
    command_line.append("--metrics")
    command_line.append(' '.join(map(str, METRICS)))

subprocess.run(command_line, shell=True)
