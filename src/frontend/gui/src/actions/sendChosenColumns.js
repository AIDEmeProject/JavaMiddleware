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

import { backend } from "../constants/constants";
import buildTSMConfiguration from "../lib/buildTSMConfiguration";

import $ from "jquery";

function sendVariableGroups(
  chosenVariables,
  groups,
  datasetMetadata,
  configuration,
  onSuccess
) {
  const usedColumnNames = chosenVariables.map((e) => e.name);
  const groupsWithIds = groups.map((variables) => variables.map((v) => v.id));

  configuration = buildTSMConfiguration(
    configuration,
    groupsWithIds,
    usedColumnNames,
    datasetMetadata
  );

  $("#conf").val(JSON.stringify(configuration));

  $.ajax({
    type: "POST",
    url: backend + "/choose-options",
    xhrFields: {
      withCredentials: true,
    },
    data: {
      configuration: JSON.stringify(configuration),
      columnIds: JSON.stringify(chosenVariables.map((e) => e.idx)),
    },
    success: onSuccess,
  });
}

function sendColumns(chosenColumns, configuration, onSuccess) {
  if (chosenColumns.length === 0) {
    alert("Please select attributes.");
    return;
  }

  $.ajax({
    type: "POST",
    url: backend + "/choose-options",
    xhrFields: {
      withCredentials: true,
    },
    data: {
      configuration: JSON.stringify(configuration),
      columnIds: JSON.stringify(chosenColumns.map((e) => e.idx)),
    },
    success: onSuccess,
  });
}

export default {
  sendColumns,
  sendVariableGroups,
};
