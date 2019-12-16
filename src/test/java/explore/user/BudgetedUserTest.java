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
import data.IndexedDataset;
import machinelearning.classifier.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BudgetedUserTest {
    private BudgetedUser budgetedUser;
    private final User user = mock(User.class);
    private final int budget = 10;
    private final DataPoint dataPoint = new DataPoint(0, Vector.FACTORY.make(0, 1, 2));

    @BeforeEach
    void setUp() {
        when(user.getLabel(dataPoint)).thenReturn(Label.POSITIVE);
        budgetedUser = new BudgetedUser(user, budget);
    }

    @Test
    void constructor_nullUser_throwsException() {
        assertThrows(NullPointerException.class, () -> new BudgetedUser(null, 1));
    }

    @Test
    void constructor_zeroBudget_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new BudgetedUser(user, 0));
    }

    @Test
    void constructor_negativeBudget_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new BudgetedUser(user, -1));
    }

    @Test
    void getNumberOfLabeledPoints_noLabeledPointsSoFar_returnsZero() {
        assertEquals(0, budgetedUser.getNumberOfLabeledPoints());
    }

    @Test
    void getNumberOfLabeledPoints_lessThanBudgetPointsLabeled_returnsExpectedNumber() {
        for (int i = 0; i < budget; i++) {
            budgetedUser.getLabel(dataPoint);
            assertEquals(i+1, budgetedUser.getNumberOfLabeledPoints());
        }
    }

    @Test
    void isWilling_lessThanBudgetPointsLabeled_returnsTrue() {
        for (int i = 0; i < budget; i++) {
            assertTrue(budgetedUser.isWilling());
            budgetedUser.getLabel(dataPoint);
        }
    }

    @Test
    void isWilling_budgetPointsWereLabeled_returnsFalse() {
        for (int i = 0; i < budget; i++) {
            budgetedUser.getLabel(dataPoint);
        }
        assertFalse(budgetedUser.isWilling());
    }

    @Test
    void getLabel_stubbedUserAlwaysReturnsPositive_returnsPositive() {
        assertEquals(Label.POSITIVE, budgetedUser.getLabel(dataPoint));
    }

    @Test
    void getLabel_attemptToLabelPastBudget_throwsException() {
        for (int i = 0; i < budget; i++) {
            budgetedUser.getLabel(dataPoint);
        }
        assertThrows(IllegalStateException.class, () -> budgetedUser.getLabel(dataPoint));
    }

    @Test
    void getLabel_dataPointCollectionLargerThanBudget_throwsException() {
        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        for (int i = 0; i < budget+1; i++) {
            builder.add(dataPoint);
        }

        assertThrows(IllegalStateException.class, () -> budgetedUser.getLabel(builder.build()));
    }
}