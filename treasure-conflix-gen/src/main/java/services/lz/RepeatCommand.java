package services.lz;

import services.Utils;

public class RepeatCommand extends services.lz.Command {
    
    int shift;
    int length;
    
    REPEAT_ALGORITHM algorithm;

    public RepeatCommand(int shift, int length, REPEAT_ALGORITHM algorithm) {
        this.shift = shift;
        this.length = length;
        this.algorithm = algorithm;
    }

    public byte[] getBytes() {
        byte[] bytes = new byte[2];
        byte a = (byte) ((shift >>> 8) & algorithm.getMask());
        a = (byte) (a | ((length-3 & 0xFF) * algorithm.getMultiplier()));
        byte b = (byte) (shift & 0xFF);
        bytes[0] = b;
        bytes[1] = a;
        return bytes;
    }

    public int getShift() {
        return shift;
    }

    public void setShift(int shift) {
        this.shift = shift;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "RepeatCommand{" +
                "shift=" + shift +
                ", length=" + length +
                ", bytes=" + Utils.bytesToHex(getBytes()) +
                '}';
    }
}
