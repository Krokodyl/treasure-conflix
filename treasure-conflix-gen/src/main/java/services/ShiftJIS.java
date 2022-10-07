package services;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ShiftJIS {

    public static byte[] convertJapaneseToBytes(String japanese) {
        return japanese.getBytes( Charset.forName("SHIFT-JIS"));
    }

    public static String convertBytesToJapanese(byte[] bytes) {
        return new String(bytes, Charset.forName("SHIFT-JIS"));
    }
    
}
