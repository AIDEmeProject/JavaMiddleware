package explore.sampling;

import data.DataPoint;
import data.IndexedDataset;
import explore.user.User;
import utils.Validator;

import java.util.Arrays;
import java.util.List;

public class FixedSampler implements InitialSampler {
    private final long positiveId;
    private final long[] negativeIds;
    private int id;

    public FixedSampler(long positiveId, long[] negativeIds) {
        Validator.assertNotEmpty(negativeIds);

        this.positiveId = positiveId;
        this.negativeIds = negativeIds;
        this.id = -1;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public List<DataPoint> runInitialSample(IndexedDataset unlabeledSet, User user) {
        return Arrays.asList(
                unlabeledSet.getFromIndex(positiveId),
                unlabeledSet.getFromIndex(negativeIds[id])
        );
    }

    @Override
    public String toString() {
        return "FixedSampler{" +
                "positiveId=" + positiveId +
                ", negativeIds=" + Arrays.toString(negativeIds) +
                ", id=" + id +
                '}';
    }
}
