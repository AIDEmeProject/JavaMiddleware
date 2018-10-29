package application;

import data.DataPoint;
import data.LabeledPoint;

import java.util.ArrayList;

public interface FacadeInterface {





    /**
     *
     * @param userLabeledPoints points labeled by the user
     * @return the next points to be labeled by the user
     *
     *
     * The first step return several points (for initial Iteration)
     * and then it return either one point to label or several depending
     * on some configuration
     *
     *
     */
    public ArrayList<DataPoint> nextIteration(ArrayList<LabeledPoint> userLabeledPoints);

}
