package services;

import entities.*;
import enums.ColorDepth;
import org.apache.commons.lang.ArrayUtils;
import services.lz.LzCompressor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static services.Utils.padLeft;

public class SpriteReader {


    BufferedImage image;

    byte[] imageData;

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public void generateSpriteShopList() throws IOException {
        generateSpriteDataFromImage(
                "src/main/resources/sprites/shop.png",
                "src/main/resources/gen/sprite-uncompressed.data",
                new Palette4bpp("/palettes/shop.png"),
                4
        );
        String uncomp = "src/main/resources/gen/sprite-uncompressed.data";
        String outputFile = "src/main/resources/data/7A600.data";
        byte[] bytes = Files.readAllBytes(new File(uncomp).toPath());
        LzCompressor compressor = new LzCompressor();
        byte[] compressData = compressor.compressData(bytes, false);
        DataWriter.saveData(outputFile, compressData);
        //CompressedSpriteManager compressedSpriteManager = new CompressedSpriteManager(null);
        //compressedSpriteManager.compressCopyFile(uncomp, Header.LATIN_SPRITES_HEADER, outputFile);
        //compressedSpriteManager.decompressFile(outputFile, "src/main/resources/data/decomp-1B8000.data");
    }

    private String generateSpriteDataFromImage(String image, String output, Palette palette, int bpp) throws IOException {
        System.out.println("Generating Sprite Data from image "+image);
        String s = "";
        if (bpp==2) s = loadFontImage2bpp(image, palette);
        else s = loadFontImage4bpp(image, palette);
        byte[] bytes = getBytes();

        try (FileOutputStream fos = new FileOutputStream(output)) {
            fos.write(bytes);
            fos.close();
            //There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
        }

        return s;
    }

    public byte[] getBytes() {
        return outputStream.toByteArray();
    }

    public String loadFontImage2bpp(String file, Palette palette) {
        StringBuffer sb = new StringBuffer();
        try {
            image = ImageIO.read(new File(file));
        } catch (IOException e) {

        }
        boolean stop = false;
        int tileX = 0, tileY = 0, x = 0, y = 0;
        while (tileY++<image.getHeight()/8) {
            tileX=0;
            while (tileX++<image.getWidth()/8) {
                y=0;
                while (y++<8) {
                    x=0;
                    int encodedLine = 0;
                    while (x++<8) {
                        int rgb = image.getRGB(((tileX - 1) * 8 + (x - 1)), ((tileY - 1) * 8 + (y - 1)));
                        if (rgb==0) {
                            stop = true;
                            break;
                        }
                        String color = Utils.getColorAsHex(rgb).toLowerCase();
                        FontColor fontColor = palette.getFontColor(color);
                        int mask = fontColor.getMask();
                        mask = mask >> (x-1);
                        encodedLine = encodedLine | mask;
                    }
                    if (stop) break;
                    int leftByte = encodedLine >> 8;
                    int rightByte = encodedLine & 0x00FF;
                    outputStream.write(leftByte);
                    outputStream.write(rightByte);
                    String hex = Utils.toHexString(leftByte, 2)+" "+Utils.toHexString(rightByte,2);
                    sb.append(hex.replaceAll(" ",""));
                    //System.out.print(hex+" ");
                }
                if (stop) break;
                //System.out.println();
            }
            if (stop) break;
        }
        return sb.toString();
    }

    public String loadFontImage4bpp(String file, Palette palette) {
        StringBuffer sb = new StringBuffer();
        byte[] output = new byte[0];
        int indexOutput = 0;
        try {
            image = ImageIO.read(new File(file));
            output = new byte[image.getHeight()*image.getWidth()/2];
        } catch (IOException e) {

        }
        boolean stop = false;
        int tileX = 0, tileY = 0, x = 0, y = 0;
        while (tileY++<image.getHeight()/8) {
            tileX=0;
            while (tileX++<image.getWidth()/8) {
                y=0;
                while (y++<8) {
                    x=0;
                    long encodedLine = 0;
                    while (x++<8) {
                        int rgb = image.getRGB(((tileX - 1) * 8 + (x - 1)), ((tileY - 1) * 8 + (y - 1)));
                        String color = Utils.getColorAsHex(rgb).toLowerCase();
                        if (rgb==0) {
                            stop = true;
                            break;
                        }
                        FontColor fontColor = palette.getFontColor(color);
                        long mask = fontColor.getLongMask();
                        mask = mask >> (x-1);
                        encodedLine = encodedLine | mask;
                    }
                    if (stop) break;
                    //int leftByte = encodedLine >> 8;
                    //int rightByte = encodedLine & 0x00FF;
                    long byte1 = encodedLine >> 24;
                    long byte2 = (encodedLine >> 16) & 0x00FF;
                    long byte3 = (encodedLine >> 8) & 0x00FF;
                    long byte4 = (encodedLine) & 0x00FF;

                    output[indexOutput] = (byte) ((byte1) & 0xFF);
                    output[indexOutput+1] = (byte) ((byte2) & 0xFF);
                    output[indexOutput+16] = (byte) ((byte3) & 0xFF);
                    output[indexOutput+17] = (byte) ((byte4) & 0xFF);
                    indexOutput += 2;
                }
                indexOutput += 16;
                if (stop) break;
            }
            if (stop) break;
        }
        int k = 0;
        for (byte b:output) {
            //if (k++%16==0) System.out.println();
            String s = Utils.toHexString(b) + " ";
            sb.append(s);
            outputStream.write(b);
            //System.out.print(s);
        }
        return sb.toString();
    }
    
