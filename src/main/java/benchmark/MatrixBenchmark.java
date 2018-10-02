package benchmark;

import org.openjdk.jmh.annotations.*;
import utils.linalg.CholeskyDecomposition;
import utils.linalg.EigenvalueDecomposition;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)  // measure the Average Time of execution of each method
@OutputTimeUnit(TimeUnit.MICROSECONDS)  // Return average time in MICROSECONDS
@State(Scope.Benchmark)  // All threads running a benchmark share the same state (i.e. the below class attributes)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class MatrixBenchmark {

    // run benchmarks with different matrix and vector sizes
    @Param({"16", "32", "64", "128", "256", "512", "1024"})
    public int size;

    private double PI = 3.1415;  // do NOT set as final (or Constant Folding could take place)
    private Matrix a, b, spd;
    private Vector c;

    @Setup(Level.Trial)  // fixture method. Used for initializing the class attributes. Executed once before the entire benchmark run
    public void doSetup() {
        a = getRandomMatrix(size, 1234);
        b = getRandomMatrix(size, 4321);
        c = getRandomVector(size, 5678);
        spd = getRandomSPDMatrix(size, 8765);
    }

    // compute a random vector
    private static Vector getRandomVector(int size, long seed) {
        Random random = new Random(seed);
        double[] values = new double[size];

        for (int i = 0; i < size; i++) {
            values[i] = random.nextDouble();
        }
        return Vector.FACTORY.make(values);
    }

    // compute a random matrix
    private static Matrix getRandomMatrix(int size, long seed) {
        Random random = new Random(seed);
        double[][] values = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                values[i][j] = random.nextDouble();
            }
        }

        return Matrix.FACTORY.make(values);
    }

    // compute a random SPD matrix
    private static Matrix getRandomSPDMatrix(int size, long seed) {
        Matrix A = getRandomMatrix(size, seed);
        A = A.add(A.transpose()).scalarMultiply(0.5);
        A = A.add(Matrix.FACTORY.identity(size).scalarMultiply(size));
        return A;
    }

    @Benchmark
    public Matrix add() {
        return a.add(b);
    }

    @Benchmark
    public Matrix scalarMultiply() {
        return a.scalarMultiply(PI);
    }

    @Benchmark
    public Vector vectorMultiply() {
        return a.multiply(c);
    }

    @Benchmark
    @Warmup(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
    public Matrix matrixMultiply() {
        return a.multiply(b);
    }

    @Benchmark
    @Warmup(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
    public double eigen() {
        return new EigenvalueDecomposition(spd).getEigenvalue(0);
    }

    @Benchmark
    @Warmup(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
    public Matrix cholesky() {
        return new CholeskyDecomposition(spd).getL();
    }
}
