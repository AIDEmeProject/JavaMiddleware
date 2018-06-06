import data.LabeledData;
import learner.Learner;
import learner.TimedLearner;
import metrics.ConfusionMatrix;
import sampling.ReservoirSampler;
import sampling.StratifiedSampler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

    public Collection<Map<String, Double>> run(double[][] X, int[] y, Learner learner){
        return run(X, y, learner, System.currentTimeMillis());
    }

    public Collection<Map<String, Double>> run(double[][] X, int[] y, Learner learner, long seed){
        // set random seed
        setSeed(seed);

        // TODO: maybe we should pass the labeledData instance directly as parameter ?
        Collection<Map<String, Double>> metrics = new ArrayList<>();
        LabeledData data = new LabeledData(X, y);

        for (int iter = 0; iter < budget && data.getNumUnlabeledRows() > 0; iter++){
            metrics.add(runSingleIteration(data, learner));
        }

        // TODO: return labeledData object or labeled rows indexes only?
        return metrics;
    }

    private Map<String, Double> runSingleIteration(LabeledData data, Learner learner){
        Map<String, Double> metrics = new HashMap<>();
        learner = new TimedLearner(learner, metrics);  // Apply timing decorator

        // find next points to label
        int[] rows = getNextPointToLabel(data, learner);

        // update labeled set
        data.addLabeledRow(rows);
        metrics.put("labeledRow", (double) rows[0]);

        // retrain model
        learner.fit(data);

        // compute accuracy metrics
        int[] prediction = learner.predict(data);

        ConfusionMatrix confusionMatrix = new ConfusionMatrix();
        confusionMatrix.fit(data.getY(), prediction);
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
