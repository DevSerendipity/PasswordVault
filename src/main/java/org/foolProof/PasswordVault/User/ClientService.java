package org.foolProof.PasswordVault.User;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service public class ClientService {
    private final ClientRepository clientRepository;

    @Autowired public ClientService( ClientRepository clientRepository ) {
        this.clientRepository = clientRepository;
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public void addNewClient( Client client ) {
        if ( emailExists( client.getEmail() ) ) {
            throw new IllegalStateException( "email taken" );
        }
        clientRepository.save( client );
    }

    private boolean emailExists( String email ) {
        return clientRepository.findClientByEmail( email ) != null;
    }

    public void deleteClient( Long clientId ) {
        boolean clientExist = clientRepository.existsById( clientId );
        if ( !clientExist ) {
            throw new IllegalStateException( "client with id:" + clientId + " does not exist." );
        }
        clientRepository.deleteById( clientId );
    }

    @Transactional public void updateClient( Long clientId, String email ) {
        Client client = clientRepository.findById( clientId )
                .orElseThrow( () -> new IllegalStateException( "client with id:" + clientId + " does not exist" ) );

        if ( email != null && email.length() > 0 && !Objects.equals( client.getEmail(), email ) ) {
            if ( emailExists( client.getEmail() ) ) {
                throw new IllegalStateException( "email taken" );
            }
            client.setEmail( email );
        }
    }
}
