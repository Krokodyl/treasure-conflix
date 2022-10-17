package services.menu;

import java.util.Map;
import java.util.TreeMap;

import static services.MenuReader.RAM_START;
import static services.MenuReader.verbose;
import static services.Utils.bytesToHex;
import static services.Utils.h;

public class Menu {

    byte[] data;
    int offsetData;
    
    // Map PointerOffset, PointerValue
    Map<Integer, Integer> pointers = new TreeMap<>();


    public Menu(int offsetData, byte[] data) {
        if (verbose) System.out.println("MENU - "+h(offsetData)+"/"+h(RAM_START+offsetData)+" - "+bytesToHex(data));
        this.offsetData = offsetData;
        this.data = data;
    }
    
    public void addPointer(int offset, int value) {
        pointers.put(offset, value);
    }

    public int getOffsetData() {
        return offsetData;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Map<Integer, Integer> getPointers() {
        return pointers;
    }
    
    public void printPointers() {
        for (Map.Entry<Integer, Integer> e : pointers.entrySet()) {
            System.out.println(h(e.getKey())+":"+h(e.getValue()));
        }

    }
}
