package org.foolProof.PasswordVault.web;

import org.foolProof.PasswordVault.User.Client;
import org.foolProof.PasswordVault.User.ClientService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller public class WebsiteController {

    private final ClientService clientService;

    public WebsiteController( ClientService clientService ) {
        this.clientService = clientService;
    }

    @GetMapping("/getFile") public String getFolderSelected() {
        return "/filePicker.html";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET) public String getClient( Model model ) {
        List<Client> clients = clientService.getAllClients();
        model.addAttribute( "clients", clients );
        model.addAttribute( "client", new Client() );
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String saveStudent( Model model, @ModelAttribute Client client ) {
        Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder();
        client.setPassword( argon2PasswordEncoder.encode( client.getPassword() ) );
        System.out.println( client.getPassword() );
        model.addAttribute( "password", client.getPassword() );
        clientService.addNewClient( client );
        return "redirect:/register";
    }

}
