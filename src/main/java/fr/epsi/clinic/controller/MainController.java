package fr.epsi.clinic.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String getWelcome(){
        return "connected";
    }

    @GetMapping("/login")
    public String login(Model model){
        return "index";
    }
}
