package machinelearning.classifier.svm;

import org.junit.jupiter.api.Test;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

abstract class AbstractKernelTest {
    protected Kernel kernel;

    @Test
    void compute_vectorsOfDifferentLengths_throwsException() {
        assertThrows(RuntimeException.class, () -> kernel.compute(Vector.FACTORY.zeros(1), Vector.FACTORY.zeros(2)));
    }

    void assertKernelFunctionIsCorrect(double expected, double[] arr1, double[] arr2) {
        Vector x, y;
        x = Vector.FACTORY.make(arr1);
        y = Vector.FACTORY.make(arr2);
        assertEquals(expected, kernel.compute(x, y));
    }

    void assertKernelVectorIsCorrect(double[] expected, double[][] arr1, double[] arr2) {
        Matrix x = Matrix.FACTORY.make(arr1);
        Vector y = Vector.FACTORY.make(arr2);
        assertEquals(Vector.FACTORY.make(expected), kernel.compute(x, y));
    }

    void assertKernelMatrixIsCorrectlyComputed(double[][] expected, double[][] toCompute) {
        assertEquals(Matrix.FACTORY.make(expected), kernel.compute(Matrix.FACTORY.make(toCompute)));
    }
}