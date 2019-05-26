package data;

import java.util.List;

public class FakePointInitialSamplingGenerator {


    public List<DataPoint> getFakePoint(IndexedDataset dataset){

        return dataset.sample(1).toList();
    }
}
