package io.json;

import com.google.gson.*;
import config.TsmConfiguration;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TSMConfigurationAdapter  implements com.google.gson.JsonDeserializer<TsmConfiguration> {


    public TsmConfiguration deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {

        JsonObject object = json.getAsJsonObject();

        boolean hasTSM = object.getAsJsonPrimitive("hasTSM").getAsBoolean();
        double probability = object.getAsJsonPrimitive("searchUnknownRegionProbability").getAsDouble();

        TsmConfiguration  configuration = new TsmConfiguration(hasTSM);
        configuration.setSearchUnknownRegionProbability(probability);

        if (object.has("columns")) {
            JsonArray columns = object.getAsJsonArray("columns");
            String[] columnNames = new String[columns.size()];
            for (int i = 0; i < columns.size(); i++) {
                columnNames[i] = columns.get(i).getAsString();
            }
            configuration.setColumns(columnNames);
        }

        if (object.has("featureGroups")) {
            JsonArray jsonFeatureGroups = object.getAsJsonArray("featureGroups");

            ArrayList<String[]> featureGroups = new ArrayList<>();
            for (int i = 0; i < jsonFeatureGroups.size(); i++) {
                JsonArray jsonFeatureGroup = jsonFeatureGroups.get(i).getAsJsonArray();

                String[] featureGroup = new String[jsonFeatureGroup.size()];

                for (int iVar = 0; iVar < jsonFeatureGroup.size(); iVar++) {
                    featureGroup[iVar] = jsonFeatureGroup.get(iVar).getAsString();
                }

                featureGroups.add(featureGroup);
            }

            configuration.setFeatureGroups(featureGroups);
        }

        if (object.has("flags")) {
            JsonArray jsonFlags = object.getAsJsonArray("flags");

            ArrayList<boolean[]> flags = new ArrayList<>();
            for (int i = 0; i < jsonFlags.size(); i++) {
                boolean[] flag = {true, false};
                flags.add(flag);
            }

            configuration.setFlags(flags);
        }

        return configuration;

    }
}
