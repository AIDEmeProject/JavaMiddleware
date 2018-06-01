package classifier.SVM;


/**
 * This class encapsulates the choice of kernel for SVM classifier. Four choices are avaiable: linear, polynomial, gaussian
 * (RBF), and sigmoid. In most applications, linear or gaussian kernels are enough.
 *
 * Currently we do not support precomputed or user-defined kernels.
 *
 * If gamma is not set, we use 1.0 / num_features in all computations.
 *
 * @author luciano
 */
public class Kernel {
    private KernelType kernelType = KernelType.RBF;
    private double gamma = 0;
    private double coef0 = 0;
    private int degree = 3;

    public Kernel() {
    }

    public KernelType getKernelType() {
        return kernelType;
    }

    public double getGamma() {
        return gamma;
    }

    public double getCoef0() {
        return coef0;
    }

    public int getDegree() {
        return degree;
    }

    /**
     * Sets a new kernel. Default one is RBF.
     * @param kernelType: KernelType instance representing the choice of kernel
     * @return this
     * @throws NullPointerException is kernelType is null
     */
    public Kernel kernelType(KernelType kernelType){
        if(kernelType == null){
            throw new NullPointerException("Kernel type cannot be null.");
        }
        this.kernelType = kernelType;
        return this;
    }

    /**
     * Sets a new gamma value. Default one is 1 / num_features.
     * @param gamma: gamma parameter in RBF and SIGMOID kernels
     * @return this
     * @throws IllegalArgumentException is gamma is negative
     */
    public Kernel gamma(double gamma){
        if(gamma <= 0){
            throw new IllegalArgumentException("Gamma must be a positive number.");
        }
        this.gamma = gamma;
        return this;
    }

    /**
     * Sets a new coef0 value. Default one 0.
     * @param coef0: coef0 parameter in RBF, POLY and SIGMOID kernels
     * @return this
     * @throws IllegalArgumentException is coef0 is non-positive
     */
    public Kernel coef0(double coef0){
        if(coef0 < 0){
            throw new IllegalArgumentException("Coef0 must be a non-negative number.");
        }
        this.coef0 = coef0;
        return this;
    }

    /**
     * Sets a new degree value. Default one 3.
     * @param degree: polynome degree in POLY kernel
     * @return this
     * @throws IllegalArgumentException is degree is negative
     */
    public Kernel degree(int degree){
        if(degree <= 0){
            throw new IllegalArgumentException("Degree must be a positive integer.");
        }
        this.degree = degree;
        return this;
    }

    /**
     * Computes the kernel function over x and y
     * @param x: first parameter
     * @param y: second parameter
     * @return k(x,y)
     * @throws IllegalArgumentException if x and y have different sizes
     */
    public double compute(double[] x, double[] y){
        if(x.length != y.length){
            throw new IllegalArgumentException("Both parameters must have the same dimensions!");
        }

        // use default gamma if needed
        double gamma = (this.gamma > 0) ? this.gamma : 1.0 / x.length;

        switch (this.kernelType){
            case LINEAR:
                return dot(x,y);
            case POLY:
                return Math.pow(gamma * dot(x,y) + coef0, degree);
            case RBF:
                return Math.exp(-gamma * squaredDifference(x,y));
            case SIGMOID:
                return Math.tanh(gamma * dot(x, y) + coef0);
            default:
                throw new RuntimeException("Invalid kernel type found.");
        }

    }

    private static double squaredDifference(double[] x, double[] y){
        return dot(x,x) + dot(y,y) - 2*dot(x,y);
    }

    private static double dot(double[] x, double[] y) {
        double sum = 0.0D;
        for(int i=0; i < x.length; i++) {
            sum += x[i] * y[i];
        }
        return sum;
    }
}
