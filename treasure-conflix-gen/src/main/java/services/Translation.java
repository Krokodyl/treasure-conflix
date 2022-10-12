package services;

public class Translation {
    
    String pointerFile = "";
    int pointerOffset;
    int pointerValue;
    
    String dataFile = "";
    int dataOffset;
    byte[] dataValue;
    
    String japanese;
    String english;

    public String getPointerFile() {
        return pointerFile;
    }

    public void setPointerFile(String pointerFile) {
        this.pointerFile = pointerFile;
    }

    public int getPointerOffset() {
        return pointerOffset;
    }

    public void setPointerOffset(int pointerOffset) {
        this.pointerOffset = pointerOffset;
    }

    public int getPointerValue() {
        return pointerValue;
    }

    public void setPointerValue(int pointerValue) {
        this.pointerValue = pointerValue;
    }

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    public int getDataOffset() {
        return dataOffset;
    }

    public void setDataOffset(int dataOffset) {
        this.dataOffset = dataOffset;
    }

    public byte[] getDataValue() {
        return dataValue;
    }

    public void setDataValue(byte[] dataValue) {
        this.dataValue = dataValue;
    }

    public String getJapanese() {
        return japanese;
    }

    public void setJapanese(String japanese) {
        this.japanese = japanese;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }
}
