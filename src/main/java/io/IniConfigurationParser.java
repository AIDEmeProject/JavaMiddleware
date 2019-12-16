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

import org.ini4j.Profile.Section;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration parser for INI files. Basically it is a simple wrapper over the ini4j library.
 *
 * @see <a href="http://ini4j.sourceforge.net/">ini4j homepage</a>
 */
public class IniConfigurationParser {
    /**
     * Path to resources folder
     */
    private static final String folder = "./src/main/resources/";

    /**
     * Path to ini file
     */
    private final String path;

    /**
     * @param config: name of configuration file to read. If it does not have with the .ini extension.
     */
    public IniConfigurationParser(String config) {
        this.path = buildPath(config);
    }

    /**
     * Reads a section of the .ini file into a Map object
     * @param section: name of section to read
     * @return section to be read
     * @throws IllegalArgumentException if section does not exist in configuration file
     * @throws RuntimeException if reading the configuration file failed (i.e. file was not found)
     */
    public Map<String, String> read(String section){
        Map<String, String> map = new HashMap<>();
        try {
            Wini ini = new Wini(new File(path));

            Section sec = ini.get(section);
            if (sec == null){
                throw new IllegalArgumentException("Section not found. Verify your configuration file.");
            }

            for (String optionKey: sec.keySet()) {
                map.put(optionKey, sec.get(optionKey).trim());
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
            throw new RuntimeException("Failed to read configuration file.");
        }

        return map;
    }

    private String buildPath(String config){
        if (!config.endsWith(".ini")){
            config += ".ini";
        }
        return folder + config;
    }
}
