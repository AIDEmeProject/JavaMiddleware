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