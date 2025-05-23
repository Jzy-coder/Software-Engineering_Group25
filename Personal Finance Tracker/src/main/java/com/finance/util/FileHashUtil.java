package com.finance.util;

import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * Utility class for calculating file hashes.
 */
public class FileHashUtil {
    /**
     * Calculates the MD5 hash of a given file.
     * @param filePath The path to the file.
     * @return The MD5 hash as a String, or null if an error occurs.
     */
    public static String calculateMD5(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                md.update(buffer, 0, read);
            }
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}