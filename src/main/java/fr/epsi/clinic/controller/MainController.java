package fr.epsi.clinic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class MainController {

    @GetMapping("/")
    public String getWelcome(){
        return "connected";
    }

    @GetMapping("/login")
    public String login(){
        return "index";
    }
}
