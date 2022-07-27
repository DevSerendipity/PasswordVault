package org.foolProof.PasswordVault.web;

import jakarta.servlet.http.HttpServletResponse;
import org.foolProof.PasswordVault.User.Client;
import org.foolProof.PasswordVault.User.ClientService;
import org.foolProof.PasswordVault.cryptography.AESHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller public class WebsiteController {

    private final ClientService clientService;
    @Value("${file.upload-dir}") private String fileDirectory;

    public WebsiteController(ClientService clientService) {
        this.clientService = clientService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET) public String login() {
        return "/login.html";
    }

    @GetMapping("/file") public String file() {
        return "/filePicker.html";
    }

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public void fileUpload(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {
        String fileName = file.getOriginalFilename();
        writeFileToDirectory(file, fileName);
        AESHandler.startCryptography(fileDirectory + fileName);
        response.sendRedirect("/download/" + AESHandler.getTrueFileName());
    }

    private void writeFileToDirectory(MultipartFile file, String fileName) {
        try ( FileOutputStream fos = new FileOutputStream(fileDirectory + fileName); ) {
            fos.write(file.getBytes());
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Object> downloadFileFromLocal(@PathVariable String fileName) {
        Path path = Paths.get(fileDirectory + fileName);
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch ( MalformedURLException e ) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/pdf"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
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
