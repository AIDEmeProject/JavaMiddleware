package application.filtering;

import io.DatabaseReader;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


public class DatabaseFiltering {
    private final DatabaseReader reader;
    private final String table;
    private final String id;

    /**
     * @param reader: the database instances
     * @param table: the table to read
     * @param id: the id column name
     */
    public DatabaseFiltering(DatabaseReader reader, String table, String id) {
        this.reader = reader;
        this.table = table;
        this.id = id;
    }

    /**
     * @param filters: array of filters to apply
     * @return the set of database object ids matching all filters
     */
    public Set<Long> getElementsMatchingFilter(Filter[] filters) {
        return reader.readKeys(table, id, buildPredicate(filters));
    }

    public String buildPredicate(Filter[] filters) {
        return Arrays.stream(filters)
                .map(Filter::buildPredicate)
                .filter(x -> !x.isEmpty())
                .collect(Collectors.joining(" AND "));
    }
}


