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

package data;

import java.util.Random;

public class ColumnSpecification{

    protected boolean isNumeric;

    protected double min;

    protected double max;

    protected int nPointInRange;

    public ColumnSpecification(boolean isNumeric, double min, double max, int nPointInRange){
        this.isNumeric = isNumeric;
        this.min = min;
        this.max = max;
        this.nPointInRange = nPointInRange;
    }

    public int getNPointToGenerate(){

        if (this.isNumeric){

            return nPointInRange;
        }

        return (int) (max - min + 1 );
    }

    public double generateValue(){

        if (this.isNumeric){
            return (new Random()).nextFloat() * (max - min) + min;
        }

        return (new Random()).nextInt((int) (max - min + 1)) + min;
    }


    public double[] generateValues(){
        int nPoint = this.getNPointToGenerate();
        double[] values = new double[nPoint];

        for (int i=0; i<nPoint; i++){

            if (this.isNumeric){
                values[i] = (max - min) / nPointInRange * i;
            }
            else{
                values[i] = i + min;
            }

        }
        return values;
    }
}


