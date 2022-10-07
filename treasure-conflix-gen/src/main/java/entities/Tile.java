package entities;

import enums.ColorDepth;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Tile {
    
    private byte[] bytes;
    private ColorDepth bpp;

    public Tile(byte[] bytes, ColorDepth bpp) {
        this.bytes = bytes;
        this.bpp = bpp;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
