package benchmark;

import org.openjdk.jmh.annotations.*;
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
public class AbstractBenchmark {
    // run benchmarks with different matrix and vector sizes
    @Param({"16", "32", "64", "128", "256", "512", "1024"})
    public int size;

    public double PI = 3.1415;  // do NOT set as final (or Constant Folding could take place)

    // compute a random vector
    static Vector getRandomVector(int size, long seed) {
        Random random = new Random(seed);
        double[] values = new double[size];

        for (int i = 0; i < size; i++) {
            values[i] = random.nextDouble();
        }
        return Vector.FACTORY.make(values);
    }

    // compute a random matrix
    static Matrix getRandomMatrix(int rows, int cols, long seed) {
        Random random = new Random(seed);
        double[][] values = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                values[i][j] = random.nextDouble();
            }
        }

        return Matrix.FACTORY.make(values);
    }

    // compute a random SPD matrix
    static Matrix getRandomSPDMatrix(int size, long seed) {
        Matrix A = getRandomMatrix(size, size, seed);
        A = A.add(A.transpose()).scalarMultiply(0.5);
        A = A.add(Matrix.FACTORY.identity(size).scalarMultiply(size));
        return A;
    }
}
