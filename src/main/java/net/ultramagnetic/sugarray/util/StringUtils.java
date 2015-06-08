package net.ultramagnetic.sugarray.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.StringTokenizer;

/**
 * 文字列操作系のユーティリティクラス
 */
public class StringUtils {

    private StringUtils() {
        ;
    }

    /**
     * @param 文字列
     * @return 対象文字列がnullか空の場合にtrueを返す。
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * @param 文字列
     * @return 対象文字列がnullか空の場合にfalseを返す。
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String decodeURL(String s) {
        if (s == null) {
            return "";
        }
        if (s.indexOf('+') < 0) {
            try {
                return URLDecoder.decode(s, "UTF-8");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return "";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }
        }
        StringTokenizer st = new StringTokenizer(s, "+", true);
        StringBuilder sb = new StringBuilder();
        while (st.hasMoreTokens()) {
            String tk = st.nextToken();
            if ("+".equals(tk)) {
                sb.append("+");
            } else {
                try {
                    tk = URLDecoder.decode(tk, "UTF-8");
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                sb.append(tk);
            }
        }
        return sb.toString();
    }

    public static String encodeURL(String s) {
        if (s == null)
            return "";
        StringBuilder sb = new StringBuilder();
        int i = 0, sl = s.length();
        for (; i < sl; ++i) {
            char sc = s.charAt(i);
            if (('0' <= sc && sc <= '9') || ('a' <= sc && sc <= 'z')
                    || ('A' <= sc && sc <= 'Z')
                    || (";/?:@=&% $-_.+!*\'\"(),{}|\\^~[]".indexOf(sc) >= 0)) {
                sb.append(sc);
                continue;
            }
            try {
                byte[] bytes = new String(new char[]{sc}).getBytes("UTF-8");
                int j = 0, bl = bytes.length;
                for (; j < bl; ++j) {
                    sb.append('%');
                    byte b = bytes[j];
                    char bc = Character.forDigit((b >> 4) & 0xf, 16);
                    if (('a' <= bc && bc <= 'z'))
                        bc -= 0x20;
                    sb.append(bc);
                    bc = Character.forDigit(b & 0xf, 16);
                    if (('a' <= bc && bc <= 'z'))
                        bc -= 0x20;
                    sb.append(bc);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
