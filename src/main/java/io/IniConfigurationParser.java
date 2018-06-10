package io;

import org.ini4j.Profile.Section;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IniConfigurationParser {
    private static final String folder = "./src/main/resources/";
    private final String path;

    public IniConfigurationParser(String config) {
        this.path = buildPath(config);
    }

    private String buildPath(String config){
        if (!config.endsWith(".ini")){
            config += ".ini";
        }
        return folder + config;
    }

    public Map<String, String> read(String section){
        Map<String, String> map = new HashMap<>();
        try {
            Wini ini = new Wini(new File(path));
            Section sec = ini.get(section);
            for (String optionKey: sec.keySet()) {
                map.put(optionKey, sec.get(optionKey));
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        return map;
    }
}
