import subprocess

from scripts import *

#############################
# EXPERIMENT CONFIGURATIONS
#############################
# task id, as defined in the tasks.ini file
TASKS = [
    #"sdss_Q1_0.1%", #"sdss_Q1_1%", "sdss_Q1_10%",  # rowc, colc
    #"sdss_Q2_circle_0.1%", #"sdss_Q2_circle_1%", "sdss_Q2_circle_10%",  # rowc, colc
    #"sdss_Q3_0.1%", "sdss_Q3_1%", "sdss_Q3_10%",  # ra, dec
    #"sdss_Q4_0.1%", "sdss_Q4_1%", "sdss_Q4_10%",  # rowv, colv
    #"sdss_Q2_circle_10%_Q3_rect_1%", # "sdss_Q2_circle_1%_Q3_rect_1%",  # 4D
    #"sdss_Q2_circle_10%_Q3_rect_10%_Q4_1%",  # 6D
    #'sdss_log_7.8%',
    #'sdss_log_squared',
    #"sdss_overlapping_5.5%",
    #"sdss_overlapping_1.5%",
    #"sdss_overlapping_0.5%",
    #"sdss_overlapping_0.1%",
    #"sdss_overlapping_5.5%_tsm", "sdss_overlapping_1.5%_tsm", "sdss_overlapping_0.5%_tsm",
]

TASKS.extend(
    ['user_study_' + s for s in [
        #'01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11','12', '13', '14', '15', '16', '17', '18'
        '01',  #'03', '06', '07'
        #'06', '07'
    ]]
)

# size of unlabeled sample. Use float('inf') if no sub-sampling is to be performed
SUBSAMPLE_SIZE = float('inf')


# Run modes to perform. There are four kinds: NEW, RESUME, EVAL, and AVERAGE
MODES = [
    'NEW',  # run new exploration
    #'RESUME',    # resume a previous exploration
    'EVAL',      # run evaluation procedure over finished runs
    #'AVERAGE'    # average all evaluation file for a given metric
]


# Number of new explorations to run. Necessary for the NEW mode only
NUM_RUNS = 1


# Maximum number of new points to be labeled by the user. Necessary for NEW and RESUME modes
BUDGET = 50


# Runs to perform evaluation. Necessary for RESUME and EVAL modes
RUNS = []
if len(RUNS) == 0 and ('RESUME' in MODES or 'EVAL' in MODES):
    RUNS = [x + 1 for x in range(NUM_RUNS)]


# Probability of sampling from the uncertain set instead of the entire unlabeled set.
SAMPLE_UNKNOWN_REGION_PROBABILITY = 0.5


# Multiple TSM configuration. Set as None if you do now want it to be used.
mTSM = None
#mTSM = MultipleTSM(SAMPLE_UNKNOWN_REGION_PROBABILITY)


# INITIAL SAMPLING
# Default behavior (= None): try to read initial samples from tasks.ini; if none are found, use StratifiedSampling(1, 1)
# You can override the default behavior below by choosing the method yourself
#INITIAL_SAMPLING = None
INITIAL_SAMPLING = StratifiedSampler(pos=1, neg=1, negative_in_all_subspaces=True)
#INITIAL_SAMPLING = FixedSampler(posId=401695194, negIds=[200736144, 200736143, 200738016, 200736146, 200736148, 200736149, 200738013, 200736147, 401707487, 401598585])


# Whether to use categorical variables information (if the algorithm supports)
USE_CATEGORICAL = True


# Active Learning algorithm to run. Necessary for NEW and RESUME modes.
# Check the scripts/active_learners.py file for all possibilities
#ACTIVE_LEARNER = SimpleMargin(C=1024, kernel="gaussian", gamma=0)
#ACTIVE_LEARNER = RandomSampler()
#ACTIVE_LEARNER = SubspatialSampler(
#     [
#         #SimpleMargin(C=1024, kernel="gaussian", gamma=0),
#         #SimpleMargin(C=1024, kernel="gaussian", gamma=0),
#         #SimpleMargin(C=1024, kernel="gaussian", gamma=0),
#         # UncertaintySampler(MajorityVote(
#         #     num_samples=8,
#         #     warmup=100, thin=10, chain_length=500, selector="single", rounding=True, cache=True,  # hit-and-run
#         #     kernel='gaussian', gamma=0.5,  # kernel
#         #     add_intercept=True, solver="gurobi"  # extra
#         # )),
#         # UncertaintySampler(MajorityVote(
#         #     num_samples=8,
#         #     warmup=100, thin=10, chain_length=500, selector="single", rounding=True, cache=True,  # hit-and-run
#         #     kernel='gaussian', gamma=0.05,  # kernel
#         #     add_intercept=True, solver="gurobi"  # extra
#         # )),
#         # UncertaintySampler(MajorityVote(
#         #     num_samples=8,
#         #     warmup=100, thin=10, chain_length=500, selector="single", rounding=True, cache=True,  # hit-and-run
#         #     kernel='gaussian', gamma=0.001,  # kernel
#         #     add_intercept=True, solver="gurobi"  # extra
#         # )),
#     ]
#)

mv = MajorityVote(
    num_samples=8,
    warmup=100, thin=10, chain_length=500, selector="single", rounding=True, cache=True,  # hit-and-run
    kernel='gaussian', gamma=0, diagonal=(0.5, 0.5, 0.005, 0.005),  # kernel
    add_intercept=True, solver="gurobi"  # extra
)

