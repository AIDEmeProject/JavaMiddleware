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

package explore.user;

import data.DataPoint;
import machinelearning.classifier.Label;
import machinelearning.threesetmetric.LabelGroup;
import utils.Validator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User for the conjunctive query scenario. In this case, the user must provide a collection of partial labels, which will
 * be pieced together in a final label through a CONJUNCTION of the partial labels.
 */
public class FactoredUser implements User {
    private final List<UserStub> partialUsers;

    /**
     * @param listOfPositiveKeys: list of all positive keys in each subspace
     * @throws IllegalArgumentException if input is empty
     */
    public FactoredUser(List<Set<Long>> listOfPositiveKeys) {
        Validator.assertNotEmpty(listOfPositiveKeys);
        this.partialUsers = listOfPositiveKeys.stream()
                .map(UserStub::new)
                .collect(Collectors.toList());
    }

    @Override
    public LabelGroup getLabel(DataPoint point) {
        return new LabelGroup(
                partialUsers.stream()
                        .map(x -> x.getLabel(point))
                        .toArray(Label[]::new)
        );
    }
}