    /**
     * prints the contents of buff2 on buff1 with the given opaque value.
     */
    public static void addImage(BufferedImage buff1, BufferedImage buff2,
                          float opaque, int x, int y) {
        Graphics2D g2d = buff1.createGraphics();
        g2d.setComposite(
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opaque));
        g2d.drawImage(buff2, x, y, null);
        g2d.dispose();
    }
    
    public static BufferedImage getImage(Sprite sprite, Palette palette) {
        int width = 8*sprite.getTilesPerRow();
        int height = 8*(sprite.getTiles().size()/sprite.getTilesPerRow());
        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        int line = 0;
        int col = 0;
        for (Tile tile : sprite.getTiles()) {
            if (col==width) {
                col = 0;
                line += 8;
            }
            BufferedImage image = getImage(tile, palette);
            addImage(out, image, 1, col, line);
            col += 8;
        }
        return out;
    }
    
    public static BufferedImage getImage(Tile tile, Palette palette) {
        BufferedImage out = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) out.getGraphics();
        byte[] bytes = tile.getBytes();
        for (int y=0;y<8;y++) {
            for (int x = 0; x <8; x++) {
                byte a = bytes[y*2];
                byte b = bytes[y*2+1];
                a = (byte) (((a << x) & 0x80) & 0xFF);
                b = (byte) (((b << x) & 0x80) & 0xFF);
                String pattern = padLeft(Integer.toBinaryString(a & 0xFF), '0', 8)
                        + padLeft(Integer.toBinaryString(b & 0xFF), '0', 8);
                String hexaValue = palette.getHexaValue(FontColor.valueOfPattern(pattern));
                out.setRGB(x,y, Color.decode("#"+hexaValue).getRGB());
            }
        }
        return out;
    }
    
    public static Sprite readSatellaviewCharacter(byte[] data, int offset) {
        byte[] sprite1bpp = readSprite1bpp(data, offset);
        System.out.println(Utils.bytesToHex(sprite1bpp));
        byte[] sprite2bpp = convert1bppTo2bpp(sprite1bpp);
        System.out.println(Utils.bytesToHex(sprite2bpp));
        Sprite sprite = new Sprite(sprite2bpp, ColorDepth._2BPP, 2);
        return sprite;
    }
    
    public static void saveSatellaviewCharacterSprite(Sprite sprite, String file) {
        try {
            BufferedImage image = getImage(sprite, new Palette2bpp("/palettes/satellaview.png"));
            ImageIO.write(image, "png", new File(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static byte[] readSprite1bpp(byte[] data, int offset) {
        byte[] leadingZeros = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        byte[] bytes = Arrays.copyOfRange(data, offset, offset + 8 * 3);
        /*System.out.println(Utils.bytesToHex(bytes));
        for (int i = 0, bytesLength = bytes.length; i < bytesLength; i++) {
            bytes[i] = (byte) (bytes[i] >> 1);
        }
        System.out.println(Utils.bytesToHex(bytes));*/

        bytes = ArrayUtils.addAll(leadingZeros, bytes);
        return bytes;
    }
    
    public static byte[] convert1bppTo2bpp(byte[] bytes) {
        byte[] result = new byte[bytes.length*2];
        for (int i = 0, bytesLength = bytes.length; i < bytesLength; i = i + 2) {
            byte a = bytes[i];
            byte b = bytes[i+1];
            
            /*byte a1 = (byte) (a << 1);
            if ((b & 0xFF)>=0x80) a1++;
            byte a2 = a;
            byte b1 = (byte) ((b & 0x7F) << 1);
            byte b2 = b;*/
            
            byte a1 = a;
            byte a2 = (byte) (a >> 1);
            if ((b & 0xFF)>=0x80) a2++;
            byte b2 = (byte) ((b & 0x7F) << 1);
            byte b1 = b;
            
            int shift = (i/16)*16;
            
            result[shift+i] = a1;
            result[shift+i+1] = a2;
            result[shift+i+16] = b1;
            result[shift+i+1+16] = b2;
        }
        return result;
    }
    
    public static void mergeImages(String folder, int columns, String output) {
        try {
            List<File> files = Files.list(Paths.get(folder)).map(Path::toFile).collect(Collectors.toList());
            BufferedImage image = ImageIO.read(files.get(0));
            int rows = files.size() / columns;
            if (files.size()%columns>0) rows++;
            BufferedImage out = new BufferedImage(columns*image.getWidth(), rows*image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            int x = 0;
            int y = 0;
            for (File file : files) {
                if (x==out.getWidth()) {
                    x = 0;
                    y = y + image.getHeight();
                }
                BufferedImage read = ImageIO.read(file);
                addImage(out, read, 1, x, y);
                x = x + image.getWidth();
            }
            ImageIO.write(out, "png", new File(output));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
