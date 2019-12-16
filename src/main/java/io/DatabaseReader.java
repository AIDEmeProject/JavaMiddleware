/*
 * Copyright (c) 2019 École Polytechnique
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, you can obtain one at http://mozilla.org/MPL/2.0
 *
 * Authors:
 *       Luciano Di Palma <luciano.di-palma@polytechnique.edu>
 *       Enhui Huang <enhui.huang@polytechnique.edu>
 *       Laurent Cetinsoy <laurent.cetinsoy@gmail.com>
 *
 * Description:
 * AIDEme is a large-scale interactive data exploration system that is cast in a principled active learning (AL) framework: in this context,
 * we consider the data content as a large set of records in a data source, and the user is interested in some of them but not all.
 * In the data exploration process, the system allows the user to label a record as “interesting” or “not interesting” in each iteration,
 * so that it can construct an increasingly-more-accurate model of the user interest. Active learning techniques are employed to select
 * a new record from the unlabeled data source in each iteration for the user to label next in order to improve the model accuracy.
 * Upon convergence, the model is run through the entire data source to retrieve all relevant records.
 */

package io;

import data.IndexedDataset;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

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
    public IndexedDataset readTable(String table, String key, String[] columns){
        IndexedDataset.Builder builder = new IndexedDataset.Builder();

        // build SQL query
        String SQL = buildSQLString(table, key, columns, "");

        try (
                Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL)
        ) {
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

                builder.add(id, data);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Couldn't read data from database.");
        }

        return builder.build();
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
