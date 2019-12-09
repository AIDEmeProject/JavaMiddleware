package application.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import data.DataPoint;
import data.LabeledPoint;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;


import explore.user.GuiUserLabel;
import machinelearning.classifier.Label;
import machinelearning.threesetmetric.LabelGroup;
import utils.linalg.Vector;

public class LabeledPointsDTO {

    public Collection<LabeledPointDTO> labeledPoints;

    public LabeledPointsDTO(){

    }


    public LabeledPoint getFakePoint(String json){
        Gson gson = new Gson();

        FakePointDTO dtoPoint = gson.fromJson(json, FakePointDTO.class);

        DataPoint point = new DataPoint(-1, dtoPoint.data);

        GuiUserLabel label = new GuiUserLabel(dtoPoint.label);
        LabeledPoint fakePoint = new LabeledPoint(point, label);

        return fakePoint;
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


    public Collection<LabeledPoint> getTSMLabeledPoints(String json){

        Gson gson = new Gson();

        Type collectionType = new TypeToken<Collection<TSMLabeledPointDTO>>(){}.getType();
        Collection<TSMLabeledPointDTO> points = gson.fromJson(json, collectionType);
        System.out.println(points.size());

        ArrayList<LabeledPoint> lblPoints = new ArrayList<>();

        for (TSMLabeledPointDTO point : points){

            DataPoint dataPoint = new DataPoint(point.id, point.data.array);

            int nPartialLabel = point.labels.length;


            Label[] partialLabels = new Label[nPartialLabel];

            for (int i = 0; i < nPartialLabel; i++){

                Label label = Label.fromSign((double) point.labels[i]);
                partialLabels[i] = label;


            }

            LabelGroup labelGroup = new LabelGroup(partialLabels);
            LabeledPoint lblPoint = new LabeledPoint(dataPoint, labelGroup);

            lblPoints.add(lblPoint);
        }

        return lblPoints;
    }

    public static void main(String[] args){

        String json = "[" +
                "{" +
                "   \"id\":0," +
                "   \"data\":" +
                "       {" +
                "           \"array\":[22,0]," +
            "               \"shape\":{\"dimensions\":[2],\"capacities\":[2,1]}" +
            "           }," +
                "   \"labels\":[1, 0]" +
                "}" +
        "]";

        Gson gson = new Gson();


        LabeledPointsDTO s = new LabeledPointsDTO();
        s.getTSMLabeledPoints(json);

    }
    
}


class FakePointDTO{

    public double[] data;

    int label;


}

class TSMLabeledPointDTO{

    public long id;

    public Integer[] labels;

    public data data;

    class data{
        public double[] array;
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
