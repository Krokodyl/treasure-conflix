package services;

import org.apache.commons.lang.ArrayUtils;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static services.Utils.*;

public class ShiftJIS {

    static char SPECIAL_MARKER_START = '{';
    static char SPECIAL_MARKER_END = '}';
    
    static Map<String, byte[]> SPECIAL_CODES;

    static {
        SPECIAL_CODES = new HashMap<>();
        SPECIAL_CODES.put("00", new byte[]{(byte)0x00});
        SPECIAL_CODES.put("NL", new byte[]{(byte)0x2F});
        SPECIAL_CODES.put("NL2", new byte[]{(byte)0x2F});
        SPECIAL_CODES.put("NL4", new byte[]{(byte)0x04});
        SPECIAL_CODES.put("TAB", new byte[]{(byte)0x2A});
        SPECIAL_CODES.put("SP", new byte[]{(byte)0x81, 0x40});
        SPECIAL_CODES.put("7B", new byte[]{(byte)0x7B});
        SPECIAL_CODES.put("EL", new byte[]{(byte)0x7C});
        SPECIAL_CODES.put("WP", new byte[]{(byte)0x05});
        SPECIAL_CODES.put("*", new byte[]{0x02,(byte)0x85,0x49,0x7F});
        SPECIAL_CODES.put("20", new byte[]{(byte)0x20});
        SPECIAL_CODES.put("23", new byte[]{(byte)0x23});
        SPECIAL_CODES.put("30", new byte[]{(byte)0x30});
        SPECIAL_CODES.put("31", new byte[]{(byte)0x31});
        SPECIAL_CODES.put("32", new byte[]{(byte)0x32});
        SPECIAL_CODES.put("33", new byte[]{(byte)0x33});
        SPECIAL_CODES.put("34", new byte[]{(byte)0x34});
        SPECIAL_CODES.put("35", new byte[]{(byte)0x35});
        SPECIAL_CODES.put("39", new byte[]{(byte)0x39});
        SPECIAL_CODES.put("3A", new byte[]{(byte)0x3A});
        SPECIAL_CODES.put("7F", new byte[]{(byte)0x7F});
        SPECIAL_CODES.put("→", new byte[]{(byte)0x81, (byte)0xA8});
        SPECIAL_CODES.put("←", new byte[]{(byte)0x81, (byte)0xA9});
        SPECIAL_CODES.put("↑", new byte[]{(byte)0x81, (byte)0xAA});
        SPECIAL_CODES.put("↓", new byte[]{(byte)0x81, (byte)0xAB});
        //81 a8 a9 aa ab → ← ↑ ↓
        SPECIAL_CODES.put("SP-10", new byte[]{
                (byte) 0x81, (byte) 0x40,
                (byte) 0x81, (byte) 0x40,
                (byte) 0x81, (byte) 0x40,
                (byte) 0x81, (byte) 0x40,
                (byte) 0x81, (byte) 0x40
        });
        SPECIAL_CODES.put("SP-20", new byte[]{
                (byte) 0x81, (byte) 0x40,
                (byte) 0x81, (byte) 0x40,
                (byte) 0x81, (byte) 0x40,
                (byte) 0x81, (byte) 0x40,
                (byte) 0x81, (byte) 0x40,
                (byte) 0x81, (byte) 0x40,
                (byte) 0x81, (byte) 0x40,
                (byte) 0x81, (byte) 0x40,
                (byte) 0x81, (byte) 0x40,
                (byte) 0x81, (byte) 0x40
        });
        SPECIAL_CODES.put("20-3", new byte[]{
                (byte) 0x20, (byte) 0x20, (byte) 0x20
        });
        SPECIAL_CODES.put("20-4", new byte[]{
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20
        });
        SPECIAL_CODES.put("EQ-LM", new byte[]{
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20
        });
        SPECIAL_CODES.put("EQ-RM", new byte[]{
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20
        });
        
        SPECIAL_CODES.put("20-7", new byte[]{
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20
        });
        SPECIAL_CODES.put("20-30", new byte[]{
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
        });
        SPECIAL_CODES.put("20-25", new byte[]{
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
        });
        SPECIAL_CODES.put("20-22", new byte[]{
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20
        });
        SPECIAL_CODES.put("20-36", new byte[]{
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20
        });
        SPECIAL_CODES.put("20-38", new byte[]{
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
                (byte) 0x20, (byte) 0x20, (byte) 0x20
        });
        
        
        
    }

