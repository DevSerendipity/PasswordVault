package org.foolProof.PasswordVault.web;

import org.foolProof.PasswordVault.User.Client;
import org.foolProof.PasswordVault.User.ClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Controller public class WebsiteController {

    private final ClientService clientService;

    public WebsiteController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/getFolderSelected") public String getFolderSelected() {
        return "/folderPicker.html";
    }

    @Value("${file.upload-dir}") String File_Directory;

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public ResponseEntity<Object> fileUpload(@RequestParam("File") MultipartFile file) throws IOException {
        File myFile = new File(File_Directory + file.getOriginalFilename());

        myFile.createNewFile();

        FileOutputStream fos = new FileOutputStream(myFile);

        fos.write(file.getBytes());
        fos.close();
        return new ResponseEntity<>("The file uploaded successfully", HttpStatus.OK);
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET) public String getClient(Model model) {
        List<Client> clients = clientService.getAllClients();
        model.addAttribute("clients", clients);
        model.addAttribute("client", new Client());
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String saveStudent(Model model, @ModelAttribute Client client) {
        Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder();
        client.setPassword(argon2PasswordEncoder.encode(client.getPassword()));
        System.out.println(client.getPassword());
        model.addAttribute("password", client.getPassword());
        clientService.addNewClient(client);
        return "redirect:/register";
    }

}
