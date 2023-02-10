package services;

import entities.Palette2bpp;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class SpriteReaderTest {

    @org.junit.jupiter.api.Test
    void readSprite2bpp() {
        SpriteReader spriteReader = new SpriteReader();
        try {
            byte[] bytes = spriteReader.loadImage2bpp("src/test/resources/480C0.png", new Palette2bpp("/satellaview.png"));
            System.out.println(Utils.bytesToHex(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void convert2bppTo1bpp() {
        SpriteReader spriteReader = new SpriteReader();
        try {
            byte[] bytes = spriteReader.loadImage2bpp("src/test/resources/480C0.png", new Palette2bpp("/satellaview.png"));
            System.out.println(Utils.bytesToHex(bytes));
            byte[] convert2bppTo1bpp = SpriteReader.convert2bppTo1bpp(bytes);
            System.out.println(Utils.bytesToHex(convert2bppTo1bpp));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}