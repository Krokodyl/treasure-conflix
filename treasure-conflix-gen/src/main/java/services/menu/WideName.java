package services.menu;

public class WideName {
    
    
    String name;
    int offsetData;
    int offsetCode;
    byte[] header;

    public WideName(String name, int offsetData, int offset, byte[] header) {
        this.name = name;
        this.offsetData = offsetData;
        this.offsetCode = offset;
        this.header = header;
    }

    public void setOffsetData(int offsetData) {
        this.offsetData = offsetData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOffsetData() {
        return offsetData;
    }

    public int getOffsetCode() {
        return offsetCode;
    }

    public void setOffsetCode(int offsetCode) {
        this.offsetCode = offsetCode;
    }

    public byte[] getHeader() {
        return header;
    }

    public void setHeader(byte[] header) {
        this.header = header;
    }
}
