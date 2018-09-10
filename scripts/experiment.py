#!/usr/bin/python
"""
SCRIPT FOR RUNNING EXPERIMENTS

Operations:
  1) Translate a ActiveLearner specification to JSON configuration string
  2) Create directory for storing experimental data, together with a config.json file
"""

import os

from printable import Printable
from active_learners import ActiveLearner
from validation import *


class Experiment(Printable):
    def __init__(self, task, active_learner, subsample=float('inf')):
        super().__init__(add_name=False)

        assert_positive('subsample', subsample)
        assert_is_instance(active_learner, ActiveLearner)

        self.task = task
        self.activeLearner = active_learner
        if subsample < float('inf'):
            self.subsampleSize = subsample


def create_config_file(base_dir, printable):
    folder = os.path.join(base_dir, str(printable))

    if not os.path.exists(folder):
        os.makedirs(folder)

    # create config file if needed
    config_file = os.path.join(folder, 'config.json')
    if not os.path.exists(config_file):
        with open(config_file, 'w+') as f:
            f.write(printable.to_json())

    return folder
