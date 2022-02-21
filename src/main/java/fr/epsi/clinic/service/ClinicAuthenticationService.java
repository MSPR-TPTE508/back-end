package fr.epsi.clinic.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class ClinicAuthenticationService {

    public User getUserByUsername(String username){
        return null;
    }

    public void addUser(User user){
        //save to repository
    }

    public boolean isUsuerIpIsUsual(HttpServletRequest request, User user){
        //TODO implémenter la logique
        return true;
    }

    public boolean isUserBrowserIsUsual(HttpServletRequest request, User user){
        //TODO implémenter la logique
        return true;
    }

    public boolean isUserAntiBruteForceDisabled(User user){
        //TODO implémenter la logique
        return true;
    }

    public void doubleAuthentication(){
        //TODO implémenter la logique
    }

    public boolean verifyGivenOTP(String givenOTP, User user){
        //TODO implémenter la logique
        return true;
    }

    public void deleteUserOTP(User user){
        //Delete user from BDD
    }

    public void sendEmailToConfirmUserIdentity(User user){
        //TODO implémenter la logique
    }
}
