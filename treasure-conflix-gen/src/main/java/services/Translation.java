package services;

import java.util.HashSet;
import java.util.Set;

public class Translation {
    
    String pointerFile = "";
    //int pointerOffset;
    //int pointerValue;
    Set<Pointer> pointers = new HashSet<>();
    boolean globalPointer = false;
    
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

    public boolean isGlobalPointer() {
        return globalPointer;
    }

    public void setGlobalPointer(boolean globalPointer) {
        this.globalPointer = globalPointer;
    }

    public Set<Pointer> getPointers() {
        return pointers;
    }

    public void addPointer(Pointer pointer) {
        this.pointers.add(pointer);
    }

    @Override
    public String toString() {
        return "Translation{" +
                "pointerFile='" + pointerFile + '\'' +
                ", globalPointer=" + globalPointer +
                ", dataFile='" + dataFile + '\'' +
                ", dataOffset=" + dataOffset +
                ", english='" + english + '\'' +
                '}';
    }
}
