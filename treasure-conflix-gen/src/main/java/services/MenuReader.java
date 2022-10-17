package services;

import enums.ByteType;
import org.apache.commons.lang.ArrayUtils;
import services.lz.LzCompressor;
import services.menu.Menu;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static services.Utils.*;

public class MenuReader {

    public static final int RAM_START = x("4800"); 
    
    public static boolean  verbose = true;

    List<Menu> menus = new ArrayList<>();
    
    public void generateMenuBytes() {
        int offsetData = 0;
        byte[] bytes;
        int id = 0;

        System.out.println(id++);
        Menu menu = new Menu(offsetData, readMenuFileData("src/main/resources/translations/menus/00-code.data"));
        offsetData += menu.getData().length;
        menus.add(menu);

        System.out.println(id++);
        menu = new Menu(offsetData, readMenuFile("translations/menus/01-bridge-menu.txt"));
        menu.addPointer(x("6CC7"), offsetData);
        offsetData += menu.getData().length;
        menus.add(menu);

        System.out.println(id++);
        menu = new Menu(offsetData, readMenuFile("translations/menus/02-save-menu.txt"));
        menu.addPointer(x("6EC5"), offsetData);
        menu.addPointer(x("6EEB"), offsetData+x("25"));
        int extraBytes = 6;
        menu.addPointer(x("6B28"), offsetData+x("15")+extraBytes);
        menu.addPointer(x("6B52"), offsetData+x("33")+extraBytes+4);
        menu.addPointer(x("6B6A"), offsetData+x("50")+extraBytes+8);
        menu.addPointer(x("6B82"), offsetData+x("6D")+extraBytes+12);
        menu.addPointer(x("6B9A"), offsetData+x("8A")+extraBytes+16);
        
        menu.addPointer(x("6EE2"), offsetData+menu.getData().length-1);
        
        offsetData += menu.getData().length;
        menus.add(menu);

        System.out.println(id++);
        menu = new Menu(offsetData, readMenuFile("translations/menus/03-load-menu.txt"));
        menu.addPointer(x("6FAA"), offsetData);
        extraBytes = 4;
        menu.addPointer(x("6B4C"), offsetData+x("29")+extraBytes+4);
        menu.addPointer(x("6B64"), offsetData+x("46")+extraBytes+8);
        menu.addPointer(x("6B7C"), offsetData+x("63")+extraBytes+12);
        menu.addPointer(x("6B94"), offsetData+x("80")+extraBytes+16);
        offsetData += menu.getData().length;
        menus.add(menu);

        System.out.println(id++);
        menu = new Menu(offsetData, readMenuFile("translations/menus/04-free-space.txt"));
        menu.addPointer(x("6D87"), offsetData);
        offsetData += menu.getData().length;
        menus.add(menu);
        
        // Manual pointers
        byte[] manualPointers = new byte[24];
        int manualPointerIndex = 0;
        Menu menuManualPointer = new Menu(offsetData, manualPointers);
        menuManualPointer.addPointer(x("6C39"), offsetData);
        menuManualPointer.addPointer(x("6C47"), offsetData+6);
        menuManualPointer.addPointer(x("6C55"), offsetData+14);
        offsetData += menuManualPointer.getData().length;
        menus.add(menuManualPointer);

        System.out.println(id++);
        manualPointers[manualPointerIndex++]=Utils.getPointerByte(RAM_START+offsetData, ByteType.LEFT);
        manualPointers[manualPointerIndex++]=Utils.getPointerByte(RAM_START+offsetData, ByteType.RIGHT);
        menu = new Menu(offsetData, readMenuFile("translations/menus/05-manual-map-1.txt"));
        offsetData += menu.getData().length;
        menus.add(menu);

        System.out.println(id++);
        manualPointers[manualPointerIndex++]=Utils.getPointerByte(RAM_START+offsetData, ByteType.LEFT);
        manualPointers[manualPointerIndex++]=Utils.getPointerByte(RAM_START+offsetData, ByteType.RIGHT);
        menu = new Menu(offsetData, readMenuFile("translations/menus/06-manual-map-2.txt"));
        offsetData += menu.getData().length;
        menus.add(menu);
        manualPointers[manualPointerIndex++]=0;
        manualPointers[manualPointerIndex++]=0;

        System.out.println(id++);
        manualPointers[manualPointerIndex++]=Utils.getPointerByte(RAM_START+offsetData, ByteType.LEFT);
        manualPointers[manualPointerIndex++]=Utils.getPointerByte(RAM_START+offsetData, ByteType.RIGHT);
        menu = new Menu(offsetData, readMenuFile("translations/menus/07-manual-combat-1.txt"));
        offsetData += menu.getData().length;
        menus.add(menu);

        System.out.println(id++);
        manualPointers[manualPointerIndex++]=Utils.getPointerByte(RAM_START+offsetData, ByteType.LEFT);
        manualPointers[manualPointerIndex++]=Utils.getPointerByte(RAM_START+offsetData, ByteType.RIGHT);
        menu = new Menu(offsetData, readMenuFile("translations/menus/08-manual-combat-2.txt"));
        offsetData += menu.getData().length;
        menus.add(menu);

        System.out.println(id++);
        manualPointers[manualPointerIndex++]=Utils.getPointerByte(RAM_START+offsetData, ByteType.LEFT);
        manualPointers[manualPointerIndex++]=Utils.getPointerByte(RAM_START+offsetData, ByteType.RIGHT);
        menu = new Menu(offsetData, readMenuFile("translations/menus/09-manual-combat-3.txt"));
        offsetData += menu.getData().length;
        menus.add(menu);
        manualPointers[manualPointerIndex++]=0;
        manualPointers[manualPointerIndex++]=0;

        System.out.println(id++);
        manualPointers[manualPointerIndex++]=Utils.getPointerByte(RAM_START+offsetData, ByteType.LEFT);
        manualPointers[manualPointerIndex++]=Utils.getPointerByte(RAM_START+offsetData, ByteType.RIGHT);
        menu = new Menu(offsetData, readMenuFile("translations/menus/10-manual-town-1.txt"));
        offsetData += menu.getData().length;
        menus.add(menu);

        System.out.println(id++);
        manualPointers[manualPointerIndex++]=Utils.getPointerByte(RAM_START+offsetData, ByteType.LEFT);
        manualPointers[manualPointerIndex++]=Utils.getPointerByte(RAM_START+offsetData, ByteType.RIGHT);
        menu = new Menu(offsetData, readMenuFile("translations/menus/11-manual-town-2.txt"));
        offsetData += menu.getData().length;
        menus.add(menu);

        System.out.println(id++);
        manualPointers[manualPointerIndex++]=Utils.getPointerByte(RAM_START+offsetData, ByteType.LEFT);
        manualPointers[manualPointerIndex++]=Utils.getPointerByte(RAM_START+offsetData, ByteType.RIGHT);
        menu = new Menu(offsetData, readMenuFile("translations/menus/12-manual-town-3.txt"));
        offsetData += menu.getData().length;
        menus.add(menu);

        System.out.println(id++);
        manualPointers[manualPointerIndex++]=Utils.getPointerByte(RAM_START+offsetData, ByteType.LEFT);
        manualPointers[manualPointerIndex++]=Utils.getPointerByte(RAM_START+offsetData, ByteType.RIGHT);
        menu = new Menu(offsetData, readMenuFile("translations/menus/13-manual-town-4.txt"));
        offsetData += menu.getData().length;
        menus.add(menu);
        manualPointers[manualPointerIndex++]=0;
        manualPointers[manualPointerIndex++]=0;

        menuManualPointer.setData(manualPointers);
        if (verbose) System.out.println("Manual pointers : "+bytesToHex(menuManualPointer.getData()));

        int equipmentOffset = offsetData + 4;
        Menu equipmentPointer = new Menu(offsetData, new byte[]{
                Utils.getPointerByte(RAM_START+equipmentOffset, ByteType.LEFT),
                Utils.getPointerByte(RAM_START+equipmentOffset, ByteType.RIGHT),
                0,0});
        offsetData += equipmentPointer.getData().length;
        menus.add(equipmentPointer);
                
        System.out.println(id++);
        menu = new Menu(offsetData, readMenuFile("translations/menus/14-equipment.txt"));
        menu.addPointer(x("6C2B"), equipmentOffset-4);
        menu.addPointer(x("6FD5"), equipmentOffset-4+x("14")+4);
        menu.addPointer(x("6FDF"), equipmentOffset-4+x("3B")+8+3);
        menu.addPointer(x("6FE9"), equipmentOffset-4+x("63")-1+12+3+4);
        menu.addPointer(x("6FF3"), equipmentOffset-4+x("89")+16+3+4+4);
        menu.addPointer(x("7004"), equipmentOffset-4+x("B1")+22+3+4+4);
        offsetData += menu.getData().length;
        menus.add(menu);

        System.out.println(id++);
        menu = new Menu(offsetData, readMenuFile("translations/menus/15-equipment-list.txt"));
        menu.addPointer(x("702B"), offsetData);
        offsetData += menu.getData().length;
        menus.add(menu);

        System.out.println(id++);
        menu = new Menu(offsetData, readMenuFile("translations/menus/16-introduction.txt"));
        menu.addPointer(x("7C24"), offsetData);
        offsetData += menu.getData().length;
        menus.add(menu);

        System.out.println(id++);
        menu = new Menu(offsetData, readMenuFile("translations/menus/17-locations.txt"));
        String names = "";
        menu.addPointer(x("8068")+14, offsetData);
        names += "    Razzle Town ";
        menu.addPointer(x("8068")+6, offsetData+names.length());
        names += "    Landos Base ";
        menu.addPointer(x("8068")+4, offsetData+names.length());
        names += "    Kazusa Base ";
        menu.addPointer(x("8068")+12, offsetData+names.length());
        names += "    Fort Fatras ";
        menu.addPointer(x("8068"), offsetData+names.length());
        names += "Peppermint Junktown ";
        menu.addPointer(x("8068")+10, offsetData+names.length());
        names += "    Elder Village ";
        menu.addPointer(x("8068")+8, offsetData+names.length());
        names += "     Cloud Temple ";
        menu.addPointer(x("8068")+2, offsetData+names.length());
        names += "     Salvage Ship ";
        menu.addPointer(x("8068")+16, offsetData+names.length());
        offsetData += menu.getData().length;
        menus.add(menu);

        System.out.println(id++);
        menu = new Menu(offsetData, readMenuFileData("src/main/resources/translations/menus/99-code.data"));
        menu.addPointer(x("7FC0"), offsetData);
        offsetData += menu.getData().length;
        menus.add(menu);

        bytes = new byte[0];
        for (Menu m : menus) {
            bytes = ArrayUtils.addAll(bytes, m.getData());
        }


        DataWriter.saveData("src/main/resources/translations/menus/decompressed/95B7.data", bytes);
    }

