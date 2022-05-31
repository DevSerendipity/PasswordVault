package org.foolProof.PasswordVault.User;

import org.foolProof.PasswordVault.File.FileManagement;
import org.foolProof.PasswordVault.encryption.Encryption;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ClientConfig {
    FileManagement fileManagement = new FileManagement();

    @Bean
    CommandLineRunner commandLineRunner(ClientRepository clientRepository) {
        int getLastClientById = clientRepository.findClientById();

        fileManagement.addAllEmails();
        System.out.println("START");
        return args -> {
            Encryption encryption = new Encryption();
            for (int i = 1; i < 2; i++) {
                clientRepository.saveAll(List.of
                        (new Client(fileManagement.getEmails().get(getLastClientById + i),
                                "Notion",
                                encryption.generateHashPassword(fileManagement.getEmails().get((getLastClientById + i))))));
            }
        };
    }
}
