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


import java.util.ArrayList;
import java.util.HashMap;

public class ValueParser{
    protected int nColumns;

    protected ArrayList<HashMap<String, Integer>> columns;

    public ValueParser(int nColumns){

        this.nColumns = nColumns;
        this.columns = new ArrayList<>();

        for (int i = 0; i<nColumns; i++){
            columns.add(new HashMap<>());
        }
    }

    public double parseValue(String data, int iColumn){
        double val;
        try{
            val = Double.parseDouble(data);
        }
        catch (NumberFormatException e){
            val = this.getCategory(data, iColumn);
        }
        return val;
    }

    protected int getCategory(String data, int iColumn){

        HashMap<String, Integer> cat = this.columns.get(iColumn);
        int newCategory = cat.size();

        if (! cat.containsKey(data)){
            cat.put(data, newCategory);
            return newCategory;
        }
        return cat.get(data);
    }

    public int[] getUniqueValueCount(){
        int[] counts = new int[this.nColumns];


        for (int iCol=0;iCol<nColumns ; iCol++){
            counts[iCol] = this.columns.get(iCol).size();
        }
        return counts;
    }

}