mv = SVM(C=1e7, kernel='gaussian', gamma=0)

ACTIVE_LEARNER = SubspatialSampler(mv, loss="MARGIN")
#ACTIVE_LEARNER = UncertaintySampler(mv)

# Evaluation metrics. Necessary for EVAL and AVERAGE modes.
# Check the scripts/metrics.py file for all possibilities
METRICS = [
    ConfusionMatrix(SubspatialLearner(mv)),
    #SubspatialConfusionMatrix(SubspatialLearner(mv, use_categorical=False))
    #ConfusionMatrix(SVM(C=1024, kernel='gaussian', gamma=0)),
    #LabeledSetConfusionMatrix(SVM(C=1e7, kernel='gaussian')),
    #ThreeSetMetric(),
    #ConfusionMatrix(mv),
    # ConfusionMatrix(SubspatialLearner(
    #     [
    #         MajorityVote(
    #             num_samples=8,
    #             warmup=100, thin=10, chain_length=500, selector="single", rounding=True, cache=True,  # hit-and-run
    #             kernel='gaussian', gamma=0.5,  # kernel
    #             add_intercept=True, solver="gurobi"  # extra
    #         ),
    #         MajorityVote(
    #             num_samples=8,
    #             warmup=100, thin=10, chain_length=500, selector="single", rounding=True, cache=True,  # hit-and-run
    #             kernel='gaussian', gamma=0.05,  # kernel
    #             add_intercept=True, solver="gurobi"  # extra
    #         ),
    #         MajorityVote(
    #             num_samples=8,
    #             warmup=100, thin=10, chain_length=500, selector="single", rounding=True, cache=True,  # hit-and-run
    #             kernel='gaussian', gamma=0.001,  # kernel
    #             add_intercept=True, solver="gurobi"  # extra
    #         ),
    #     ]
    # )),
]

#############################
# DO NOT CHANGE
#############################

CATEGORIES = {
    'user_study_01': [2, 3, 4],
    'user_study_02': [6, 7],
    'user_study_03': [4, 5],
    'user_study_04': [5],
    'user_study_05': [2, 3, 4, 5],
    'user_study_06': [2, 3],
    'user_study_07': [3, 4, 5],
    'user_study_08': [5, 6, 7],
    'user_study_09': [3],
    'user_study_10': [4, 5, 6, 7],
    'user_study_11': [3, 4, 5],
    'user_study_13': [7, 8, 9],
    'user_study_14': [3, 4],
    'user_study_15': [4, 5],
    'user_study_16': [3],
    'user_study_17': [4, 5],
    'user_study_18': [4, 5],
}

NUM_PARTITIONS = {
    'sdss_Q2_circle_1%_Q3_rect_1%': 2,
    'sdss_Q2_circle_10%_Q3_rect_1%': 2,
    'sdss_Q2_circle_10%_Q3_rect_10%_Q4_1%': 3,
    'sdss_log_squared': 2,
    'sdss_log_7.8%': 2,
    'sdss_overlapping_5.5%': 6,
    'sdss_overlapping_1.5%': 4,
    'sdss_overlapping_0.5%': 5,
    'sdss_overlapping_0.1%': 5,
    'sdss_overlapping_5.5%_tsm': 4,
    'sdss_overlapping_1.5%_tsm': 1,
    'sdss_overlapping_0.5%_tsm': 1,
    'user_study_01': 5,
    'user_study_02': 8,
    'user_study_03': 6,
    'user_study_04': 6,
    'user_study_05': 6,
    'user_study_06': 4,
    'user_study_07': 6,
    'user_study_08': 8,
    'user_study_09': 4,
    'user_study_10': 8,
    'user_study_11': 6,
    'user_study_12': 4,
    'user_study_13': 10,
    'user_study_14': 5,
    'user_study_15': 6,
    'user_study_16': 4,
    'user_study_17': 6,
    'user_study_18': 6,
}

# PRINTS
print("TASKS =", TASKS)
print("SUBSAMPLE_SIZE =", SUBSAMPLE_SIZE)
print("MODES =", MODES)
if 'NEW' in MODES:
    print("NUM_RUNS =", NUM_RUNS)
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
    cat_indexes = CATEGORIES.get(TASK, None) if USE_CATEGORICAL else None
    cat_suffix = "" if TASK not in CATEGORIES else "_cat=true" if USE_CATEGORICAL else "_cat=false"
    experiment_dir = os.path.join('experiment', TASK, folder + cat_suffix, str(ACTIVE_LEARNER))

    if ACTIVE_LEARNER.is_factorized():
        ACTIVE_LEARNER.set_repeat(NUM_PARTITIONS.get(TASK, 1))
        ACTIVE_LEARNER.set_categorical(cat_indexes)

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

            if hasattr(m, 'learner') and isinstance(m.learner, SubspatialLearner):
                m.learner.set_repeat(NUM_PARTITIONS.get(TASK, 1))
                m.learner.set_categorical(cat_indexes)

            if hasattr(m, 'subspatialLearner') and isinstance(m.subspatialLearner, SubspatialLearner):
                m.subspatialLearner.set_repeat(NUM_PARTITIONS.get(TASK, 1))
                m.subspatialLearner.set_categorical(cat_indexes)

            command_line.append(str(m))
            m.dump_to_config_file(experiment_dir, add_name=True)

    try:
        subprocess.run(' '.join(command_line), shell=True)
    except:
        print("Task " + TASK + " failed")
