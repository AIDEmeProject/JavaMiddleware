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
