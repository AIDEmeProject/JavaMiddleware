package classifier.SVM;

import libsvm.svm_parameter;

/**
 * Adaptor class for svm_parameter module in LibSVM. It constructs an svm_parameter object through the Builder pattern.
 * Default values for parameters are the same used in SkLearn Python library.
 * Right now, we only support binary classification scenario.
 *
 * @see <a href="https://www.csie.ntu.edu.tw/~r94100/libsvm-2.8/README">LibSVM parameters reference</a>
 * @author luciano
 */
public class SvmParameterAdapter {
    private double C              = 1.0;
    private Kernel kernel         = new Kernel();
    private int cacheSize         = 100;
    private double tolerance      = 1e-3;
    private boolean shrinking     = true;
    private boolean probability   = false;
    private double[] classWeights = new double[0];

    public SvmParameterAdapter() {
    }

    /**
     * Set SVM penalty parameter, C. Default value is 1.0
     * @param C: new C parameter
     * @return this
     * @throws IllegalArgumentException if C is negative
     */
    public SvmParameterAdapter C(double C){
        if(C <= 0){
            throw new IllegalArgumentException("C must be a positive number.");
        }
        this.C = C;
        return this;
    }

    /**
     * Set SVM kernel. Default is RBF, with gamma = 1 / num_features
     * @param kernel: new kernel
     * @return this
     */
    public SvmParameterAdapter kernel(Kernel kernel){
        if(kernel == null){
            throw new NullPointerException("Kernel cannot be null.");
        }
        this.kernel = kernel;
        return this;
    }

    /**
     * Set cache size for Svm solver. Default value is 100 MB.
     * @param cacheSize: new cache size to use (in MB)
     * @return this
     * @throws IllegalArgumentException if cacheSize is negative
     */
    public SvmParameterAdapter cacheSize(int cacheSize){
        if(cacheSize <= 0){
            throw new IllegalArgumentException("Cache size must be a positive number.");
        }
        this.cacheSize = cacheSize;
        return this;
    }

    /**
     * Set SVM solver tolerance. Default value is 0.001 .
     * @param tolerance: new solver tolerance
     * @return this
     * @throws IllegalArgumentException if tolerance is negative
     */
    public SvmParameterAdapter tolerance(double tolerance){
        if(tolerance <= 0){
            throw new IllegalArgumentException("Tolerance must be a positive number.");
        }
        this.tolerance = tolerance;
        return this;
    }

    /**
     * Whether to use the shrinking heuristic when training the SVM, which can significantly reduce training time.
     * Default value is true.
     * @param shrinking: use shrinking?
     * @return this
     */
    public SvmParameterAdapter shrinking(boolean shrinking){
        this.shrinking = shrinking;
        return this;
    }

    /**
     * Whether to compute probability class estimates. Default value is false.
     * @param probability: compute probability estimates?
     * @return this
     */
    public SvmParameterAdapter probability(boolean probability){
        this.probability = probability;
        return this;
    }

    /**
     * Set new class weights. By default, both classes are unweighted.
     * @param classWeights: weights for negative and positive classes, respectively
     * @return this
     * @throws IllegalArgumentException if more or less than two weights are received, or if any weight is negative
     */
    public SvmParameterAdapter classWeights(double[] classWeights){
        if(classWeights.length != 2){
            throw new IllegalArgumentException("Expected 2 class weights, received " + classWeights.length);
        }

        if(classWeights[0] <= 0 || classWeights[1] <= 0){
            throw new IllegalArgumentException("All class weights must be positive.");
        }

        this.classWeights = classWeights;
        return this;
    }

    /**
     * Build an svm_parameter object (from LibSVM) from this object's current state.
     * @return svm_parameter object
     */
    svm_parameter build(){
        svm_parameter param = new svm_parameter();

        param.svm_type = svm_parameter.C_SVC;
        param.C = C;

        param.kernel_type = kernel.getKernelType().getId();
        param.gamma = kernel.getGamma();
        param.coef0 = kernel.getCoef0();
        param.degree = kernel.getDegree();

        param.eps = tolerance;
        param.cache_size = cacheSize;
        param.shrinking = shrinking ? 1 : 0;

        param.probability = probability ? 1 : 0;

        param.weight = classWeights;
        param.nr_weight = classWeights.length;  // if 0, then no weighting is performed
        param.weight_label = new int[] {-1,1};

        return param;
    }
}
