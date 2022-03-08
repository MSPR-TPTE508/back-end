package fr.epsi.clinic.service;

import java.net.InetAddress;
import java.security.InvalidParameterException;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import fr.epsi.clinic.configuration.EmailServiceConfiguration;
import fr.epsi.clinic.configuration.GeoIpConfiguration;
import fr.epsi.clinic.model.Staff;
import fr.epsi.clinic.model.StaffLdapDetails;
import fr.epsi.clinic.provider.totp.TotpProvider;

@Service
public class ClinicAuthenticationService {

    @Autowired
    StaffService staffService;

    @Autowired
    GeoIpConfiguration geoIpConfiguration;

    private final TotpProvider totpProvider = new TotpProvider();
    private final EmailServiceConfiguration emailServiceConfiguration = new EmailServiceConfiguration();

    public User getUserByUsername(String username){
        return null;
    }

    public Optional<Staff> saveStaff(HttpServletRequest request, Staff staff){
        Staff staffToSave = new Staff();
        String userAgent = request.getHeader("user-agent");
        String currentIp = request.getRemoteAddr();

        //Initialize a staff object with its AD and Request values.
        staffToSave.setEmail(staff.getEmail());
        staffToSave.setBrowser(userAgent);
        staffToSave.setLastIpAddress(currentIp);

       return staffService.saveStaff(staffToSave);
    }

    public boolean isUserIpIsUsual(HttpServletRequest request, Staff staff){
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
            this.resetStaffFailedConnections(staff);
        }

        if(failedConnections >= nbOfFailedMax){
            lockStaffAccount(staff);
            return false;
        }

        return true;
    }

    /**
     * Reset staff failed connections
     * @param staff
     */
    public void resetStaffFailedConnections(Staff staff){
        staff.setFailedConnections(0);
        this.staffService.saveStaff(staff);
    }

    /**
     * Check if the user's connection is from the clinic domain
     * @param request
     * @return
     */
    public boolean isUserIpAddressIsFromDomain(HttpServletRequest request){
        try {
            InetAddress ip = InetAddress.getByName(request.getRemoteAddr());

            if(ip.isAnyLocalAddress() || ip.isLoopbackAddress()){
                return true;
            } else {
                return false;
            }
        } catch(Exception e) {
            return false;
        }
        
    }

        /**
     * Check if the user's connection is from france
     * @param request
     * @return
     */
    public boolean isUserIpAddressConform(HttpServletRequest request){
        
        try{
            InetAddress ip = InetAddress.getByName(request.getRemoteAddr());
            CountryResponse response = geoIpConfiguration.getDbReader().country(ip);
            
            return response.getCountry().getName().equals("France");
        } catch(Exception e){
            return false;
        }
    }

    /**
     * Set current date to lock staff account
     * @param staff
     */
    public void lockStaffAccount(Staff staff){
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

        //If the staff connectionFailedTimePassed returned false, increment by one, else restart the counter to 1
        if(!this.isStaffConnectionFailedTimePassed(staff)){
            staff.setFailedConnections(staff.getFailedConnections() + 1);
        } else {
            staff.setFailedConnections(1);
            staff.setFailedConnectionTime(new Date());
        }
        
        staffService.saveStaff(staff);
    }

    /**
     * Check the staff failed connection time to reset its attempts if it passed a certain time.
     * @return true if the user has to reset the number of connection else false
     */
    private boolean isStaffConnectionFailedTimePassed(Staff staff){
        if(Objects.isNull(staff.getFailedConnectionTime())){
            return true;
        }

        int failedConnectionDuration = 1 * 60 * 1000;
        long failedConnectionTimeInMillis = staff.getFailedConnectionTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();
         
        //if staff connection date + the duration is less than the current date
        if ((failedConnectionTimeInMillis + failedConnectionDuration) < currentTimeInMillis) {
            return true;
        }

        return false;
    }

    public void doubleAuthentication(Staff staff){
        //generate OTP
        String OTP = this.totpProvider.generateOneTimePassword();

        //Save OTP in staff object
        staff.setOTP(OTP);
        this.staffService.saveStaff(staff);

        //Send OTP to the user email
        this.emailServiceConfiguration.sendHtmlMessage(
            staff.getEmail(),
            "no-reply@epsi.fr",
            "Clinic double authentification avec mot de passe unique",
            "<h1>Mot de passe à usage unique</h1>" +
            "<h2>Veuillez copier et coller ce mot de passe à usage unique dans le formulaire</h2>" +
            "<h3>"+ OTP +"</h3>"
        );
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
        staff.setOTP(null);
        this.staffService.saveStaff(staff);
    }

    public void sendEmailToConfirmUserIdentity(Staff staff){
        //TODO implémenter la logique
    }

    public void updateStaffIpAddress(Staff staff, HttpServletRequest request){
        staff.setLastIpAddress(request.getRemoteAddr());
        this.staffService.saveStaff(staff);
    }
}
