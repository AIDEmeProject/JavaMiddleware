package io;

import data.DataPoint;

import java.sql.*;
import java.util.*;

/**
 * This module is responsible for reading data from a database using JDBC. We make the following assumptions about the
 * data being read:
 *
 *      - The columns being read are numerical values
 *      - There is an ID column, which contains an unique value per row, of type Long
 */
public class DatabaseReader {
    /**
     * database connection string. Usually on the format: driver://address:port/database
     */
    private final String connectionString;

    /**
     * database username
     */
    private final String user;

    /**
     * user's password
     */
    private final String password;

    /**
     * @param url: connection string header, on the format:  driver://address:port
     * @param database: database to connect to
     * @param user: database username
     * @param password: user's database password
     */
    public DatabaseReader(String url, String database, String user, String password) {
        this.connectionString = url + '/' + database;
        this.user = user;
        this.password = password;
    }

    /**
     * Reads a SQL table into memory.
     * @param table: table's name
     * @param key: name of column to use as key. Should contain unique values, and of type Long
     * @param columns: list of data columns to read. Must be of numeric type.
     * @return Indexes dataset instance containing both the keys and the data read from the database
     */
    public List<DataPoint> readTable(String table, String key, String[] columns){
        List<DataPoint> points = new ArrayList<>();

        // build SQL query
        String SQL = buildSQLString(table, key, columns, "");

        try (
                Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL)
        ) {
            // row number
            int row = 0;

            // number of features
            int size = rs.getMetaData().getColumnCount() - 1;

            // read all records
            while(rs.next()) {
                // store point id
                long id = rs.getLong(1);

                // store rows
                double[] data = new double[size];
                for (int i = 0; i < size; i++) {
                    data[i] = rs.getDouble(i+2);
                }

                points.add(new DataPoint(row, id, data));
                row++;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Couldn't read data from database.");
        }

        return points;
    }

    /**
     * Obtain the ids of all rows satisfying a given query predicate
     * @param table: table's name
     * @param key: name of column to use as key. Should contain unique values, and of type Long
     * @param predicate: query predicate
     * @return a set containing all the row keys satisfying the given predicate
     */
    public Set<Long> readKeys(String table, String key, String predicate){
        Set<Long> keys = new HashSet<>();

        // build SQL query
        String SQL = buildSQLString(table, key, new String[] {}, predicate);

        try (
                Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL)
        ) {
            // read all keys
            while(rs.next()) {
                keys.add(rs.getLong(1));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Couldn't read data from database.");
        }

        return keys;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:" + connectionString, user, password);
    }

    private String buildSQLString(String table, String key, String[] columns, String predicate){
        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append("SELECT ");

        // add columns
        sqlBuilder.append(key);  // add key

        for (String col : columns){
            sqlBuilder.append(',');
            sqlBuilder.append(col);
        }

        // add FROM clause
        sqlBuilder.append(" FROM ");
        sqlBuilder.append(table);

        // add WHERE clause
        if (!predicate.isEmpty()){
            sqlBuilder.append(" WHERE ");
            sqlBuilder.append(predicate);
        }

        sqlBuilder.append(';');

        return sqlBuilder.toString();
    }
}
