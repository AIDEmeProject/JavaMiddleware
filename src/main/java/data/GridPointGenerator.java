package data;


import java.util.ArrayList;
import java.util.Random;


public class GridPointGenerator{


    protected int[] indices;

    public static void main(String[] args){

        ArrayList<ColumnSpecification> specs = new ArrayList<>();
        specs.add(new ColumnSpecification(true, 0, 150, 75));
        specs.add(new ColumnSpecification(false, 0, 1, 0));
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
            nPoint *= this.gridSpecifications.get(iCol).getNPointToGenerate();
        }
        return nPoint;
    }

    public IndexedDataset buildGridOfFakePoints(){



        int nColumn = this.gridSpecifications.size();
        int nPointToCompute = this.getNPointsToCompute();
        System.out.println("N point to compute");
        System.out.println(nPointToCompute);

        System.out.println("N columns");
        System.out.println(nColumn);

        IndexedDataset.Builder builder = new IndexedDataset.Builder();

        for (int iPoint=0; iPoint < nPointToCompute; iPoint++){

            double[] data = new double[nColumn];
            for (int iCol = 0; iCol < nColumn; iCol++){

                data[iCol] = this.gridSpecifications.get(iCol).generateValue();
            }
            builder.add(iPoint, data);
        }

        IndexedDataset fakePointGrid = builder.build();

        return fakePointGrid;
    }


    public IndexedDataset generatePoints(){

        ArrayList<double[]> cartesianProduct = new ArrayList();
        IndexedDataset.Builder builder = new IndexedDataset.Builder();

        int nColumn = this.gridSpecifications.size();
        this.indices = new int[nColumn];
        for (int iCol = 0; iCol< nColumn; iCol++){
            cartesianProduct.add(this.gridSpecifications.get(iCol).generateValues());
            this.indices[iCol] = 0;
        }

        int iValue;
        for (int iPoint = 0; iPoint < this.getNPointsToCompute(); iPoint++){

            double[] data = new double[nColumn];
            for (int iCol = 0; iCol< nColumn; iCol++){

                iValue = indices[iCol];
                data[iCol] = cartesianProduct.get(iCol)[iValue];
                this.updateIndices(iCol);
            }
            builder.add(iPoint, data);
        }

        IndexedDataset fakePointGrid = builder.build();

        return fakePointGrid;
    }

    protected void updateIndices(int iCol){

        if (iCol >= this.gridSpecifications.size()){
            return;
        }

        int nPoint = this.gridSpecifications.get(iCol).getNPointToGenerate();

        int currentIndice = this.indices[iCol];
        if (currentIndice == nPoint -1){
            this.indices[iCol] = 0;
            this.updateIndices(iCol + 1);
        }
        else{
            this.indices[iCol] += 1;
        }

    }
}
