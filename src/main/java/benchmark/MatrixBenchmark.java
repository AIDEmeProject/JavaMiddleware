package benchmark;

import org.openjdk.jmh.annotations.*;
import utils.linalg.CholeskyDecomposition;
import utils.linalg.EigenvalueDecomposition;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.concurrent.TimeUnit;


public class MatrixBenchmark extends AbstractBenchmark {

    private Matrix a, b, spd;
    private Vector c;

    @Setup(Level.Trial)  // fixture method. Used for initializing the class attributes. Executed once before the entire benchmark run
    public void doSetup() {
        a = getRandomMatrix(size, size, 1234);
        b = getRandomMatrix(size, size,4321);
        c = getRandomVector(size, 5678);
        spd = getRandomSPDMatrix(size, 8765);
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
        return a.matrixMultiply(b);
    }

    @Benchmark
    @Warmup(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
    public Matrix multiplyTranspose() {
        return a.multiplyTranspose(b);
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

    @Benchmark
    public Matrix addRow() {
        return a.addRow(c);
    }

    @Benchmark
    public Matrix iAddRow() {
        return a.iAddRow(c);
    }

    @Benchmark
    public Matrix addColumn() {
        return a.addColumn(c);
    }

    @Benchmark
    public Matrix iAddColumn() {
        return a.iAddColumn(c);
    }
}
