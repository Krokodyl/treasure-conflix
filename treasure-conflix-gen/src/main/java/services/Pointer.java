package services;

public class Pointer {

    int offset;
    int value;

    public Pointer(int offset, int value) {
        this.offset = offset;
        this.value = value;
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
}