    public byte[] readMenuFileData(String file) {
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(new File(file).toPath());
        } catch (IOException ex) {
            Logger.getLogger(Conflix.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bytes;
    }
    
    public byte[] readMenuFile(String file) {
        byte[] bytes = new byte[0];
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            Objects.requireNonNull(Conflix.class.getClassLoader().getResourceAsStream(file)), StandardCharsets.UTF_8));
            String line = br.readLine();
            while (line!=null) {
                bytes = ArrayUtils.addAll(bytes, ShiftJIS.convertEnglishToBytes(line));
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public void compressMenu() {
        byte[] bytes = readMenuFileData("src/main/resources/translations/menus/decompressed/95B7.data");
        LzCompressor compressor = new LzCompressor();
        try {
            byte[] compressData = compressor.compressData(bytes, false);
            DataWriter.saveData("src/main/resources/translations/menus/compressed/95B7.data", compressData);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    public void writeMenu(byte[] data) {
        byte[] bytes = readMenuFileData("src/main/resources/translations/menus/compressed/95B7.data");
        int offset = x("95B7");
        for (byte b : bytes) {
            data[offset++] = b;
        }
        data[x("7032")+1] = 0x13; 

    }

    public void writeMenuPointers(byte[] data) {
        for (Menu menu : menus) {
            Map<Integer, Integer> pointers = menu.getPointers();
            for (Map.Entry<Integer, Integer> e : pointers.entrySet()) {
                int offset = e.getKey()+1; // +1 to skip the Ax op code
                int value = e.getValue()+MenuReader.RAM_START;
                data[offset] = Utils.getPointerByte(value, ByteType.LEFT);
                data[offset+1] = Utils.getPointerByte(value, ByteType.RIGHT);
            }
        }
    }
}
