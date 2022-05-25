package org.foolProof.PasswordVault.User;

import org.foolProof.PasswordVault.encryption.Encryption;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ClientConfig {

    @Bean
    CommandLineRunner commandLineRunner(ClientRepository clientRepository){
        return args -> {
            Encryption encryption = new Encryption();
            Client firstClient = new Client(
                    "person@gmail.com",
                    "Notion",
                    encryption.generateHashPassword("firstClient")

            );
            Client secondClient = new Client(
                    "barcode@gmail.com",
                    "Obsidian",
                    encryption.generateHashPassword("secondClient")
            );
        clientRepository.saveAll(List.of(firstClient,secondClient));
        };
    }
}
