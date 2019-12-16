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
import data.LabeledPoint;

import java.util.ArrayList;

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