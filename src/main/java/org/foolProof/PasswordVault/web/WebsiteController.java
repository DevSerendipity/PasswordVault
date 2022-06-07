package org.foolProof.PasswordVault.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebsiteController {

    @GetMapping("/register")
    public String getRegistration() {
        return "/register.html";
    }

    @GetMapping("/getFolder")
    public String getFolderSelected() {
        return "/folderPicker.html";
    }
    @GetMapping("/password")
    public String password() {
        return "/password.html";
    }
}