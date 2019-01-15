import subprocess

from scripts import *

#############################
# EXPERIMENT CONFIGURATIONS
#############################
# task id, as defined in the tasks.ini file
TASKS = [
    # "sdss_Q1_0.1%", "sdss_Q1_1%", "sdss_Q1_10%",  # rowc, colc
    # "sdss_Q2_circle_0.1%", "sdss_Q2_circle_1%", "sdss_Q2_circle_10%",  # rowc, colc
    # "sdss_Q3_0.1%", "sdss_Q3_1%", "sdss_Q3_10%",  # ra, dec
    # "sdss_Q4_0.1%", "sdss_Q4_1%", "sdss_Q4_10%",  # rowv, colv
    # "sdss_Q2_circle_10%_Q3_rect_1%", # "sdss_Q2_circle_1%_Q3_rect_1%",  # 4D
    "sdss_Q2_circle_10%_Q3_rect_10%_Q4_1%",  # 6D
]

TASKS.extend(
    ['user_study_' + s for s in [
        #'01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18'
        #'10'
    ]]
)

# size of unlabeled sample. Use float('inf') if no sub-sampling is to be performed
SUBSAMPLE_SIZE = float('inf')

# Run modes to perform. There are four kinds: NEW, RESUME, EVAL, and AVERAGE
MODES = [
    #'NEW',  # run new exploration
    # 'RESUME',    # resume a previous exploration
    'EVAL',      # run evaluation procedure over finished runs
    # 'AVERAGE'    # average all evaluation file for a given metric
]

# Number of new explorations to run. Necessary for the NEW mode only
NUM_RUNS = 1

# Maximum number of new points to be labeled by the user. Necessary for NEW and RESUME modes
BUDGET = 50

# Runs to perform evaluation. Necessary for RESUME and EVAL modes
# RUNS = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
RUNS = [1]

# Evaluation metrics. Necessary for EVAL and AVERAGE modes.
# Check the scripts/metrics.py file for all possibilities
mv = MajorityVote(
    num_samples=8,
    warmup=100, thin=10, chain_length=500, selector="single", rounding=True, cache=True,  # hit-and-run
    kernel='gaussian', gamma=0.0001, diagonal=(0.5, 0.5, 0.005, 0.005),  # kernel
    add_intercept=True, solver="gurobi"  # extra
)

# Where should partition information be stored? How to specify it?
# TODO 3: add a Confusion Matrix over subspaces

METRICS = [
    #ConfusionMatrix(SVM(C=1e7, kernel='gaussian', gamma=0)),
    # LabeledSetConfusionMatrix(SVM(C=1e7, kernel='gaussian')),
    # ThreeSetMetric(),
    #ConfusionMatrix(mv),
    ConfusionMatrix(SubspatialLearner(
        [
            #SVM(C=1024, kernel='gaussian', gamma=0),
            #SVM(C=1024, kernel='gaussian', gamma=0),
            #SVM(C=1024, kernel='gaussian', gamma=0),
            MajorityVote(
                num_samples=8,
                warmup=100, thin=10, chain_length=500, selector="single", rounding=True, cache=True,  # hit-and-run
                kernel='gaussian', gamma=0.5,  # kernel
                add_intercept=True, solver="gurobi"  # extra
            ),
            MajorityVote(
                num_samples=8,
                warmup=100, thin=10, chain_length=500, selector="single", rounding=True, cache=True,  # hit-and-run
                kernel='gaussian', gamma=0.05,  # kernel
                add_intercept=True, solver="gurobi"  # extra
            ),
            MajorityVote(
                num_samples=8,
                warmup=100, thin=10, chain_length=500, selector="single", rounding=True, cache=True,  # hit-and-run
                kernel='gaussian', gamma=0.001,  # kernel
                add_intercept=True, solver="gurobi"  # extra
            ),
        ]
    ))
]

# you can override the default feature_groups, is_convex_region, and is_categorical configs by specifying them here
# note that must override all the three configurations at once
FEATURE_GROUPS = [
    # ['rowc', 'colc'], ['ra', 'dec']
]

IS_CONVEX_POSITIVE = [
    # True, True
]

IS_CATEGORICAL = [
    # False, False
]

# Probability of sampling from the uncertain set instead of the entire unlabeled set.
SAMPLE_UNKNOWN_REGION_PROBABILITY = 0.5

# Multiple TSM configuration. Set as None if you do now want it to be used.
mTSM = None
# mTSM = MultipleTSM(FEATURE_GROUPS, IS_CONVEX_POSITIVE, IS_CATEGORICAL, SAMPLE_UNKNOWN_REGION_PROBABILITY)

# INITIAL SAMPLING
# Default behavior (= None): try to read initial samples from tasks.ini; if none are found, use StratifiedSampling(1, 1)
# You can override the default behavior below by choosing the method yourself
INITIAL_SAMPLING = None
# INITIAL_SAMPLING = StratifiedSampler(pos=5, neg=1)
# INITIAL_SAMPLING = FixedSampler(posId=401695194, negIds=[200736144, 200736143, 200738016, 200736146, 200736148, 200736149, 200738013, 200736147, 401707487, 401598585])

