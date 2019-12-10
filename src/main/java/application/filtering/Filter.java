package application.filtering;

/**
 * Interface for all possible filters
 */
public interface Filter {
    /**
     * @return a SQL string encoding the filter to be applied
     */
    String buildPredicate();
}


