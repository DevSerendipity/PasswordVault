package org.foolProof.PasswordVault.File;

import java.io.IOException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class FileManagement {
    private final ArrayList<String> emails = new ArrayList<>();

    public ArrayList<String> getEmails() {
        return emails;
    }

    public void addAllEmails() {
        Path path = Paths.get("src/main/resources/mockData/userEmails.csv");
        try (Stream<String> stream = Files.lines(path)) {
            stream.forEach(emails::add);
        } catch (IOException e) {
            throw new FileSystemNotFoundException("The file could not be found, check the file or adjust the location");
        }
    }

}
