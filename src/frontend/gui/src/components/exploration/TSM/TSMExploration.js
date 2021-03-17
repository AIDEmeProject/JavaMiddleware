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

import $ from "jquery";

import DataPoints from "./DataPoints";
import GroupedPointTableHead from "./GroupedPointTableHead";
import GroupedPointTableBody from "./GroupedPointTableBody";
import ModelBehavior from "../../visualisation/ModelBehavior";
import ModelBehaviorControls from "../../visualisation/ModelBehaviorControls";
import LabelInfos from "../../visualisation/LabelInfos";
import getGridPoints from "../../../actions/getGridPoints";

import getTSMPredictions from "../../../actions/getTSMPredictionsOverGridPoints";
import getModelPredictionsOverGridPoints from "../../../actions/getModelPredictionsOverGridPoints";

import { backend, webplatformApi } from "../../../constants/constants";

import robot from "../../../resources/robot.png";

function isSimpleMargin(configuration) {
  return (
    configuration.activeLearner.name &&
    configuration.activeLearner.name === "SimpleMargin"
  );
}

class TSMExploration extends Component {
  constructor(props) {
    super(props);

    this.state = {
      initialLabelingSession: true,

      showLabelView: true,
      showLabelHistory: false,
      showModelBehavior: false,

      pointsToLabel: this.props.pointsToLabel.map((e) => e),
      labeledPoints: [],
      allLabeledPoints: [],
      hasYes: false,
      hasNo: false,

      fakePointGrid: [],
      TSMPredictionHistory: [],
      modelPredictionHistory: [],
      projectionHistory: [],
      nIteration: 0,
      iteration: 0,
    };
  }

  render() {
    var dataset = this.props.dataset;
    return (
      <div>
        {!this.state.initialLabelingSession && (
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
                Labeleling
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
        )}

        {this.state.showLabelView && (
          <div className="center card">
            <div className="row">
              <div className="col col-lg-8 offset-lg-2">
                {this.state.initialLabelingSession && (
                  <p className="card">
                    <span className="chatbot-talk">
                      <img src={robot} width="50" alt="robot" />
                      <q>
                        The first phase of labeling continues until we obtain a
                        positive example and a negative example.
                      </q>
                    </span>
                  </p>
                )}

                <p className="card">
                  <span className="chatbot-talk">
                    <img src={robot} width="50" alt="robot" />
                    <q>
                      Grouped variable exploration. If you chose no, you will be
                      asked to label each subgroup independently.
                    </q>
                  </span>
                </p>

                <table className="group-variable">
                  <GroupedPointTableHead groups={this.props.groups} />
                  <GroupedPointTableBody
                    pointsToLabel={this.state.pointsToLabel}
                    dataset={dataset}
                    groups={this.props.groups}
                    groupWasLabeledAsYes={this.groupWasLabeledAsYes.bind(this)}
                    groupWasLabeledAsNo={this.groupWasLabeledAsNo.bind(this)}
                    groupSubLabelisationFinished={this.groupSubLabelisationFinished.bind(
                      this
                    )}
                    onSubgroupNo={this.onSubgroupNo.bind(this)}
                  />
                </table>
              </div>
            </div>
          </div>
        )}

        {this.state.showModelBehavior && (
          <div className="row">
            <div className="col col-lg-4">
              <ModelBehaviorControls
                iteration={this.getIteration()}
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
                iteration={this.getIteration()}
                labeledPoints={this.state.allLabeledPoints}
                availableVariables={this.props.chosenColumns}
                fakePointGrid={this.state.fakePointGrid}
                TSMPredictionHistory={this.state.TSMPredictionHistory}
                modelPredictionHistory={this.state.modelPredictionHistory}
                projectionHistory={this.state.projectionHistory}
                hasTSM={this.state.TSMPredictionHistory.length > 0}
                plotProjection={false}
              />
            </div>
          </div>
        )}

        {this.state.showLabelHistory && (
          <DataPoints
            availableVariables={this.props.chosenColumns}
            labeledPoints={this.state.allLabeledPoints}
            chosenColumns={this.props.chosenColumns}
            groups={this.props.groups}
            dataset={this.props.dataset}
          />
        )}
      </div>
    );
  }

