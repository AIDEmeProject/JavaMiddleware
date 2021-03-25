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

import ModelBehaviorControls from "../visualisation/ModelBehaviorControls";

import PointLabelisation from "../PointLabelisation";
import ModelBehavior from "../visualisation/ModelBehavior";
import LabelInfos from "../visualisation/LabelInfos";

import DataPoints from "../DataPoints";

import wholeDatasetLabelizationWasAsked from "../../actions/statisticCollection/wholeDatasetLabelizationWasAsked";
import explorationSendLabeledPoint from "../../actions/explorationSendLabeledPoint";
import getWholedatasetLabeled from "../../actions/getWholeLabeledDataset";

import getModelPredictionsOverGridPoints from "../../actions/getModelPredictionsOverGridPoints";

class Exploration extends Component {
  constructor(props) {
    super(props);
    this.state = {
      showLabelView: true,
      showLabelHistory: false,
      showModelBehavior: false,

      labeledPoints: [],
      pointsToLabel: [...this.props.pointsToLabel],
      allLabeledPoints: [...this.props.allLabeledPoints],

      fakePointGrid: [],
      modelPredictionHistory: [],
      iteration: 0,
      nIteration: 0,
    };
  }

  render() {
    return (
      <div>
        <ul className="nav nav-tabs bg-primary">
          <li className="nav-item">
            <a
              className={
                this.state.showLabelView ? "nav-link active" : "nav-link"
              }
              href="javascript:void(0)"
              onClick={() =>
                this.setState({
                  showLabelView: true,
                  showLabelHistory: false,
                  showModelBehavior: false,
                })
              }
            >
              Labeling
            </a>
          </li>

          <li className="nav-item">
            <a
              className={
                this.state.showLabelHistory ? "nav-link active" : "nav-link"
              }
              href="javascript:void(0)"
              onClick={() =>
                this.setState({
                  showLabelView: false,
                  showLabelHistory: true,
                  showModelBehavior: false,
                })
              }
            >
              History
            </a>
          </li>

          <li className="nav-item">
            <a
              className={
                this.state.showModelBehavior ? "nav-link active" : "nav-link"
              }
              href="javascript:void(0)"
              onClick={this.onModelBehaviorClick.bind(this)}
            >
              Model Behavior
            </a>
          </li>

          <li className="nav-item">
            <a
              className="nav-link"
              onClick={this.onLabelWholeDatasetClick.bind(this)}
            >
              Auto-labeling
            </a>
          </li>
        </ul>

        {this.state.showLabelView && (
          <div className="row">
            <div className="col col-lg-12">
              <PointLabelisation
                chosenColumns={this.props.chosenColumns}
                pointsToLabel={this.state.pointsToLabel}
                onPositiveLabel={this.onPositiveLabel.bind(this)}
                onNegativeLabel={this.onNegativeLabel.bind(this)}
                dataset={this.props.dataset}
              />
            </div>
          </div>
        )}

        {this.state.showModelBehavior && (
          <div className="row">
            <div className="col col-lg-4">
              <ModelBehaviorControls
                iteration={this.state.iteration}
                nIteration={this.state.nIteration}
                onPreviousIteration={this.onPreviousIteration.bind(this)}
                onNextIteration={this.onNextIteration.bind(this)}
              />

              <LabelInfos
                iteration={this.state.iteration}
                labeledPoints={this.state.allLabeledPoints}
              />
            </div>

            <div className="col col-lg-8">
              <ModelBehavior
                iteration={this.state.iteration}
                labeledPoints={this.state.allLabeledPoints}
                availableVariables={this.props.chosenColumns}
                fakePointGrid={this.state.fakePointGrid}
                modelPredictionHistory={this.state.modelPredictionHistory}
                hasTSM={false}
                plotProjection={false}
              />
            </div>
          </div>
        )}

        {this.state.showLabelHistory && (
          <div className="row">
            <div className="col col-lg-8 offset-lg-2">
              <DataPoints
                availableVariables={this.props.finalVariables}
                points={this.state.allLabeledPoints.flat()}
                chosenColumns={this.props.chosenColumns}
                show={true}
                normal={true}
                dataset={this.props.dataset}
              />
            </div>
          </div>
        )}
      </div>
    );
  }

  onPreviousIteration() {
    this.setState({
      iteration: Math.max(this.state.iteration - 1, 0),
    });
  }

  onNextIteration() {
    this.setState({
      iteration: Math.min(this.state.iteration + 1, this.state.nIteration - 1),
    });
  }

  onModelBehaviorClick(e) {
    const hasBehaviorData = this.state.modelPredictionHistory.length > 0;

    if (hasBehaviorData) {
      this.setState({
        showLabelView: false,
        showLabelHistory: false,
        showModelBehavior: true,
      });
    } else {
      alert(
        "Please label at least one more point or wait for computation to finish."
      );
    }
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

    this.setState({
      allLabeledPoints: [...this.state.allLabeledPoints, [newLabeledPoint]],
      labeledPoints: newLabeledPoints,
      pointsToLabel: newPointsToLabel,
    });

    explorationSendLabeledPoint(
      {
        labeledPoints: newLabeledPoints,
      },
      this.props.tokens,
      (response) => {
        this.setState({
          labeledPoints: [],
          pointsToLabel: [
            ...this.state.pointsToLabel,
            ...this.parseReceivedPoints(response),
          ],
        });

        this.getModelBehaviorData();
      }
    );
  }

  parseReceivedPoints(points) {
    return points.map((id) => ({ id }));
  }

  getModelBehaviorData() {
    if (this.state.nIteration === 0) this.getFakePointGrid();

    getModelPredictionsOverGridPoints((predictedLabels) => {
      this.setState({
        nIteration: this.state.nIteration + 1,
        modelPredictionHistory: [
          ...this.state.modelPredictionHistory,
          predictedLabels,
        ],
      });
    });
  }

  getFakePointGrid() {
    const chosenColumnNames = this.props.chosenColumns.map((e) => e["name"]);
    var grid = this.props.dataset.get_parsed_columns_by_names(
      chosenColumnNames
    );
    this.setState({
      fakePointGrid: grid,
    });
  }

  onLabelWholeDatasetClick(e) {
    e.preventDefault();

    getWholedatasetLabeled();

    wholeDatasetLabelizationWasAsked(this.props.tokens);
  }
}

Exploration.defaultProps = {
  useRealData: true,
};

export default Exploration;
