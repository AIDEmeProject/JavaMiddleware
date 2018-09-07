#!/usr/bin/python
"""
SCRIPT FOR RUNNING EXPERIMENTS

Operations:
  1) Translate a ActiveLearner specification to JSON configuration string
  2) Create directory for storing experimental data, together with a config.json file
"""

import os
import json

# SUPPORTED VALUES
SUPPORTED_LINPROG_SOLVERS = ['apache', 'ojalgo']
SUPPORTED_LEARNERS = ['SVM', 'KNN', 'MajorityVote']
SUPPORTED_ACTIVE_LEARNERS = ['RandomSampler', 'UncertaintySampler', 'ActiveTreeSearch', 'SimpleMargin']
SUPPORTED_KERNELS = ['linear', 'gaussian']
SUPPORTED_SAMPLE_SELECTORS = ['single', 'independent']


# ASSERTION FUNCTIONS
def assert_positive(name, value, allow_zero=False):
    if value < 0 or (value == 0 and not allow_zero):
        raise ValueError("{0} value must be {1} positive, found {2}".format(name.capitalize(), "" if allow_zero else "strictly", value))


def assert_in_range(name, value, lower, upper):
    assert lower < upper
    if value < lower or value > upper:
        raise ValueError("{0} value must be in [{1}, {2}], found {3}".format(name, lower, upper, value))


def assert_in_list(value, list):
    if value not in list:
        raise ValueError("Value {0} not between supported values: {1}".format(value, list))


class Printable:
    @property
    def name(self):
        return self.__class__.__name__

    @name.setter
    def name(self, x):
        self.__class__.__name__ = x

    @property
    def add_name(self):
        return True

    def __repr__(self):
        return ' '.join(['{0}={1}'.format(k, v) for k, v in self.__flatten_dict(self.as_dict()).items()])

    @staticmethod
    def __flatten_dict(d):
        flattened = {}
        for k, v in d.items():
            if isinstance(v, dict):
                if 'name' in v:
                    flattened[k] = v.pop('name')
                flattened.update(Printable.__flatten_dict(v))
            elif k != 'name':
                flattened[k] = v
        return flattened

    def as_dict(self):
        result = {}
        # if self.add_name:
        #     result['name'] = self.__class__.__name__
        result.update({k: self.__resolve_value(v) for k, v in self.__dict__.items()})
        if self.add_name and 'name' not in result:
            result['name'] = self.name
        return result

    @staticmethod
    def __resolve_value(value):
        if isinstance(value, Printable):
            return value.as_dict()
        return value

    def to_json(self):
        return json.dumps(self.as_dict(), sort_keys=True, indent=4, separators=(',', ': '), allow_nan=False)


# CLASSIFIERS
class Kernel(Printable):
    def __init__(self, name='gaussian', gamma=0):
        assert_in_list(name, SUPPORTED_KERNELS)
        assert_positive('gamma', gamma, allow_zero=True)

        self.name = name
        if self.name != 'linear':
            self.gamma = gamma

    def __repr__(self):
        return 'linear' if self.name == 'linear' else 'gaussian gamma=' + str(self.gamma)


class SVM(Printable):
    def __init__(self, C=1.0, kernel='gaussian', gamma=0):
        assert_positive('C', C)

        self.C = C
        self.kernel = Kernel(kernel, gamma)


class KNN(Printable):
    def __init__(self, k=5, gamma=0.):
        assert_positive('k', k)
        assert_in_range('gamma', gamma, lower=0, upper=1)

        self.k = k
        self.gamma = gamma


class SampleSelector(Printable):
    def __init__(self, name="single", warmup=100, thin=10, chain_length=64):
        assert_positive('warmup', warmup, allow_zero=True)
        assert_positive('thin', thin)
        assert_positive('chain_length', chain_length)
        assert_in_list(name, SUPPORTED_SAMPLE_SELECTORS)

        self.name = 'WarmUpAndThin' if name == 'single' else 'IndependentChains'
        if self.name == 'WarmUpAndThin':
            self.warmUp = warmup
            self.thin = thin
        else:
            self.chainLength = chain_length


class HitAndRun(Printable):
    def __init__(self, selector, rounding=True, cache=True):
        self.cache = cache
        self.rounding = rounding
        self.selector = selector

    @property
    def add_name(self):
        return False


class VersionSpace(Printable):
    def __init__(self, hit_and_run,
                 kernel='linear', gamma=0,
                 add_intercept=True, solver="ojalgo"):
        assert_in_list(solver, SUPPORTED_LINPROG_SOLVERS)

        self.addIntercept = add_intercept
        self.solver = solver
        self.kernel = Kernel(kernel, gamma)
        self.hitAndRunSampler = hit_and_run

    @property
    def add_name(self):
        return False


class MajorityVote(Printable):
    def __init__(self, num_samples=8,
                 warmup=100, thin=10, chain_length=64, selector="single", rounding=True, cache=True,
                 kernel='linear', gamma=0,
                 add_intercept=True, solver="ojalgo"):
        assert_positive('num_samples', num_samples)

        self.sampleSize = num_samples
        self.versionSpace = VersionSpace(
            hit_and_run=HitAndRun(
                selector=SampleSelector(name=selector, warmup=warmup, thin=thin, chain_length=chain_length),
                rounding=rounding, cache=cache
            ),
            kernel=kernel, gamma=gamma,
            add_intercept=add_intercept, solver=solver
        )


# ACTIVE LEARNERS
class SimpleMargin(SVM):
    pass


class RandomSampler(Printable):
    pass


class UncertaintySampler(Printable):
    def __init__(self, learner):
        assert_in_list(learner.name, SUPPORTED_LEARNERS)
        self.learner = learner


class ActiveTreeSearch(Printable):
    def __init__(self, learner, lookahead):
        assert_positive('lookahead', lookahead)
        assert_in_list(learner.name, SUPPORTED_LEARNERS)
        self.lookahead = lookahead
        self.learner = learner


class Experiment(Printable):
    def __init__(self, task, active_learner, subsample=float('inf')):
        assert_positive('subsample', subsample)
        assert_in_list(active_learner.name, SUPPORTED_ACTIVE_LEARNERS)

        self.task = task
        self.activeLearner = active_learner
        if subsample < float('inf'):
            self.subsampleSize = subsample

    @property
    def add_name(self):
        return False


def create_experiment_dir(exp):
    # create output directory if needed
    folder = os.path.join('experiment', exp.task, exp.activeLearner.name, str(exp.activeLearner))

    if not os.path.exists(folder):
        os.makedirs(folder)

    # create config file if needed
    config_file = os.path.join(folder, 'config.json')
    if not os.path.exists(config_file):
        with open(config_file, 'w+') as f:
            f.write(exp.to_json())

    return folder


# TASK
MODE = 'NEW'
RUNS = range(10)
BUDGET = 20
# TODO: how to set evaluation metrics ?

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


experiment = Experiment(task=TASK, subsample=SUBSAMPLE_SIZE, active_learner=ACTIVE_LEARNER)
directory = create_experiment_dir(experiment)

# run middleware on 'directory', with specified 'MODE' and 'BUDGET', over RUNS files

