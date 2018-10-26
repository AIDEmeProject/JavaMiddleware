package machinelearning.threesetmetric.TSM;

import data.DataPoint;
import data.LabeledPoint;
import machinelearning.classifier.Label;
import machinelearning.threesetmetric.LabelGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MultiTSMLearnerTest {
    private MultiTSMLearner multiTSMLearner;
    private Collection<LabeledPoint> labeledPoints;
    private ArrayList<int[]> featureGroup;
    private ArrayList<boolean[]> tsmFlags;

    @BeforeEach
    void setUp(){
        /**
         *    "attributes": [
         *       "length",
         *       "height",
         *       "width",
         *       "year",
         *       "price_msrp",
         *       "engine_type_diesel",
         *       "engine_type_flex_fuel_ffv",
         *       "engine_type_gas",
         *       "engine_type_hybrid"
         *
         *    Query: year >= 2016 and  length * height * width >= 15.0 and price_msrp < 100000 and engine_type_gas = 1
         *
         * 10 examples:
         * 7,1.5,2.5,2016,69390,0,0,1,0
         * 5.26796,1.9,1.94818,2017,77904,0,0,1,0
         * 5.26288,1.48082,1.94818,2017,98764,0,0,1,0
         * 5.26288,1.48082,1.94818,2017,123881,0,0,1,0
         * 5.25526,1.4605,1.89992,2017,73818,0,0,1,0
         * 5.26288,1.48082,1.94818,2016,91868,0,0,1,0
         * 6.30936,1.88214,2.00914,2017,50723,0,0,1,0
         * 5.10032,1.48082,1.905,2017,43545,0,0,1,0
         * 5.207,1.48082,1.87452,2015,67108,0,0,1,0
         * 5.2451,1.49098,1.89992,2017,82247,0,0,0,1
         */

        featureGroup = new ArrayList<>();
        featureGroup.add(new int[]{0,1,2});
        featureGroup.add(new int[]{3});
        featureGroup.add(new int[]{4});
        featureGroup.add(new int[]{5, 6, 7, 8});

        tsmFlags = new ArrayList<>();
        tsmFlags.add(new boolean[]{true,false});
        tsmFlags.add(new boolean[]{true,false});
        tsmFlags.add(new boolean[]{true,false});
        tsmFlags.add(new boolean[]{true,true});

        labeledPoints = new ArrayList<>();

        LabelGroup label_1 = new LabelGroup(new Label[]{Label.POSITIVE, Label.POSITIVE, Label.POSITIVE, Label.POSITIVE});
        labeledPoints.add(new LabeledPoint(new DataPoint(1, new double[]{7,1.5,2.5,2016,69390,0,0,1,0}), label_1));

        LabelGroup label_2 = new LabelGroup(new Label[]{Label.POSITIVE, Label.POSITIVE, Label.POSITIVE, Label.POSITIVE});
        labeledPoints.add(new LabeledPoint(new DataPoint(2, new double[]{5.26796,1.9,2.5,2017,77904,0,0,1,0}), label_2));

        LabelGroup label_3 = new LabelGroup(new Label[]{Label.POSITIVE, Label.POSITIVE, Label.POSITIVE, Label.POSITIVE});
        labeledPoints.add(new LabeledPoint(new DataPoint(3, new double[]{5.26288,1.48082,1.94818,2017,98764,0,0,1,0}), label_3));

        LabelGroup label_4 = new LabelGroup(new Label[]{Label.POSITIVE, Label.POSITIVE, Label.NEGATIVE, Label.POSITIVE});
        labeledPoints.add(new LabeledPoint(new DataPoint(4, new double[]{5.26288,1.48082,1.94818,2017,123881,0,0,1,0}), label_4));

        LabelGroup label_5 = new LabelGroup(new Label[]{Label.NEGATIVE, Label.POSITIVE, Label.POSITIVE, Label.POSITIVE});
        labeledPoints.add(new LabeledPoint(new DataPoint(5, new double[]{5.25526,1.4605,1.89992,2017,73818,0,0,1,0}), label_5));

        LabelGroup label_6 = new LabelGroup(new Label[]{Label.POSITIVE, Label.POSITIVE, Label.POSITIVE, Label.POSITIVE});
        labeledPoints.add(new LabeledPoint(new DataPoint(6, new double[]{5.26288,1.48082,1.94818,2016,91868,0,0,1,0}), label_6));

        LabelGroup label_7 = new LabelGroup(new Label[]{Label.POSITIVE, Label.POSITIVE, Label.POSITIVE, Label.POSITIVE});
        labeledPoints.add(new LabeledPoint(new DataPoint(7, new double[]{6.30936,1.88214,2.00914,2017,50723,0,0,1,0}), label_7));

        LabelGroup label_8 = new LabelGroup(new Label[]{Label.NEGATIVE, Label.POSITIVE, Label.POSITIVE, Label.POSITIVE});
        labeledPoints.add(new LabeledPoint(new DataPoint(8, new double[]{5.10032,1.48082,1.905,2017,43545,0,0,1,0}), label_8));

        LabelGroup label_9 = new LabelGroup(new Label[]{Label.NEGATIVE, Label.NEGATIVE, Label.POSITIVE, Label.POSITIVE});
        labeledPoints.add(new LabeledPoint(new DataPoint(9, new double[]{5.207,1.48082,1.87452,2015,67108,0,0,1,0}), label_9));

        LabelGroup label_10 = new LabelGroup(new Label[]{Label.NEGATIVE, Label.POSITIVE, Label.POSITIVE, Label.NEGATIVE});
        labeledPoints.add(new LabeledPoint(new DataPoint(10, new double[]{5.2451,1.49098,1.89992,2017,82247,0,0,0,1}), label_10));

        multiTSMLearner = new MultiTSMLearner(featureGroup, tsmFlags);
        multiTSMLearner.update(labeledPoints);
    }

    @Test
    void update() {
        System.out.println(multiTSMLearner.toString());
    }

    @Test
    void predict_positive() {
        assertEquals(1, multiTSMLearner.predict(new DataPoint(0, new double[]{6,1.7,2.25,2016,69390,0,0,1,0})).asSign());
    }

    @Test
    void predict_negative() {
        assertEquals(-1, multiTSMLearner.predict(new DataPoint(0, new double[]{9,1.7,2.5,2016,69390,0,0,1,0})).asSign());
    }

    @Test
    void isInConvexRegion_t() {
        assertTrue(multiTSMLearner.isInPosRegion(new DataPoint(0, new double[]{6, 1.7, 2.25, 2016, 69390, 0, 0, 1, 0})));
    }

//    @Test
//    void isInConvexRegion_f() {
//        assertEquals(false, multiTSMLearner.isInNegRegion(new DataPoint(0, new double[]{9,1.7,2.5,2016,69390,0,0,1,0})));
//    }
//
//    @Test
//    void isInConcaveRegion_t() {
//        assertEquals(true, multiTSMLearner.isInPosRegion(new DataPoint(0, new double[]{5.2451,1.49098,1.89992,2015,69390,0,0,1,0})));
//    }

//    @Test
//    void isInConcaveRegion_f() {
//        assertEquals(false, multiTSMLearner.isInConcaveRegion(new DataPoint(0, new double[]{6,1.7,2.25,2016,69390,0,0,1,0})));
//    }

    @Test
    void isRunning() {
        assertEquals(true, multiTSMLearner.isRunning());
    }

    @Test
    void factorizeFeatures() {
    }

    @Test
    void factorizeFeatures1() {
    }
}