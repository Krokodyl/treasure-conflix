package services;

import entities.Sprite;
import enums.ByteType;
import enums.PointerTableType;
import org.apache.commons.lang.ArrayUtils;
import services.lz.LzCompressor;
import services.lz.LzDecompressor;
import services.menu.WideName;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static services.Utils.*;
import static services.Utils.hexStringToByteArray;

public class Conflix {
    
    static byte[] data;

    private final static String INPUT_ROM = "D:\\git\\treasure-conflix\\roms\\patch\\BS Treasure Conflix (Japan).sfc";
    private final static String OUTPUT_ROM = "D:\\git\\treasure-conflix\\roms\\patch\\BS Treasure Conflix (English).sfc";
    
    static Map<Integer, Integer> dataFilePointerFileMap = new HashMap<>();
    
    static {
        // Razzle Town - Office
        dataFilePointerFileMap.put(x("85131"),x("80100"));
        // Razzle Town - Lobby
        dataFilePointerFileMap.put(x("85B89"),x("80470"));
        // Razzle Town - Melk's Hangar
        dataFilePointerFileMap.put(x("860A5"),x("8066B"));

        // Bridge
        dataFilePointerFileMap.put(x("8A5AB"),x("81B99"));
        // Bridge - End
        dataFilePointerFileMap.put(x("8C421"),x("828DB"));

        
        
        //Landos Base - Lobby
        dataFilePointerFileMap.put(x("86BA2"),x("80A72"));
        //Landos Base - Auction Hall
        dataFilePointerFileMap.put(x("8AE2B"),x("82749"));
        //Landos Base - Hunters Office
        dataFilePointerFileMap.put(x("8666D"),x("80826"));
        
        // Melk's Shop
        dataFilePointerFileMap.put(x("84905"),x("80100"));
        
        // East Forest Treasure / Golden Brooch
        dataFilePointerFileMap.put(x("8AE04"),x("826B8"));
        
        // Treasure descriptions - pointer offset ignored
        dataFilePointerFileMap.put(x("84100"),x("99999"));

        // Peppermint Junktown - Lobby
        dataFilePointerFileMap.put(x("88AE2"),x("81681"));
        // Peppermint Junktown - Hangar
        dataFilePointerFileMap.put(x("890E7"),x("818E7"));
        // Peppermint Junktown - Office
        dataFilePointerFileMap.put(x("88507"),x("81458"));
        
        // Kazusa Base - Lobby
        dataFilePointerFileMap.put(x("87876"),x("80EFD"));
        // Kazusa Base - Office
        dataFilePointerFileMap.put(x("871DF"),x("80CCC"));

        // Fort Fatras - Lobby
        dataFilePointerFileMap.put(x("8809C"),x("81290"));
        // Fort Fatras - Office
        dataFilePointerFileMap.put(x("87CAE"),x("810AD"));

        // Cloud Temple
        dataFilePointerFileMap.put(x("8A151"),x("822A1"));

        // Ancient Village
        dataFilePointerFileMap.put(x("89A1A"),x("819E3"));

        // Salvage Ship
        dataFilePointerFileMap.put(x("8934B"),x("8202D"));
        // Salvage Ship - Hangar
        dataFilePointerFileMap.put(x("8987B"),x("821C9"));

        // Sunken Ship 1 - Treasure
        dataFilePointerFileMap.put(x("8AD80"),x("824ED"));
        // Sunken Ship 1 - Empty
        dataFilePointerFileMap.put(x("8ADE5"),x("82633"));
        // Sunken Ship 1 - 
        dataFilePointerFileMap.put(x("8ADA7"),x("82587"));


        // Altar
        dataFilePointerFileMap.put(x("8A473"),x("823C4"));
        
    }

