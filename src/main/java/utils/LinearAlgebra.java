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

package utils;

import org.apache.commons.math3.linear.FieldLUDecomposition;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.util.BigReal;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class provides several common methods in Linear Algebra
 */

public class LinearAlgebra {

    public static BigReal getDeterminantBigReal(double[][] a){
        //TODO: think of a better way to avoid using BigReal
        BigReal[][] bigA = new BigReal[a.length][];
        for(int i = 0; i < a.length; i++){
            for(int j=0; j < a[0].length; j++){
                bigA[i][j] = new BigReal(a[i][j]);
            }
        }
        FieldMatrix<BigReal> bigRealMatrix = MatrixUtils.createFieldMatrix(bigA);
        return (new FieldLUDecomposition<BigReal>(bigRealMatrix).getDeterminant());
    }


    public static double[][] invertMatrix(double[][] a) throws IOException {
        int n = a.length;
        double x[][] = new double[n][n];
        double b[][] = new double[n][n];
        int index[] = new int[n];
        for (int i = 0; i < n; ++i)
            b[i][i] = 1;

        // Transform the matrix into an upper triangle
        gaussian(a, index);

        // Update the matrix b[i][j] with the ratios stored
        for (int i = 0; i < n - 1; ++i)
            for (int j = i + 1; j < n; ++j)
                for (int k = 0; k < n; ++k)
                    b[index[j]][k] -= a[index[j]][i] * b[index[i]][k];

        // Perform backward substitutions
        for (int i = 0; i < n; ++i) {
            if (a[index[n - 1]][n - 1] == 0) {
                throw new IOException("not invertible"); // Todo: Add a proper Exception here
            }
            x[n - 1][i] = b[index[n - 1]][i] / a[index[n - 1]][n - 1];
            for (int j = n - 2; j >= 0; --j) {
                x[j][i] = b[index[j]][i];
                for (int k = j + 1; k < n; ++k) {
                    x[j][i] -= a[index[j]][k] * x[k][i];
                }
                if (a[index[j]][j] == 0) {
                    throw new IOException("not invertible"); // Todo: Add a proper Exception here
                }
                x[j][i] /= a[index[j]][j];
            }
        }
        return x;
    }

    private static void gaussian(double a[][], int index[]) {
        int n = index.length;
        double c[] = new double[n];

        // Initialize the index
        for (int i = 0; i < n; ++i)
            index[i] = i;

        // Find the rescaling factors, one from each row
        for (int i = 0; i < n; ++i) {
            double c1 = 0;
            for (int j = 0; j < n; ++j) {
                double c0 = Math.abs(a[i][j]);
                if (c0 > c1) c1 = c0;
            }
            c[i] = c1;
        }

        // Search the pivoting element from each column
        int k = 0;
        for (int j = 0; j < n - 1; ++j) {
            double pi1 = 0;
            for (int i = j; i < n; ++i) {
                double pi0 = Math.abs(a[index[i]][j]);
                pi0 /= c[index[i]];
                if (pi0 > pi1) {
                    pi1 = pi0;
                    k = i;
                }
            }

            // Interchange rows according to the pivoting order
            int itmp = index[j];
            index[j] = index[k];
            index[k] = itmp;
            for (int i = j + 1; i < n; ++i) {
                double pj = a[index[i]][j] / a[index[j]][j];

                // Record pivoting ratios below the diagonal
                a[index[i]][j] = pj;

                // Modify other elements accordingly
                for (int l = j + 1; l < n; ++l)
                    a[index[i]][l] -= pj * a[index[j]][l];
            }
        }
    }

    public static double[] multiply(double[][] matrix, double[] vector) throws IOException {
        if (matrix[0].length != vector.length) {
            throw new IOException("cannot multiply a " + matrix.length + "x" + matrix[0].length + " matrix with a " + vector.length + "x1 vector"); // Todo: Add a proper Exception here
        }
        double[] res = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            res[i] = 0;
            for (int j = 0; j < matrix[0].length; j++) {
                res[i] = res[i] + matrix[i][j] * vector[j];
            }
        }
        return res;
    }

    public static double determinant(ArrayList<ArrayList<Double>> array) throws IOException {
        double[][] lu = new double[array.size()][array.size()];
        for (int i = 0; i < array.size(); i++) {
            for (int j = 0; j < array.size(); j++) {
                if (i >= j) {
                    lu[i][j] = array.get(i).get(j);
                } else {
                    lu[i][j] = array.get(j).get(i);
                }
            }
        }
        return determinant(lu);
    }

    public static double determinant(double[][] lu) {
        if (lu.length != lu[0].length)
            throw new IllegalArgumentException("matrix need to be square."); // Todo: Add a proper Exception here

        final int m = lu.length;
        int[] pivot = new int[m];

        // Initialize permutation array and parity
        for (int row = 0; row < m; row++) {
            pivot[row] = row;
        }
        boolean even = true;

        // Loop over columns
        for (int col = 0; col < m; col++) {

            // upper
            for (int row = 0; row < col; row++) {
                final double[] luRow = lu[row];
                double sum = luRow[col];
                for (int i = 0; i < row; i++) {
                    sum -= luRow[i] * lu[i][col];
                }
                luRow[col] = sum;
            }

            // lower
            int max = col; // permutation row
            double largest = Double.NEGATIVE_INFINITY;
            for (int row = col; row < m; row++) {
                final double[] luRow = lu[row];
                double sum = luRow[col];
                for (int i = 0; i < col; i++) {
                    sum -= luRow[i] * lu[i][col];
                }
                luRow[col] = sum;

                // maintain best permutation choice
                if (Math.abs(sum) > largest) {
                    largest = Math.abs(sum);
                    max = row;
                }
            }

            // Singularity check
            if (Math.abs(lu[max][col]) < 1e-11) {
                return 0;
            }

            // Pivot if necessary
            if (max != col) {
                double tmp;
                final double[] luMax = lu[max];
                final double[] luCol = lu[col];
                for (int i = 0; i < m; i++) {
                    tmp = luMax[i];
                    luMax[i] = luCol[i];
                    luCol[i] = tmp;
                }
                int temp = pivot[max];
                pivot[max] = pivot[col];
                pivot[col] = temp;
                even = !even;
            }

            // Divide the lower elements by the "winning" diagonal elt.
            final double luDiag = lu[col][col];
            for (int row = col + 1; row < m; row++) {
                lu[row][col] /= luDiag;
            }
        }


        double determinant = even ? 1 : -1;
        for (int i = 0; i < m; i++) {
            determinant *= lu[i][i];
        }
        return determinant;

    }

    private static double getDeterminant(double[][] matrix) throws IOException {
        if (matrix.length != matrix[0].length)
            throw new IOException("matrix need to be square."); // Todo: Add a proper Exception here
        int n = matrix.length;
        if (n == 1) {
            return matrix[0][0];
        }
        if (n == 2) {
            return (matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]);
        }
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            sum += ((i & 1) == 0 ? 1 : -1) * matrix[0][i] * getDeterminant(createSubMatrix(matrix, 0, i));
        }
        return sum;
    }

    public static double[][] createSubMatrix(double[][] matrix, int excluding_row, int excluding_col) {
        double[][] mat = new double[matrix.length - 1][matrix[0].length - 1];
        int r = -1;
        for (int i = 0; i < matrix.length; i++) {
            if (i == excluding_row)
                continue;
            r++;
            int c = -1;
            for (int j = 0; j < matrix[0].length; j++) {
                if (j == excluding_col)
                    continue;
                mat[r][++c] = matrix[i][j];
            }
        }
        return mat;
    }
}
