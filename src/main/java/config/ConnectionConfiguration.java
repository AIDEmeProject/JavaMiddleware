package config;

import io.IniConfigurationParser;

import java.util.Map;

/**
 * This class holds the database connection configuration properties
 */
public class ConnectionConfiguration {
    /**
     * Database connection url (something on the format driver://url:port)
     */
    public final String url;

    /**
     * Database user
     */
    public final String user;

    /**
     * Database password
     */
    public final String password;

    public ConnectionConfiguration(String connection) {
        IniConfigurationParser parser = new IniConfigurationParser("connections");
        Map<String, String> config = parser.read(connection);
        this.url = config.get("url");
        this.user = config.get("user");
        this.password = config.get("password");
    }
}
