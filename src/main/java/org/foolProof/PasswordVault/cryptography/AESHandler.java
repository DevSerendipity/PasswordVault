package org.foolProof.PasswordVault.cryptography;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class AESHandler {


    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;
    private static String trueFileName = "";

    public static SecretKey getAESKeyFromPassword(char[] password, byte[] salt) {
        KeySpec spec = new PBEKeySpec(password, salt, ITERATION_COUNT, KEY_LENGTH);
        try {
            return new SecretKeySpec(getSecretKey().generateSecret(spec).getEncoded(), "AES");
        } catch ( InvalidKeySpecException e ) {
            throw new RuntimeException(e);
        }
    }

    public static SecretKeyFactory getSecretKey() {
        SecretKeyFactory factory;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch ( NoSuchAlgorithmException e ) {
            throw new RuntimeException(e);
        }
        return factory;
    }

    public static byte[] getRandomNonce(int numBytes) {
        byte[] nonce = new byte[numBytes];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    public static void startCryptography(String fromFile) {
        String password = "password123"; // TODO now we need to encrypt this
        String file = fromFile;
        Path path = Paths.get(file);
        if ( isFileEncrypted(file) ) {
            AESFileDecryption.decryptController(password, file, path);
        } else {
            AESFileEncryption.encryptController(password, file, path);
        }
    }

    private static boolean isFileEncrypted(String fromFile) {
        return fromFile.contains("-encrypted.txt");
    }

    public static String getTrueFileName() {
        return trueFileName.replace("C:/testing/", "").trim();
    }

    public static void setTrueFileName(String trueFileName) {
        AESHandler.trueFileName = trueFileName;
    }
}
