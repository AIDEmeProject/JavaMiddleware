package application.filtering;

import java.util.StringJoiner;

/**
 * Filters for numerical attributes. They represents filters on the form:
 *                          min <= column <= max
 */
public class RangeFilter implements Filter {
    private final String columnName;
    private double min = Double.NEGATIVE_INFINITY;
    private double max = Double.POSITIVE_INFINITY;

    public RangeFilter(String columnName) {
        this.columnName = columnName;
    }

    public RangeFilter(String columnName, double min, double max) {
        this(columnName);
        setMin(min);
        setMax(max);
    }

    public void setMin(double min) {
        if (min > this.max) {
            throw new IllegalArgumentException("Minimum cannot be larger than maximum.");
        }
        this.min = min;
    }

    public void setMax(double max) {
        if (max < this.min) {
            throw new IllegalArgumentException("Maximum cannot be smaller than minimum.");
        }
        this.max = max;
    }

    @Override
    public String buildPredicate() {
        StringJoiner joiner = new StringJoiner(" AND ", "(", ")");

        if (Double.isFinite(min)) {
            joiner.add(columnName + " >= " + min);
        }

        if (Double.isFinite(max)) {
            joiner.add(columnName + " <= " + max);
        }

        return joiner.toString();
    }

    @Override
    public String toString() {
        return "RangeFilter{" +
                "columnName='" + columnName + '\'' +
                ", min=" + min +
                ", max=" + max +
                '}';
    }
}
