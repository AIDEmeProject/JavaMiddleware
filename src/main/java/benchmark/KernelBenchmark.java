package benchmark;

import machinelearning.classifier.svm.DistanceKernel;
import machinelearning.classifier.svm.GaussianKernel;
import machinelearning.classifier.svm.Kernel;
import machinelearning.classifier.svm.LinearKernel;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import utils.linalg.Matrix;


public class KernelBenchmark extends AbstractBenchmark {
    private Kernel linearKernel = new LinearKernel();
    private Kernel gaussianKernel = new GaussianKernel();
    private Kernel distanceKernel = new DistanceKernel();

    private Matrix a, b;

    @Setup(Level.Trial)
    public void doSetup() {
        a = getRandomMatrix(50, 2, 3333);
        b = getRandomMatrix(size, 2, 4444);
    }

    @Benchmark
    public Matrix distance() {
        return distanceKernel.compute(a, b);
    }

    @Benchmark
    public Matrix linear() {
        return linearKernel.compute(a, b);
    }

    @Benchmark
    public Matrix gaussian() {
        return gaussianKernel.compute(a, b);
    }
}
