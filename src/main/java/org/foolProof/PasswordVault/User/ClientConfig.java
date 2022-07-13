package org.foolProof.PasswordVault.User;

import org.foolProof.PasswordVault.File.FileManagement;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.util.List;

@Configuration public class ClientConfig {
    private final FileManagement fileManagement = new FileManagement();

    @Bean CommandLineRunner commandLineRunner( ClientRepository clientRepository ) {
        int getLastClientById = clientRepository.findClientById();
        fileManagement.addAllEmails();
        System.out.println( "START" );
        return args -> {
            Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder();
            for ( int i = 0; i < 2; i++ ) {
                clientRepository.saveAll( List.of( new Client( fileManagement.getEmails().get( getLastClientById + i ),
                                                               argon2PasswordEncoder.encode( fileManagement.getEmails()
                                                                                                     .get( ( getLastClientById
                                                                                                             + i ) ) ) ) ) );
            }
        };
    }
}
