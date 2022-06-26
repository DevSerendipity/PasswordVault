package org.foolProof.PasswordVault.web;

import org.foolProof.PasswordVault.User.Client;
import org.foolProof.PasswordVault.User.ClientService;
import org.foolProof.PasswordVault.encryption.Encryption;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller public class WebsiteController {

    private final ClientService clientService;

    public WebsiteController( ClientService clientService ) {
        this.clientService = clientService;
    }

    @GetMapping("/register") public String register() {
        return "/register.html";
    }

    @GetMapping("/login") public String getRegistration() {
        return "/login.html";
    }

    @GetMapping("/getFolder") public String getFolderSelected() {
        return "/folderPicker.html";
    }

    @GetMapping("/getFile") public String getFileSelected() {
        return "/filePicker.html";
    }

    @RequestMapping(value = "/password", method = RequestMethod.GET) public String getClient( Model model ) {
        List<Client> clients = clientService.getAllClients();
        model.addAttribute( "clients", clients );
        model.addAttribute( "client", new Client() );
        return "password";
    }

    @RequestMapping(value = "/password", method = RequestMethod.POST)
    public String saveStudent( Model model, @ModelAttribute Client client ) {
        Encryption encryption = new Encryption();
        client.setPassword( encryption.generateHashPassword( client.getPassword() ) );
        System.out.println( client.getPassword() );
        model.addAttribute( "password", client.getPassword() );
        clientService.addNewClient( client );
        return "redirect:/password";
    }
}