    // To be deleted in due time
    public static void main(String[] args) {
        
        try {
            data = Files.readAllBytes(new File(INPUT_ROM).toPath());
        } catch (IOException ex) {
            Logger.getLogger(Conflix.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        generateEmptyTranslationFiles();
        PointerTable tableData1 = new PointerTable(
                x("84000"),
                x("84033"),
                x("80000"),
                x("84050")
        );
        PointerTable tablePointer1 = new PointerTable(
                x("80000"),
                x("80035"),
                x("80000"),
                x("80050")
        );
        tableData1.setType(PointerTableType.DATA);
        tablePointer1.setType(PointerTableType.POINTER);
        loadPointers(tableData1);
        loadPointers(tablePointer1);
        generateEnglishFiles(tableData1);
        writeEnglishFiles(data, tableData1);
        writeEnglishFiles(data, tablePointer1);

        MenuReader menuReader = new MenuReader();
        menuReader.generateMenuBytes();
        menuReader.compressMenu();
        menuReader.writeMenu(data);
        menuReader.writeMenuPointers(data);

        writeTownNames(data);
        writeDemoTexts(data);
        writeCombatMessages(data);
        writeEnding(data);
        writeRomName(data);

        /*
            x84905	decompressed to		7FD900		(Equipment List + modding menu)
            x84100	decompressed to		7FE400		(Treasure list + some auction texts)	

            e400-d900 = b00
            84905 decompressed is C43 (x143 too big = 323 chars)
         */
        writeBytes(data, new byte[]{01, (byte) 0xE6}, x("12329"));
        writeBytes(data, new byte[]{00, (byte) 0xE6}, x("149EB"));
        writeBytes(data, new byte[]{00, (byte) 0xE6}, x("14623"));

        //compareSizes();

        SpriteReader spriteReader = new SpriteReader();
        try {
            spriteReader.generateSpriteShopList();
            writeDataFile(data, "src/main/resources/data/7A600.data", x("7A5C0"));
            byte[] shopListCode = new byte[]{
                    (byte) 0xA2, (byte) 0xC0, (byte) 0xA5, (byte) 0x8E, 0x30, 0x1C, (byte) 0xA9, (byte) 0xC7, 0x00, (byte) 0x8D, 32, 0x1C
            };
            writeBytes(data, shopListCode, x("55E1"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Jpn Translation Count: "+translationCount);
        System.out.println("Eng Translation Count: "+translationEngCount);

        //DataWriter.saveData("D:\\git\\treasure-conflix\\roms\\patch\\BS Treasure Conflix (English) - SNES - extended.sfc", data);
        DataWriter.saveData(OUTPUT_ROM, data);
    }

    private static void compareSizes() {
        File folder = new File("D:\\git\\treasure-conflix\\treasure-conflix-gen\\src\\main\\resources\\translations\\japanese\\data-files");
        File[] listOfFiles = folder.listFiles();
        List<File> files = Arrays.asList(listOfFiles);
        Collections.sort(files);
        int row=0;
        int col=0;
        int count = 1;
        for (File file : files) {
            if (file.isFile()) {
                String name = file.getName();
                File english = new File("D:\\git\\treasure-conflix\\treasure-conflix-gen\\src\\main\\resources\\translations\\english\\data-files\\"+name);
                System.out.println("File "+name+"\t\t"+h(english.length()-file.length()));
            }
        }
    }

    public static void extractTreasureIcons(String input, String output) {
        File folder = new File(input);
        File[] listOfFiles = folder.listFiles();
        List<File> files = Arrays.asList(listOfFiles);
        Collections.sort(files);
        int row=0;
        int col=0;
        int count = 1;
        for (File file : files) {
            if (file.isFile()) {
                try {
                    BufferedImage image = ImageIO.read(file);
                    BufferedImage icon = image.getSubimage(col * 16, row * 16, 16, 16);
                    String name = Integer.toString((count++));
                    if (name.length()==1) name = "0"+name;
                    ImageIO.write(icon, "png", new File(output+"/"+name+".png"));
                    col++;
                    if (col==8) {
                        col=0;
                        row++;
                    }
                    /*for (int row=0;row<4;row++) {
                        for (int col=0;col<8;col++) {
                            BufferedImage icon = image.getSubimage(col * 16, row * 16, 16, 16);
                            ImageIO.write(icon, "png", new File(output+"/"+(count++)+".png"));
                        }
                    }*/
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void writeRomName(byte[] data) {
        String s = "54 52 45 41 53 55 52 45 20 43 4F 4E 46 4C 49 58";
        byte[] bytes = hexStringToByteArray(s.split(" "));
        writeBytes(data, bytes, x("FFC0"));
    }

    private static void printTable(PointerTable table) {
        for (Map.Entry<Integer, PointerEntry> e : table.pointers.entrySet()) {
            PointerEntry p = e.getValue();
            int value = p.getValue();
            
            String dataFile = h(value);
            byte[] bytes = new byte[0];
            try {
                bytes = Files.readAllBytes(new File(String.format("src/main/resources/translations/japanese/data-files/%s.data", dataFile)).toPath());
            } catch (IOException ex) {
                Logger.getLogger(Conflix.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(dataFile+"\t"+bytesToHex(bytes));
            System.out.println(dataFile+"\t"+ShiftJIS.convertBytesToJapanese(bytes));
        }

    }

    /**
     * Line : max 40 chars (optimial : 37)
     */
    public static void writeDemoTexts(byte[] data) {
        int offset = x("7633");
        int index = 0;
        byte[] res = new byte[0];
        byte[] pointers = new byte[14];
        List<String> lines = new ArrayList<>();
        lines.add("                    Voice{NL2}{NL2}" +
                "A young but skilled treasure hunter{NL2}" +
                "His goal: all the legendary treasures{00}");
        lines.add("                    Loud{NL2}{NL2}" +
                "Veteran treasure hunter{NL2}" +
                "Voice's senior, now on the ropes{00}");
        lines.add("                    Melk{NL2}{NL2}" +
                "Runs the workshop in Raizeltown{NL2}" +
                "Second to none in airship modding{00}");
        lines.add("                    Aleph{NL2}{NL2}" +
                "Mechanic working for Melk{NL2}" +
                "Dreams of having his own workshop{00}");
        lines.add("                    Rag{NL2}{NL2}" +
                "Captain of a salvage ship{NL2}" +
                "Trustworthy seaman{00}");
        lines.add("                    Gilmore{NL2}{NL2}" +
                "Outsider whose occupation is unknown{NL2}" +
                "Seeks a treasure called the Cloud Gate{00}");
        lines.add("                    Red Spade{NL2}{NL2}" +
                "Leader of the sky pirates{NL2}" +
                "The fastest and strongest pilot{00}");
        System.out.println("DEMO TEXTS");
        int i = 0;
        for (String line:lines) {
            pointers[i++] = getPointerByte(offset, ByteType.LEFT);
            pointers[i++] = getPointerByte(offset, ByteType.RIGHT);
            byte[] bytes = ShiftJIS.convertEnglishToBytes(line);
            System.out.println(h(offset)+"\t"+h(index)+"\t\t"+Utils.bytesToHex(bytes));
            res = ArrayUtils.addAll(res, bytes);
            offset += bytes.length;
            index += bytes.length;
        }
        /*System.out.println("demo p"+"\t\t"+Utils.bytesToHex(pointers));
        System.out.println("demo"+"\t\t"+Utils.bytesToHex(res));*/
        writeBytes(data, res, x("7633"));
        writeBytes(data, pointers, x("7624"));
    }

    public static void writeEnding(byte[] data) {
        int offsetData = x("91CF");
        String line = "{TAB}Anyway...{TAB}let's start over...{TAB}{00}";
        writeBytes(data, ShiftJIS.convertEnglishToBytes(line), offsetData);
        
        offsetData = x("C900");
        int offsetPointer = x("914D");
        String file = "translations/credits/credits.txt";
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            Objects.requireNonNull(Conflix.class.getClassLoader().getResourceAsStream(file)), StandardCharsets.UTF_8));
            line = br.readLine();
            while (line!=null) {
                line = "{7F}"+line;
                byte[] bytes = ShiftJIS.convertEnglishToBytes(line);
                writeBytes(data, bytes, offsetData);
                writePointer(offsetPointer, offsetData);
                offsetData+=bytes.length;
                offsetPointer+=8;
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        offsetData = x("914F");
        byte[] cols = new byte[]{
                (byte) 0xE0, (byte) 0xD0, (byte) 0xD0,
                (byte) 0xF8, (byte) 0xF8, (byte) 0xF8,
                (byte) 0xA0, (byte) 0xA0, (byte) 0xA0,
                (byte) 0xC8, (byte) 0xC8, (byte) 0xC8,
                0x70,0x70,0x70,
        };
        for (byte b : cols) {
            data[offsetData] = b;
            offsetData+=8;
        }

    }

    public static void writeCombatMessages(byte[] data) {
        int offsetDataStart = x("C700");
        Map<Integer, String> pointersLines = new TreeMap<>();
        pointersLines.put(x("A42"), "Sky Pirates Attack!{TAB}{25}");
        pointersLines.put(x("A48"), "STAGE 1{25}Time Limit  500s{25}Defeat all the sky pirates!{TAB}{00}");
        pointersLines.put(x("A65"), "Defensive Weapon Activated!{TAB}{25}");
        pointersLines.put(x("A6B"), "STAGE 2{25}Time Limit  500s{25}Defeat the boss!{TAB}{00}");
        pointersLines.put(x("A97"), "Underwater{TAB}{25}");
        pointersLines.put(x("A9D"), "STAGE 3{25}Time Limit  500s{25}Defeat all the enemies!{TAB}{00}");
        pointersLines.put(x("ACB"), "Sea of Clouds{TAB}{25}");
        pointersLines.put(x("AD1"), "STAGE 4{25}Time Limit  500s{25}Defeat the boss!{TAB}{00}");
        pointersLines.put(x("B73"), "Final Stage{TAB}{25}");
        pointersLines.put(x("B79"), "STAGE 5{25}Time Limit  500s{25}Defeat the boss!{TAB}{00}");
        pointersLines.put(x("BF7"), "Bonus Stage 1{TAB}{00}");
        pointersLines.put(x("BFC"), "Bonus Stage 2{TAB}{00}");
        pointersLines.put(x("C15"), "Bonus Stage 3{TAB}{00}");
        int offset = offsetDataStart;
        for (Map.Entry<Integer, String> e : pointersLines.entrySet()) {
            Integer pointer = e.getKey();
            String english = e.getValue();
            byte[] bytes = ShiftJIS.convertEnglishToBytes(english);
            writePointer(pointer, offset);
            writeBytes(data, bytes, offset);
            offset+=bytes.length;
        }
    }
    
    public static void writeTownNames(byte[] data) {
        List<WideName> wideNames = new ArrayList<>();
        int offsetDataStart = x("D000");
        int offsetCodeStart = x("357E");
        wideNames.add(new WideName("　　　　　　Ｒａｉｚｅｌｔｏｗｎ", x("D000"), x("357E"), new byte[]{1,0,0x24}));
        wideNames.add(new WideName("　　　　　　Ｌａｎｄｏｓ　Ｂａｓｅ", x("D000"), x("357E"), new byte[]{4,0,0x25}));
        wideNames.add(new WideName("　　　　　　Ｋａｚｕｓａ　Ｂａｓｅ", x("D000"), x("357E"), new byte[]{6,0,0x26}));
        wideNames.add(new WideName("　　　　　　Ｆｏｒｔ　Ｆａｔｒａｓ", x("D000"), x("357E"), new byte[]{8,0,0x27}));
        wideNames.add(new WideName("　　Ｐｅｐｐｅｒｍｉｎｔ　Ｊｕｎｋｔｏｗｎ", x("D000"), x("357E"), new byte[]{0xA,0,0x28}));
        wideNames.add(new WideName("　　　　  Ｈｉｄｄｅｎ　Ｖｉｌｌａｇｅ", x("D000"), x("357E"), new byte[]{0xC,0,0x29}));
        wideNames.add(new WideName("　　　　　　Ｃｌｏｕｄ　Ｔｅｍｐｌｅ", x("D000"), x("357E"), new byte[]{0x10,0,0x2B}));
        wideNames.add(new WideName("　　　　　　Ｓａｌｖａｇｅ　Ｓｈｉｐ", x("D000"), x("357E"), new byte[]{0x0E,0,0x2A}));
        int offsetData = offsetDataStart;
        int offsetCode = offsetCodeStart;
        for (WideName wideName : wideNames) {
            wideName.setOffsetData(offsetData);
            wideName.setOffsetCode(offsetCode);
            offsetData += wideName.getName().length()*2+5;// header(3) + terminal(2)
            offsetCode += x("10");
        }
        for (WideName wideName : wideNames) {
            byte[] header = wideName.getHeader();
            byte[] bytes = ShiftJIS.convertJapaneseToBytes(wideName.getName());
            offsetData = wideName.getOffsetData();
            for (byte b:header) data[offsetData++] = b;
            for (byte b:bytes) data[offsetData++] = b;
            data[offsetData++] = 0x2A;
            data[offsetData] = 0x00;
            data[wideName.getOffsetCode()] = Utils.getPointerByte(wideName.getOffsetData(), ByteType.LEFT);
            data[wideName.getOffsetCode()+1] = Utils.getPointerByte(wideName.getOffsetData(), ByteType.RIGHT);
            data[wideName.getOffsetCode()+4] = Utils.getPointerByte(wideName.getOffsetData(), ByteType.LEFT);
            data[wideName.getOffsetCode()+5] = Utils.getPointerByte(wideName.getOffsetData(), ByteType.RIGHT);
            data[wideName.getOffsetCode()+8] = Utils.getPointerByte(wideName.getOffsetData(), ByteType.LEFT);
            data[wideName.getOffsetCode()+9] = Utils.getPointerByte(wideName.getOffsetData(), ByteType.RIGHT);
            data[wideName.getOffsetCode()+12] = Utils.getPointerByte(wideName.getOffsetData(), ByteType.LEFT);
            data[wideName.getOffsetCode()+13] = Utils.getPointerByte(wideName.getOffsetData(), ByteType.RIGHT);
            if (wideName.getOffsetCode()==x("35DE")) {
                data[x("35FE")] = Utils.getPointerByte(wideName.getOffsetData(), ByteType.LEFT);
                data[x("35FE")+1] = Utils.getPointerByte(wideName.getOffsetData(), ByteType.RIGHT);
                data[x("3602")] = Utils.getPointerByte(wideName.getOffsetData(), ByteType.LEFT);
                data[x("3602")+1] = Utils.getPointerByte(wideName.getOffsetData(), ByteType.RIGHT);
                data[x("3606")] = Utils.getPointerByte(wideName.getOffsetData(), ByteType.LEFT);
                data[x("3606")+1] = Utils.getPointerByte(wideName.getOffsetData(), ByteType.RIGHT);
                data[x("360A")] = Utils.getPointerByte(wideName.getOffsetData(), ByteType.LEFT);
                data[x("360A")+1] = Utils.getPointerByte(wideName.getOffsetData(), ByteType.RIGHT);
                data[x("360E")] = Utils.getPointerByte(wideName.getOffsetData(), ByteType.LEFT);
                data[x("360E")+1] = Utils.getPointerByte(wideName.getOffsetData(), ByteType.RIGHT);
                data[x("3612")] = Utils.getPointerByte(wideName.getOffsetData(), ByteType.LEFT);
                data[x("3612")+1] = Utils.getPointerByte(wideName.getOffsetData(), ByteType.RIGHT);
            }
        }

    }

    private static void writeEnglishFiles(byte[] data, PointerTable table) {
        int offsetData = table.newOffsetStart;
        for (Map.Entry<Integer, PointerEntry> e : table.pointers.entrySet()) {
            PointerEntry p = e.getValue();
            int offset = p.getOffset();
            int value = p.getValue();
            
            String filename = h(value);
            File file = null;
            if (table.getType()== PointerTableType.DATA) {
                file = new File("src/main/resources/translations/english/data-files/"+filename+".data");
                if (!file.exists()) {
                    file = new File("src/main/resources/translations/japanese/data-files/"+filename+".data");
                }
            } else {
                file = new File("src/main/resources/translations/english/pointer-files/"+filename+".data");
                if (!file.exists()) {
                    file = new File("src/main/resources/translations/japanese/pointer-files/"+filename+".data");
                }
            }
            try {
                byte[] bytes = Files.readAllBytes(file.toPath());
                LzCompressor compressor = new LzCompressor();
                byte[] compressData = compressor.compressData(bytes, false);

                // Write new pointer value
                data[offset] = (byte) ((offsetData % 256) & 0xFF);
                data[offset + 1] = (byte) (offsetData / 256);
                
                // Write new data
                for (int i=0;i<compressData.length;i++) {
                    data[offsetData++] = compressData[i];
                }
                
            } catch (IOException ex) {
                Logger.getLogger(Conflix.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static void loadPointers(PointerTable table) {
        for (int i=table.getOffsetStart();i<table.getOffsetEnd();i = i + 2) {
            int offset = i;
            byte a = data[i];
            byte b = data[i+1];
            int value = table.getShift() + ((b & 0xFF) * 0x100) + (a & 0xFF);
            PointerEntry pointer = new PointerEntry(offset, value, table.getShift());
            table.add(pointer);
        }
    }

    public static byte[] loadPointerFile(int offset) {
        String file = h(offset);
        if (offset == x("99999")) return new byte[0];
        try {
            return Files.readAllBytes(new File(String.format("src/main/resources/translations/japanese/pointer-files/%s.data", file)).toPath());
        } catch (IOException ex) {
            Logger.getLogger(Conflix.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void checkTranslationPointers(PointerTable table) {
        byte[] tableData;
        for (Map.Entry<Integer, PointerEntry> e : table.pointers.entrySet()) {
            PointerEntry p = e.getValue();
            int value = p.getValue();
            tableData = new byte[0];
            byte[] pointerFile = null;
            String pointerFileName = "";
            if (dataFilePointerFileMap.containsKey(value)) {
                int pf = dataFilePointerFileMap.get(value);
                pointerFile = loadPointerFile(pf);
                pointerFileName = h(pf);
            }
            int offsetTranslation = 0;
            Map<Integer, Translation> translationMap = loadTranslations(value);
            if (translationMap==null) continue;
            HashSet<Integer> readPointers = new HashSet<>();
            for (Map.Entry<Integer, Translation> f : translationMap.entrySet()) {
                Translation translation = f.getValue();
                byte[] dataValue = translation.getDataValue();
                String english = translation.getEnglish();
                if (english!=null && !english.isEmpty()) {
                    dataValue = ShiftJIS.convertEnglishToBytes(english);
                }
                tableData = ArrayUtils.addAll(tableData, dataValue);
                
                for (Pointer pointer : translation.getPointers()) {
                    int pointerOffset = pointer.getOffset();
                    int pointerValue = pointer.getValue();
                    if (translation.isGlobalPointer() && offsetTranslation>0 && pointerOffset >0) {
                        //writePointer(pointerOffset,offsetTranslation+1);
                        int readValue = readPointer(data, pointerOffset);
                        if (readPointers.contains(pointerValue))
                            System.out.println("pointerOffset="+h(pointerOffset)+" read="+h(readValue)+" off="+h(pointerValue));
                        else readPointers.add(pointerValue);
                    } else if (offsetTranslation>0 && pointerOffset >0 && pointerFile!=null) {
                        int readValue = readPointer(pointerFile, pointerOffset);
                        if (readPointers.contains(pointerValue))
                            System.out.println("pointerOffset="+h(pointerOffset)+" read="+h(readValue)+" off="+h(pointerValue));
                        else readPointers.add(pointerValue);
                    /*pointerFile[pointerOffset] = (byte) (((offsetTranslation+1) % 256) & 0xFF);
                    pointerFile[pointerOffset + 1] = (byte) ((offsetTranslation+1) / 256);*/
                    }
                }

                
                offsetTranslation += dataValue.length;
            }
        }
    }

    private static int readPointer(byte[] bytes, int pointerOffset) {
        byte a = bytes[pointerOffset];
        byte b = bytes[pointerOffset+1];
        return ((b & 0xFF) * 0x100) + (a & 0xFF);
    }

    public static void generateEnglishFiles(PointerTable table) {
        byte[] tableData;
        for (Map.Entry<Integer, PointerEntry> e : table.pointers.entrySet()) {
            PointerEntry p = e.getValue();
            int value = p.getValue();
            tableData = new byte[0];
            byte[] pointerFile = null;
            String pointerFileName = "";
            if (dataFilePointerFileMap.containsKey(value)) {
                int pf = dataFilePointerFileMap.get(value);
                pointerFile = loadPointerFile(pf);
                pointerFileName = h(pf);
            }
            int offsetTranslation = 0;
            Map<Integer, Translation> translationMap = loadTranslations(value);
            if (translationMap==null) continue;
            for (Map.Entry<Integer, Translation> f : translationMap.entrySet()) {
                Translation translation = f.getValue();
                byte[] dataValue = translation.getDataValue();
                String english = translation.getEnglish();
                if (english!=null && !english.isEmpty()) {
                    dataValue = ShiftJIS.convertEnglishToBytes(english);
                }
                tableData = ArrayUtils.addAll(tableData, dataValue);
                /*int pointerOffset = translation.getPointerOffset();
                if (translation.isGlobalPointer() && offsetTranslation>0 && pointerOffset >0) {
                    writePointer(pointerOffset,offsetTranslation+1);
                } else if (offsetTranslation>0 && pointerOffset >0 && pointerFile!=null) {
                    pointerFile[pointerOffset] = (byte) (((offsetTranslation+1) % 256) & 0xFF);
                    pointerFile[pointerOffset + 1] = (byte) ((offsetTranslation+1) / 256);
                }*/
                for (Pointer pointer : translation.getPointers()) {
                    int pointerOffset = pointer.getOffset();
                    if (translation.isGlobalPointer() && offsetTranslation>0 && pointerOffset >0) {
                        writePointer(pointerOffset,offsetTranslation+1);
                    } else if (offsetTranslation>0 && pointerOffset >0 && pointerFile!=null) {
                        pointerFile[pointerOffset] = (byte) (((offsetTranslation+1) % 256) & 0xFF);
                        pointerFile[pointerOffset + 1] = (byte) ((offsetTranslation+1) / 256);
                    }
                }
                offsetTranslation += dataValue.length;
            }
            try {
                new FileOutputStream("src/main/resources/translations/english/data-files/"+h(value)+".data").write(tableData);
                if (pointerFile!=null){
                    new FileOutputStream("src/main/resources/translations/english/pointer-files/" + pointerFileName + ".data").write(pointerFile);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    static int translationCount = 0;
    static int translationEngCount = 0;
    
    static Map<Integer, Translation> loadTranslations(int offset) {
        System.out.println("Loading Translations for "+h(offset));
        Map<Integer, Translation> translationMap = new TreeMap<>();
        String file = String.format("translations/%s.txt", h(offset));
        if (!new File("src/main/resources/"+file).exists()) return null;
        BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(Conflix.class.getClassLoader().getResourceAsStream(file)), StandardCharsets.UTF_8));
        String line = null;
        try {
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int jpnCount = 0;
        int engCount = 0;
        Translation t = new Translation();
        while (line != null) {
            if (line.contains("=")) {
                String[] split = line.split("=");
                if (split.length > 0) {
                    if (split[0].equals(Constants.TRANSLATION_FILE_POINTER)) {
                        String[] pointer = split[1].split(";");
                        t.setPointerFile(pointer[0]);
                        if (pointer[1].length()>0) {
                            //t.setPointerOffset(x(pointer[1]));
                            //t.setPointerValue(x(pointer[2]));
                            t.addPointer(new Pointer(x(pointer[1]), x(pointer[2])));
                        }
                        if (pointer.length>=4) {
                            t.setGlobalPointer(pointer[3].equals("GLOBAL"));
                        }
                    }
                    if (split[0].equals(Constants.TRANSLATION_FILE_DATA)) {
                        String[] data = split[1].split(";");
                        t.setDataFile(data[0]);
                        t.setDataOffset(x(data[1]));
                        t.setDataValue(hexStringToByteArray(data[2].split(" ")));
                    }
                    if (split[0].equals(Constants.TRANSLATION_FILE_JPN)) {
                        t.setJapanese(split[1]);
                        translationCount++;
                        jpnCount++;
                    }
                    if (split[0].equals(Constants.TRANSLATION_FILE_ENG)) {
                        if (split.length>1 && split[1].length()>0) {
                            t.setEnglish(split[1]);
                            translationEngCount++;
                            engCount++;
                        }
                        if (translationMap.containsKey(t.getDataOffset())) {
                            System.err.println(t);
                        }
                        translationMap.put(t.getDataOffset(), t);
                        t = new Translation();
                    }
                }
            }
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Trans Count "+file+" "+engCount+"/"+jpnCount);
        return translationMap;
    }
    
    public static void generateEmptyTranslationFiles() {
        for (Map.Entry<Integer, Integer> e : dataFilePointerFileMap.entrySet()) {
            String dataFile = h(e.getKey());
            String pointerFile = h(e.getValue());
            PrintWriter pw = null;
            byte[] bytes = new byte[0];
            try {
                bytes = Files.readAllBytes(new File(String.format("src/main/resources/translations/japanese/data-files/%s.data", dataFile)).toPath());
                pw = new PrintWriter(String.format("src/main/resources/gen/translations/%s.txt", dataFile));
            } catch (IOException ex) {
                Logger.getLogger(Conflix.class.getName()).log(Level.SEVERE, null, ex);
            }
            int offset = 0;
            int offsetAuction = x("988FE");
            String option = "";
            while (offset<bytes.length) {
                int dataOffset = offset;
                int pointerValue = dataOffset+1;
                byte[] dataValue = ShiftJIS.readUntilEndOfLine(bytes, offset);
                String japanese = ShiftJIS.convertBytesToJapanese(dataValue);
                String offsetAuctionString = "";
                if (dataFile.equals("8AE2B") && pointerValue<=x("28A6")) {
                    if (offsetAuction==x("98AB6")) offsetAuction += 2;
                    if (offsetAuction==x("98AD4")) offsetAuction += 4;
                    if (offsetAuction==x("98B18")) offsetAuction += 16;
                    offsetAuctionString = h(offsetAuction);
                    offsetAuction+=2;
                    option = ";GLOBAL";
                } else option = "";
                String out = Constants.TRANSLATION_FILE_POINTER+"="+pointerFile+";"+offsetAuctionString+";"+h(pointerValue) + option;
                if (pw!=null) pw.write(out+"\n");
                System.out.println(out);

                out = Constants.TRANSLATION_FILE_DATA+"="+dataFile+";"+h(dataOffset)+";"+bytesToHex(dataValue);
                if (pw!=null) pw.write(out+"\n");
                System.out.println(out);

                out = Constants.TRANSLATION_FILE_JPN+"="+japanese;
                if (pw!=null) pw.write(out+"\n");
                System.out.println(out);

                out = Constants.TRANSLATION_FILE_ENG+"=";
                if (pw!=null) pw.write(out+"\n");
                System.out.println(out);

                if (pw!=null) pw.write("\n");
                System.out.println();
                
                offset += dataValue.length;
            }
            if (pw!=null) {
                pw.flush();
                pw.close();
            }
        }
    }

    public static void decompressDataFiles(byte[] data) {
        int start = x("84000");
        int end = x("84033");
        int shift = x("80000");
        String output = "src/main/resources/translations/japanese/data-files";
        decompressTable(data, start, end, shift, output);
    }

    public static void decompressPointerFiles(byte[] data) {
        int start = x("80000");
        int end = x("80035");
        int shift = x("80000");
        String output = "src/main/resources/translations/japanese/pointer-files";
        decompressTable(data, start, end, shift, output);
    }
                                        
    private static void decompressTable(byte[] data, int start, int end, int shift, String outputFolder) {
        for (int i=start;i<end;i = i + 2) {
            byte a = data[i];
            byte b = data[i+1];
            int offset = shift + ((b & 0xFF) * 0x100) + (a & 0xFF);
            LzDecompressor lzDecompressor = new LzDecompressor();
            lzDecompressor.decompressData(data, offset, outputFolder+"/"+ h(offset)+".data", false);
            byte[] decompressedData = lzDecompressor.getDecompressedData();
            System.out.println(ShiftJIS.convertBytesToJapanese(decompressedData));
        }
    }

    public static void readTxt(String file) {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            Objects.requireNonNull(Conflix.class.getClassLoader().getResourceAsStream(file)), StandardCharsets.UTF_8));
            String line = br.readLine();
            while (line!=null) {
                for (String s : line.split("(?<=\\G.{4})")) {
                    System.out.println(s);
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void generateSatellaviewCharacterSprites(int offset) {
        String bios = "D:\\git\\treasure-conflix\\tools\\BS-X BIOS (English) [No DRM] [2016 v1.3].sfc";
        byte[] dataBios = new byte[0];
        try {
            dataBios = Files.readAllBytes(new File(bios).toPath());
        } catch (IOException ex) {
            Logger.getLogger(Conflix.class.getName()).log(Level.SEVERE, null, ex);
        }
        int start = offset;
        int end = start + x("8000");
        while (offset<end) {
            Sprite sprite = SpriteReader.readSatellaviewCharacter(dataBios, offset);
            String file = "src/main/resources/gen/"+h(start)+"/" + h(offset) + ".png";
            SpriteReader.saveSatellaviewCharacterSprite(sprite, file);
            offset += x("18");
        }
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
    
    public static void analyzeAll(boolean decompress) {
        LzDecompressor lzDecompressor = new LzDecompressor();
        for (Integer i : readTable(data, x("18000"), x("1802F"))) {
            System.out.print(h(i) + "\t");
            if (decompress) lzDecompressor.decompressData(data, i);
        }
        System.out.println();
        for (Integer i : readTable(data, x("18100"), x("1812F"))) {
            System.out.print(h(i) + "\t");
            if (decompress) lzDecompressor.decompressData(data, i);
        }
        System.out.println();
        for (Integer i : readTable(data, x("18200"), x("1822F"))) {
            System.out.print(h(i) + "\t");
            if (decompress) lzDecompressor.decompressData(data, i);
        }
        System.out.println();
        for (Integer i : readTable(data, x("18300"), x("1832F"))) {
            System.out.print(h(i) + "\t");
            if (decompress) lzDecompressor.decompressData(data, i);
        }
        System.out.println();
        for (Integer i : readTable(data, x("18400"), x("1842F"))) {
            System.out.print(h(i) + "\t");
            if (decompress) lzDecompressor.decompressData(data, i);
        }
        System.out.println();
        for (Integer i : readTable(data, x("18500"), x("1852F"))) {
            System.out.print(h(i) + "\t");
            if (decompress) lzDecompressor.decompressData(data, i);
        }
        System.out.println();
        for (Integer i : readTable(data, x("18600"), x("1862F"))) {
            System.out.print(h(i) + "\t");
            if (decompress) lzDecompressor.decompressData(data, i);
        }
        System.out.println();
        for (Integer i : readTable(data, x("18700"), x("1872F"))) {
            System.out.print(h(i) + "\t");

        }
        for (Integer i : readTable(data, x("50000"), x("5001A"))) {
            System.out.print(h(i) + "\t");
            if (decompress) lzDecompressor.decompressData(data, i);
        }
        System.out.println();
        for (Integer i : readTable(data, x("50100"), x("50123"))) {
            System.out.print(h(i) + "\t");
            if (decompress) lzDecompressor.decompressData(data, i);
        }
        System.out.println();
        for (Integer i : readTable(data, x("50200"), x("50220"))) {
            System.out.print(h(i) + "\t");
            if (decompress) lzDecompressor.decompressData(data, i);
        }

        for (Integer i : readTable(data, x("FFF00"), x("FFFE6"))) {
            try {
                System.out.print(h(i)+"\t");
                if (decompress) lzDecompressor.decompressData(data, i);
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
    
    public static void writePointer(int pointerOffset, int value) {
        data[pointerOffset] = (byte) (((value) % 256) & 0xFF);
        data[pointerOffset + 1] = (byte) ((value) / 256);
        data[pointerOffset] = (byte) (((value) % 256) & 0xFF);
        data[pointerOffset + 1] = (byte) ((value) / 256);
    }
    
    public static void writeDataFile(byte[] data, String file, int offset) {
        try {
            byte[] bytes = Files.readAllBytes(new File(file).toPath());
            writeBytes(data, bytes, offset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void writeBytes(byte[] data, byte[] bytes, int offset) {
        for (byte b : bytes) {
            data[offset++]=b;
        }
    }
}
