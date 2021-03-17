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

import MicroModalComponent from "./MicroModalComponent";
import Group from "./Group";

import robot from "../../resources/robot.png";

class GroupEditor extends Component {
  render() {
    const variablesInGroup = this.props.group.map((variable) => variable.idx);
    return (
      <div>
        {this.props.chosenColumns.map((variable, i) => {
          return (
            <div key={i} className="form-inline in-line">
              <label htmlFor={"column-group-" + i}>{variable.name}</label>
              <input
                id={"column-group-" + i}
                type="checkbox"
                className="form-control"
                data-variableid={variable.idx}
                checked={variablesInGroup.includes(variable.idx)}
                onChange={this.onVariableAddedClick.bind(this)}
              />
            </div>
          );
        })}
      </div>
    );
  }

  onVariableAddedClick(e) {
    const iVariable = parseInt(e.target.dataset.variableid);

    if (e.target.checked) {
      this.props.onVariableAddedToGroup(this.props.iGroup, iVariable);
    } else {
      this.props.onVariableRemovedFromGroup(this.props.iGroup, iVariable);
    }
  }
}

class GroupVariables extends Component {
  constructor(props) {
    super(props);

    this.state = {
      groups: [[]],
      editedGroupId: null,
    };
  }

  render() {
    return (
      <div>
        <h4>Variable subgroups</h4>

        <p className="card">
          <span className="chatbot-talk">
            <img src={robot} width="50" alt="robot" />
            <q>
              By grouping variables in formal subgroups, the convergence speed
              can be improved. Group variables by click on "Edit" on a given
              group.
            </q>
          </span>
        </p>

        <div>
          <button
            role="button"
            type="button"
            className="btn btn-primary btn-raised"
            onClick={this.addVariableGroup.bind(this)}
          >
            Add group
          </button>

          <button
            type="button"
            role="button"
            className="btn btn-primary btn-raised"
            onClick={this.validateGroups.bind(this)}
          >
            Validate groups
          </button>
        </div>

        <div>
          {this.state.groups.map((group, iGroup) => {
            return (
              <div key={iGroup} className="card group">
                <p>
                  Group {iGroup + 1}
                  <button
                    type="button"
                    role="button"
                    data-group={iGroup}
                    className="btn btn-primary"
                    onClick={this.onGroupEdit.bind(this)}
                  >
                    Edit
                  </button>
                </p>

                <Group
                  group={group}
                  iGroup={iGroup}
                  onVariableRemovedFromGroup={this.onVariableRemovedFromGroup.bind(
                    this
                  )}
                />
              </div>
            );
          })}
        </div>

        {this.state.editedGroupId !== null && (
          <MicroModalComponent
            title={
              "Edition of group " + String(Number(this.state.editedGroupId) + 1)
            }
            onClose={this.closeFactorizationGroupEdition.bind(this)}
          >
            <GroupEditor
              group={this.state.groups[this.state.editedGroupId]}
              iGroup={this.state.editedGroupId}
              chosenColumns={this.props.chosenColumns.filter((e) => e.isUsed)}
              onVariableAddedToGroup={this.onVariableAddedToGroup.bind(this)}
              onVariableRemovedFromGroup={this.onVariableRemovedFromGroup.bind(
                this
              )}
            />
          </MicroModalComponent>
        )}
      </div>
    );
  }

  componentDidUpdate() {
    window.$("input").bootstrapMaterialDesign();
  }

  closeFactorizationGroupEdition(e) {
    e.preventDefault();
    this.setState({ editedGroupId: null });
  }

  onGroupEdit(e) {
    const groupId = e.target.dataset.group;

    this.setState({
      editedGroupId: groupId,
    });
  }

  isVariableInGroup(group, variable) {
    const names = group.map((e) => e.name);

    return names.includes(variable.name);
  }

  onVariableAddedToGroup(groupId, variableId) {
    var variable = this.props.chosenColumns[variableId];

    var newGroupsState = this.state.groups.map((e) => e);

    var modifiedGroup = newGroupsState[groupId];

    variable["realId"] = variableId;
    variable["id"] = variableId;

    if (!this.isVariableInGroup(modifiedGroup, variable)) {
      modifiedGroup.push(variable);
    }

    newGroupsState[groupId] = modifiedGroup;

    this.setState({
      groups: newGroupsState,
    });
  }

  onVariableRemovedFromGroup(groupId, variableId) {
    var newGroupsState = this.state.groups.map((e) => e);
    var modifiedGroup = newGroupsState[groupId];

    const removedColumnId = modifiedGroup.findIndex(
      (e) => e.idx === variableId
    );
    modifiedGroup.splice(removedColumnId, 1);

    newGroupsState[groupId] = modifiedGroup;
    this.setState({
      groups: newGroupsState,
    });
  }

  addVariableGroup() {
    var groups = this.state.groups.map((e) => e);

    groups.push([]);
    this.setState({
      groups: groups,
    });
  }

  validateGroups() {
    const nVariableInGroups = this.state.groups.reduce((acc, a) => {
      return a.length + acc;
    }, 0);

    if (nVariableInGroups === 0) {
      alert("Please put at least a variable in a group.");
      return;
    }

    this.props.onValidateGroupsClick(this.state.groups);
  }
}

export default GroupVariables;
