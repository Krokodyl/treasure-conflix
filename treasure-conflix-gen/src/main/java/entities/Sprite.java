package entities;

import enums.ColorDepth;
import services.SpriteReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sprite {
    
    int tilesPerRow = 2;
    List<Tile> tiles = new ArrayList<>();
    
    public Sprite(byte[] data, ColorDepth bpp, int tilesPerRow) {
        int bytesPerTile = bpp.getBytesPerTile();
        for(int i=0;i<data.length;i+= bytesPerTile) {
            byte[] bytes = Arrays.copyOfRange(data, i, Math.min(data.length, i + bytesPerTile));
            Tile tile = new Tile(bytes, bpp);
            tiles.add(tile);
            /*try {
                BufferedImage imageTile = SpriteReader.getImage(tile, new Palette2bpp("/palettes/satellaview.png"));
                ImageIO.write(image, "png", new File("src/main/resources/gen/"+i+".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }

    public int getTilesPerRow() {
        return tilesPerRow;
    }

    public List<Tile> getTiles() {
        return tiles;
    }
}