    static String latin = " !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[¥]^– abcdefghijklmnopqrstuvwxyz{|}_";
    static String latin7F = " !\"#$§&\'()§+,-.§0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[¥]^- abcdefghijklmnopqrstuvwxyz{§}_§";

    static Map<Integer, String> SINGLE_CODES;

    static {
        SINGLE_CODES = new HashMap<>();
        SINGLE_CODES.put(0x20, " ");
        SINGLE_CODES.put(0x2A, "{TAB}");
        SINGLE_CODES.put(0x2F, "{NL2}");
        SINGLE_CODES.put(0x04, "{NL4}");
        SINGLE_CODES.put(0x05, "{WP}");
        SINGLE_CODES.put(0x7C, "{EL}");
        SINGLE_CODES.put(0x21, "!");
        SINGLE_CODES.put(0x22, "\"");
        SINGLE_CODES.put(0x28, "(");
        SINGLE_CODES.put(0x29, ")");
        SINGLE_CODES.put(0x3A, ":");
        SINGLE_CODES.put(0x3F, "?");
    }
    
    static String SINGLE_CODE_06_0F = "ガギグゲゴザジズゼゾ";
    static String SINGLE_CODE_10 = "ダヂヅデドバビブベボパピプペポヴ";
    static String SINGLE_CODE_30_39 = "0123456789";
    static String SINGLE_CODE_40_5A = "…ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯ" +
            "ＰＱＲＳＴＵＶＷＸＹＺ";
    static String SINGLE_CODE_60_7A = " ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏ" + 
            "ｐｑｒｓｔｕｖｗｘｙｚ";
    static String SINGLE_CODE_A0_DD = " 。「」、・ヲァィゥェォャュョッ" +
            "ーアイウエオカキクケコサシスセソ" +
            "タチツテトナニヌネノハヒフヘホマ" +
            "ミムメモヤユヨラリルレロワン";
    
    static String YES_NO_CODE_IN = "{YES/NO}";
    static String YES_NO_CODE_OUT = "       {02}YES{7F}         {02}NO{7F}";
    
    public static byte[] convertJapaneseToBytes(String japanese) {
        return japanese.getBytes( Charset.forName("SHIFT-JIS"));
    }

    public static String convertBytesToJapanese(byte[] bytes) {
        String result = "";
        boolean hiragana = false;
        for (int i = 0; i < bytes.length; i++) {
            byte a = bytes[i];
            //|| (a & 0xFF) == 0x04
            if ((a & 0xFF) == 0x7C) {
                hiragana = false;
            }
            if ((a & 0xFF) == 0x02) {
                hiragana = true;
            }
            else if ((a & 0xFF) == 0x03) {
                hiragana = false;
            }
            else if ((a & 0xFF) == 0x7B) {
                i++;
                byte b = bytes[i];
                result += "{" + h2(a) + "}" + "{" + h2(b) + "}";
            }
            else if ((a & 0xFF) >= 0x80 && (a & 0xFF)< 0xA0) {
                i++;
                byte b = bytes[i];
                String read = "";
                if ((a & 0xFF) == 0x85) {
                    read = latin.charAt(b-0x3F)+"";
                } else read = new String(new byte[]{a, b}, Charset.forName("SHIFT-JIS"));
                if (hiragana) read = hiragana(read);
                result += read;
            } else {
                int prev = result.length();
                int aInt = a & 0xFF;
                if (SINGLE_CODES.containsKey(aInt)) {
                    String read = SINGLE_CODES.get(aInt);
                    if (hiragana) read = hiragana(read);
                    result += read;
                }
                if (aInt >= 0x06 && aInt <= 0x0F) {
                    String read = ""+SINGLE_CODE_06_0F.charAt(aInt-0x06);
                    if (hiragana) read = hiragana(read);
                    result += read;
                }
                if (aInt >= 0x10 && aInt <= 0x1F) {
                    String read = ""+SINGLE_CODE_10.charAt(aInt-0x10);
                    if (hiragana) read = hiragana(read);
                    result += read;
                }
                if (aInt >= 0x30 && aInt <= 0x39) {
                    String read = ""+SINGLE_CODE_30_39.charAt(aInt-0x30);
                    if (hiragana) read = hiragana(read);
                    result += read;
                }
                if (aInt >= 0x40 && aInt <= 0x5A) {
                    String read = ""+SINGLE_CODE_40_5A.charAt(aInt-0x40);
                    if (hiragana) read = hiragana(read);
                    result += read;
                }
                if (aInt >= 0x60 && aInt <= 0x7A) {
                    String read = ""+SINGLE_CODE_60_7A.charAt(aInt-0x60);
                    if (hiragana) read = hiragana(read);
                    result += read;
                }
                if (aInt >= 0xA0 && aInt <= 0xDD) {
                    String read = ""+SINGLE_CODE_A0_DD.charAt(aInt-0xA0);
                    if (hiragana) read = hiragana(read);
                    result += read;
                }
                if (result.length()==prev) {
                    //System.out.println("Unknown code : "+h(a));
                    result += "{" + h2(a) + "}";
                }
            }

        }
        return result;
    }
    
