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

package utils.linalg;

import org.ojalgo.array.Array1D;
import org.ojalgo.matrix.decomposition.Eigenvalue;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.scalar.ComplexNumber;

/**
 * This class computes the real Eigenvalue decomposition of a real, symmetric {@link Matrix}. It is basically a wrapper over
 * Apache Commons Math's EigenDecomposition class.
 */
public class EigenvalueDecomposition {
    /**
     * The computed real eigenvectors
     */
    private Vector eigenvalues;

    /**
     * The computed eigenvectors, disposed row-wise
     */
    private Matrix eigenvectors;

    /**
     * @param matrix: {@link Matrix} to compute decomposition
     * @throws IllegalArgumentException if matrix is not square, or does not have a eigenvalue decomposition over the real numbers
     */
    public EigenvalueDecomposition(Matrix matrix) {
        Eigenvalue<Double> decomposition = Eigenvalue.PRIMITIVE.make();  // TODO: use constructor (Matrix, boolean) for symmetric matrices
        decomposition.decompose(PrimitiveDenseStore.FACTORY.rows(matrix.toArray()));

        eigenvalues = getEigenvalues(decomposition);
        eigenvectors = getEigenvectors(decomposition);
    }

    private static Vector getEigenvalues(Eigenvalue<Double> decomposition) {
        Array1D<ComplexNumber> eigenvalues = decomposition.getEigenvalues();

        double[] values = eigenvalues.stream()
                .filter(x -> x.isReal())
                .mapToDouble(ComplexNumber::doubleValue)
                .toArray();

        if (values.length != eigenvalues.length) {
            System.out.println(eigenvalues);
            throw new RuntimeException("Matrix does not have real eigenvalue decomposition.");
        }

        return Vector.FACTORY.make(values);
    }

    private static Matrix getEigenvectors(Eigenvalue<Double> decomposition) {
        MatrixStore<Double> VT = decomposition.getV().transpose();
        return Matrix.FACTORY.make(VT.toRawCopy2D());
    }

    /**
     * @param index position of eigenvalue to retrieve
     * @return the index-th eigenvalue
     * @throws ArrayIndexOutOfBoundsException if index is negative or larger than or equal to matrix dimension
     */
    public double getEigenvalue(int index) {
        return eigenvalues.get(index);
    }

    /**
     * @param index position of eigenvector to retrieve
     * @return the eigenvector associated with the index-th eigenvalue
     * @throws ArrayIndexOutOfBoundsException if index is negative or larger than or equal to matrix dimension
     */
    public Vector getEigenvector(int index) {
        return eigenvectors.getRow(index);
    }
}
