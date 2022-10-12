package services.lz;

import com.google.common.primitives.Bytes;
import services.DataWriter;
import services.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static services.Utils.h;
import static services.Utils.x;
import static services.lz.REPEAT_ALGORITHM.buildRepeatCommand;

public class LzCompressor {
    
    // input
    byte[] data;

    final int WINDOW_SIZE = 4095; 
    //final int BUFFER_SIZE = 18; // binary: 1111 + BUFFER_MIN_SIZE
    final int BUFFER_MIN_SIZE = 3;
    final int BUFFER_SIZE = x("F")+BUFFER_MIN_SIZE;
    
    boolean SKIP_FOOTER_COUNT = false;
    
    REPEAT_ALGORITHM repeatAlgorithm = REPEAT_ALGORITHM.REPEAT_ALGORITHM_SIZE_4BITS;
    
    public byte[] compressData(byte[] data, boolean verbose) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<Command> commandList = new ArrayList<>();
        int bufferStart=0, bufferEnd=BUFFER_SIZE-1;
        int windowStart = 0;

        byte[] bytes = new byte[1];
        bytes[0] = data[bufferStart];
        commandList.add(new WriteCommand(bytes));
        bufferStart++;
        
        bufferEnd = bufferStart+BUFFER_SIZE;
        windowStart = bufferEnd-1-WINDOW_SIZE;
        if (windowStart<0) windowStart=0;
        int windowEnd = windowStart + WINDOW_SIZE;
        if (windowEnd>=bufferEnd-1) windowEnd=bufferEnd-1;
        byte[] window = getWindow(data, windowStart, windowEnd);
        
        while (bufferStart<data.length){
            boolean found = false;
            boolean endSearch = false;
            int index = -1;
            while (!found && !endSearch) {
                byte[] buffer = getBuffer(data, bufferStart, bufferEnd);
                windowStart = bufferEnd - 1 - WINDOW_SIZE;
                if (windowStart < 0) windowStart = 0;
                windowEnd = windowStart + WINDOW_SIZE;
                if (windowEnd >= bufferEnd - 1) windowEnd = bufferEnd - 1;
                window = getWindow(data, windowStart, windowEnd);
                index = Utils.lastIndexOf(window, buffer);
                if (buffer.length<=4)
                    index = Bytes.indexOf(window, buffer);
                    
                if (index < 0) {
                    bufferEnd--;
                    if (bufferEnd - bufferStart < BUFFER_MIN_SIZE) endSearch = true;
                } else {
                    if (bufferEnd - bufferStart < BUFFER_MIN_SIZE) endSearch = true;
                    else found = true;  
                }
            }
            if (!found) {
                bytes = new byte[1];
                bytes[0] = data[bufferStart];
                Command command = commandList.get(commandList.size()-1);
                boolean append = false;
                if (command instanceof WriteCommand) {
                    WriteCommand writeCommand = (WriteCommand) command;
                    //if (writeCommand.getData().length<126) writeCommand.appendData(bytes);
                    commandList.add(new WriteCommand(bytes));
                }
                else 
                commandList.add(new WriteCommand(bytes));
                bufferStart++;
            } else {
                RepeatCommand repeatCommand = new RepeatCommand(bufferStart - (windowStart + index), bufferEnd - bufferStart, repeatAlgorithm);
                //RepeatCommand repeatCommand = buildRepeatCommand(bufferStart - (windowStart + index), bufferEnd - bufferStart, repeatAlgorithm);
                commandList.add(repeatCommand);
                bufferStart = bufferEnd;
            }
            bufferEnd = bufferStart + BUFFER_SIZE;
            if (bufferEnd>data.length) bufferEnd=data.length;
        }
        
        List<Command> commandsWithFlags = new ArrayList<>();
        List<Command> tmpCommands = new ArrayList<>();
        List<Command> footerCommandsWithFlags = new ArrayList<>();
        int count = 0;
        int flags = 0;
        int byteCount = 0;
        for (Command command : commandList) {
            count++;
            if (command instanceof RepeatCommand) {
                flags = (flags | 0x80);
            }
            tmpCommands.add(command);
            if (count==8) {
                commandsWithFlags.add(new FlagCommand((byte) (flags & 0xFF)));
                //System.out.println("byteCount "+byteCount);
                commandsWithFlags.addAll(tmpCommands);
                tmpCommands.clear();
                count = 0;
                flags = 0;
            } else {
                flags = (flags >>> 1);
            }
        }
        byteCount=0;
        for (Command c : commandsWithFlags) {
            byteCount += c.getBytes().length;
        }


