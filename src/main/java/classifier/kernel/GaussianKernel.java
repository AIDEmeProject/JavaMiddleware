package classifier.kernel;

import utils.Validator;
import utils.linalg.LinearAlgebra;

public class GaussianKernel implements Kernel {
    private double gamma;

    public GaussianKernel(double gamma) {
        Validator.assertPositive(gamma);
        this.gamma = gamma;
    }

    public GaussianKernel() {
        this.gamma = 0;
    }

    @Override
    public double compute(double[] x, double[] y) {
        double gamma = this.gamma > 0 ? this.gamma : 1.0 / x.length;
        return Math.exp(-gamma * LinearAlgebra.sqDistance(x, y));
    }
}