    public static byte[] readUntilEndOfLine(byte[] bytes, int start) {
        return readUntil(bytes, start, (byte) 0x7C);
    }

    public static byte[] readUntil(byte[] bytes, int start, byte end) {
        byte[] result = new byte[0];
        for (int i = start; i < bytes.length; i++) {
            byte a = bytes[i];
            result = ArrayUtils.addAll(result, new byte[]{a});
            if ((a & 0xFF) == end) {
                return result;
            } else if ((a & 0xFF) >= 0x80 && (a & 0xFF) < 0xA0) {
                i++;
                byte b = bytes[i];
                result = ArrayUtils.addAll(result, new byte[]{b});
            }
        }
        return result;
    }
    
    public static byte[] convertEnglishToBytes(String english) {
        english = english.replace(YES_NO_CODE_IN, YES_NO_CODE_OUT);
        byte[] result = new byte[0];
        boolean specialCode = false;
        String specialCodeValue = "";
        for (char c : english.toCharArray()) {
            if (c==SPECIAL_MARKER_START) specialCode = true;
            else if (specialCode) {
                if (c!=SPECIAL_MARKER_END) {
                    specialCodeValue += c;
                }
                else {
                    specialCode = false;
                    if (!SPECIAL_CODES.containsKey(specialCodeValue)) {
                        byte[] code = new byte[]{b(specialCodeValue)};
                        result = ArrayUtils.addAll(result, code);
                        //System.out.println("Unknown special code : "+specialCodeValue);
                    }
                    else {
                        byte[] code = SPECIAL_CODES.get(specialCodeValue);
                        result = ArrayUtils.addAll(result, code);
                    }
                    specialCodeValue = "";
                }
            }
            else {
                byte[] code = getCodeSingle(c);
                if (c=='　') code = new byte[]{(byte) 0x81, 0x40};
                //if (c=='　') code = new byte[]{(byte) 0x20};
                result = ArrayUtils.addAll(result, code);
            }
        }
        result = ArrayUtils.addAll(new byte[]{0x7F}, result);
        return result;
    }

    private static byte[] getCode(char c) {
        int i = latin.indexOf(c + "");
        i = i + x("3F");
        return new byte[]{(byte) 0x85, (byte) i};
    }

    private static byte[] getCodeSingle(char c) {
        int i = latin7F.indexOf(c + "");
        i = i + x("20");
        return new byte[]{(byte) i};
    }

    public static String hiragana(String katakanaString)
    {
        StringBuilder sb = new StringBuilder();
        for(Character c : katakanaString.toCharArray())
        {
            // Es un character medio-ancho?
            if(('\uff66' <= c) && (c <= '\uff9d'))
            {
                sb.append((char)(c-0xcf25));
            }
            else if(('\u30a1' <= c) && (c <= '\u30fe')) // ancho-completo
            {
                sb.append((char)(c-0x60));
            }
            else
            {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
}
