package fr.epsi.clinic.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import fr.epsi.clinic.service.VerificationInformationTokenService;

@Controller
public class MainController {

    @Autowired
    VerificationInformationTokenService verificationInformationTokenService;

    @GetMapping("/")
    public String getWelcome() {
        return "connected";
    }

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {

        return "index";
    }

    @GetMapping("/validation-identity")
    public ModelAndView validationIdentity(@RequestParam("otp") String otp){
        ModelAndView model = new ModelAndView("validation-identity");
        boolean isOTPValid = this.verificationInformationTokenService.isOTPValid(otp);

        if(isOTPValid){ 
            this.verificationInformationTokenService.updateStaffWithTokenInformation(otp);
            model.addObject("message", "Votre connexion à été vérifiée et normalisée, veuillez vous connecter à votre compte.");
        } else {
            model.setViewName("index");
        }

        return model;
    }

}
