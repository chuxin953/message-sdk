package com.xiangxi.message.sms.tencent;

/**
 * @author 初心
 * Create by on 2025/9/22 11:36 55
 */
public class DatatypeConverter {
    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();
    private static final char[] encodeMap = initEncodeMap();

    public static String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);

        for(byte b : data) {
            r.append(hexCode[b >> 4 & 15]);
            r.append(hexCode[b & 15]);
        }

        return r.toString();
    }

    public static String printBase64Binary(byte[] input) {
        char[] buf = new char[(input.length + 2) / 3 * 4];
        int ptr = _printBase64Binary(input, 0, input.length, buf, 0);

        assert ptr == buf.length;

        return new String(buf);
    }

    private static int _printBase64Binary(byte[] input, int offset, int len, char[] buf, int ptr) {
        int remaining = len;

        int i;
        for(i = offset; remaining >= 3; i += 3) {
            buf[ptr++] = encode(input[i] >> 2);
            buf[ptr++] = encode((input[i] & 3) << 4 | input[i + 1] >> 4 & 15);
            buf[ptr++] = encode((input[i + 1] & 15) << 2 | input[i + 2] >> 6 & 3);
            buf[ptr++] = encode(input[i + 2] & 63);
            remaining -= 3;
        }

        if (remaining == 1) {
            buf[ptr++] = encode(input[i] >> 2);
            buf[ptr++] = encode((input[i] & 3) << 4);
            buf[ptr++] = '=';
            buf[ptr++] = '=';
        }

        if (remaining == 2) {
            buf[ptr++] = encode(input[i] >> 2);
            buf[ptr++] = encode((input[i] & 3) << 4 | input[i + 1] >> 4 & 15);
            buf[ptr++] = encode((input[i + 1] & 15) << 2);
            buf[ptr++] = '=';
        }

        return ptr;
    }

    private static char[] initEncodeMap() {
        char[] map = new char[64];

        for(int i = 0; i < 26; ++i) {
            map[i] = (char)(65 + i);
        }

        for(int var2 = 26; var2 < 52; ++var2) {
            map[var2] = (char)(97 + (var2 - 26));
        }

        for(int var3 = 52; var3 < 62; ++var3) {
            map[var3] = (char)(48 + (var3 - 52));
        }

        map[62] = '+';
        map[63] = '/';
        return map;
    }

    public static char encode(int i) {
        return encodeMap[i & 63];
    }
}
