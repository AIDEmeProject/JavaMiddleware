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
    def __init__(self, learners, connection):
        super().__init__()

        assert_in_list(connection.upper(), ['L1', 'L2', 'PROD', 'ENTROPY'])

        self.learners = learners
        self.connectionFunctionId = connection

    def set_repeat(self, repeat):
        if isinstance(self.learners, Learner):
            self.repeat = repeat

    def set_categorical(self, categorical):
        if categorical:
            self.categorical = categorical

    def is_factorized(self):
        return True
