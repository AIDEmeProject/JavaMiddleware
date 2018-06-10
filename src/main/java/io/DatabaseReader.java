package io;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseReader {
    private final String url;
    private final String user;
    private final String password;

    public DatabaseReader(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private String buildSQLString(String table, String[] columns){
        String cols = Arrays.toString(columns);

        return "SELECT DISTINCT " + cols.substring(1, cols.length()-1) + " FROM " + table + ';';
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
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
}
