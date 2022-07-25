package org.foolProof.PasswordVault.cryptography;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class AESFileEncryption {

    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;

    public static byte[] encrypt(byte[] pText, String password) {
        byte[] salt = AESHandler.getRandomNonce(SALT_LENGTH_BYTE);
        // GCM recommended 12 bytes iv?
        byte[] iv = AESHandler.getRandomNonce(IV_LENGTH_BYTE);
        SecretKey aesKeyFromPassword = AESHandler.getAESKeyFromPassword(password.toCharArray(), salt);
        Cipher cipher = AESCipherHandler.getCipherInstance();
        // ASE-GCM needs GCMParameterSpec
        AESCipherHandler.cipherInitialization(cipher, Cipher.ENCRYPT_MODE, aesKeyFromPassword, iv);
        byte[] cipherText = AESCipherHandler.getCipherText(pText, cipher);
        return ByteBuffer.allocate(iv.length + salt.length + cipherText.length).put(iv).put(salt).put(cipherText)
                .array();
    }

    public static void encryptFile(String fromFile, String toFile, String password) {
        // read a normal txt file
        byte[] fileContent = AESFileHandler.getFileContent(fromFile);
        // encrypt with a password
        byte[] encryptedText = encrypt(fileContent, password);
        Path path = Paths.get(toFile);
        AESFileHandler.writeToFile(encryptedText, path);
    }

    static void encryptController(String password, String fromFile, Path path) {
        try {
            Files.copy(path, Paths.get(fromFile.concat("-encrypted.txt")), StandardCopyOption.REPLACE_EXISTING);
        } catch ( IOException e ) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        AESHandler.setTrueFileName(fromFile);
        AESFileEncryption.encryptFile(fromFile, fromFile, password);
    }

}
