package utils.linalg;

import utils.Validator;

import java.util.Arrays;

public class Shape {
    private final int[] dimensions;
    private final int[] capacities;

    public Shape(int... dimensions) {
        this.dimensions = validateDimensions(dimensions);
        this.capacities = computeCapacity();
    }

    private int[] validateDimensions(int[] dimensions) {
        Validator.assertNotEmpty(dimensions);
        for (int dim : dimensions) {
            if (dim <= 0) {
                throw new IllegalArgumentException();
            }
        }
        return dimensions;
    }

    private int[] computeCapacity() {
        int[] capacities = new int[dimensions.length + 1];

        capacities[capacities.length - 1] = 1;
        for (int i = dimensions.length - 1; i >= 0 ; i--) {
            capacities[i] = capacities[i + 1] * dimensions[i];
        }

        return capacities;
    }

    public boolean isEmpty() {
        return getCapacity() == 0;
    }

    public int getCapacity() {
        return capacities[0];
    }

    public int get(int index) {
        return dimensions[index];
    }

    public int getPosition(int... indexes) {
        Validator.assertEqualLengths(indexes, dimensions);

        int pos = 0;
        for (int i = 0; i < indexes.length; i++) {
            Validator.assertIndexInBounds(indexes[i], 0, dimensions[i]);
            pos += capacities[i+1] * indexes[i];
        }

        return pos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shape shape = (Shape) o;
        return Arrays.equals(dimensions, shape.dimensions);
    }

    @Override
    public String toString() {
        return Arrays.toString(dimensions);
    }
}