# Active Learning algorithm to run. Necessary for NEW and RESUME modes.
# Check the scripts/active_learners.py file for all possibilities
#ACTIVE_LEARNER = SimpleMargin(C=1e7, kernel="gaussian", gamma=0)
#ACTIVE_LEARNER = RandomSampler()
#ACTIVE_LEARNER = UncertaintySampler(mv)
ACTIVE_LEARNER = SubspatialSampler(
    [
        #SimpleMargin(C=1024, kernel="gaussian", gamma=0),
        #SimpleMargin(C=1024, kernel="gaussian", gamma=0),
        #SimpleMargin(C=1024, kernel="gaussian", gamma=0),
        UncertaintySampler(MajorityVote(
            num_samples=8,
            warmup=100, thin=10, chain_length=500, selector="single", rounding=True, cache=True,  # hit-and-run
            kernel='gaussian', gamma=0.5,  # kernel
            add_intercept=True, solver="gurobi"  # extra
        )),
        UncertaintySampler(MajorityVote(
            num_samples=8,
            warmup=100, thin=10, chain_length=500, selector="single", rounding=True, cache=True,  # hit-and-run
            kernel='gaussian', gamma=0.05,  # kernel
            add_intercept=True, solver="gurobi"  # extra
        )),
        UncertaintySampler(MajorityVote(
            num_samples=8,
            warmup=100, thin=10, chain_length=500, selector="single", rounding=True, cache=True,  # hit-and-run
            kernel='gaussian', gamma=0.001,  # kernel
            add_intercept=True, solver="gurobi"  # extra
        )),
    ]
)

#############################
# DO NOT CHANGE
#############################

# PRINTS
print("TASKS =", TASKS)
print("SUBSAMPLE_SIZE =", SUBSAMPLE_SIZE)
print("MODES =", MODES)
if 'NEW' in MODES:
    print("NUM_RUNS =", NUM_RUNS)
if 'NEW' in MODES or 'RESUME' in MODES:
    if isinstance(ACTIVE_LEARNER, (SubspatialSampler,)):
        FACTORIZED = True

    print("BUDGET =", BUDGET)
    print('ACTIVE LEARNER =', ACTIVE_LEARNER)
if 'RESUME' in MODES or 'EVAL' in MODES:
    print("RUNS =", RUNS)
if 'EVAL' in MODES or 'AVERAGE' in MODES:
    print('METRICS =', METRICS)
if mTSM:
    print("TSM =", mTSM)
print("INITIAL_SAMPLER =", INITIAL_SAMPLING)

# PARAMETER VALIDATION
for mode in MODES:
    assert_in_list(mode, ['NEW', 'RESUME', 'EVAL', 'AVERAGE'])
assert_positive("NUM_RUNS", NUM_RUNS)
assert_positive("BUDGET", BUDGET)
assert_positive("SUBSAMPLE_SIZE", SUBSAMPLE_SIZE)

folder_elems = [ACTIVE_LEARNER.name]
if SUBSAMPLE_SIZE < float('inf'):
    folder_elems.append('ss=%d' % SUBSAMPLE_SIZE)
if mTSM:
    folder_elems.append(str(mTSM))
if INITIAL_SAMPLING is not None:
    folder_elems.append(str(INITIAL_SAMPLING))
folder = '_'.join(folder_elems)

if 'EVAL' in MODES and not ACTIVE_LEARNER.is_factorized():
    for m in METRICS:
        if hasattr(m, 'learner') and isinstance(m.learner, SubspatialLearner):
            raise RuntimeError("Metric {0} uses a factorized classifier, but {1} is a non-factorized Active Learner.".format(m, ACTIVE_LEARNER))

# BUILD EXPERIMENT
for TASK in TASKS:
    print(TASK)
    experiment_dir = os.path.join('experiment', TASK, folder, str(ACTIVE_LEARNER))

    if len(experiment_dir) > 100:
        experiment_dir = experiment_dir[:100]

    experiment = Experiment(task=TASK, subsample=SUBSAMPLE_SIZE, active_learner=ACTIVE_LEARNER, mTSM=mTSM,
                            initial_sampler=INITIAL_SAMPLING)
    experiment.dump_to_config_file(os.path.join(experiment_dir, 'Runs'))

    # BUILD COMMAND LINE ARGUMENTS
    command_line = [
        "java -cp target/data_exploration-1.0-SNAPSHOT-jar-with-dependencies.jar RunExperiment",
        "--experiment_dir", "\"" + experiment_dir + "\"",
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
            command_line.append(str(m)[:100])
            m.dump_to_config_file(experiment_dir, add_name=True)

    try:
        subprocess.run(' '.join(command_line), shell=True)
    except:
        print("Task " + TASK + " failed")
