package machinelearning.classifier.svm;

import libsvm.svm_parameter;
import utils.Validator;

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
     * @return SVM's penalty parameter. Default value is 1.0.
     */
    public double C() {
        return C;
    }

    /**
     * Set SVM's penalty parameter, C. Default value is 1.0
     * @param C: new C parameter
     * @return this
     * @throws IllegalArgumentException if C is negative
     */
    public SvmParameterAdapter C(double C){
        Validator.assertPositive(C);
        this.C = C;
        return this;
    }

    /**
     * @return SVM kernel. Default is RBF, with gamma = 1 / num_features.
     */
    public Kernel kernel() {
        return kernel;
    }

    /**
     * Set SVM kernel. Default is RBF, with gamma = 1 / num_features
     * @param kernel: new kernel
     * @return this
     */
    public SvmParameterAdapter kernel(Kernel kernel){
        this.kernel = kernel;
        return this;
    }

    /**
     * @return memory cache size for SVM solver. Default value is 100 MB.
     */
    public int cacheSize() {
        return cacheSize;
    }

    /**
     * Set memory cache size for SVM solver. Default value is 100 MB.
     * @param cacheSize: new cache size to use (in MB)
     * @return this
     * @throws IllegalArgumentException if cacheSize is negative
     */
    public SvmParameterAdapter cacheSize(int cacheSize){
        Validator.assertPositive(cacheSize);
        this.cacheSize = cacheSize;
        return this;
    }

    /**
     * @return SVM solver tolerance. Default value is 0.001.
     */
    public double tolerance() {
        return tolerance;
    }

    /**
     * Set SVM solver tolerance. Default value is 0.001 .
     * @param tolerance: new solver tolerance
     * @return this
     * @throws IllegalArgumentException if tolerance is negative
     */
    public SvmParameterAdapter tolerance(double tolerance){
        Validator.assertPositive(tolerance);
        this.tolerance = tolerance;
        return this;
    }

    /**
     * @return whether to use the shrinking heuristic. Used for speeding up computations. Default value is true.
     */
    public boolean shrinking() {
        return shrinking;
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
     * @return whether to compute class probability estimates. Default value is false.
     */
    public boolean probability() {
        return probability;
    }

    /**
     * Whether to compute class probability estimates. Default value is false.
     * @param probability: compute probability estimates?
     * @return this
     */
    public SvmParameterAdapter probability(boolean probability){
        this.probability = probability;
        return this;
    }

    /**
     * @return class weights. By default, both classes are unweighted.
     */
    public double[] classWeights() {
        return classWeights;
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

        Validator.assertPositive(classWeights[0]);
        Validator.assertPositive(classWeights[1]);

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
        param.weight_label = new int[] {0,1};

        return param;
    }
}