package machinelearning.threesetmetric.TSM;

import data.DataPoint;
import data.LabeledPoint;
import javafx.scene.control.Labeled;
import machinelearning.classifier.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class CatTSMTest {
    private Collection<LabeledPoint> labeledPoints;
    private int[] indices;

    private CatTSM catTSM;



    @BeforeEach
    void setUp(){
        /**
         *  "attributes": [
         *     "price_msrp",
         *     "height",
         *     "basic_year",
         *     "body_type_convertible",
         *     "body_type_coupe",
         *     "body_type_hatchback",
         *     "body_type_minivan",
         *     "body_type_sedan",
         *     "body_type_suv",
         *     "body_type_truck",
         *     "body_type_van",
         *     "body_type_wagon"]
         * Query:
         *      "price_msrp < 26000 and (body_type_van = 1 or body_type_truck = 1) and height < 2.5 and height > 2 and basic_year > 3"
         *
         * 8 examples for test
         * 22766,2.13106,5,0,0,0,0,0,0,0,1,0,1
         * 200344,1.2827,3,0,1,0,0,0,0,0,0,0,-1
         * 36402,1.73736,3,0,0,0,1,0,0,0,0,0,-1
         * 39965,2.794,3,0,0,0,0,0,0,0,1,0,-1
         * 29386,2.08788,3,0,0,0,0,0,0,0,1,0,-1
         * 31119,2.667,5,0,0,0,0,0,0,0,1,0,-1
         * 25184,1.8923,5,0,0,0,0,0,0,1,0,0,-1
         * 22766,2.13106,5,0,0,0,0,0,0,1,0,0,1
         *
         */
        indices = new int[]{9, 10};
        labeledPoints = new ArrayList<>();
        labeledPoints.add(new LabeledPoint(new DataPoint(1, new double[]{0,0,0,0,0,0,0,1,0}), Label.POSITIVE));
        labeledPoints.add(new LabeledPoint(new DataPoint(2, new double[]{0,1,0,0,0,0,0,0,0}), Label.NEGATIVE));
        labeledPoints.add(new LabeledPoint(new DataPoint(3, new double[]{0,0,0,1,0,0,0,0,0}), Label.NEGATIVE));
        labeledPoints.add(new LabeledPoint(new DataPoint(4, new double[]{0,0,0,0,0,0,0,1,0}), Label.POSITIVE));
        labeledPoints.add(new LabeledPoint(new DataPoint(5, new double[]{0,0,0,0,1,0,0,0,0}), Label.NEGATIVE));
        labeledPoints.add(new LabeledPoint(new DataPoint(6, new double[]{0,0,0,0,0,0,0,1,0}), Label.POSITIVE));
        labeledPoints.add(new LabeledPoint(new DataPoint(7, new double[]{0,0,0,0,0,0,1,0,0}), Label.POSITIVE));
        labeledPoints.add(new LabeledPoint(new DataPoint(8, new double[]{0,0,1,0,0,0,0,0,0}), Label.NEGATIVE));
        catTSM = new CatTSM();
        catTSM.updateCat(labeledPoints);
    }

    @Test
    void updateCat_pos() {
        HashSet<Integer> posIndices = new HashSet<>(Arrays.asList(6,7));
        assertArrayEquals(posIndices.toArray(), catTSM.getTruthLines().toArray());
    }

    @Test
    void updateCat_neg() {
        HashSet<Integer> negIndices = new HashSet<>(Arrays.asList(1,2,3,4));
        assertArrayEquals(negIndices.toArray(), catTSM.getFalseLines().toArray());
    }

    @Test
    void isOnTruthLines_True() {
        assertEquals(true, catTSM.isOnTruthLines(new DataPoint(11, new double[]{0,0,0,0,0,0,1,0,0})));
    }

    @Test
    void isOnTruthLines_False() {
        assertEquals(false, catTSM.isOnTruthLines(new DataPoint(12, new double[]{0,0,0,0,0,0,0,0,1})));
    }

    @Test
    void isOnFalseLines_True() {
        assertEquals(true, catTSM.isOnFalseLines(new DataPoint(13, new double[]{0,0,0,1,0,0,0,0,0})));
    }

    @Test
    void isOnFalseLines_False() {
        assertEquals(false, catTSM.isOnFalseLines(new DataPoint(14, new double[]{0,0,0,0,0,0,0,0,1})));
    }

}