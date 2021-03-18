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

import sendFilters from "../../../actions/sendFilters";
import PointLabelisation from "../../PointLabelisation";

import robot from "../../../resources/robot.png";

class NumericalFilter extends Component {
  constructor(props) {
    super(props);

    const valuesInColumn = this.props.dataset.get_column_name(
      this.props.variable.name
    );
    const [min, max] = d3.extent(valuesInColumn);

    this.state = {
      min,
      max,
      step: (max - min) / 100,
      minValue: min,
      maxValue: max,
    };
  }

  render() {
    return (
      <div className="card filter inline-block">
        <p>
          {this.props.variable.name} <br />
          range: [{this.state.minValue}, {this.state.maxValue}]
        </p>
        <label>
          Min
          <input
            value={this.state.minValue}
            onChange={this.minChanged.bind(this)}
            type="range"
            min={this.state.min}
            max={this.state.max}
            step={this.state.step}
          />
        </label>

        <br />

        <label>
          Max
          <input
            value={this.state.maxValue}
            onChange={this.maxChanged.bind(this)}
            type="range"
            min={this.state.min}
            max={this.state.max}
            step={this.state.step}
          />
        </label>
      </div>
    );
  }

  minChanged(e) {
    var newValue = parseFloat(e.target.value);
    if (newValue >= this.state.maxValue) {
      newValue = this.state.maxValue;
    }

    this.setState({
      minValue: newValue,
    });

    this.props.filterChanged(this.props.iFilter, { min: newValue });
  }

  maxChanged(e) {
    var newValue = parseFloat(e.target.value);
    if (newValue <= this.state.minValue) {
      newValue = this.state.minValue;
    }

    this.setState({ maxValue: newValue });

    this.props.filterChanged(this.props.iFilter, { max: newValue });
  }
}

class CategoricalFilter extends Component {
  constructor(props) {
    super(props);

    var uniqueValues = Object.entries(
      this.props.dataset.uniqueValues(this.props.variable.name)
    ).map((e) => e[0]);

    this.state = {
      uniqueValues,
      filterValues: [],
    };
  }

  render() {
    return (
      <div className="card filter categorical-filter inline-block">
        <p>{this.props.variable.name}</p>
        {this.state.uniqueValues.map((value, i) => {
          return (
            <div>
              <label htmlFor={"cat-filter-" + i}>{value}</label>
              <input
                type="checkbox"
                data-value={value}
                onChange={this.categoryWasClicked.bind(this)}
              />
            </div>
          );
        })}
      </div>
    );
  }

  categoryWasClicked(e) {
    var value = e.target.dataset.value;

    var parsedValue = parseFloat(value);
    if (!isNaN(parsedValue)) value = parsedValue;

    const newFilterValues = e.target.checked
      ? [...this.state.filterValues, value]
      : this.state.filterValues.filter((e) => e !== value);

    this.setState(
      {
        filterValues: newFilterValues,
      },
      () => {
        this.props.filterChanged(this.props.iFilter, {
          filterValues: newFilterValues,
        });
      }
    );
  }
}

class FilteringPoints extends Component {
  constructor(props) {
    super(props);

    this.state = {
      filters: this.props.chosenVariables.map((e) => ({
        columnName: e.name,
      })),
      points: [],
    };
  }

  render() {
    return (
      <div>
        <p className="card">
          <span className="chatbot-talk">
            <img src={robot} width="50" alt="robot" />
            <q>Filter positive points and click on Get points.</q>
          </span>
        </p>

        {this.props.chosenVariables.map((variable, i) => {
          var Filter =
            variable.type === "numerical" ? NumericalFilter : CategoricalFilter;
          return (
            <Filter
              iFilter={i}
              filterChanged={this.filterChanged.bind(this)}
              key={i}
              variable={variable}
              dataset={this.props.dataset}
            />
          );
        })}

        <p>
          <button
            className="btn btn-primary btn-raised"
            onClick={this.getPoints.bind(this)}
          >
            Get Points
          </button>
        </p>

        {this.state.points.length > 0 && (
          <div>
            <PointLabelisation
              chosenColumns={this.props.chosenVariables}
              dataset={this.props.dataset}
              pointsToLabel={this.state.points}
              onPositiveLabel={this.onPositiveLabel.bind(this)}
              onNegativeLabel={this.onNegativeLabel.bind(this)}
            />
          </div>
        )}
      </div>
    );
  }

  onPositiveLabel(e) {
    var iPoint = e.target.dataset.key;

    var points = [...this.state.points];
    points.splice(iPoint, 1);
    this.setState({
      points,
    });

    this.props.onPositiveLabel(e);
  }

  onNegativeLabel(e) {
    var iPoint = e.target.dataset.key;

    var points = [...this.state.points];
    points.splice(iPoint, 1);
    this.setState({
      points,
    });

    this.props.onNegativeLabel(e);
  }

  filterChanged(iFilter, change) {
    var newFilters = [...this.state.filters];
    Object.assign(newFilters[iFilter], change);
    this.setState({ filters: newFilters });
  }

  getPoints() {
    sendFilters(this.state.filters, this.pointsReceived.bind(this));
  }

  pointsReceived(points) {
    if (points.length === 0) alert("No points satisfy the criteria.");

    var formattedPoints = points.map((id) => ({ id }));
    // .filter((e, i) => {
    //   return i < 25;
    // });

    this.setState({
      points: formattedPoints,
    });
  }
}

export default FilteringPoints;
