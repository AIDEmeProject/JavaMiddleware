package benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import utils.linalg.Matrix;
import utils.linalg.Vector;


public class VectorBenchmark extends AbstractBenchmark {
    private Vector a, b;

    @Setup(Level.Trial)  // fixture method. Used for initializing the class attributes. Executed once before the entire benchmark run
    public void doSetup() {
        a = getRandomVector(size, 1234);
        b = getRandomVector(size, 4321);
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

    @Benchmark
    public Matrix outerProduct() {
        return a.outerProduct(b);
    }
}
