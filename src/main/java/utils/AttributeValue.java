package utils;


/**
 * This class combines the indices of attributes with their values
 */
public class AttributeValue implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Index of an attribute
     */
    public int index;

    /**
     * Value of an attribute
     */
    public double value;


    /**
     * Map between index and value
     */
    public AttributeValue(int index, double value) {
        this.index = index;
        this.value = value;
    }

    /**
     * Assign the index and value
     * @param index
     * @param value
     */
    public void set(int index, double value) {
        this.index = index;
        this.value = value;
    }

    public String toString() {
        return index + ":" + value;
    }
}
