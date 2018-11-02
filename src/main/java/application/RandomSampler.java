package application;

import data.DataPoint;
import data.PartitionedDataset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class RandomSampler {


    private final PartitionedDataset dataset;

    public RandomSampler(PartitionedDataset dataset) {

        this.dataset = dataset;
    }

    public ArrayList<DataPoint> getPoints(int nPoint){

        ArrayList<DataPoint> pointsToLabel = new ArrayList<>();

        //ArrayList<DataPoint> copy = new ArrayList<>(Arrays.asList(this.dataset.getUnlabeledPoints()));
        //Collections.shuffle(copy);

        for (int i=0;i<nPoint;i++){

            int random = i;
            pointsToLabel.add(this.dataset.getUnlabeledPoints().get(i));
        }

        return pointsToLabel;
    }
}
