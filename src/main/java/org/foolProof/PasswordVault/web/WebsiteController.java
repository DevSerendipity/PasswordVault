package org.foolProof.PasswordVault.web;

import org.foolProof.PasswordVault.User.Client;
import org.foolProof.PasswordVault.User.ClientService;
import org.foolProof.PasswordVault.cryptography.PasswordEncryption;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class WebsiteController {

    private final ClientService clientService;

    public WebsiteController( ClientService clientService ) {
        this.clientService = clientService;
    }

    @GetMapping("/register")
    public String getRegistration() {
        return "/register.html";
    }

    @GetMapping("/getFile")
    public String getFolderSelected() {
        return "/filePicker.html";
    }

    @RequestMapping(value = "/password", method = RequestMethod.GET)
    public String getClient( Model model ) {
        List<Client> clients = clientService.getAllClients();
        model.addAttribute( "clients", clients );
        model.addAttribute( "client", new Client() );
        return "password";
    }

    @RequestMapping(value = "/password", method = RequestMethod.POST)
    public String saveStudent( Model model, @ModelAttribute Client client ) {
        PasswordEncryption passwordEncryption = new PasswordEncryption();
        client.setPassword( passwordEncryption.generateHashPassword( client.getPassword() ) );
        System.out.println( client.getPassword() );
        model.addAttribute( "password", client.getPassword() );
        clientService.addNewClient( client );
        return "redirect:/password";
    }
}
