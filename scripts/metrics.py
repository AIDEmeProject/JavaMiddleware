from .learners import *


class Metric(Printable):
    def __init__(self):
        super().__init__(add_name=True)

    def __repr__(self):

        return "{0}_{1}".format(self.name, super().__repr__()).strip('_')


class ConfusionMatrix(Metric):
    def __init__(self, learner):
        super().__init__()

        assert_is_instance(learner, Learner)

        self.learner = learner


class ThreeSetMetric(Metric):
    pass
