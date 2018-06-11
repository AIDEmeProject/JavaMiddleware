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
                map.put(optionKey, sec.get(optionKey));
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