  onModelBehaviorClick(e) {
    const hasBehaviorData = this.state.modelPredictionHistory.length > 0;

    if (hasBehaviorData) {
      this.setState({
        showModelVisualisation: false,
        showLabelView: false,
        showHeatmap: false,
        showLabelHistory: false,
        showModelBehavior: true,
      });
    } else {
      alert(
        "Please label at least one more point or wait for computation to finish."
      );
    }
  }

  newPointsToLabel(points) {
    var newPoints = points.map((e) => {
      return {
        id: e.id,
        data: e.data.array,
      };
    });

    var pointsToLabel = this.state.pointsToLabel.map((e) => e);

    this.setState({
      pointsToLabel: pointsToLabel.concat(newPoints),
      labeledPoints: [],
      //initialLabelingSession: ! (this.state.hasYes && this.state.hasNo)
    });
  }

  groupWasLabeledAsYes(e) {
    var pointId = e.target.dataset.point;
    var labeledPoint = this.state.pointsToLabel[pointId];
    labeledPoint.labels = this.props.groups.map((g) => 1);
    labeledPoint.label = 1;

    var pointsToLabel = this.state.pointsToLabel.map((e) => e);
    pointsToLabel.splice(pointId, 1);

    if (this.state.initialLabelingSession) {
      this.pointWasLabeledDuringInitialSession(labeledPoint, pointsToLabel);
    } else {
      this.pointWasLabeledAfterInitialSession(labeledPoint, pointsToLabel);
    }
  }

  groupWasLabeledAsNo(e) {
    var pointId = e.target.dataset.point;

    var pointsToLabel = this.state.pointsToLabel.map((e) => e);

    var point = pointsToLabel[pointId];
    point.labels = this.props.groups.map((e) => 1);
    point.label = 0;

    this.setState({
      pointsToLabel: pointsToLabel,
    });
  }

  groupSubLabelisationFinished(e) {
    var pointId = e.target.dataset.point;
    var pointsToLabel = this.state.pointsToLabel.map((e) => e);
    var labeledPoint = pointsToLabel[pointId];

    if (
      labeledPoint.labels.reduce((acc, v) => acc + v) ===
      labeledPoint.labels.length
    ) {
      alert("Please label at least one subgroup.");
      return;
    }

    pointsToLabel.splice(pointId, 1);
    var labeledPoints = this.state.labeledPoints.map((e) => e);
    labeledPoints.push(labeledPoint);

    // this.setState({
    //   hasNo: true,
    // });

    if (this.state.initialLabelingSession) {
      this.pointWasLabeledDuringInitialSession(labeledPoint, pointsToLabel);
    } else {
      this.pointWasLabeledAfterInitialSession(labeledPoint, pointsToLabel);
    }
  }

  pointWasLabeledDuringInitialSession(labeledPoint, pointsToLabel) {
    var labeledPoints = this.state.labeledPoints.map((e) => e);
    labeledPoints.push(labeledPoint);

    const isYes = labeledPoint.label === 1;
    var hasYesAndNo =
      (isYes && this.state.hasNo) || (!isYes && this.state.hasYes);

    if (hasYesAndNo) {
      this.setState(
        {
          pointsToLabel: [],
          allLabeledPoints: this.state.allLabeledPoints.concat(labeledPoints),
          initialLabelingSession: false,
        },
        () => {
          sendLabels(labeledPoints, this.newPointsToLabel.bind(this));
        }
      );
    } else if (pointsToLabel.length === 0) {
      this.setState(
        {
          pointsToLabel: [],
          allLabeledPoints: this.state.allLabeledPoints.concat(labeledPoints),
          hasYes: isYes,
          hasNo: !isYes,
        },
        () => {
          sendLabels(labeledPoints, this.newPointsToLabel.bind(this));
        }
      );
    } else {
      this.setState({
        pointsToLabel: pointsToLabel,
        labeledPoints: labeledPoints,
        hasYes: isYes,
        hasNo: !isYes,
      });
    }
  }

  pointWasLabeledAfterInitialSession(labeledPoint) {
    var labeledPoints = this.state.labeledPoints;
    labeledPoints.push(labeledPoint);

    this.setState(
      {
        allLabeledPoints: this.state.allLabeledPoints.concat(labeledPoints),
        pointsToLabel: [],
        labeledPoints: labeledPoints,
      },
      () => {
        sendLabels(labeledPoints, (response) => {
          this.newPointsToLabel(response);
          this.getModelBoundaries();
        });
      }
    );
  }

