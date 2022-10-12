package services;

import enums.PointerTableType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PointerTable {

    int offsetStart;
    int offsetEnd;
    int shift;
    
    int newOffsetStart;
    
    PointerTableType type;
    
    Map<Integer, PointerEntry> pointers = new TreeMap<>();

    public PointerTable(int offsetStart, int offsetEnd, int shift, int newOffsetStart) {
        this.offsetStart = offsetStart;
        this.offsetEnd = offsetEnd;
        this.shift = shift;
        this.newOffsetStart = newOffsetStart;
    }

    public PointerTableType getType() {
        return type;
    }

    public void setType(PointerTableType type) {
        this.type = type;
    }

    public int getOffsetStart() {
        return offsetStart;
    }

    public void setOffsetStart(int offsetStart) {
        this.offsetStart = offsetStart;
    }

    public int getOffsetEnd() {
        return offsetEnd;
    }

    public void setOffsetEnd(int offsetEnd) {
        this.offsetEnd = offsetEnd;
    }

    public int getShift() {
        return shift;
    }

    public void setShift(int shift) {
        this.shift = shift;
    }

    public int getNewOffsetStart() {
        return newOffsetStart;
    }

    public void setNewOffsetStart(int newOffsetStart) {
        this.newOffsetStart = newOffsetStart;
    }

    public void add(PointerEntry pointer) {
        pointers.put(pointer.getOffset(), pointer);
    }
    
    public void applyTranslation(Map<Integer, Translation> translationMap) {
        for (Map.Entry<Integer, PointerEntry> e : pointers.entrySet()) {
            PointerEntry p = e.getValue();
            int offset = p.getOffset();
            
        }
    }
    
}
