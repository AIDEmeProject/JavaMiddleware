package benchmark;

import org.openjdk.jmh.annotations.*;
import utils.linalg.Vector;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)  // measure the Average Time of execution of each method
@OutputTimeUnit(TimeUnit.MICROSECONDS)  // Return average time in MICROSECONDS
@State(Scope.Benchmark)  // All threads running a benchmark share the same state (i.e. the below class attributes)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class VectorBenchmark {

    // run benchmarks with different matrix and vector sizes
    @Param({"16", "32", "64", "128", "256", "512", "1024"})
    public int size;

    private double PI = 3.1415;  // do NOT set as final (or Constant Folding could take place)
    private Vector a, b;

    @Setup(Level.Trial)  // fixture method. Used for initializing the class attributes. Executed once before the entire benchmark run
    public void doSetup() {
        a = getRandomVector(size, 1234);
        b = getRandomVector(size, 4321);
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

    @Benchmark
    public Vector add() {
        return a.add(b);
    }

    @Benchmark
    public Vector scalarMultiply() {
        return a.scalarMultiply(PI);
    }

    @Benchmark
    public double dot() {
        return a.dot(b);
    }

    @Benchmark
    public double squaredNorm() {
        return a.squaredNorm();
    }

    @Benchmark
    public double squaredDistance() {
        return a.squaredDistanceTo(b);
    }
}
