package application;

import data.DataPoint;

import java.util.ArrayList;

public class RandomSampler {


    public ArrayList<DataPoint> getPoints(){

        ArrayList<DataPoint> pointsToLabel = new ArrayList<>();

        double[] data = {1, 2};
        pointsToLabel.add((new DataPoint(1, data)));
        return pointsToLabel;
    }
}
