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
