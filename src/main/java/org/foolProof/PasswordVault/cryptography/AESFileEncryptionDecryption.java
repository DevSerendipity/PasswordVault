package org.foolProof.PasswordVault.cryptography;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class AESFileEncryptionDecryption {

    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128; // must be one of {128, 120, 112, 104, 96}
    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;
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

    public static byte[] encrypt(byte[] pText, String password) {
        byte[] salt = getRandomNonce(SALT_LENGTH_BYTE);
        // GCM recommended 12 bytes iv?
        byte[] iv = getRandomNonce(IV_LENGTH_BYTE);
        SecretKey aesKeyFromPassword = getAESKeyFromPassword(password.toCharArray(), salt);
        Cipher cipher = getCipherInstance();
        // ASE-GCM needs GCMParameterSpec
        cipherInitialization(cipher, Cipher.ENCRYPT_MODE, aesKeyFromPassword, iv);
        byte[] cipherText = getCipherText(pText, cipher);
        return ByteBuffer.allocate(iv.length + salt.length + cipherText.length).put(iv).put(salt).put(cipherText)
                .array();
    }

    private static void cipherInitialization(Cipher cipher, int encryptMode, SecretKey aesKeyFromPassword, byte[] iv) {
        try {
            cipher.init(encryptMode, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        } catch ( InvalidKeyException | InvalidAlgorithmParameterException e ) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] getCipherText(byte[] pText, Cipher cipher) {
        byte[] cipherText;
        try {
            cipherText = cipher.doFinal(pText);
        } catch ( IllegalBlockSizeException | BadPaddingException e ) {
            throw new RuntimeException(e);
        }
        return cipherText;
    }

    private static Cipher getCipherInstance() {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(ENCRYPT_ALGO);
        } catch ( NoSuchAlgorithmException | NoSuchPaddingException e ) {
            throw new RuntimeException(e);
        }
        return cipher;
    }

    // we need the same password, salt and iv to decrypt it
    private static byte[] decrypt(byte[] cText, String password) {
        ByteBuffer bb = ByteBuffer.wrap(cText);
        byte[] iv = new byte[12];
        bb.get(iv);

        byte[] salt = new byte[16];
        bb.get(salt);

        byte[] cipherText = new byte[bb.remaining()];
        bb.get(cipherText);
        // get back the aes key from the same password and salt
        SecretKey aesKeyFromPassword = getAESKeyFromPassword(password.toCharArray(), salt);
        Cipher cipher = getCipherInstance();
        cipherInitialization(cipher, Cipher.DECRYPT_MODE, aesKeyFromPassword, iv);
        try {
            return cipher.doFinal(cipherText);
        } catch ( IllegalBlockSizeException | BadPaddingException e ) {
            throw new RuntimeException(e);
        }
    }

    public static void encryptFile(String fromFile, String toFile, String password) {
        // read a normal txt file
        byte[] fileContent = getFileContent(fromFile);
        // encrypt with a password
        byte[] encryptedText = encrypt(fileContent, password);
        // save a file
        Path path = Paths.get(toFile);
        writeToFile(encryptedText, path);
    }

    private static byte[] getFileContent(String fromFile) {
        byte[] fileContent;
        try {
            fileContent = Files.readAllBytes(Paths.get(fromFile));
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
        return fileContent;
    }

    private static void writeToFile(byte[] encryptedText, Path path) {
        try {
            Files.write(path, encryptedText);
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] decryptFile(String fromEncryptedFile, String password) {
        byte[] file = getFile(fromEncryptedFile);
        return decrypt(file, password);
    }

    private static byte[] getFile(String fromEncryptedFile) {
        byte[] file;
        try {
            file = Files.readAllBytes(Paths.get(fromEncryptedFile));
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
        return file;
    }

    public static void startCryptography(String fromFile) {
        String password = "password123"; // TODO now we need to encrypt this
        String file = fromFile;
        Path path = Paths.get(file);
        if ( isFileEncrypted(file) ) {
            // decrypt file
            byte[] decryptedText = decryptFile(file, password);
            file = file.replace("-encrypted.txt", "");
            Path newPath = path.resolveSibling(file);
            try {
                // rename a file in the same directory
                Files.copy(path, newPath, StandardCopyOption.REPLACE_EXISTING);
            } catch ( IOException e ) {
                e.printStackTrace();
            }
            setTrueFileName(file);
            try {
                Files.write(newPath, new String(decryptedText, StandardCharsets.UTF_8).getBytes());
            } catch ( IOException e ) {
                throw new RuntimeException(e);
            }
        } else {
            file = file.replace(file, file.concat("-encrypted.txt"));
            try {
                Files.copy(path, Paths.get(file), StandardCopyOption.REPLACE_EXISTING);
            } catch ( IOException e ) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
            setTrueFileName(file);
            encryptFile(file, file, password);
        }
    }

    private static boolean isFileEncrypted(String fromFile) {
        return fromFile.contains("-encrypted.txt");
    }

    public static String getTrueFileName() {
        return trueFileName.replace("C:/testing/", "").trim();
    }

    public static void setTrueFileName(String trueFileName) {
        AESFileEncryptionDecryption.trueFileName = trueFileName;
    }
}
