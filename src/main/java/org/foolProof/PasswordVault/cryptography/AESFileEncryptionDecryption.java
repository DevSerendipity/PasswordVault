package org.foolProof.PasswordVault.cryptography;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class AESFileEncryptionDecryption {

    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128; // must be one of {128, 120, 112, 104, 96}
    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;

    public static SecretKey getAESKeyFromPassword( char[] password, byte[] salt )
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA256" );
        KeySpec spec = new PBEKeySpec( password, salt, ITERATION_COUNT, KEY_LENGTH );
        return new SecretKeySpec( factory.generateSecret( spec ).getEncoded(), "AES" );
    }

    public static byte[] getRandomNonce( int numBytes ) {
        byte[] nonce = new byte[ numBytes ];
        new SecureRandom().nextBytes( nonce );
        return nonce;
    }

    public static byte[] encrypt( byte[] pText, String password )
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        byte[] salt = getRandomNonce( SALT_LENGTH_BYTE );
        // GCM recommended 12 bytes iv?
        byte[] iv = getRandomNonce( IV_LENGTH_BYTE );
        SecretKey aesKeyFromPassword = getAESKeyFromPassword( password.toCharArray(), salt );
        Cipher cipher = Cipher.getInstance( ENCRYPT_ALGO );
        // ASE-GCM needs GCMParameterSpec
        cipher.init( Cipher.ENCRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec( TAG_LENGTH_BIT, iv ) );
        byte[] cipherText = cipher.doFinal( pText );
        return ByteBuffer.allocate( iv.length + salt.length + cipherText.length ).put( iv ).put( salt )
                .put( cipherText ).array();
    }

    // we need the same password, salt and iv to decrypt it
    private static byte[] decrypt( byte[] cText, String password )
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        ByteBuffer bb = ByteBuffer.wrap( cText );
        byte[] iv = new byte[ 12 ];
        bb.get( iv );

        byte[] salt = new byte[ 16 ];
        bb.get( salt );

        byte[] cipherText = new byte[ bb.remaining() ];
        bb.get( cipherText );
        // get back the aes key from the same password and salt
        SecretKey aesKeyFromPassword = getAESKeyFromPassword( password.toCharArray(), salt );
        Cipher cipher = Cipher.getInstance( ENCRYPT_ALGO );
        cipher.init( Cipher.DECRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec( TAG_LENGTH_BIT, iv ) );
        return cipher.doFinal( cipherText );
    }

    public static void encryptFile( String fromFile, String toFile, String password )
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException, IOException,
            URISyntaxException {
        // read a normal txt file
        byte[] fileContent = Files.readAllBytes( Paths.get( ClassLoader.getSystemResource( fromFile ).toURI() ) );
        // encrypt with a password
        byte[] encryptedText = encrypt( fileContent, password );
        // save a file
        Path path = Paths.get( toFile );
        Files.write( path, encryptedText );
    }

    public static byte[] decryptFile( String fromEncryptedFile, String password )
            throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException
            , NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        byte[] file = Files.readAllBytes( Paths.get( fromEncryptedFile ) );
        return decrypt( file, password );
    }

    public static void main( String[] args )
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, IOException, InvalidKeyException,
            URISyntaxException {
        String password = "password123"; // TODO now we need to encrypt this
        String fromFile = "some.txt";
        String toFile = "C:\\Users\\X\\Desktop\\someText-encrypted.txt";
        // encrypt file
        encryptFile( fromFile, toFile, password );
        // decrypt file
        byte[] decryptedText = decryptFile( toFile, password );
        String pText = new String( decryptedText );
        System.out.println( pText );
    }
}
