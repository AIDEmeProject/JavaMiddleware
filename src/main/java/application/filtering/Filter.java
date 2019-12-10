package application.filtering;

import java.util.StringJoiner;

/**
 * Interface for all possible filters
 */
public interface Filter {
    /**
     * @return a SQL string encoding the filter to be applied
     */
    String buildPredicate();
}


/**
 * Filters for numerical attributes. They represents predicates on the form:
 *                          min <= column <= max
 */
class NumericalPredicate implements Filter {
    private double min = Double.NEGATIVE_INFINITY;
    private double max = Double.POSITIVE_INFINITY;
    private final String columnName;

    public NumericalPredicate(String columnName) {
        this.columnName = columnName;
    }

    public NumericalPredicate(String columnName, double min, double max) {
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
}

/**
 * Filters for categorical attributes. They represents predicates on the form:
 *                     column IN (value1, value2, ...)
 */
class CategoricalPredicate implements Filter {
    private final String[] filterValues;
    private final String columnName;

    public CategoricalPredicate(String columnName, String[] filterValues) {
        this.filterValues = filterValues;
        this.columnName = columnName;
    }

    @Override
    public String buildPredicate() {
        if (filterValues.length == 0) {
            return "";
        }
        return "(" + columnName + " IN (" + String.join(", ", filterValues) + "))";
    }
}

