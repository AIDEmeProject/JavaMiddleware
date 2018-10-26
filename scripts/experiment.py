#!/usr/bin/python
"""
SCRIPT FOR RUNNING EXPERIMENTS

Operations:
  1) Translate a ActiveLearner specification to JSON configuration string
  2) Create directory for storing experimental data, together with a config.json file
"""

from .active_learners import ActiveLearner
from .printable import Printable
from .validation import *
from .tsm import MultipleTSM


class Experiment(Printable):
    def __init__(self, task, active_learner, subsample, mTSM):
        super().__init__(add_name=False)

        assert_positive('subsample', subsample)
        assert_is_instance(active_learner, ActiveLearner)

        self.task = task
        self.activeLearner = active_learner
        if subsample < float('inf'):
            self.subsampleSize = subsample
        if mTSM is not None:
            assert_is_instance(mTSM, MultipleTSM)
            self.multiTSM = mTSM
