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
                values[i] = i + 1   ;
            }

        }
        return values;
    }
}


