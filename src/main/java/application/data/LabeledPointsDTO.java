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
