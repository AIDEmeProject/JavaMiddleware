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


class Experiment(Printable):
    def __init__(self, task, active_learner, subsample=float('inf'), sample_from_unknown_region_probability=1.0):
        super().__init__(add_name=False)

        assert_positive('subsample', subsample)
        assert_is_instance(active_learner, ActiveLearner)
        assert_in_range("UNCERTAIN_SET_SAMPLE_PROBABILITY", sample_from_unknown_region_probability, 0, 1)

        self.task = task
        self.activeLearner = active_learner
        self.searchUncertainRegionProbability = sample_from_unknown_region_probability
        if subsample < float('inf'):
            self.subsampleSize = subsample
