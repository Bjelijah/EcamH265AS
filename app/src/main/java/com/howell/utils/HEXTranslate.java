package com.howell.utils;

import java.math.BigInteger;

public class HEXTranslate {
    public static String getHexString(byte[] buf) throws Exception {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static byte[] getByteArray(String hexString) {
        return new BigInteger(hexString, 16).toByteArray();
    }
}
