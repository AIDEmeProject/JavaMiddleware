/*
 * Copyright (c) 2019 École Polytechnique
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, you can obtain one at http://mozilla.org/MPL/2.0
 *
 * Authors:
 *       Luciano Di Palma <luciano.di-palma@polytechnique.edu>
 *       Enhui Huang <enhui.huang@polytechnique.edu>
 *       Laurent Cetinsoy <laurent.cetinsoy@gmail.com>
 *
 * Description:
 * AIDEme is a large-scale interactive data exploration system that is cast in a principled active learning (AL) framework: in this context,
 * we consider the data content as a large set of records in a data source, and the user is interested in some of them but not all.
 * In the data exploration process, the system allows the user to label a record as “interesting” or “not interesting” in each iteration,
 * so that it can construct an increasingly-more-accurate model of the user interest. Active learning techniques are employed to select
 * a new record from the unlabeled data source in each iteration for the user to label next in order to improve the model accuracy.
 * Upon convergence, the model is run through the entire data source to retrieve all relevant records.
 */

import React, { Component } from "react";

class LearnerOptions extends Component {
  render() {
    return (
      <div className="form-group">
        <label htmlFor="algorithm-selection">Learner</label>
        <select
          className="form-control"
          id="algorithm-selection"
          name="active-learner"
          onChange={this.onLearnerChange.bind(this)}
        >
          {this.props.learners.map((learner, idx) => (
            <option
              key={`learner-${idx}`}
              value={learner.value}
              selected={learner.value === this.props.selected}
            >
              {learner.label}
            </option>
          ))}
        </select>
      </div>
    );
  }

  onLearnerChange(e) {
    this.props.learnerChanged(e.target.value);
  }
}

export default LearnerOptions;
