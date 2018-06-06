import data.LabeledData;
import learner.Learner;
import metrics.ConfusionMatrix;
import sampling.ReservoirSampler;
import sampling.StratifiedSampler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

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

    public Collection<HashMap<String, Double>> run(double[][] X, int[] y, Learner learner){
        return run(X, y, learner, System.currentTimeMillis());
    }

    public Collection<HashMap<String, Double>> run(double[][] X, int[] y, Learner learner, long seed){
        // set random seed
        setSeed(seed);

        // TODO: maybe we should pass the labeledData instance directly as parameter ?
        Collection<HashMap<String, Double>> metrics = new ArrayList<>();
        LabeledData data = new LabeledData(X, y);

        for (int iter = 0; iter < budget && data.getNumUnlabeledRows() > 0; iter++){
            metrics.add(runSingleIteration(data, learner));
        }

        // TODO: return labeledData object or labeled rows indexes only?
        return metrics;
    }

    private HashMap<String, Double> runSingleIteration(LabeledData data, Learner learner){
        long initialInstant, elapsedTime;
        HashMap<String, Double> metrics = new HashMap<>();

        // find next points to label
        initialInstant = System.nanoTime();
        int[] rows = getNextPointToLabel(data, learner);
        elapsedTime = System.nanoTime() - initialInstant;
        metrics.put("retrieveUnlabeledPointTimeMillis", elapsedTime / 1000000.0);

        // update labeled set
        data.addLabeledRow(rows);
        metrics.put("labeledRow", (double) rows[0]);

        // retrain model
        initialInstant = System.nanoTime();
        learner.fit(data);
        elapsedTime = System.nanoTime() - initialInstant;
        metrics.put("fitTimeMillis", elapsedTime / 1000000.0);

        metrics.put("iterationTimeMillis", metrics.get("fitTimeMillis") + metrics.get("retrieveUnlabeledPointTimeMillis"));


        // compute accuracy metrics
        initialInstant = System.nanoTime();
        int[] prediction = learner.predict(data);
        elapsedTime = System.nanoTime() - initialInstant;
        metrics.put("predictTimeMillis", elapsedTime / 1000000.0);

        ConfusionMatrix confusionMatrix = ConfusionMatrix.compute(data.getY(), prediction);
        metrics.put("truePositives", confusionMatrix.truePositives());
        metrics.put("trueNegatives", confusionMatrix.trueNegatives());
        metrics.put("falsePositives", confusionMatrix.falsePositives());
        metrics.put("falseNegatives", confusionMatrix.falseNegatives());
        metrics.put("precision", confusionMatrix.precision());
        metrics.put("recall", confusionMatrix.recall());
        metrics.put("fscore", confusionMatrix.fscore());
        return metrics;
    }

    private int[] getNextPointToLabel(LabeledData data, Learner learner){
        // initial sampling
        if (data.getNumLabeledRows() == 0){
            return initialSampler.sample(data.getY());
        }

        // retrieve most informative point according to model
        return new int[] {learner.retrieveMostInformativeUnlabeledPoint(data)};
    }
}
