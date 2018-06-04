import data.LabeledData;
import learner.Learner;
import sampling.ReservoirSampler;
import sampling.StratifiedSampler;

import java.util.Collection;

public class Explore {

    private int budget;
    private StratifiedSampler initialSampler;

    public Explore(StratifiedSampler initialSampler, int budget) {
        if (initialSampler == null){
            throw new NullPointerException("Initial Sampler cannot be null.");
        }

        if (budget <= 0){
            throw new IllegalArgumentException("Budget must be a positive number.");
        }

        this.initialSampler = initialSampler;
        this.budget = budget;
    }

    private void setSeed(long seed){
        ReservoirSampler.setSeed(seed);
    }

    public Collection<Integer> run(double[][] X, int[] y, Learner learner){
        return run(X, y, learner, System.currentTimeMillis());
    }

    public Collection<Integer> run(double[][] X, int[] y, Learner learner, long seed){
        // set random seed
        setSeed(seed);

        // TODO: maybe we should pass the labeledData instance directly as parameter ?
        LabeledData data = new LabeledData(X, y);

        // initial sampling: one negative and one positive random samples
        for (int row : initialSampler.sample(y)){
            data.addLabeledRow(row);
        }

        // fit model to initial sample
        learner.fit(data);

        for (int iter = 0; iter < budget && data.getNumUnlabeledRows() > 0; iter++){
            // find next point to label
            int row = learner.retrieveMostInformativeUnlabeledPoint(data);
            data.addLabeledRow(row);

            // retrain model
            learner.fit(data);
        }

        // TODO: return labeledData object or labeled rows indexes only?
        return data.getLabeledRows();
    }
}
