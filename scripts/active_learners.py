from .learners import *


class ActiveLearner(Printable):
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
