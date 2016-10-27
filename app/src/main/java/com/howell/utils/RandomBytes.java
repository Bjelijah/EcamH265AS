package com.howell.utils;

import java.util.Random;

public class RandomBytes {
    public static byte[] getRandombyte() {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 8; i++) {
            int is = random.nextInt(9);
            sb.append(is);
        }
        return sb.toString().getBytes();
    }
}
