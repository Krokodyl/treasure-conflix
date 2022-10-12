package services.lz;

import static services.Utils.b;
import static services.Utils.x;

public enum REPEAT_ALGORITHM {
    
    REPEAT_ALGORITHM_SIZE_4BITS(4, (byte) 0x0F, b("10")),
    REPEAT_ALGORITHM_SIZE_3BITS(3, (byte) 0x07, b("10"));
    
    int shift;
    byte mask;
    byte multiplier;

    REPEAT_ALGORITHM(int value, byte mask, byte multiplier) {
        this.shift = value;
        this.mask = mask;
        this.multiplier = multiplier;
    }

    public static RepeatCommand buildRepeatCommand(byte a, byte b, REPEAT_ALGORITHM algorithm) {
        /*if (algorithm==3) return buildRepeatCommand3(a, b);
        else return buildRepeatCommand4(a, b);*/
        int length = ((b & 0xFF) >>> algorithm.getShift()) + 3;
        int shift = ((b & algorithm.getMask()) * x("100")) + (a & 0xFF);
        RepeatCommand repeatCommand = new RepeatCommand(shift, length, algorithm);
        return repeatCommand;
    }

    public int getShift() {
        return shift;
    }

    public byte getMask() {
        return mask;
    }

    public byte getMultiplier() {
        return multiplier;
    }
}
