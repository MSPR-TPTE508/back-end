package fr.epsi.clinic.service;

import java.security.InvalidParameterException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import fr.epsi.clinic.model.Staff;
import fr.epsi.clinic.model.StaffLdapDetails;

@Service
public class ClinicAuthenticationService {

    @Autowired
    StaffService staffService;

    public User getUserByUsername(String username){
        return null;
    }

    public void addUser(HttpServletRequest request, StaffLdapDetails staffLdapDetails){
        Staff staff = new Staff();
        String userAgent = request.getHeader("user-agent");
        String currentIp = request.getRemoteAddr();

        //Initialize a staff object with its AD and Request values.
        staff.setEmail(staffLdapDetails.getActiveDirectoryEmail());
        staff.setBrowser(userAgent);
        staff.setLastIpAddress(currentIp);

        staffService.addStaff(staff);
    }

    public boolean isUsuerIpIsUsual(HttpServletRequest request, Staff staff){
        // Get current Ip
        if (request == null) {
            throw new InvalidParameterException("request is empty");
        }

        final String currentIp = request.getRemoteAddr();

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

    public boolean isUserAntiBruteForceDisabled(Staff staff){
        //TODO implémenter la logique
        return true;
    }

    public void doubleAuthentication(){
        //TODO implémenter la logique
    }

    public boolean verifyGivenOTP(String givenOTP, Staff staff){
        //TODO implémenter la logique
        return true;
    }

    public void deleteUserOTP(Staff staff){
        //Delete user from BDD
    }

    public void sendEmailToConfirmUserIdentity(Staff staff){
        //TODO implémenter la logique
    }
}
