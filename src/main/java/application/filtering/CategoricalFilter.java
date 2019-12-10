package application.filtering;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Filters for categorical attributes. They represents filters on the form:
 *                  column IN (value1, value2, ...)
 */
public class CategoricalFilter implements Filter {
    private final String columnName;
    private final String[] filterValues;

    public CategoricalFilter(String columnName, String[] filterValues) {
        this.filterValues = filterValues;
        this.columnName = columnName;
    }

    @Override
    public String buildPredicate() {
        if (filterValues.length == 0) {
            return "";
        }

        String list = Arrays.stream(filterValues)
                .map(x -> "'" + x + "'")
                .collect(Collectors.joining(", ", "(", ")"));

        return "(" + columnName + " IN " + list + ")";
    }

    @Override
    public String toString() {
        return "CategoricalFilter{" +
                "columnName='" + columnName + '\'' +
                ", filterValues=" + Arrays.toString(filterValues) +
                '}';
    }
}
