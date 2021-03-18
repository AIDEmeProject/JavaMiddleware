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

import FilteringPoints from "./FilteringPoints";
import PointLabelisation from "../../PointLabelisation";
import explorationSendLabeledPoint from "../../../actions/explorationSendLabeledPoint";

import robot from "../../../resources/robot.png";

class InitialSampling extends Component {
  constructor(props) {
    super(props);

    this.state = {
      showLabeling: false,
      showFilterBasedSampling: false,

      pointsToLabel: [...this.props.pointsToLabel],
      // randomPointsToLabel: [...this.props.pointsToLabel],
      // filteredPointsToLabel: [],
      labeledPoints: [],
      allLabeledPoints: [],

      hasYes: false,
      hasNo: false,
    };
  }

  render() {
    return (
      <div className="card">
        <div>
          <div className="row">
            <div className="col col-lg-8 offset-lg-2">
              <h4>Initial sampling</h4>

              <p className="card">
                <span className="chatbot-talk">
                  <img src={robot} width="50" alt="robot" />
                  <q>
                    The first phase of labeling continues until we obtain a
                    positive example and a negative example. <br />
                    To get the initial samples, would you like to go through
                    random sampling or attribute filtering?
                  </q>
                </span>
              </p>

              <ul className="nav nav-tabs bg-light">
                <li className="nav-item">
                  <a
                    className={
                      this.state.showLabeling ? "nav-link active" : "nav-link"
                    }
                    href="javascript:void(0)"
                    onClick={() =>
                      this.setState({
                        showLabeling: true,
                        showFilterBasedSampling: false,
                      })
                    }
                  >
                    Random sampling
                  </a>
                </li>

                <li className="nav-item">
                  <a
                    className={
                      this.state.showFilterBasedSampling
                        ? "nav-link active"
                        : "nav-link"
                    }
                    href="javascript:void(0)"
                    onClick={() =>
                      this.setState({
                        showLabeling: false,
                        showFilterBasedSampling: true,
                      })
                    }
                  >
                    Faceted search
                  </a>
                </li>
              </ul>
            </div>
          </div>

          {this.state.showLabeling && (
            <div>
              <PointLabelisation
                // pointsToLabel={this.props.pointsToLabel}
                pointsToLabel={this.state.pointsToLabel}
                // pointsToLabel={this.state.randomPointsToLabel}
                chosenColumns={this.props.chosenColumns}
                dataset={this.props.dataset}
                // onPositiveLabel={this.props.onPositiveLabel}
                // onNegativeLabel={this.props.onNegativeLabel}
                onPositiveLabel={this.onPositiveLabel.bind(this)}
                onNegativeLabel={this.onNegativeLabel.bind(this)}
                // onPositiveLabel={this.onLabelRandomPointPositive}
                // onNegativeLabel={this.onLabelRandomPointNegative}
              />
            </div>
          )}

          {this.state.showFilterBasedSampling && (
            <div className="row">
              <div className="col col-lg-8 offset-lg-2">
                <FilteringPoints
                  chosenVariables={this.props.chosenColumns}
                  dataset={this.props.dataset}
                  // onPositiveLabel={this.props.onPositiveLabel}
                  // onNegativeLabel={this.props.onNegativeLabel}
                  onPositiveLabel={this.onPositiveLabel.bind(this)}
                  onNegativeLabel={this.onNegativeLabel.bind(this)}
                  // onPositiveLabel={this.onLabelFilteredPointPositive}
                  // onNegativeLabel={this.onLabelFilteredPointPositive}
                />
              </div>
            </div>
          )}
        </div>
      </div>
    );
  }

  onPositiveLabel(e) {
    var dataIndex = parseInt(e.target.dataset.key);
    this.dataWasLabeled(dataIndex, 1);
  }

  onNegativeLabel(e) {
    var dataIndex = parseInt(e.target.dataset.key);
    this.dataWasLabeled(dataIndex, 0);
  }

  dataWasLabeled(dataIndex, label) {
    const newLabeledPoint = { ...this.state.pointsToLabel[dataIndex], label };
    const newLabeledPoints = [...this.state.labeledPoints, newLabeledPoint];

    var newPointsToLabel = [...this.state.pointsToLabel];
    newPointsToLabel.splice(dataIndex, 1);

    const isYes = label === 1;

    this.setState(
      {
        allLabeledPoints: [...this.state.allLabeledPoints, newLabeledPoint],
        labeledPoints: newLabeledPoints,
        pointsToLabel: newPointsToLabel,
        hasYes: this.state.hasYes || isYes,
        hasNo: this.state.hasNo || !isYes,
      },
      () => {
        this.labelForInitialSession(newLabeledPoints, newPointsToLabel);
      }
    );
  }

  labelForInitialSession(labeledPoints, pointsToLabel) {
    var tokens = this.props.tokens;

    if (this.state.hasYes && this.state.hasNo) {
      explorationSendLabeledPoint(
        {
          labeledPoints,
        },
        tokens,
        (response) => {
          this.props.hasPositiveAndNegativeLabels(
            this.state.allLabeledPoints,
            this.parseReceivedPoints(response)
          );
        }
      );

      return;
    }

    if (pointsToLabel.length === 0) {
      explorationSendLabeledPoint(
        {
          labeledPoints,
        },
        tokens,
        (response) => {
          this.setState({
            labeledPoints: [],
            pointsToLabel: [
              ...this.state.pointsToLabel,
              ...this.parseReceivedPoints(response),
            ],
          });
        }
      );
    }
  }

  parseReceivedPoints(points) {
    return points.map((id) => ({ id }));
  }

  // onLabelRandomPointPositive() {}

  // onLabelRandomPointNegative() {}

  // onLabelRandomPoint(pointId, label) {}
}

export default InitialSampling;
