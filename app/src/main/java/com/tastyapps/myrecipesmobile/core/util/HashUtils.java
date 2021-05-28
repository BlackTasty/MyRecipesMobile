package com.tastyapps.myrecipesmobile.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {
    public static String hashSHA512(String input) {
        return hashWithAlgorithm(input, "SHA-512");
    }

    public static String hashSHA256(String input, String salt) {
        return hashWithAlgorithm(salt + input, "SHA-256");
    }

    private static String hashWithAlgorithm(String input, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
