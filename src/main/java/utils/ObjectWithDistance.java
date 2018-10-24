package utils;

/**
 * This utility class group objects with their distance for the sake of sorting the objects by the distance
 * @param <T> type of objects
 */

public class ObjectWithDistance<T> {
    /**
     * Customized distance
     */
    private double distance;

    /**
     * Object
     */
    private T object;

    public ObjectWithDistance(double distance, T object) {
        this.distance = distance;
        this.object = object;
    }

    public double getDistance() {
        return distance;
    }

    public T getObject() {
        return object;
    }
}