  getModelBoundaries() {
    this.setState(
      {
        isFetchingModelPrediction: true,
        isFetchingTSMPrediction: true,
        isFetchingProjection: true,
        nIteration: this.state.nIteration + 1,
      },
      this._getModelBoundaries.bind(this)
    );
  }

  _getModelBoundaries() {
    this.getGridPoints();

    if (isSimpleMargin(this.props.configuration)) {
      getTSMPredictions((predictedLabels) => {
        var TSMPredictionHistory = this.state.TSMPredictionHistory;
        TSMPredictionHistory.push(predictedLabels);

        this.setState({
          TSMPredictionHistory: TSMPredictionHistory,
          isFetchingTSMPrediction: false,
        });
      });
    }

    getModelPredictionsOverGridPoints((predictions) => {
      var history = this.state.modelPredictionHistory;

      history.push(predictions);
      this.setState({
        modelPredictionHistory: history,
        isFetchingModelPrediction: false,
      });
    });
  }

  getNumberOfIterations() {
    return this.state.nIteration;
  }

  getIteration() {
    return this.state.iteration;
  }

  onPreviousIteration() {
    var iteration = this.getIteration() - 1;
    this.setState({
      iteration: Math.max(iteration, 0),
    });
  }

  onNextIteration() {
    const nIteration = this.getNumberOfIterations();
    var iteration = this.getIteration() + 1;

    this.setState({
      iteration: Math.min(iteration, nIteration - 1),
    });
  }

  getGridPoints() {
    if (this.props.useRealData) {
      //var grid = buildRealDatasetGrid(this.props.dataset)
      const usedColumnNames = this.props.chosenColumns.map((e) => e["name"]);
      console.log(usedColumnNames);
      var grid = this.props.dataset.get_parsed_columns_by_names(
        usedColumnNames
      );
      this.setState({
        fakePointGrid: grid,
      });
    } else {
      if (this.state.fakePointGrid.length === 0) {
        getGridPoints((points) => {
          this.setState({
            fakePointGrid: points,
          });
        });
      }
    }
  }

  onSubgroupNo(e) {
    var data = e.target.dataset;
    var pointId = data.point;
    var subgroupId = data.subgroup;

    var pointsToLabel = this.state.pointsToLabel.map((e) => e);
    pointsToLabel[pointId].labels[subgroupId] =
      1 - pointsToLabel[pointId].labels[subgroupId];

    this.setState({
      pointsToLabel: pointsToLabel,
    });
  }

  onLabelWholeDatasetClick(e) {
    e.preventDefault();

    getWholedatasetLabeled();

    notifyLabelWholeDataset(this.props.tokens);
  }
}

function getWholedatasetLabeled() {
  $.ajax({
    type: "GET",
    url: backend + "/get-labeled-dataset",
    xhrFields: {
      withCredentials: true,
    },
    success: (response) => {
      var blob = new Blob([response]);
      var link = document.createElement("a");

      link.href = window.URL.createObjectURL(blob);
      document.body.appendChild(link);
      link.download = "labeled_dataset.csv";
      link.click();
    },
  });
}

function notifyLabelWholeDataset(tokens) {
  var wasAskedToLabelDatasetUrl =
    webplatformApi + "/session/" + tokens.sessionToken + "/label-whole-dataset";

  $.ajax({
    type: "PUT",
    dataType: "JSON",
    url: wasAskedToLabelDatasetUrl,
    headers: {
      Authorization: "Token " + tokens.authorizationToken,
    },
    data: {
      clicked_on_label_dataset: true,
    },
  });
}

function sendLabels(labeledPoints, onSuccess) {
  var labeledPoints = labeledPoints.map((e) => {
    return {
      id: e.id,
      labels: e.labels,
      data: {
        array: e.data,
      },
    };
  });

  var endPoint = backend + "/data-point-were-labeled"; // /tsm-data-point-were-labeled
  $.ajax({
    type: "POST",
    dataType: "JSON",
    url: endPoint,
    xhrFields: {
      withCredentials: true,
    },
    data: {
      labeledPoints: JSON.stringify(labeledPoints),
    },

    success: onSuccess,
  });
}

TSMExploration.defaultProps = {
  useRealData: true,
};

export default TSMExploration;
