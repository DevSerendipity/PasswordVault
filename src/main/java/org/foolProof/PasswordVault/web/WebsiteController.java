package org.foolProof.PasswordVault.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WebsiteController {

    @GetMapping("/register")
    public String getStyledPage() {
        return "/register.html";
    }
}