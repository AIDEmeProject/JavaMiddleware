package io;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

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
        ArrayList<double[]> X = new ArrayList<>();
        String SQL = buildSQLString(table, columns);

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
            throw new RuntimeException("Couldn't read data from Postgres.");
        }

        return X.toArray(new double[X.size()][]);
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:" + connectionString, user, password);
    }

    private String buildSQLString(String table, String[] columns){
        String cols = Arrays.toString(columns);
        cols = cols.substring(1, cols.length()-1);  // remove brackets from string's start and end
        return "SELECT DISTINCT " + cols + " FROM " + table + ';';
    }
}