        if (verbose) System.out.println("byteCount="+h(byteCount));
        HeaderCommand headerCommand = new HeaderCommand(byteCount);
        out.write(headerCommand.getBytes());
        if (verbose) System.out.println(headerCommand);
        for (Command commandsWithFlag : commandsWithFlags) {
            if (verbose) System.out.println(commandsWithFlag);
            out.write(commandsWithFlag.getBytes());
            out.flush();
        }

        FooterCommand footerCommand = null;
        if (!tmpCommands.isEmpty()) {
            byteCount = 0;
            flags = 0xFF;
            for (Command tmpCommand : tmpCommands) {
                if (tmpCommand instanceof RepeatCommand) {
                    byteCount += 2;
                } else {
                    flags = (flags & 0x7F);
                    byteCount++;
                }
                flags = (flags >>> 1);
                flags = flags | 0x80;
            }
            flags = (flags >>> (8-tmpCommands.size()-1));
            byte fill = (byte) (0xFF << tmpCommands.size());
            flags = flags | fill;
            footerCommandsWithFlags.add(new FlagCommand((byte) (flags & 0xFF)));
            byteCount++;
            footerCommand = new FooterCommand(headerCommand.getLength()+2+3+byteCount);
            //footerCommand.setCommandCount(byteCount-1);
            footerCommand.setCommandCount(tmpCommands.size());
            footerCommandsWithFlags.addAll(tmpCommands);
        }

        //System.out.println(Utils.bytesToHex(headerCommand.getBytes()));
        
        if (footerCommand!=null) {
            //System.out.println(Utils.bytesToHex(footerCommand.getBytes()));
            if (!SKIP_FOOTER_COUNT) out.write(footerCommand.getBytes());
            if (verbose) System.out.println(footerCommand);
            for (Command footerCommandsWithFlag : footerCommandsWithFlags) {
                out.write(footerCommandsWithFlag.getBytes());
                if (verbose) System.out.println(footerCommandsWithFlag);
                out.flush();
            }
        }

        out.write(0);
        out.flush();

        /*for (Command commandsWithFlag : commandsWithFlags) {
            System.out.println(commandsWithFlag);
        }*/


        
        
        
        
        for (Command command : commandList) {
            byte[] commandBytes = command.getBytes();
            //System.out.println(Utils.bytesToHex(commandBytes));
            //out.write(commandBytes);
            //out.flush();
        }
        if (verbose) System.out.printf("LzCompress %s bytes (%s)\n", data.length, Integer.toHexString(data.length));
        if (verbose) System.out.printf("Compressed size : %s (%s)\n",out.toByteArray().length, Integer.toHexString(out.toByteArray().length));
        return out.toByteArray();

    }

    private byte[] getBuffer(byte[] data, int bufferStart, int bufferEnd) {
        return Arrays.copyOfRange(data, bufferStart, bufferEnd);
    }

    private byte[] getWindow(byte[] data, int windowStart, int windowEnd) {
        return Arrays.copyOfRange(data, windowStart, windowEnd);
    }

    public static void main(String[] args) throws IOException {
        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(new File("src/main/resources/gen/18EBE.data").toPath());
        } catch (IOException ex) {
            Logger.getLogger(LzCompressor.class.getName()).log(Level.SEVERE, null, ex);
        }
        LzCompressor compressor = new LzCompressor();
        byte[] compressed = compressor.compressData(data, false);
        DataWriter.saveData("src/main/resources/gen/comp.data",compressed);

        System.out.println("decomp");
        LzDecompressor decompressor = new LzDecompressor();
        decompressor.decompressData(compressed, x("0"));
        byte[] decompressed = decompressor.getDecompressedData();
        DataWriter.saveData("src/main/resources/gen/decomp.data", decompressed);
        
        /*Decompressor decompressor = new Decompressor();
        byte[] decompressed = decompressor.decompress(compressed);
        DataWriter.saveData("src/main/resources/gen/lz/decomp.data", decompressed);*/

    }
    
}
