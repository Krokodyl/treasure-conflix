package services;

import entities.*;
import enums.ColorDepth;
import org.apache.commons.lang.ArrayUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
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
