package services.lz;

import com.google.common.primitives.Bytes;

public class FooterCommand extends services.lz.HeaderCommand {

    int commandCount;

    public FooterCommand(byte a, byte b) {
        super(a, b);
    }

    public FooterCommand(int length) {
        super(length);
    }

    @Override
    public byte[] getBytes() {
        byte[] b = super.getBytes();
        byte[] a = new byte[1];
        a[0] = (byte) (commandCount & 0xFF);
        return Bytes.concat(a, b);
    }

    public int getCommandCount() {
        return commandCount;
    }

    public void setCommandCount(int commandCount) {
        this.commandCount = commandCount;
    }

    @Override
    public String toString() {
        return "FooterCommand{" +
                "commandCount=" + commandCount +
                ", length=" + length +
                '}';
    }
}
