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

package machinelearning.active.learning.versionspace.manifold.direction;

import machinelearning.active.learning.versionspace.manifold.Manifold;
import machinelearning.active.learning.versionspace.manifold.euclidean.EuclideanSpace;
import machinelearning.active.learning.versionspace.manifold.sphere.UnitSphere;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RandomDirectionSamplerTest {
    private Manifold manifold;
    private RandomDirectionSampler randomDirectionSampler;

    @BeforeEach
    void setUp() {
        manifold = mock(Manifold.class);
        randomDirectionSampler = new RandomDirectionSampler(manifold);
    }

    @Test
    void constructor_nullManifold_throwsException() {
        assertThrows(NullPointerException.class, () -> new RandomDirectionSampler(null));
    }

    @Test
    void sampleDirection_mockedParameters_sampleVelocityCalledWithInputParameters() {
        Vector point = mock(Vector.class);
        Random rand = mock(Random.class);

        randomDirectionSampler.sampleDirection(point, rand);

        verify(manifold).sampleVelocity(point, rand);
    }

    @Test
    void equals_compareWithNull_returnsFalse() {
        assertNotEquals(randomDirectionSampler, null);
    }

    @Test
    void equals_distinctManifolds_returnsFalse() {
        Manifold m1 = EuclideanSpace.getInstance(), m2 = UnitSphere.getInstance();
        assertNotEquals(new RandomDirectionSampler(m1), new RandomDirectionSampler(m2));
    }

    @Test
    void equals_sameManifolds_returnsTrue() {
        Manifold m1 = EuclideanSpace.getInstance(), m2 = EuclideanSpace.getInstance();
        assertEquals(new RandomDirectionSampler(m1), new RandomDirectionSampler(m2));
    }
}