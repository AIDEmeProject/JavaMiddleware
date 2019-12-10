package config;

import application.filtering.DatabaseFiltering;
import io.DatabaseReader;
import io.IniConfigurationParser;

import java.util.Map;

/**
 * This class holds the dataset's configuration properties
 */
 public class DatasetConfiguration {
    /**
     * Connection name (in connections.ini file)
     */
    private final String connection;

    /**
     * Database name
     */
    public final String database;

    /**
     * Table name
     */
    public final String table;

    /**
     * Column to use as key
     */
    public final String key;

    public DatasetConfiguration(String dataset) {
        IniConfigurationParser parser = new IniConfigurationParser("datasets");
        Map<String, String> config = parser.read(dataset);
        this.connection = config.get("connection");
        this.database = config.get("database");
        this.table = config.get("table");
        this.key = config.get("key");
    }

    public DatabaseReader buildReader() {
        ConnectionConfiguration connectionConfig = new ConnectionConfiguration(connection);
        return new DatabaseReader(connectionConfig.url, database, connectionConfig.user, connectionConfig.password);
    }

    public DatabaseFiltering buildFilter() {
        return new DatabaseFiltering(buildReader(), table, key);
    }
}
