package fr.epsi.clinic.service;

import java.security.InvalidParameterException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import fr.epsi.clinic.model.Staff;

@Service
public class ClinicAuthenticationService {

    public User getUserByUsername(String username){
        return null;
    }

    public void addUser(User user){
        //save to repository
    }

    public boolean isUsuerIpIsUsual(HttpServletRequest request, Staff staff){
        // Get current Ip
        if (request == null) {
            throw new InvalidParameterException("request is empty");
        }

        final String currentIp = request.getHeader("host");

        if (currentIp == null) {
            throw new NullPointerException("currentIp from client cannot be found");
        }

        // Get last ip
        if (staff == null) {
            throw new InvalidParameterException("staff is empty");
        }
        
        final String lastIp = staff.getLastIpAddress();

        if (lastIp == null) {
            throw new NullPointerException("Last ip cannot be found");
        }

        // Compare both of them
        return currentIp.equals(lastIp);
    }

    public boolean isUserBrowserIsUsual(HttpServletRequest request, Staff staff){
        // Get current browser
        if (request == null) {
            throw new InvalidParameterException("request is empty");
        }

        final String currentBrowser = request.getHeader("user-agent");

        if (currentBrowser == null) {
            throw new NullPointerException("Current browser from client cannot be found");
        }

        // Get last browser
        if (staff == null) {
            throw new InvalidParameterException("staff is empty");
        }
        
        final String lastBrowser = staff.getBrowser();

        if (lastBrowser == null) {
            throw new NullPointerException("Last browser cannot be found");
        }

        // Compare both of them
        return currentBrowser.equals(lastBrowser);
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
