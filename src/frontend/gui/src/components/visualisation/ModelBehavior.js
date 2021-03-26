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

import * as d3 from "d3";

import ModelBehaviorPlotter from "./ModelBehaviorPlotter";

const colors = {
  "-1": "red",
  "0": "grey",
  "1": "green",
};

class ModelBehavior extends Component {
  render() {
    const scale = this.state.scale;

    return (
      <div>
        <div className="row">
          <div className="col-lg-12 behavior-options">
            <div className="form-inline">
              <div className="form-group">
                <select
                  value={this.state.firstVariable}
                  className="form-control inline"
                  onChange={this.firstVariableChanged.bind(this)}
                >
                  {this.props.availableVariables.map((variable, i) => {
                    return (
                      <option
                        key={i}
                        className="form-control"
                        value={i}
                        data-value={i}
                      >
                        {variable.name}
                      </option>
                    );
                  })}
                </select>
              </div>

              <br />

              <div className="form-group">
                <label htmlFor="xMin">Minimum</label>
                <input
                  id="xMin"
                  data-name="xMin"
                  className="range-input"
                  value={scale.xMin}
                  onChange={this.onChangeScale.bind(this)}
                />
              </div>

              <div className="form-group">
                <label htmlFor="xMax">Maximum</label>
                <input
                  id="xMax"
                  data-name="xMax"
                  className="range-input"
                  value={scale.xMax}
                  onChange={this.onChangeScale.bind(this)}
                />
              </div>
            </div>

            <div className="form-inline">
              <div className="form-group">
                <select
                  value={this.state.secondVariable}
                  className="form-control"
                  onChange={this.secondVariableChanged.bind(this)}
                >
                  {this.props.availableVariables.map((variable, i) => {
                    return (
                      <option
                        className="form-control"
                        data-value={variable.realId}
                        value={i}
                        key={i}
                      >
                        {variable.name}
                      </option>
                    );
                  })}
                </select>
              </div>

              <br />

              <div className="form-group">
                <label htmlFor="yMin">Minimum</label>
                <input
                  id="yMin"
                  data-name="yMin"
                  className="range-input"
                  value={scale.yMin}
                  onChange={this.onChangeScale.bind(this)}
                />
              </div>

              <div className="form-group">
                <label htmlFor="yMax">Maximum</label>
                <input
                  id="yMax"
                  data-name="yMax"
                  className="range-input"
                  value={scale.yMax}
                  onChange={this.onChangeScale.bind(this)}
                />
              </div>
            </div>
          </div>
        </div>

        {this.props.hasTSM && (
          <div className="row">
            <div className="col col-lg-12">
              <h4 className="left-title">Polytope Model</h4>
              <svg id="tsm-plot"></svg>
            </div>
          </div>
        )}

        <div className="row">
          <div className="col col-lg-12">
            <h4 className="left-title">Model predictions</h4>
            <svg id="model-predictions-grid-point"></svg>
          </div>
        </div>

        {this.props.plotProjection && (
          <div className="row">
            <div className="col-lg-12">
              {this.props.hasTSM ? (
                <h4 className="left-title">
                  Polytope model Predictions over projected dataset
                </h4>
              ) : (
                <h4 className="left-title">
                  Predictions over projected dataset
                </h4>
              )}

              <svg id="projection"></svg>
            </div>
          </div>
        )}
      </div>
    );
  }

  constructor(props) {
    super(props);

    this.state = {
      firstVariable: 0,
      secondVariable: 1,
      scale: {
        xMin: -5,
        xMax: 5,
        yMin: -5,
        yMax: 5,
      },
    };
  }

  componentWillMount() {
    if (this.props.modelPredictionHistory.length > 0) {
      this.setState({
        scale: this.computeMinMaxOfRawData(),
      });
    }
  }

  componentDidMount() {
    const columnNames = this.props.availableVariables.map((e) => e.name);

    if (this.props.availableVariables.length <= 4 || this.props.realDataset) {
      this.modelPredictionPlotter = new ModelBehaviorPlotter(columnNames);
      this.modelPredictionPlotter.createPlot(
        "#model-predictions-grid-point",
        this.state.scale
      );
      this.modelPredictionPlotter.setPlotLabels(this.props.plotLabels);
    }

    this.projectionPlotter = new ModelBehaviorPlotter(["X", "Y"]);
    this.projectionPlotter.createPlot("#projection", this.state.scale);
    this.projectionPlotter.setPlotLabels(this.props.plotLabels);

    if (this.props.hasTSM) {
      this.tsmPlotter = new ModelBehaviorPlotter(columnNames);
      this.tsmPlotter.createPlot("#tsm-plot", this.state.scale);
      this.tsmPlotter.setPlotLabels(this.props.plotLabels);
    }

    this.plotAll();
  }

