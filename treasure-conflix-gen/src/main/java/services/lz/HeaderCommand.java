package services.lz;

import static services.Utils.h;
import static services.Utils.x;

public class HeaderCommand extends Command {

    int length;
    
    public HeaderCommand(byte a, byte b) {
        length = (b & 0xFF)*x("100")+(a & 0xFF);
    }
    
    public HeaderCommand(int length) {
        this.length = length;
    }
    
    @Override
    public byte[] getBytes() {
        byte[] bytes = new byte[2];
        int a = length % x("100");
        int b = length / x("100");
        bytes[0] = (byte) (a & 0xFF);
        bytes[1] = (byte) (b & 0xFF);
        return bytes;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "HeaderCommand{" +
                "length="+ length + " (" + h(length) + ")" + 
                '}';
    }
}
