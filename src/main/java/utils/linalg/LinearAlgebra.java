package utils.linalg;

import utils.Validator;

public class LinearAlgebra {
    public static double dot(double[] x, double[] y){
        Validator.assertEqualLengths(x, y);

        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += x[i] * y[i];
        }
        return sum;
    }

    public static double sqNorm(double[] x){
        return dot(x,x);
    }
}
