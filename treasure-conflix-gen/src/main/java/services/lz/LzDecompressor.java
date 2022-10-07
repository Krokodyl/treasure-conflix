package services.lz;

import services.DataWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static services.Utils.*;

public class LzDecompressor {

    ByteArrayOutputStream decompressedData = new ByteArrayOutputStream();
    
    final static int REPEAT_BIT = 0x01;
    final static int WRITE_BIT = 0x00;
    
    int end = 0;
    boolean verbose = false;

    public void decompressData(byte[] input, int start, boolean verbose) {
        this.verbose = verbose;
        decompressData(input, start);
    }
    
    public void decompressData(byte[] input, int start) {
        decompressedData = new ByteArrayOutputStream();
        List<Command> commands = buildCommands(input, start);
        try {
            processCommands(commands);
            byte[] bytes = decompressedData.toByteArray();
            byte[] first = Arrays.copyOfRange(bytes, 0, 20*16);
            DataWriter.saveData("src/main/resources/gen/"+h(start)+".data", bytes);
            System.out.println(h(start)+"-"+h(end)+"\t\t"+h(bytes.length)+"\t"+bytesToHex(bytes));
            //System.out.println(h(start)+"-"+h(end)+"\t\t"+h(bytes.length)+"\t");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Command> buildCommands(byte[] input, int start) {
        return buildCommands(input, start, start, 10, 0);
    }
    
    public List<Command> buildCommands(byte[] input, int start, int offset, int commandCount, int algorithm) {
        List<Command> commands = new ArrayList<>();
        //int offset = start;
        HeaderCommand headerCommand = new HeaderCommand(input[offset++],input[offset++]);
        int offsetEnd = start+headerCommand.getLength();
        this.end = offsetEnd+2;
        //System.out.println("input[offsetEnd+2] = "+h(input[offsetEnd+2]));
        if (algorithm==0) algorithm =  ((input[offsetEnd+2] & 0xC0) == 0) ? 4 : 3;
        boolean end = false;
        int count = 0;
        while (!end) {
            FlagCommand flagCommand = new FlagCommand(input[offset++]);
            if (verbose) System.out.println(flagCommand);
            count = 0;
            while (count<8 && count<commandCount) {
                int bit = flagCommand.getBit(count++);
                if (bit==WRITE_BIT) {
                    WriteCommand writeCommand = new WriteCommand(input[offset++]);
                    if (verbose) System.out.println(writeCommand);
                    commands.add(writeCommand);
                }
                else {
                    RepeatCommand repeatCommand = buildRepeatCommand(algorithm, input[offset++], input[offset++]);
                    if (verbose) System.out.println(repeatCommand);
                    commands.add(repeatCommand);
                }
            }
            if (offset>=offsetEnd || count==commandCount) end = true;
            //end = true;
        }
        this.end = offset;
        if (offset<input.length) {
            int endCommandCount = input[offset++];
            if ((endCommandCount & 0x3F) > 0) {
                commands.addAll(buildCommands(input, start, offset, endCommandCount & 0x3F, algorithm));
            }
        }

        //System.out.println("\t\t"+h(start)+"\t"+h(offsetEnd-2)+"\t\t");
        if (verbose) System.out.println("end\t"+h(offsetEnd-2));
        if (verbose) System.out.println(h(offset));
        
        return commands;
    }
    
    public void processCommands(List<Command> commands) throws IOException {
        for (Command command : commands) {
            if (command instanceof WriteCommand) {
                WriteCommand writeCommand = (WriteCommand) command;
                decompressedData.write(writeCommand.getBytes());
            }
            if (command instanceof RepeatCommand) {
                RepeatCommand repeatCommand = (RepeatCommand) command;
                int shift = repeatCommand.getShift();
                int length = repeatCommand.getLength();
                byte[] output = decompressedData.toByteArray();
                int repeatStart = output.length-shift;
                if (repeatStart<0) repeatStart=0;
                int repeatIndex = repeatStart;
                while (length>0) {
                    byte data = output[repeatIndex++];
                    if (repeatIndex>output.length-1) repeatIndex=repeatStart;
                    decompressedData.write(data);
                    length--;
                }
            }
        }
    }

    public RepeatCommand buildRepeatCommand(int algorithm, byte a,byte b) {
        if (algorithm==3) return buildRepeatCommand3(a, b);
        else return buildRepeatCommand4(a, b);
    }

    public RepeatCommand buildRepeatCommand4(byte a,byte b) {
        int length = ((b & 0xFF) >>> 4) + 3;
        int shift = ((b & 0x0F) * x("100")) + (a & 0xFF);
        RepeatCommand repeatCommand = new RepeatCommand(shift, length);
        return repeatCommand;
    }

    public RepeatCommand buildRepeatCommand3(byte a,byte b) {
        int length = ((b & 0xFF) >>> 3) + 3;
        int shift = ((b & 0x07) * x("100")) + (a & 0xFF);
        RepeatCommand repeatCommand = new RepeatCommand(shift, length);
        return repeatCommand;
    }

    public byte[] getDecompressedData() {
        return decompressedData.toByteArray();
    }
}
