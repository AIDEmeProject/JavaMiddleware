package data;

public class LabeledPoint extends DataPoint {
    private int label;

    public LabeledPoint(int id, double[] data, int label) {
        super(id, data);
        setLabel(label);
    }

    public LabeledPoint(DataPoint point, int label) {
        this(point.getId(), point.getData(), label);
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        if (label < 0 || label > 1){
            throw new IllegalArgumentException();
        }
        this.label = label;
    }
}
