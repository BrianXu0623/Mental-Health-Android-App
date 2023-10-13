package comp5216.sydney.edu.au.mentalhealth.activities;


import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MyUtils {
    public static boolean validatePassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }
        if (!password.matches(".*\\d.*")) {
            return false;
        }
        if (!password.matches(".*[a-z].*")) {
            return false;
        }
        return true;
    }

    public static String encrypt(String password) throws IOException {
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Add password bytes to digest
            md.update(password.getBytes());

            // Get the hash's bytes
            byte[] bytes = md.digest();

            // Convert bytes to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception or throw it further
            e.printStackTrace();
            return null;
        }
    }
}