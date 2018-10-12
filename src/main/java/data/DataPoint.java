package data;

import utils.linalg.Vector;

/**
 * A DataPoint is an indexed collection of values. More specifically, it is composed of two entities:
 *
 *   - id: a {@code long} uniquely identifying this data points (i.e. a database id).
 *   - data: array of {@code double} values representing the data point's content
 */
public class DataPoint {

    /**
     * data point's unique id
     */
    protected long id;

    /**
     * data point's values
     */
    protected Vector data;


    /**
     * @param id: data point's unique ID
     * @param data: the features array
     * @throws IllegalArgumentException if data is emtpy
     */
    public DataPoint(long id, Vector data) {
        this.id = id;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public Vector getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataPoint)) return false;

        DataPoint dataPoint = (DataPoint) o;
        return id == dataPoint.id && data.equals(dataPoint.data);
    }

    @Override
    public String toString() {
        return "{\"id\": " + getId()  + ", \"data\": " + data + '}';
    }
}
