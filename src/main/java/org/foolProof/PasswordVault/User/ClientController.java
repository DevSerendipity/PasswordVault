package org.foolProof.PasswordVault.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping(path = "api/v1/client") public class ClientController {

    private final ClientService clientService;

    @Autowired public ClientController( ClientService userService ) {
        this.clientService = userService;
    }

    @GetMapping public List<Client> getAllClients() {
        return clientService.getAllClients();
    }

    @PostMapping public void newClient( @RequestBody Client client ) {
        clientService.addNewClient( client );
    }

    @DeleteMapping(path = "{clientId}") public void deleteClient( @PathVariable("clientId") Long id ) {
        clientService.deleteClient( id );
    }

    @PutMapping(path = "{clientId}")
    public void updateClient( @PathVariable("clientId") Long clientId, @RequestParam(required = false) String email ) {
        clientService.updateClient( clientId, email );
    }
}
