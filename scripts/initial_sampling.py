from .printable import Printable
from .validation import *


class InitialSampler(Printable):
    pass


class StratifiedSampler(InitialSampler):
    def __init__(self, pos=1, neg=1, negative_in_all_subspaces=False):
        super().__init__()
        assert_positive('pos', pos)
        assert_positive('neg', neg)

        self.positiveSamples = pos
        self.negativeSamples = neg
        self.negativeInAllSubspaces = negative_in_all_subspaces

    def __eq__(self, other):
        return self.positiveSamples == other.positiveSamples and \
               self.negativeSamples == other.negativeSamples and \
               self.negativeInAllSubspaces == other.negativeInAllSubspaces


class FixedSampler(InitialSampler):
    def __init__(self, posId, negIds):
        super().__init__()

        self.positiveId = posId
        self.negativeIds = negIds
