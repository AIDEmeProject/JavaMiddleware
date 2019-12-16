/*
 * Copyright (c) 2019 École Polytechnique
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, you can obtain one at http://mozilla.org/MPL/2.0
 *
 * Authors:
 *       Luciano Di Palma <luciano.di-palma@polytechnique.edu>
 *       Enhui Huang <enhui.huang@polytechnique.edu>
 *       Laurent Cetinsoy <laurent.cetinsoy@gmail.com>
 *
 * Description:
 * AIDEme is a large-scale interactive data exploration system that is cast in a principled active learning (AL) framework: in this context,
 * we consider the data content as a large set of records in a data source, and the user is interested in some of them but not all.
 * In the data exploration process, the system allows the user to label a record as “interesting” or “not interesting” in each iteration,
 * so that it can construct an increasingly-more-accurate model of the user interest. Active learning techniques are employed to select
 * a new record from the unlabeled data source in each iteration for the user to label next in order to improve the model accuracy.
 * Upon convergence, the model is run through the entire data source to retrieve all relevant records.
 */

package machinelearning.classifier;


import data.IndexedDataset;
import utils.linalg.Matrix;
import utils.linalg.Vector;

/**
 * A classifier is any object capable of "learning from training data" and "make predictions for new data points".
 *
 * @author luciano
 */
public interface Classifier {

    /**
     * @param vector: a feature vector
     * @return probability of vector belonging to the positive class
     */
    double probability(Vector vector);

    /**
     * @param matrix: a matrix whose every line corresponds to a feature vector
     * @return class probability estimation for each row of the matrix
     */
    default Vector probability(Matrix matrix) {
        double[] probas = new double[matrix.rows()];
        for (int i = 0; i < probas.length; i++) {
            probas[i] = probability(matrix.getRow(i));
        }
        return Vector.FACTORY.make(probas);
    }

    default Vector probability(IndexedDataset dataset) {
        return probability(dataset.getData());
    }

    /**
     * @param vector: a feature vector
     * @return predicted label for the input vector
     */
    default Label predict(Vector vector){
        return probability(vector) > 0.5 ? Label.POSITIVE : Label.NEGATIVE;
    }

    /**
     * @param matrix:  a matrix whose every line corresponds to a feature vector
     * @return predicted class labels for each row of the matrix
     */
    default Label[] predict(Matrix matrix) {
        Label[] labels = new Label[matrix.rows()];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = predict(matrix.getRow(i));
        }
        return labels;
    }

    default Label[] predict(IndexedDataset dataset) {
        return predict(dataset.getData());
    }
}
