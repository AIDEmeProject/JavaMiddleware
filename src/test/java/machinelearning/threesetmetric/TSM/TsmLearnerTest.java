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

package machinelearning.threesetmetric.TSM;

import data.DataPoint;
import data.LabeledPoint;
import machinelearning.classifier.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TsmLearnerTest {
    private TsmLearner tsmLearner;
    private TsmLearner tsmLearner_neg;
    private Collection<LabeledPoint> labeledPoints;
    private int dim;



    @BeforeEach
    void setUp(){
        /**
         * "attributes":["rowc","colc"]
         *
         * Query:
         *      (rowc-682.5)^2/4+(colc-1022.5)^2/9<37^2
         * 8 examples for test
         * 1521593981966: 1237646751433819640,732.906982,1096.43994,1,0, init
         * 1521593981968: 1237652997939462754,632.432007,450.998993,-1,0, init
         * 1521593982077: 1237651496837775756,196.979004,923.468994,-1,0, usr
         * 1521593982383: 1237648674530001499,328.699005,1666.70996,-1,0, usr
         * 1521593982440: 1237655463784284444,1028.14001,1807.05005,-1,0, usr
         * 1521593982739: 1237656236322456217,732.708008,1583.72998,-1,0, usr
         * 1521593982792: 1237652954969211476,1223.82996,948.091003,-1,0, usr
         * 1521593983083: 1237649806759428162,425.289001,887.221008,-1,0, usr
         * 1521593986137: 1237657613407945434,668.385986,960.786011,1,0, usr
         * 1521593986346: 1237646706341970533,626.927002,1030.94995,1,0, usr
         *
         */
        dim = 2;
        labeledPoints = new ArrayList<>();
        labeledPoints.add(new LabeledPoint(new DataPoint(1, new double[]{732.906982,1096.43994}), Label.POSITIVE));
        labeledPoints.add(new LabeledPoint(new DataPoint(2, new double[]{632.432007,450.998993}), Label.NEGATIVE));
        labeledPoints.add(new LabeledPoint(new DataPoint(3, new double[]{196.979004,923.468994}), Label.NEGATIVE));
        labeledPoints.add(new LabeledPoint(new DataPoint(4, new double[]{328.699005,1666.70996}), Label.NEGATIVE));
        labeledPoints.add(new LabeledPoint(new DataPoint(5, new double[]{1028.14001,1807.05005}), Label.NEGATIVE));
        labeledPoints.add(new LabeledPoint(new DataPoint(6, new double[]{732.708008,1583.72998}), Label.NEGATIVE));
        labeledPoints.add(new LabeledPoint(new DataPoint(7, new double[]{1223.82996,948.091003}), Label.NEGATIVE));
        labeledPoints.add(new LabeledPoint(new DataPoint(8, new double[]{425.289001,887.221008}), Label.NEGATIVE));
        labeledPoints.add(new LabeledPoint(new DataPoint(9, new double[]{668.385986,960.786011}), Label.POSITIVE));
        labeledPoints.add(new LabeledPoint(new DataPoint(10, new double[]{626.927002,1030.94995}), Label.POSITIVE));

        tsmLearner = new TsmLearner(dim);
        tsmLearner.updatePosRatio(labeledPoints);

        tsmLearner_neg = new TsmLearner(dim);
        Collection<LabeledPoint> negLabeledPoints = new ArrayList<>();
        for(LabeledPoint point : labeledPoints){
            negLabeledPoints.add(new LabeledPoint(new DataPoint(point.getId(), point.getData().toArray()), point.getLabel().isPositive()? Label.NEGATIVE: Label.POSITIVE));
        }
        tsmLearner_neg.updateNegRatio(negLabeledPoints);
    }

    @Test
    void isUsefulSample_true() {
        DataPoint dataPoint = new DataPoint(0, new double[]{733, 1096});
        assertEquals(true, tsmLearner.isUsefulSample(dataPoint, true));
    }

    @Test
    void isUsefulSample_inConvexRegion() {
        DataPoint dataPoint = new DataPoint(-1, new double[]{682.5, 1022.5});
        assertEquals(false, tsmLearner.isUsefulSample(dataPoint, true));
    }

    @Test
    void isUsefulSample_inConcaveRegion() {
        DataPoint dataPoint = new DataPoint(-2, new double[]{732.708008, 1586});
        assertEquals(false, tsmLearner.isUsefulSample(dataPoint, true));
    }

    @Test
    void isInConvexRegion_true() {
        DataPoint dataPoint = new DataPoint(11, new double[]{682.5, 1022.5});
        assertEquals(true, tsmLearner.isInConvexRegion(dataPoint, true));
    }

    @Test
    void isInConvexRegion_false() {
        DataPoint dataPoint = new DataPoint(12, new double[]{0.5, 0.5});
        assertEquals(false, tsmLearner.isInConvexRegion(dataPoint, true));
    }

    @Test
    void isInConcaveRegion_true() {
        DataPoint dataPoint = new DataPoint(13, new double[]{732.708008, 1586});
        assertEquals(true, tsmLearner.isInConcaveRegion(dataPoint, true));
    }

    @Test
    void isInConcaveRegion_false() {
        DataPoint dataPoint = new DataPoint(14, new double[]{0.5, 0.5});
        assertEquals(false, tsmLearner.isInConcaveRegion(dataPoint, true));
    }

    @Test
    void findDuplicates_array_t() {
        double[][] arrays = new double[labeledPoints.size()][];
        int index = 0;
        for(LabeledPoint labeledPoint: labeledPoints){
            arrays[index] = labeledPoint.getData().toArray();
            index++;
        }

        assertEquals(true, TsmLearner.findDuplicates(arrays, new double[]{1028.14001,1807.05005}));
    }

    @Test
    void findDuplicates_hashSet_t() {
        HashSet<double[]> arrays = new HashSet<>();
        int index = 0;
        for(LabeledPoint labeledPoint: labeledPoints){
            arrays.add(labeledPoint.getData().toArray());
            index++;
        }

        assertEquals(true, TsmLearner.findDuplicates(arrays, new double[]{1028.14001,1807.05005}));
    }

    @Test
    void findDuplicates_array_f() {
        double[][] arrays = new double[labeledPoints.size()][];
        int index = 0;
        for(LabeledPoint labeledPoint: labeledPoints){
            arrays[index] = labeledPoint.getData().toArray();
            index++;
        }

        assertEquals(false, TsmLearner.findDuplicates(arrays, new double[]{1028,1807}));
    }

    @Test
    void findDuplicates_hashSet_f() {
        HashSet<double[]> arrays = new HashSet<>();
        int index = 0;
        for(LabeledPoint labeledPoint: labeledPoints){
            arrays.add(labeledPoint.getData().toArray());
            index++;
        }

        assertEquals(false, TsmLearner.findDuplicates(arrays, new double[]{1028,1807}));
    }

    @Test
    void getConvexConesVertices() {
        HashSet<double[]> vertices = new HashSet<>();
        tsmLearner.getConvexConesVertices(tsmLearner.getConcaveRegion(), vertices);
    }

    @Test
    void getConvexHullVertices() {
        HashSet<double[]> vertices = new HashSet<>();
        tsmLearner.getConvexHullVertices(tsmLearner.getConvexRegion(), vertices);
    }

}