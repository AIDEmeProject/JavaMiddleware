package learner;

import classifier.NearestNeighborsClassifier;
import data.LabeledData;

public class ActiveTreeSearch implements Learner {
    private NearestNeighborsClassifier knn;
    private int lookahead;

    public ActiveTreeSearch(NearestNeighborsClassifier knn, int lookahead) {
        if (lookahead < 1){
            throw new IllegalArgumentException("Lookahead must be a positive number.");
        }
        this.knn = knn;
        this.lookahead = lookahead;
    }

    @Override
    public void fit(LabeledData data) {
        knn.fit(data);
    }

    private class UtilityResult {
        int index;
        double utility;

        UtilityResult(int index, double utility) {
            this.index = index;
            this.utility = utility;
        }
    }

    private double optimalUtilityGivenPoint(LabeledData data, int steps, int rowNumber, double proba){
        if (steps <= 1){
            return proba;
        }

        // store label
        int label = data.getLabel(rowNumber);

        // add rowNumber to labeled set
        data.addLabeledRow(rowNumber);

        // positive label branch
        data.setLabel(rowNumber, 1);
        double positiveUtility = utility(data, steps-1).utility;

        // negative label branch
        data.setLabel(rowNumber, 0);
        double negativeUtility = utility(data, steps-1).utility;

        // restore previous state
        data.setLabel(rowNumber, label);
        data.removeLabeledRow(rowNumber);

        return (positiveUtility + 1) * proba + negativeUtility * (1 - proba);
    }

    private double optimalUtilityUpperBound(int steps, int maxLabeledPoints){
        double pStar = knn.pStar(maxLabeledPoints);

        if (steps <= 1){
            return pStar;
        }

        double positiveUpperBound = optimalUtilityUpperBound(steps - 1, maxLabeledPoints + 1);
        double negativeUpperBound = optimalUtilityUpperBound(steps - 1, maxLabeledPoints);

        return (positiveUpperBound + 1) * pStar + negativeUpperBound * (1 - pStar);
    }

    private UtilityResult utility(LabeledData data, int steps){
        knn.fit(data);
        double[] probas = knn.probability(data);

        double optimalUtility = Double.NEGATIVE_INFINITY;
        int optimalRow = -1;

        for (int i = 0; i < data.getNumRows(); i++) {
            if (data.rowIsLabeled(i)) {
                continue;
            }

            if (probas[i] > optimalUtility){
                optimalUtility = probas[i];
                optimalRow = i;
            }
        }

        //System.out.println("max proba: " + optimalUtility);
        //System.out.println("max proba index: " + optimalRow);

        if (steps <= 1){
            return new UtilityResult(optimalRow, optimalUtility);
        }

        optimalUtility = optimalUtilityGivenPoint(data, steps, optimalRow, probas[optimalRow]);

        double u0 = optimalUtilityUpperBound(steps-1, 0);
        double u1 = optimalUtilityUpperBound(steps-1, 1);

        System.out.println("lr: " + data.getNumLabeledRows());
        System.out.println("opt util: " + optimalUtility);
        System.out.println("u0 = " + u0 + ", u1 = " + u1);

        int count = 0;
        for (int row = 0; row < data.getNumRows(); row++) {
            if (data.rowIsLabeled(row)){
                continue;
            }
            //System.out.println("opt util: " + optimalUtility);
            //System.out.println("bound: " + ((u1 + 1) * probas[row] + u0 * (1 - probas[row])));
            if ((u1 + 1) * probas[row] + u0 * (1 - probas[row]) <= optimalUtility){
                count++;
                continue;
            }

            double util = optimalUtilityGivenPoint(data, steps, row, probas[row]);
            //System.out.println("util: " + util);
            if (util > optimalUtility){
                optimalUtility = util;
                optimalRow = row;
            }
        }
        System.out.print(count);
        System.out.print(", ");
        System.out.println((double) count / data.getNumUnlabeledRows());
        System.out.println();
        return new UtilityResult(optimalRow, optimalUtility);
    }

    @Override
    public double rank(LabeledData data, int rowNumber) {
        return 0;
    }

    @Override
    public int getNext(LabeledData data) {

        int steps = Math.min(data.getNumUnlabeledRows(), this.lookahead);

        if (steps == 0){
            throw new RuntimeException("Entire dataset was labeled, cannot get next point!");
        }

        return utility(data, steps).index;
    }
}
