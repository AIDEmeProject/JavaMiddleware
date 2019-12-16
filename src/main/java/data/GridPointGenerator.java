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


import java.util.ArrayList;
import java.util.Random;


public class GridPointGenerator{


    protected int[] indices;

    protected int movingIndice;

    protected int indiceToUpdate;

    protected int carry;

    protected int[] carries;

    public static void main(String[] args){

        ArrayList<ColumnSpecification> specs = new ArrayList<>();
        specs.add(new ColumnSpecification(true, 0, 170, 100));
        specs.add(new ColumnSpecification(false, 1, 3, 0));

        specs.add(new ColumnSpecification(false, 0, 3, 0));
        GridPointGenerator generator = new GridPointGenerator(specs);

        IndexedDataset data = generator.generatePoints();

        for (DataPoint point: data.toList()) {

            System.out.println(point.toString());
        }
    }

    protected ArrayList<ColumnSpecification> gridSpecifications;

    public GridPointGenerator(ArrayList<ColumnSpecification> gridSpecications){
        this.gridSpecifications = gridSpecications;
    }

    protected int getNPointsToCompute(){
        int nPoint = 1;

        for (int iCol=0; iCol < this.gridSpecifications.size(); iCol++){

            int nPointToComputeInCol = this.gridSpecifications.get(iCol).getNPointToGenerate();
            System.out.println("iCol");
            System.out.println(iCol);
            System.out.println("n point to compute");
            System.out.println(nPointToComputeInCol);

            nPoint *= nPointToComputeInCol;
        }

        System.out.println("final npoint To compute");
        System.out.println(nPoint);

        return nPoint;
    }

    public IndexedDataset generatePoints(){

        ArrayList<double[]> cartesianProduct = new ArrayList();
        IndexedDataset.Builder builder = new IndexedDataset.Builder();

        this.movingIndice = 0;
        this.carry = 0;

        int nColumn = this.gridSpecifications.size();
        this.indices = new int[nColumn];

        System.out.println("Number of points to generate");
        for (int iCol = 0; iCol< nColumn; iCol++){

            cartesianProduct.add(this.gridSpecifications.get(iCol).generateValues());
            this.indices[iCol] = 0;

        }

        int iValue;

        int nPoint = Math.min(this.getNPointsToCompute(), 3000);

        System.out.println("N POINT GENERATE");
        System.out.println(nPoint);
        System.out.println("-----");
        for (int iPoint = 0; iPoint < nPoint ; iPoint++){

            double[] data = new double[nColumn];
            for (int iCol = 0; iCol< nColumn; iCol++){

                iValue = indices[iCol];
                data[iCol] = cartesianProduct.get(iCol)[iValue];
            }
            this.updateIndices(0);
            builder.add(iPoint, data);
        }

        IndexedDataset fakePointGrid = builder.build();

        return fakePointGrid;
    }

    protected void updateIndices(int colToUpdate){


        int nPoint = this.gridSpecifications.get(colToUpdate).getNPointToGenerate();
        int nColumn = this.gridSpecifications.size();
        this.indices[colToUpdate] = this.indices[colToUpdate] + 1;

        if (this.indices[colToUpdate] == nPoint){
            this.indices[colToUpdate] = 0;
            if (colToUpdate < nColumn - 1){
                this.updateIndices(colToUpdate +1);
            }
        }
    }
}
