package org.foolProof.PasswordVault.cryptography;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class AESFileDecryption {


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
        SecretKey aesKeyFromPassword = AESHandler.getAESKeyFromPassword(password.toCharArray(), salt);
        Cipher cipher = AESCipherHandler.getCipherInstance();
        AESCipherHandler.cipherInitialization(cipher, Cipher.DECRYPT_MODE, aesKeyFromPassword, iv);
        try {
            return cipher.doFinal(cipherText);
        } catch ( IllegalBlockSizeException | BadPaddingException e ) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] decryptFile(String fromEncryptedFile, String password) {
        byte[] file = AESFileHandler.getFile(fromEncryptedFile);
        return decrypt(file, password);
    }

    static void decryptController(String password, String fromFile, Path path) {
        String file = fromFile;
        byte[] decryptedText = AESFileDecryption.decryptFile(file, password);
        file = file.replace("-encrypted.txt", "");
        Path newPath = path.resolveSibling(file);
        try {
            Files.copy(path, newPath, StandardCopyOption.REPLACE_EXISTING);
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        AESHandler.setTrueFileName(file);
        try {
            Files.write(newPath, new String(decryptedText, StandardCharsets.UTF_8).getBytes());
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
    }
}
