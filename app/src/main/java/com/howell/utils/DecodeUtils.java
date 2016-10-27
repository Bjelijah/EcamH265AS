package com.howell.utils;

import java.security.NoSuchAlgorithmException;

public class DecodeUtils {
    public static String getEncodedPassword(String password) {
        byte[] key = { 0x48, 0x4F, 0x57, 0x45, 0x4C, 0x4C, 0x4B, 0x45 };
        byte[] iv = { 0x48, 0x4F, 0x57, 0x45, 0x4C, 0x4C, 0x56, 0x49 };
        byte[] rdKey = RandomBytes.getRandombyte();
        byte[] rdIv = RandomBytes.getRandombyte();
        String DES2Password = null;
        try {
            String MD5Password = MD5.getMD5(password);
            String hexKey = HEXTranslate.getHexString(rdKey);
            String hexIv = HEXTranslate.getHexString(rdIv);
            String DES1Password = DES.CBCEncrypt(MD5Password, rdKey, rdIv);
            DES2Password = DES.CBCEncrypt(hexKey + hexIv + DES1Password, key,
                    iv);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return DES2Password;
    }
}
