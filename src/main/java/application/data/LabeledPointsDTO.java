package application.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import data.DataPoint;
import data.LabeledPoint;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;


import machinelearning.classifier.Label;
import utils.linalg.Vector;

public class LabeledPointsDTO {

    public Collection<LabeledPointDTO> labeledPoints;

    public LabeledPointsDTO(){

    }

    public Collection<LabeledPoint> getLabeledPoints(String json){

        Gson gson = new Gson();

        Type collectionType = new TypeToken<Collection<LabeledPointDTO>>(){}.getType();
        Collection<LabeledPointDTO> points = gson.fromJson(json, collectionType);


        ArrayList<LabeledPoint> lblPoints = new ArrayList<>();

        for (LabeledPointDTO point : points){

            DataPoint dataPoint = new DataPoint(point.id, point.data.array);

            Label label = Label.fromSign((double) point.label);
            LabeledPoint lblPoint = new LabeledPoint(dataPoint, label);

            lblPoints.add(lblPoint);
        }

        return lblPoints;
    }

    public static void main(String[] args){

        String json = "[" +
                "{" +
                "   \"id\":0," +
                "   \"data\":" +
                "       {\"array\":[22,0],\"shape\":{\"dimensions\":[2],\"capacities\":[2,1]}}," +
                "   \"label\":1" +
                "}," +
        "       {" +
                "   \"id\":1," +
                "   \"data\": " +
                "       {\"array\":[23,1],\"shape\":{\"dimensions\":[2],\"capacities\":[2,1]}}," +
                "   \"label\":1" +
                "}," +
                "{" +
                "   \"id\":2," +
                "   \"data\":" +
                "       {\"array\":[33,1],\"shape\":{\"dimensions\":[2],\"capacities\":[2,1]}}," +
                    "\"label\":0" +
               "}" +
        "]";

        Gson gson = new Gson();

        Type collectionType = new TypeToken<Collection<LabeledPointDTO>>(){}.getType();
        Collection<LabeledPointDTO> points = gson.fromJson(json, collectionType);

        for (LabeledPointDTO point: points
        ) {
            System.out.println(point.id);
            System.out.println(point.label);
        }

        LabeledPointsDTO converter = new LabeledPointsDTO();
        Collection<LabeledPoint> points2 = converter.getLabeledPoints(json);

        for (LabeledPoint point: points2
             ) {
            System.out.println(point.getId());
            System.out.println(point.getLabel());
        }
    }
    
}

class LabeledPointDTO{

    public long id;

    public Integer label;

    public data data;

    class data{
        public double[] array;
    }
}

class Target{

    public Integer a;

    public Integer b;

}