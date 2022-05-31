package org.foolProof.PasswordVault.User;

import org.foolProof.PasswordVault.encryption.Encryption;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Configuration
public class ClientConfig {
    private final ArrayList<String> emails = new ArrayList<>();

    @Bean
    CommandLineRunner commandLineRunner(ClientRepository clientRepository) {
        int getLastClientById = clientRepository.findClientById();

        addAllEmails();
        System.out.println("START");
        return args -> {
            Encryption encryption = new Encryption();
            for (int i = 1; i < 2; i++) {
                clientRepository.saveAll(List.of
                        (new Client(emails.get(getLastClientById + i),
                                "Notion",
                                encryption.generateHashPassword(emails.get((getLastClientById + i))))));
            }
        };
    }

    private void addAllEmails() {
        Path path = Paths.get("src/main/resources/mockData/userEmails.csv");
        try (Stream<String> stream = Files.lines(path)) {
            stream.forEach(emails::add);
        } catch (IOException e) {
            throw new FileSystemNotFoundException("The file could not be found, check the file or adjust the location");
        }
    }
}
