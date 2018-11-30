from .printable import Printable
from .validation import *


class InitialSampler(Printable):
    pass


class StratifiedSampler(InitialSampler):
    def __init__(self, pos=1, neg=1):
        super().__init__()
        assert_positive('pos', pos)
        assert_positive('neg', neg)

        self.positiveSamples = pos
        self.negativeSamples = neg


class FixedSampler(InitialSampler):
    def __init__(self, posId, negIds):
        super().__init__()

        self.positiveId = posId
        self.negativeIds = negIds
