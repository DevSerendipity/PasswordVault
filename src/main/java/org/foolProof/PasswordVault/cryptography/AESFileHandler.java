package org.foolProof.PasswordVault.cryptography;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AESFileHandler {
    static byte[] getFileContent(String fromFile) {
        byte[] fileContent;
        try {
            fileContent = Files.readAllBytes(Paths.get(fromFile));
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
        return fileContent;
    }

    static void writeToFile(byte[] encryptedText, Path path) {
        try {
            Files.write(path, encryptedText);
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
    }

    static byte[] getFile(String fromEncryptedFile) {
        byte[] file;
        try {
            file = Files.readAllBytes(Paths.get(fromEncryptedFile));
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
        return file;
    }
}
