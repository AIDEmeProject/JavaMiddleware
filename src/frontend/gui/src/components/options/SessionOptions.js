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

import {
  simpleMarginConfiguration,
  versionSpaceConfiguration,
} from "../../constants/constants";
import actions from "../../actions/sendChosenColumns";

import AttributeSelection from "./AttributeSelection";
import GroupVariables from "./GroupVariables";
import AdvancedOptions from "./AdvancedOptions";

class SessionOptions extends Component {
  constructor(props) {
    super(props);

    var datasetInfos = this.props.datasetInfos;

    var chosenColumns = datasetInfos.columns.map((name, idx) => ({
      name,
      idx,
      isUsed: false,
      type: datasetInfos.types[idx],
    }));

    this.state = {
      showColumns: true,
      showVariableGroups: false,
      showAdvancedOptions: false,

      firstVariable: 0,
      secondVariable: 1,
      columnTypes: datasetInfos.types,

      checkboxes: datasetInfos.columns.map((c) => false),
      chosenColumns: chosenColumns,

      configuration: simpleMarginConfiguration,
    };
  }

  render() {
    return (
      <div>
        <ul className="nav nav-tabs bg-primary">
          <li className="nav-item">
            <a
              className={
                this.state.showColumns ? "nav-link active" : "nav-link"
              }
              href="javascript:void(0)"
              onClick={this.onBasicOptionClick.bind(this)}
            >
              Attribute selection
            </a>
          </li>

          <li className="nav-item">
            <a
              className={
                this.state.showVariableGroups ? "nav-link active" : "nav-link"
              }
              href="javascript:void(0)"
              onClick={this.onVariableGrouping.bind(this)}
            >
              Factorization structure
            </a>
          </li>

          <li className="nav-item">
            <a
              className={
                this.state.showAdvancedOptions ? "nav-link active" : "nav-link"
              }
              href="javascript:void(0)"
              onClick={this.onAdvancedOptionClick.bind(this)}
            >
              Algorithm selection
            </a>
          </li>

          <li>
            <a
              className="nav-link"
              onClick={this.onSessionStartClick.bind(this)}
            >
              Start session
            </a>
          </li>
        </ul>

        <form id="choose-columns" className="card">
          {this.state.showColumns && (
            <AttributeSelection
              columns={this.props.datasetInfos.columns}
              checkboxes={this.state.checkboxes}
              dataset={this.props.dataset}
              firstVariable={this.state.firstVariable}
              secondVariable={this.state.secondVariable}
              chosenColumns={this.state.chosenColumns}
              onCheckedColumn={this.onCheckedColumn.bind(this)}
            />
          )}

          {this.state.showVariableGroups && (
            <GroupVariables
              chosenColumns={this.state.chosenColumns}
              onValidateGroupsClick={this.onValidateGroupsClick.bind(this)}
            />
          )}

          {this.state.showAdvancedOptions && (
            <AdvancedOptions
              onLearnerChange={this.onLearnerChange.bind(this)}
            />
          )}
        </form>
      </div>
    );
  }

  componentDidMount() {
    window.$("form").bootstrapMaterialDesign();
    window.$("select").select();
  }

  onVariableGrouping() {
    this.setState({
      showAdvancedOptions: false,
      showColumns: false,
      showVariableGroups: true,
    });
  }

  onBasicOptionClick() {
    this.setState({
      showAdvancedOptions: false,
      showColumns: true,
      showVariableGroups: false,
    });
  }

  onAdvancedOptionClick() {
    this.setState({
      showAdvancedOptions: true,
      showColumns: false,
      showVariableGroups: false,
    });
  }

  onCheckedColumn(e) {
    var idx = e.target.value;
    var checkboxes = this.state.checkboxes.map((e) => e);

    var newChosenColumns = this.state.chosenColumns.map((e) => e);
    newChosenColumns[idx].isUsed = e.target.checked;
    idx = 0;
    newChosenColumns.forEach((e, i) => {
      if (e.isUsed) {
        newChosenColumns[i] = Object.assign({}, e, {
          finalIdx: idx,
        });
        idx++;
      }
    });

    checkboxes[idx] = e.target.checked;

    this.setState({
      chosenColumns: newChosenColumns,
      checkboxes: checkboxes,
    });
  }

  onLearnerChange(e) {
    const learner = e.target.value;
    if (learner === "versionSpace")
      this.setState({ configuration: versionSpaceConfiguration });
    else this.setState({ configuration: simpleMarginConfiguration });
  }

  onSessionStartClick(e) {
    var chosenColumns = this.state.chosenColumns.filter((col) => col.isUsed);

    actions.sendColumns(
      chosenColumns,
      this.state.configuration,
      this.props.sessionWasStarted
    );

    this.props.sessionOptionsWereChosen(
      chosenColumns,
      this.state.configuration
    );
  }

  onValidateGroupsClick(groups) {
    var chosenColumns = groups.flatMap((g) => [...g]); // columns may be repeated

    this.computeVariableColumnIndices(groups);

    var datasetMetadata = this.buildDatasetMetadata();

    actions.sendVariableGroups(
      chosenColumns,
      groups,
      datasetMetadata,
      this.state.configuration,
      this.props.sessionWasStarted
    );

    this.props.groupsWereValidated(
      chosenColumns,
      groups,
      this.state.configuration
    );
  }

  buildDatasetMetadata() {
    return {
      types: this.state.columnTypes.map((e) => e === "categorical"),
      columnNames: this.state.chosenColumns.map((e) => e["name"]),
    };
  }

  computeVariableColumnIndices(groups) {
    var i = 0;

    groups.forEach((variables) => {
      variables.forEach((variable) => {
        variable["realId"] = i;
        i++;
      });
    });
  }
}

export default SessionOptions;
