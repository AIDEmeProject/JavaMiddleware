package data;

public class LabeledPoint extends DataPoint {
    private int label;

    public LabeledPoint(long id, double[] data, int label) {
        super(id, data);
        setLabel(label);
    }

    public LabeledPoint(DataPoint point, int label) {
        super(point.getId(), point.getData());
        this.label = label;
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
