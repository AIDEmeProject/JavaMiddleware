package utils.linalg;

import utils.Validator;

public class LinearAlgebra {
    public static double[] normalize(double[] x, double newNorm) {
        Validator.assertPositive(newNorm);

        double norm = norm(x);
        Validator.assertPositive(norm);

        double[] result = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            result[i] = newNorm * x[i] / norm;
        }

        return result;
    }

    public static double dot(double[] x, double[] y){
        Validator.assertEqualLengths(x, y);

        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += x[i] * y[i];
        }
        return sum;
    }

    public static double norm(double[] x){
        return Math.sqrt(sqNorm(x));
    }

    public static double sqNorm(double[] x){
        return dot(x,x);
    }

    public static double sqDistance(double[] x, double[] y){
        return dot(x, x) + dot(y, y) - 2 * dot(x, y);
    }

    public static double[] truncateOrPaddleWithZeros(double[] values, int size) {
        if (values.length == size){
            return values;
        }

        double[] result = new double[size];
        System.arraycopy(values, 0, result, 0, Math.min(values.length, size));

        return result;
    }
}
