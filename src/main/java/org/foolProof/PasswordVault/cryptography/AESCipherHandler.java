package org.foolProof.PasswordVault.cryptography;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AESCipherHandler {

    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128; // must be one of {128, 120, 112, 104, 96}

    static void cipherInitialization(Cipher cipher, int encryptMode, SecretKey aesKeyFromPassword, byte[] iv) {
        try {
            cipher.init(encryptMode, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        } catch ( InvalidKeyException | InvalidAlgorithmParameterException e ) {
            throw new RuntimeException(e);
        }
    }

    static byte[] getCipherText(byte[] pText, Cipher cipher) {
        byte[] cipherText;
        try {
            cipherText = cipher.doFinal(pText);
        } catch ( IllegalBlockSizeException | BadPaddingException e ) {
            throw new RuntimeException(e);
        }
        return cipherText;
    }

    static Cipher getCipherInstance() {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(ENCRYPT_ALGO);
        } catch ( NoSuchAlgorithmException | NoSuchPaddingException e ) {
            throw new RuntimeException(e);
        }
        return cipher;
    }

}
