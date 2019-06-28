package com.unibg.app3dsat.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class: HashFunction
 */
public class HashFunction {

    /**
     * Method: toSha256
     * String to SHA-256
     *
     * @param password
     * @return String
     * @throws NoSuchAlgorithmException
     */
    public static String toSha256(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());
        return bytesToHex(md.digest());
    }

    /**
     * Bytes to HEX
     *
     * @param bytes
     * @return String
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte byt : bytes)
            result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }
}