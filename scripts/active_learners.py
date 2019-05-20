from .learners import *


class ActiveLearner(Printable):
    def is_factorized(self):
        return False

    def set_repeat(self, repeat):
        pass

    def set_categorical(self, categorical):
        pass


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

    def is_factorized(self):
        return isinstance(self.learner, SubspatialLearner)

    def set_repeat(self, repeat):
        if self.is_factorized():
            self.learner.set_repeat(repeat)

    def set_categorical(self, categorical):
        if categorical and self.is_factorized():
            self.learner.set_categorical(categorical)


class SubspatialSampler(ActiveLearner):
    def __init__(self, learners, loss):
        super().__init__()

        assert_in_list(loss.upper(), ['L1', 'L2', 'PROD', 'ENTROPY', 'GREEDY'])

        self.learners = learners
        self.lossFunctionId = loss

    def set_repeat(self, repeat):
        if isinstance(self.learners, Learner):
            self.repeat = repeat

    def set_categorical(self, categorical):
        if categorical is not None:
            self.categorical = categorical

    def is_factorized(self):
        return True


class QueryByDisagreement(ActiveLearner):

    def __init__(self, learner, sample_size, samples_weight):
        super().__init__()

        assert_is_instance(learner, Learner)
        assert_positive('sample_size', sample_size)
        assert_positive('sample_weight', samples_weight)

        self.learner = learner
        self.backgroundSampleSize = sample_size
        self.backgroundSamplesWeight = samples_weight
