package io;

import data.IndexedDataset;

import java.sql.*;
import java.util.*;

public class DatabaseReader {
    private final String connectionString;
    private final String user;
    private final String password;

    public DatabaseReader(String url, String database, String user, String password) {
        this.connectionString = url + "/" + database;
        this.user = user;
        this.password = password;
    }

    public IndexedDataset readTable(String table, String key, String[] columns){
        return readTable(table, key, columns, "");
    }

    public Set<Long> readKeys(String table, String key, String predicate){
        return readTable(table, key, new String[] {}, predicate).getIndexes();
    }

    private IndexedDataset readTable(String table, String key, String[] columns, String predicate){
        LinkedHashSet<Long> keys = new LinkedHashSet<>();
        ArrayList<double[]> X = new ArrayList<>();

        String SQL = buildSQLString(table, key, columns, predicate);

        try (
                Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL)
        ) {
            int size = rs.getMetaData().getColumnCount();
            double[] row = new double[size];

            // readTable all records
            while(rs.next()) {
                // store key
                keys.add(rs.getLong(1));

                // store rows
                for (int i = 1; i < size; i++) {
                    row[i-1] = rs.getDouble(i+1);
                }

                X.add(row);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Couldn't read data from database.");
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
