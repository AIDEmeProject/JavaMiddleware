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
import Exploration from "./components/exploration/Exploration";
import TSMExploration from "./components/exploration/TSM/TSMExploration";
import BreadCrumb from "./components/BreadCrumb";
import Trace from "./components/trace/Trace";

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

      datasetInfos: {},
      dataset: null,

      chosenColumns: [],
      groups: null,
      configuration: null,

      finalVariables: [],
      pointsToLabel: [],
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

  getCurrentView(step) {
    if (step === NEW_SESSION) {
      return (
        <NewSession
          fileUploaded={this.fileUploaded.bind(this)}
          onDatasetLoaded={this.onDatasetLoaded.bind(this)}
        />
      );
    }

    if (step === SESSION_OPTIONS) {
      return (
        <SessionOptions
          datasetInfos={this.state.datasetInfos}
          dataset={this.state.dataset}
          sessionWasStarted={this.sessionWasStarted.bind(this)}
          sessionOptionsWereChosen={this.sessionOptionsWereChosen.bind(this)}
          groupsWereValidated={this.groupsWereValidated.bind(this)}
        />
      );
    }

    if (step === EXPLORATION) {
      return (
        <Exploration
          dataset={this.state.dataset}
          chosenColumns={this.state.chosenColumns}
          finalVariables={this.state.finalVariables}
          pointsToLabel={this.state.pointsToLabel}
          tokens={{
            authorizationToken: this.state.authorizationToken,
            sessionToken: this.state.sessionToken,
          }}
        />
      );
    }

    if (step === TSM_EXPLORATION) {
      return (
        <TSMExploration
          dataset={this.state.dataset}
          chosenColumns={this.state.chosenColumns}
          groups={this.state.groups}
          configuration={this.state.configuration}
          pointsToLabel={this.state.pointsToLabel}
          tokens={{
            authorizationToken: this.state.authorizationToken,
            sessionToken: this.state.sessionToken,
          }}
        />
      );
    }

    if (step === TRACE) {
      return <Trace />;
    }

    return (
      <Welcome
        onInteractiveSessionClick={this.onInteractiveSessionClick.bind(this)}
        onTraceClick={this.onTraceClick.bind(this)}
      />
    );
  }

  render() {
    return (
      <div>
        <ul className="navbar navbar-dark box-shadow">
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
              {this.getCurrentView(this.state.step)}
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
    var csv = d3.csvParse(event.target.result);

    this.setState({
      dataset: new Dataset(csv),
    });
  }

  fileUploaded(response) {
    if (response.error) {
      alert(response.error);
      return;
    }

    this.setState({
      step: SESSION_OPTIONS,
      bread: this.getBreadCrum(SESSION_OPTIONS),
      datasetInfos: response,
    });
  }

  sessionOptionsWereChosen(chosenColumns, configuration) {
    this.state.dataset.set_column_names_selected_by_user(chosenColumns);

    this.setState({ chosenColumns, configuration });
  }

  groupsWereValidated(chosenColumns, groups, configuration) {
    this.state.dataset.set_column_names_selected_by_user(chosenColumns);

    this.setState({ chosenColumns, groups, configuration });
  }

  sessionWasStarted(response) {
    const pointsToLabel = response.map((pointToLabel) => {
      return {
        id: pointToLabel.id,
        data: pointToLabel.data.array,
      };
    });

    if (this.state.groups) {
      this.setState({
        step: TSM_EXPLORATION,
        bread: this.getBreadCrum(TSM_EXPLORATION),
        pointsToLabel: pointsToLabel,
      });
    } else {
      this.setState({
        step: EXPLORATION,
        bread: this.getBreadCrum(EXPLORATION),
        pointsToLabel: pointsToLabel,
      });
    }
  }

  componentDidMount() {
    MicroModal.init();
  }
}

export default App;
