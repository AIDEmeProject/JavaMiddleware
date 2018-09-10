from learners import *


class ActiveLearner(Printable):
    pass


class SimpleMargin(ActiveLearner, SVM):
    pass


class RandomSampler(ActiveLearner):
    pass


class UncertaintySampler(ActiveLearner):
    def __init__(self, learner):
        super().__init__()

        assert_is_instance(learner, Learner)

        self.learner = learner


class ActiveTreeSearch(ActiveLearner):
    def __init__(self, learner, lookahead):
        super().__init__()

        assert_positive('lookahead', lookahead)
        assert_is_instance(learner, Learner)

        self.lookahead = lookahead
        self.learner = learner
