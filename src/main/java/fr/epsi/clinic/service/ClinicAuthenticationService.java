package fr.epsi.clinic.service;

import java.security.InvalidParameterException;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
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

        staffService.saveStaff(staff);
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

    /**
     * Will increase attempt failed in the Staff values until it lock the account and compare its lock time with the time of a locked account
     * @param staff
     * @return true if the user is not locked, else false.
     */
    public boolean isUserAntiBruteForceDisabled(Staff staff){
        int failedConnections = staff.getFailedConnections();
        int nbOfFailedMax = 3;

        if(this.isUserAccountLocked(staff)){
            return false;
        }

        //If the staff has more than X attempt and the staff account is not locked
        if(failedConnections >= nbOfFailedMax && !this.isUserAccountLocked(staff)){
            staff.setFailedConnections(0);
        }

        if(failedConnections >= nbOfFailedMax){
            lockStaffAccount(staff);
            return false;
        }

        return true;
    }

    /**
     * Set current date to lock staff account
     * @param staff
     */
    private void lockStaffAccount(Staff staff){
        staff.setLockTime(new Date());
        staffService.saveStaff(staff);
    }

    /**
     * Check if a staff is locked by checking the locked account time and the current time
     * @param staff
     * @return true if the user is locked, else false
     */
    private boolean isUserAccountLocked(Staff staff){

        if(Objects.isNull(staff.getLockTime())){
            return false;
        }

        int lockTimeDuration = 2 * 60 * 1000;
        long lockTimeInMillis = staff.getLockTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();
         
        //if user account lock time is less than the current date
        if ((lockTimeInMillis + lockTimeDuration) < currentTimeInMillis) {
            return false;
        }

        return true;
    }

    /**
     * Add one to the failed connection for a given Staff
     * @param staff
     */
    public void incrementStaffFailedConnection(Staff staff){
        staff.setFailedConnections(staff.getFailedConnections() + 1);
        staffService.saveStaff(staff);
    }

    public void doubleAuthentication(){
        //TODO implémenter la logique
    }

    public boolean verifyGivenOTP(String givenOTP, Staff staff){
        // CHECK: givenOTP value
        if (givenOTP.isEmpty()) {
            throw new NullPointerException("givenOTP is null or empty");
        }

        // CHECK: staff object value
        if (Objects.isNull(staff)) {
            throw new NullPointerException("staff is null");
        }

        // CHECK: staff.givenOTP value
        final String staffGivenOtp = staff.getOTP();

        if (staffGivenOtp.isEmpty()) {
            throw new NullPointerException("staff's givenOtp is empty");
        }

        // COMPARE: staff.givenOTP WITH givenOTP
        return staffGivenOtp.equals(givenOTP);
    }

    public void deleteUserOTP(Staff staff){
        //Delete user from BDD
    }

    public void sendEmailToConfirmUserIdentity(Staff staff){
        //TODO implémenter la logique
    }
}
