package services;

import services.lz.LzDecompressor;

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

public class Conflix {
    
    static byte[] data;
    
    public static void main(String[] args) {
        String rom = "D:\\git\\treasure-conflix\\roms\\BS Treasure Conflix - copie.sfc";
        try {
            data = Files.readAllBytes(new File(rom).toPath());
        } catch (IOException ex) {
            Logger.getLogger(Conflix.class.getName()).log(Level.SEVERE, null, ex);
        }
        //analyzeBytePairs();
        LzDecompressor lzDecompressor = new LzDecompressor();
        //lzDecompressor.decompressData(data, x("70100"));

        analyzeAll();
        //listPointers();

    }
    
    public static void listPointers() {
        for (Integer i : readTable(data, x("50000"), x("5001A"))) {
            System.out.print(h(i) + "\t");
        }
        System.out.println();
        for (Integer i : readTable(data, x("50100"), x("50123"))) {
            System.out.print(h(i) + "\t");
        }
        System.out.println();
        for (Integer i : readTable(data, x("50200"), x("50220"))) {
            System.out.print(h(i) + "\t");
        }
    }
    
    public static void analyzeAll() {
        LzDecompressor lzDecompressor = new LzDecompressor();
        for (Integer i : readTable(data, x("18000"), x("1802F"))) {
            System.out.print(h(i) + "\t");
            lzDecompressor.decompressData(data, i);
        }
        System.out.println();
        for (Integer i : readTable(data, x("18100"), x("1812F"))) {
            System.out.print(h(i) + "\t");
            lzDecompressor.decompressData(data, i);
        }
        System.out.println();
        for (Integer i : readTable(data, x("18200"), x("1822F"))) {
            System.out.print(h(i) + "\t");
            lzDecompressor.decompressData(data, i);
        }
        System.out.println();
        for (Integer i : readTable(data, x("18300"), x("1832F"))) {
            System.out.print(h(i) + "\t");
            lzDecompressor.decompressData(data, i);
        }
        System.out.println();
        for (Integer i : readTable(data, x("18400"), x("1842F"))) {
            System.out.print(h(i) + "\t");
            lzDecompressor.decompressData(data, i);
        }
        System.out.println();
        for (Integer i : readTable(data, x("18500"), x("1852F"))) {
            System.out.print(h(i) + "\t");
            lzDecompressor.decompressData(data, i);
        }
        System.out.println();
        for (Integer i : readTable(data, x("18600"), x("1862F"))) {
            System.out.print(h(i) + "\t");
            lzDecompressor.decompressData(data, i);
        }
        System.out.println();
        for (Integer i : readTable(data, x("18700"), x("1872F"))) {
            System.out.print(h(i) + "\t");

        }
        for (Integer i : readTable(data, x("50000"), x("5001A"))) {
            //System.out.print(h(i) + "\t");
            lzDecompressor.decompressData(data, i);
        }
        System.out.println();
        for (Integer i : readTable(data, x("50100"), x("50123"))) {
            //System.out.print(h(i) + "\t");
            lzDecompressor.decompressData(data, i);
        }
        System.out.println();
        for (Integer i : readTable(data, x("50200"), x("50220"))) {
            //System.out.print(h(i) + "\t");
            lzDecompressor.decompressData(data, i);
        }

        for (Integer i : readTable(data, x("FFF00"), x("FFFE6"))) {
            try {
                //System.out.print(h(i)+"\t");
                lzDecompressor.decompressData(data, i);
            } catch (Exception e) {
                System.out.println("Error " +h(i));
            }

        }
    }
    
    public static List<Integer> readTable(byte[] data, int start, int end) {
        List<Integer> offsets = new ArrayList<>();
        int offset = start;
        while (offset<end) {
            int a = data[offset];
            int b = data[offset+1];
            int c = data[offset+2];
            c = c - x("C0");
            int offsetData = (c & 0xFF)*x("10000") + (b & 0xFF)*x("100")+(a & 0xFF);
            offsets.add(offsetData);
            offset += 3;
        }
        return offsets;
    }
    
    public static void analyzeBytePairs(){
        int length = 12;
        int offset = 0;
        while (offset+length*2<data.length) {
            byte[] bytes = Arrays.copyOfRange(data, offset, offset + length*2);
            BytePair[] pairs = new BytePair[length];
            for (int i = 0, bytesLength = bytes.length; i < bytesLength; i = i+2) {
                byte a = bytes[i];
                byte b = bytes[i+1];
                pairs[i/2] = new BytePair(a,b);
            }
            
            if (!allPairs(pairs, (byte) 0)
                    && !allPairs(pairs, (byte) 0xFF)
                    && pairs[0].equals(pairs[6])
                    && pairs[1].equals(pairs[4])
                    && pairs[7].equals(pairs[9])
                    && pairs[4].equals(pairs[11])
                    && !pairs[0].equals(pairs[1])
                    /*&& !pairs[0].equals(pairs[2])
                    && !pairs[0].equals(pairs[3])
                    && !pairs[0].equals(pairs[5])
                    && !pairs[1].equals(pairs[2])
                    && !pairs[1].equals(pairs[3])
                    && !pairs[1].equals(pairs[7])
                    && !pairs[8].equals(pairs[10])*/
            ) {
                System.out.printf("%s - %s\n", h(offset), Utils.bytesToHex(bytes));
            }
            //"START+SELECT
            offset++;
        }
    }
    
    public void analyzeBytes(){
        int length = 12;
        int offset = 0;
        while (offset+length<data.length) {
            byte[] bytes = Arrays.copyOfRange(data, offset, offset + length);
            if (!allBytes(bytes, (byte) 0)
                    && !allBytes(bytes, (byte) 0xFF)
                    && bytes[0]==bytes[6]
                    && bytes[1]==bytes[4]
                    && bytes[7]==bytes[9]
                    && bytes[4]==bytes[11]
                    && bytes[0]!=bytes[1]
                    && bytes[0]!=bytes[2]
                    && bytes[0]!=bytes[3]
                    && bytes[0]!=bytes[5]
                    && bytes[1]!=bytes[2]
                    && bytes[1]!=bytes[3]
                    && bytes[1]!=bytes[7]
                    && bytes[8]!=bytes[10]
            ) {
                System.out.printf("%s - %s\n", h(offset), Utils.bytesToHex(bytes));
            }
            //"START+SELECT
            offset++;
        }
    }

    public static boolean allBytes(byte[] data, byte val) {
        for (byte b:data) {
            if (b!=val) return false;
        }
        return true;
    }

    public static boolean allPairs(BytePair[] data, byte val) {
        for (BytePair bp:data) {
            if (!bp.equals(new BytePair(val, val))) return false;
        }
        return true;
    }
    
    
}
