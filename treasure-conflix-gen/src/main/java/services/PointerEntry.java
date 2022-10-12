package services;

public class PointerEntry {
    
    int offset;
    int value;
    int shift;
    byte[] data;
    
    int newValue;
    byte[] newData;

    public PointerEntry(int offset, int value, int shift) {
        this.offset = offset;
        this.value = value;
        this.shift = shift;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getShift() {
        return shift;
    }

    public void setShift(int shift) {
        this.shift = shift;
    }

    public int getNewValue() {
        return newValue;
    }

    public void setNewValue(int newValue) {
        this.newValue = newValue;
    }
}
