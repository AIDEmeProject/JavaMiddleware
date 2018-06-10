package io;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DatabaseReader {
    private final String connectionString;
    private final String user;
    private final String password;

    public DatabaseReader(String url, String database, String user, String password) {
        this.connectionString = url + "/" + database;
        this.user = user;
        this.password = password;
    }

    public double[][] read(String table, String[] columns){
        return read(table, columns, "");
    }

    public double[][] read(String table, String[] columns, String predicate){
        ArrayList<double[]> X = new ArrayList<>();
        String SQL = buildSQLString(table, columns, predicate);

        try (
                Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL)
        ) {
            int size = rs.getMetaData().getColumnCount();
            double[] row = new double[size];
            while(rs.next()) {
                for (int i = 0; i < size; i++) {
                    row[i] = rs.getDouble(i + 1);
                }

                X.add(row);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Couldn't read data from database.");
        }

        return X.toArray(new double[X.size()][]);
    }

    public Set<Long> readKeys(String table, String key, String predicate){
        Set<Long> keys = new HashSet<>();
        String SQL = buildSQLString(table, new String[] {key}, predicate);
        
        try (
                Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL)
        ) {
            while(rs.next()) {
                keys.add(rs.getLong(1));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Couldn't read key from database.");
        }

        return keys;

    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:" + connectionString, user, password);
    }

    private String buildSQLString(String table, String[] columns, String predicate){
        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append("SELECT DISTINCT ");

        // add selected columns
        String cols = Arrays.toString(columns);
        cols = cols.substring(1, cols.length()-1);  // remove brackets from string's start and end
        sqlBuilder.append(cols);

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
