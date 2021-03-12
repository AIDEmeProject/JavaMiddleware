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

import Welcome from "./components/Welcome";
import NewSession from "./components/options/NewSession";
import SessionOptions from "./components/options/SessionOptions";
import Exploration from "./components/Exploration/Exploration";
import TSMExploration from "./components/Exploration/TSM/TSMExploration";
import BreadCrumb from "./components/BreadCrumb";
import Trace from "./components/Trace/Trace";

import MicroModal from "micromodal";

import "./App.css";
import logo from "./resources/logo.png";

import * as d3 from "d3";
import Dataset from "./model/Dataset";

const EXPLORATION = "Exploration";
const NEW_SESSION = "NewSession";
const SESSION_OPTIONS = "SessionOptions";
const TSM_EXPLORATION = "TSMExploration";
const TRACE = "Trace";
const WELCOME = "Welcome";

class App extends Component {
  constructor(props) {
    super(props);

    this.state = {
      step: WELCOME,
      bread: this.getBreadCrum(NEW_SESSION),
      columns: [],

      initialLabelingSession: true,
      hasYesAndNo: false,
      hasYes: false,
      hasFalse: false,
      availableVariables: [],
      finalVariables: [],
      labeledPoints: [],
      pointsToLabel: [],
      allLabeledPoints: [],
      history: [],
    };
  }

  getBreadCrum(step) {
    var group = step === TSM_EXPLORATION ? EXPLORATION : step;

    var names = {
      [NEW_SESSION]: "New session",
      [SESSION_OPTIONS]: "Session options",
      [EXPLORATION]: "Interactive labeling",
    };
    var order = [NEW_SESSION, SESSION_OPTIONS, EXPLORATION];

    return order.map((id) => ({ name: names[id], active: id === group }));
  }

  render() {
    var views = {
      [WELCOME]: Welcome,
      [TRACE]: Trace,
      [NEW_SESSION]: NewSession,
      [SESSION_OPTIONS]: SessionOptions,
      [TSM_EXPLORATION]: TSMExploration,
      [EXPLORATION]: Exploration,
    };

    var View;
    if (views.hasOwnProperty(this.state.step)) {
      View = views[this.state.step];
    } else {
      View = NewSession;
    }

    return (
      <div>
        <ul className="navbar navbar-dark box-shadow ">
          <li className="nav-item">
            <a className="navbar-brand" href="/">
              <img src={logo} height="50" alt="logo" /> AIDEme
            </a>
          </li>

          <li className="nav-item">
            <BreadCrumb items={this.state.bread} />
          </li>
        </ul>

        <div className="App container-fluid">
          <div className="row">
            <div className="col col-lg-12">
              <View
                {...this.state}
                fileUploaded={this.fileUploaded.bind(this)}
                sessionWasStarted={this.sessionWasStarted.bind(this)}
                sessionOptionsWereChosen={this.sessionOptionsWereChosen.bind(
                  this
                )}
                tokens={{
                  authorizationToken: this.state.authorizationToken,
                  sessionToken: this.state.sessionToken,
                }}
                groupsWereValidated={this.groupsWereValidated.bind(this)}
                onDatasetLoaded={this.onDatasetLoaded.bind(this)}
                onTraceClick={this.onTraceClick.bind(this)}
                onInteractiveSessionClick={this.onInteractiveSessionClick.bind(
                  this
                )}
              />
            </div>
          </div>

          <div className="row">
            <div className="col col-lg-10 offset-lg-1"></div>
          </div>

          <div id="pandas-profiling"></div>
        </div>
      </div>
    );
  }

  onTraceClick(e) {
    this.setState({ step: TRACE });
  }

  onInteractiveSessionClick() {
    this.setState({ step: NEW_SESSION });
  }

  onDatasetLoaded(event) {
    var fileContent = event.target.result;
    var csv = d3.csvParse(fileContent);
    var dataset = new Dataset(csv);

    this.setState({
      dataset: dataset,
    });
  }

  fileUploaded(response) {
    this.setState({
      step: SESSION_OPTIONS,
      bread: this.getBreadCrum(SESSION_OPTIONS),
      datasetInfos: response,
    });
  }

  sessionOptionsWereChosen(options) {
    var newOptions = Object.assign({}, this.state.options, options);

    const chosenColumns = options.chosenColumns;

    this.state.dataset.set_column_names_selected_by_user(chosenColumns);

    this.setState({
      options: newOptions,
    });
  }

  groupsWereValidated(chosenColumns, groups, callback) {
    var options = {
      chosenColumns: chosenColumns,
      groups: groups,
    };

    this.state.dataset.set_column_names_selected_by_user(chosenColumns);

    var newOptions = Object.assign({}, this.state.options, options);

    this.setState({ options: newOptions }, callback);
  }

  sessionWasStarted(response) {
    var options = this.state.options;

    var pointsToLabel = response.map((pointToLabel) => {
      return {
        id: pointToLabel.id,
        data: pointToLabel.data.array,
      };
    });

    var finalVariables = options.chosenColumns;
    var hasTSM = options.groups;

    if (hasTSM) {
      var groups = options.groups;

      this.setState({
        step: TSM_EXPLORATION,
        bread: this.getBreadCrum(TSM_EXPLORATION),
        pointsToLabel: pointsToLabel,
        groups: groups,
        chosenColumns: finalVariables,
      });
    } else {
      this.setState({
        step: EXPLORATION,
        bread: this.getBreadCrum(EXPLORATION),
        pointsToLabel: pointsToLabel,
        chosenColumns: this.state.options.chosenColumns,
      });
    }
  }

  componentDidMount() {
    MicroModal.init();
  }
}

export default App;
