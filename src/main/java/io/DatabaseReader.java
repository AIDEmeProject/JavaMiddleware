package io;

import data.IndexedDataset;

import java.sql.*;
import java.util.*;

/**
 * This module is responsible for reading data from a database. We make the following assumptions about the data being read:
 *
 *      - The columns being read are numerical values
 *      - There is an ID column, containing an unique value per row, of type Long
 */
public class DatabaseReader {
    /**
     * database connection string. Usually on the format: driver://URL:port/database
     */
    private final String connectionString;

    /**
     * database username
     */
    private final String user;

    /**
     * user password
     */
    private final String password;

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
    public IndexedDataset readTable(String table, String key, String[] columns){
        return read(table, key, columns, "");
    }

    /**
     * Obtain the ids of all rows satisfying a given query predicate
     * @param table: table's name
     * @param key: name of column to use as key. Should contain unique values, and of type Long
     * @param predicate: query predicate
     * @return a set containing all the row keys satisfying the given predicate
     */
    public Set<Long> readKeys(String table, String key, String predicate){
        return read(table, key, new String[] {}, predicate).getIndexes();
    }

    private IndexedDataset read(String table, String key, String[] columns, String predicate){
        LinkedHashSet<Long> keys = new LinkedHashSet<>();
        ArrayList<double[]> X = new ArrayList<>();

        // build SQL query
        String SQL = buildSQLString(table, key, columns, predicate);

        try (
                Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL)
        ) {
            // number of features
            int size = rs.getMetaData().getColumnCount() - 1;

            // read all records
            while(rs.next()) {
                // store key
                keys.add(rs.getLong(1));

                // store rows
                double[] row = new double[size];
                for (int i = 0; i < size; i++) {
                    row[i] = rs.getDouble(i+2);
                }
                X.add(row);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Couldn't read data from database.");
        }

        if (keys.size() != X.size()){
            throw new IllegalArgumentException("Key column is not unique.");
        }

        return new IndexedDataset(keys, X.toArray(new double[X.size()][]));
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
