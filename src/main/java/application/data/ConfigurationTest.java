package application.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import config.ExperimentConfiguration;
import data.DataPoint;
import data.LabeledPoint;
import io.json.JsonConverter;
import machinelearning.classifier.Label;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

public class ConfigurationTest {


    public static void main(String[] args){

        String json = "[{\"dataPoint\":{\"id\":1237646508760564393,\"data\":[-0.13119041937947784,-0.014898676303238882]},\"label\":[\"POSITIVE\"]},{\"dataPoint\":{\"id\":1237654896851026426,\"data\":[-0.19952449741497963,0.02997197718063128]},\"label\":[\"POSITIVE\"]}]";

        String json2 = "{\"dataPoint\":{\"id\":1237646508760564393,\"data\":[-0.13119041937947784,-0.014898676303238882]},\"label\":[\"POSITIVE\"]},{\"dataPoint\":{\"id\":1237654896851026426,\"data\":[-0.19952449741497963,0.02997197718063128]},\"label\":[\"POSITIVE\"]}";

        Gson gson = new Gson();
        //LabeledPoint point =  JsonConverter.deserialize(json, LabeledPoint.class);
        LabeledPoint point =  gson.fromJson(json, LabeledPoint.class);

        System.out.print(point.getLabel().getLabelsForEachSubspace().length);

    }
    
}


class PointsDTO{

    public ArrayList<LabeledPoint> labeledPoints;


}