  componentDidUpdate() {
    this.plotAll();
  }

  plotAll() {
    if (this.props.availableVariables.length <= 4 || this.props.realDataset) {
      this.plotPredictions(
        this.modelPredictionPlotter,
        this.props.modelPredictionHistory
      );
    }

    if (this.props.hasTSM) {
      this.plotPredictions(this.tsmPlotter, this.props.TSMPredictionHistory);
    }

    if (this.props.plotProjection) {
      this.plotDataEmbbedingPlot();
    }
  }

  plotPredictions(plotter, history) {
    plotter.plotData(
      this.state.scale,
      this.getHumanLabeledPoints(),
      this.getChosenVariables(),
      this.getPredictions(history),
      colors
    );
  }

  plotDataEmbbedingPlot() {
    const embeddings = this.getEmbbedings();

    const x = embeddings.map((e) => e[0]);
    const y = embeddings.map((e) => e[1]);
    const scale = this.computeMinAndMaxScale(x, y);

    const humanLabeledPoints = this.getLabeledEmbedding();

    const chosenVariables = [0, 1];

    this.projectionPlotter.plotData(
      scale,
      humanLabeledPoints,
      chosenVariables,
      embeddings,
      colors
    );
  }

  getPredictions(predictionHistory) {
    const grid = this.props.fakePointGrid;
    const predictions = predictionHistory[this.getIteration()];

    const gridPoints = d3.zip(grid, predictions).map((e) => {
      const gridPoint = e[0];
      const prediction = e[1];
      return [
        gridPoint[this.state.firstVariable],
        gridPoint[this.state.secondVariable],
        prediction.label,
      ];
    });

    return gridPoints;
  }

  getEmbbedings() {
    return this.props.projectionHistory[this.getIteration()].embedding;
  }

  getHumanLabeledPoints() {
    const formattedLabeledPoints = this.props.labeledPoints
      .slice(0, this.getIteration() + 1)
      .flat()
      .map((point) => {
        const row = this.props.fakePointGrid[point.id];
        return [
          row[this.state.firstVariable],
          row[this.state.secondVariable],
          point.label,
        ];
      });
    return formattedLabeledPoints;
  }

  getLabeledEmbedding() {
    const embeddings = this.getEmbbedings();
    const labeledEmbeddings = this.props.labeledPoints
      .slice(0, this.getIteration() + 1)
      .flat()
      .map((e) => embeddings[e.id])
      .filter((e) => typeof e !== "undefined");
    return labeledEmbeddings;
  }

  getIteration() {
    return this.props.iteration;
  }

  getChosenVariables() {
    return [this.state.firstVariable, this.state.secondVariable];
  }

  computeMinMaxOfRawData() {
    const grid = this.getPredictions(this.props.modelPredictionHistory);
    const offset = {
      x: 0,
      y: 0,
    };
    const scale = this.computeMinAndMaxScale(
      grid.map((e) => e[0]),
      grid.map((e) => e[1]),
      offset
    );
    return scale;
  }

  computeMinAndMaxScale(xValues, yValues, offset = { x: 0, y: 0 }) {
    const scale = {
      xMin: d3.min(xValues) - offset.x,
      xMax: d3.max(xValues) + offset.x,
      yMin: d3.min(yValues) - offset.y,
      yMax: d3.max(yValues) + offset.y,
    };
    return scale;
  }

  firstVariableChanged(e) {
    this.setState(
      {
        firstVariable: parseInt(e.target.value),
      },
      this.variableToDisplayChanged
    );
  }

  secondVariableChanged(e) {
    this.setState(
      {
        secondVariable: parseInt(e.target.value),
      },
      this.variableToDisplayChanged
    );
  }

  variableToDisplayChanged() {
    this.setState(
      {
        scale: this.computeMinMaxOfRawData(),
      },
      this.plotAll
    );
  }

  onChangeScale(e) {
    const key = e.target.dataset.name;
    const value = e.target.value;

    if (isNaN(value)) {
      if (!isNaN(parseFloat(value))) {
        this.setState({
          scale: Object.assign({}, this.state.scale, {
            [key]: parseFloat(value),
          }),
        });
      }
    } else {
      this.setState({
        scale: Object.assign({}, this.state.scale, { [key]: value }),
      });
    }
  }
}

ModelBehavior.defaultProps = {
  plotProjection: true,
};

export default ModelBehavior;
