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

package machinelearning.threesetmetric.TSM;

import machinelearning.classifier.Label;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuickCheck {

    public static void main(String[] args) {
        ArrayList<int[]> arraylist = new ArrayList<>();
        List<int[]> list = new ArrayList<>();
        for (int i=0; i<5; i++){
            arraylist.add(new int[]{i, i+1});
            list.add(new int[]{i, i+1});
        }
        System.out.println("arraylist: " + Arrays.deepToString(arraylist.toArray()));
        System.out.println("list: " + Arrays.deepToString(list.toArray()));
        System.out.println("====================================== ");
        list.set(3, null);
        arraylist.set(3, null);
        for(int[] ele : list){
            System.out.println("the index of list is: " + list.indexOf(ele) + "," + Arrays.toString(ele));
        }
        for(int[] ele : arraylist){
            System.out.println("the index of arraylist is: " + arraylist.indexOf(ele) + "," + Arrays.toString(ele));
        }

        Label a = Label.POSITIVE;
        System.out.println(a.getClass().getName());

        double[][] simplex = new double[3][];
        simplex[0] = new double[]{1,1};
        simplex[1] = new double[]{0,0};
        simplex[2] = new double[]{1,0};
        ConvexPolytope convexPolytope = new ConvexPolytope(2,simplex);
        System.out.println(convexPolytope.getClass().getName());
    }

}
