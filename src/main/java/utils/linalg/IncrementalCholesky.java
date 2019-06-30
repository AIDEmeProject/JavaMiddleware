package utils.linalg;

import utils.Validator;

import java.util.ArrayList;
import java.util.List;

public class IncrementalCholesky {
    private int currentDim = 0;
    private final ArrayList<Vector> choleskyRows, inverseRows;

    public IncrementalCholesky() {
        choleskyRows = new ArrayList<>();
        inverseRows = new ArrayList<>();
    }

    public int getCurrentDim() {
        return currentDim;
    }

    public void increment(double... values) {
        increment(Vector.FACTORY.make(values));
    }

    public void increment(Vector point) {
        Validator.assertEquals(point.dim(), currentDim + 1);

        updateCholesky(point);
        updateInverse();

        currentDim++;
    }

    private void updateCholesky(Vector point) {
        Vector row = Vector.FACTORY.zeroslike(point);

        int col = 0;
        for (Vector cholRow: choleskyRows) {
            double sum = point.get(col);

            for (int k = 0; k < col; k++) {
                sum -= row.get(k) * cholRow.get(k);
            }

            row.set(col, sum / cholRow.get(col));

            col++;
        }

        double value = point.get(col) - row.squaredNorm();

        if (value <= 0) {
            throw new RuntimeException("Matrix is not positive definite");
        }

        row.set(col, Math.sqrt(value));
        choleskyRows.add(row);
    }

    private void updateInverse() {
        Vector row = Vector.FACTORY.zeros(currentDim + 1);

        Vector chol = choleskyRows.get(currentDim);
        double scale = 1.0 / chol.get(currentDim);
        row.set(currentDim, scale);

        for (int j = 0; j < currentDim; j++) {
            double sum = 0;

            for (int r = j; r < currentDim; r++) {
                sum += chol.get(r) * inverseRows.get(r).get(j);
            }

            row.set(j, -sum * scale);
        }

        inverseRows.add(row);
    }


    public Matrix getL() {
        return parseList(choleskyRows);
    }

    public Matrix getInverse() {
        return parseList(inverseRows);
    }

    private Matrix parseList(List<Vector> list) {
        Validator.assertPositive(currentDim);

        Matrix mat = Matrix.FACTORY.zeros(currentDim, currentDim);

        for (int i = 0; i < currentDim; i++) {
            for (int j = 0; j <= i; j++) {
                mat.set(i, j, list.get(i).get(j));
            }
        }

        return mat;
    }

}
