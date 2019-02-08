from .learners import *


class ActiveLearner(Printable):
    def is_factorized(self):
        return False


class SimpleMargin(ActiveLearner):
    def __init__(self, C=1.0, kernel='gaussian', gamma=0):
        super().__init__()
        self.svmLearner = SVM(C=C, kernel=kernel, gamma=gamma)


class RandomSampler(ActiveLearner):
    pass


class UncertaintySampler(ActiveLearner):
    def __init__(self, learner):
        super().__init__()

        assert_is_instance(learner, Learner)

        self.learner = learner


class SubspatialSampler(ActiveLearner):
    def __init__(self, active_learners):
        super().__init__()

        self.activeLearners = active_learners

    def set_repeat(self, repeat):
        if isinstance(self.activeLearners, ActiveLearner):
            self.repeat = repeat

    def is_factorized(self):
        return True


class QueryByDisagreement(ActiveLearner):

    def __init__(self, learner, sample_size):
        super().__init__()

        assert_is_instance(learner, Learner)
        assert_positive('sample_size', sample_size)

        self.learner = learner
        self.backgroundSampleSize = sample_size
