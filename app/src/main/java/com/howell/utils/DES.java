package com.howell.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DES {
    public static String CBCEncrypt(String data, byte[] key, byte[] iv) {
        try {
            DESKeySpec dks = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");
            IvParameterSpec param = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, param);
            byte encryptedData[] = cipher.doFinal(data.getBytes());
            return HEXTranslate.getHexString(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}