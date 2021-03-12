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

import {
  backend,
  webplatformApi,
  defaultConfiguration,
} from "../constants/constants";
import buildTSMConfiguration from "../lib/buildTSMConfiguration";

import $ from "jquery";

function sendVariableGroups(
  tokens,
  chosenVariables,
  groups,
  datasetMetadata,
  onSuccess
) {
  var endPoint = backend + "/choose-options";

  var configuration = defaultConfiguration;

  var usedColumnNames = chosenVariables.map((e) => e.name);
  console.log(groups);
  var groupsWithIds = groups.map((variables) => {
    return variables.map((v) => v["id"]);
  });

  configuration = buildTSMConfiguration(
    configuration,
    groupsWithIds,
    usedColumnNames,
    datasetMetadata
  );

  $("#conf").val(JSON.stringify(configuration));

  $.ajax({
    type: "POST",
    url: endPoint,
    xhrFields: {
      withCredentials: true,
    },
    data: {
      configuration: JSON.stringify(configuration),
      columnIds: JSON.stringify(chosenVariables.map((e) => e.idx)),
    },
    success: (response) => {
      onSuccess(response);
    },
  });
}

function sendColumns(tokens, chosenColumns, onSuccess) {
  var endPoint = backend + "/choose-options";

  var configuration = defaultConfiguration;

  $.ajax({
    type: "POST",
    url: endPoint,
    xhrFields: {
      withCredentials: true,
    },
    data: {
      configuration: JSON.stringify(configuration),
      columnIds: JSON.stringify(chosenColumns.map((e) => e.idx)),
    },
    success: (response) => {
      onSuccess(response);
    },
  });
}

function sendDataToWebPlateform(
  availableVariables,
  options,
  hasTSM,
  featureGroups,
  sessionOptionsUrl,
  tokens
) {
  var sessionOptionsUrl =
    webplatformApi + "/session/" + tokens.sessionToken + "/options";

  var numberOfNumerical = availableVariables.reduce((e, acc) => {
    return acc + 1 * e.type === "numerical";
  }, 0);

  var numberOfCategorical = availableVariables.reduce((e, acc) => {
    return acc + 1 * e.type === "categorical";
  }, 0);

  var statisticData = {
    column_number: availableVariables.length,
    numberOfCategorical: numberOfCategorical,
    numberOfNumerical: numberOfNumerical,
    has_tsm: hasTSM,
    learner: options.learner,
    classifier: options.classifier,
  };

  if (hasTSM) {
    if (availableVariables.length == 2) {
      var nGroups = 2;
    } else {
      var nGroups = featureGroups.length;
    }
    statisticData["number_of_variable_groups"] = nGroups;
  }

  $.ajax({
    type: "PUT",
    url: sessionOptionsUrl,
    data: statisticData,
    headers: {
      Authorization: "Token " + tokens.authorizationToken,
    },
  });
}

export default {
  sendColumns: sendColumns,
  sendVariableGroups: sendVariableGroups,
};
