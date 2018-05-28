package data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class LabeledData {
    private double[][] X;
    private int[] y;
    private LinkedHashSet<Integer> labeledRows;

    public LabeledData(double[][] X, int[] y) {
        this.X = X;
        this.y = y;
        labeledRows = new LinkedHashSet<>();
    }

    public LabeledData(int rows, int dim){
        if(rows < 1 || dim < 1){
            throw new IllegalArgumentException("Rows and dim must be positive numbers");
        }

        X = new double[rows][dim];
        y = new int[rows];
        labeledRows = new LinkedHashSet<>();
    }

    public double[][] getX() {
        return X;
    }

    public int[] getY() {
        return y;
    }

    public LinkedHashSet<Integer> getLabeledRows() {
        return labeledRows;
    }

    public int getNumRows(){
        return X.length;
    }

    public int getNumLabeledRows(){
        return labeledRows.size();
    }

    public int getNumUnlabeledRows(){
        return getNumRows() - getNumLabeledRows();
    }

    public int getDim(){
        return X[0].length;
    }

    public void addLabeledRow(int row){
        labeledRows.add(row);
    }

    public boolean rowIsLabeled(int row){
        return labeledRows.contains(row);
    }

    public void removeLabeledRow(int row){
        labeledRows.remove(row);
    }

    private class UnlabeledIterator<T> implements Iterator<T>{

        private int pos;
        private T[] array;

        UnlabeledIterator(T[] array) {
            this.array = array;
            this.pos = 0;
            movePosition();
        }

        private void movePosition(){
            while (labeledRows.contains(pos) && pos < array.length){
                pos++;
            }
        }

        public boolean hasNext() {
            return array.length > pos;
        }

        public T next() {
            T value = array[pos];
            movePosition();
            return value;
        }
    }

    public Iterator<double[]> iteratorUnlabeledX(){
        return new UnlabeledIterator<>(X);
    }

    public UnlabeledIterator<Integer> iteratorUnlabeledY(){
        Integer[] boxedY = Arrays.stream(y).boxed().toArray( Integer[]::new );
        return new UnlabeledIterator<>(boxedY);
    }
}
