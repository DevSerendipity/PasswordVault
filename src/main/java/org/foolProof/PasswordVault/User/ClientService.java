package org.foolProof.PasswordVault.User;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> getHashes() {
        return clientRepository.findAll();
    }

    public void addNewClient(Client client) {
        Optional<Client> clientOptional =
                clientRepository.findClientByEmail(client.getEmail());
        if (clientOptional.isPresent()) {
            throw new IllegalStateException("email taken");
        }
        clientRepository.save(client);
    }

    public void deleteClient(Long clientId) {
        boolean clientExist = clientRepository.existsById(clientId);
        if (!clientExist) {
            throw new IllegalStateException("client with id:" + clientId + " does not exist.");
        }
        clientRepository.deleteById(clientId);
    }

    @Transactional
    public void updateClient(Long clientId, String email, String fileName) {
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new IllegalStateException("client with id:" + clientId + " does not exist"));

        if (email != null && email.length() > 0 && !Objects.equals(client.getEmail(), email)) {
            Optional<Client> clientOptional = clientRepository.findClientByEmail(email);
            if (clientOptional.isPresent()) {
                throw new IllegalStateException("email taken");
            }
            client.setEmail(email);
        }
        if (fileName != null && fileName.length() > 0 && !Objects.equals(client.getFileName(), fileName)) {
            client.setFileName(fileName);
        }
    }
}
