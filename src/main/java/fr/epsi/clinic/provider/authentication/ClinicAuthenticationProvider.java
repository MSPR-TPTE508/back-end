package fr.epsi.clinic.provider.authentication;

import static fr.epsi.clinic.configuration.ApplicationUserRole.AUTHENTICATED;
import static fr.epsi.clinic.configuration.ApplicationUserRole.PRE_AUTHENTICATED;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import fr.epsi.clinic.configuration.EmailServiceConfiguration;
import fr.epsi.clinic.mapper.StaffMapper;
import fr.epsi.clinic.model.Staff;
import fr.epsi.clinic.model.StaffLdapDetails;
import fr.epsi.clinic.provider.totp.TotpProvider;
import fr.epsi.clinic.service.ClinicAuthenticationService;
import fr.epsi.clinic.service.StaffService;

@Component
public class ClinicAuthenticationProvider implements AuthenticationProvider {

    private ClinicAuthenticationService clinicAuthenticationService;
    private StaffService staffService;

    private ActiveDirectoryLdapAuthenticationProvider ldapProvider;
    private StaffMapper staffMapper = new StaffMapper();

    @Autowired
    LdapTemplate ldapTemplate;

    public ClinicAuthenticationProvider(String ldapDomain, String ldapUrl, StaffService staffService,
            ClinicAuthenticationService clinicAuthenticationService) {
        this.ldapProvider = new ActiveDirectoryLdapAuthenticationProvider(ldapDomain, ldapUrl);
        this.ldapProvider.setConvertSubErrorCodesToExceptions(true);
        this.ldapProvider.setUserDetailsContextMapper(staffMapper);

        this.staffService = staffService;
        this.clinicAuthenticationService = clinicAuthenticationService;
    }

    public ClinicAuthenticationProvider() {

    }

    /**
     * Main authentication where business authentication is made
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();

        //Authentication from first step
        Authentication firsAuthentication = SecurityContextHolder.getContext().getAuthentication();

        if(Objects.nonNull(firsAuthentication)){
            authentication = firsAuthentication;
            
        } else {
            authentication = this.authenticateToActiveDirectory(authentication);
        }

        if (Objects.isNull(attributes)) {
            throw new NullPointerException("Attributes cannot be found");
        }
        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();

        UserDetails userDetails = (UserDetails)authentication.getPrincipal();
        String username = userDetails.getUsername();

        Optional<Staff> optionalStaff = null;
        StaffLdapDetails staffLdapDetails = null;

        

        // Get staff informations from active directory
        Staff activeDirectoryStaff = this.staffService.findStaffInActiveDirectory(username);

        optionalStaff = staffService.findUserByEmail(activeDirectoryStaff.getEmail());

        // Create specific userDetails to ensure that the user has been authenticated by
        // the Active directory
        if (Objects.nonNull(authentication)) {
            staffLdapDetails = (StaffLdapDetails) authentication.getPrincipal();
        }

        if (optionalStaff.isPresent()) {
            // Check if antibruteforce is enabled for the staff
            boolean isUserAntiBruteForceDisabled = this.clinicAuthenticationService
                    .isUserAntiBruteForceDisabled(optionalStaff.get());

            if (!isUserAntiBruteForceDisabled) {
                throw new BadCredentialsException(
                        "Account locked due to a number of failed connections, please try again in few minutes");
            }
        }

        // If active directory cannot authenticate the user
        if (Objects.isNull(staffLdapDetails)) {
            if (optionalStaff.isPresent()) {
                this.clinicAuthenticationService.incrementStaffFailedConnection(optionalStaff.get());
            }

            throw new BadCredentialsException("Wrong username or password");
        }

        // Add a Staff if the he's not already in our Database
        if (optionalStaff.isEmpty()) {
            optionalStaff = this.clinicAuthenticationService.addUser(request, staffLdapDetails);
        }

        // Check for suspicious connection
        boolean isSuspiciousConnection = this.isSuspiciousConnection(request, optionalStaff.get());

        if (isSuspiciousConnection) {
            System.out.println("UNUSUAL CONNECTION !!");

            // Send an email that will ask the user to confirm its identity
            final TotpProvider provider = new TotpProvider();

            final EmailServiceConfiguration emailServiceConfiguration = new EmailServiceConfiguration();
            emailServiceConfiguration.sendHtmlMessage(
                    optionalStaff.get().getEmail(),
                    "no-reply@epsi.fr",
                    "Email de connection",
                    "<h1>E-mail de connexion</h1>" +
                            "<h2>Connection à partir d'une nouvelle configuration</h2>" +
                            "<p>Vous venez de vous connecter à partir d'une nouvelle configuration, veuillez saisir le code ci-dessous pour vous connecter:</p>"
                            +
                            // TODO: Rendre l'adresse persistente et affiner la méthode d'envoie du OTP
                            "<a href=\"http://localhost:8080?totp=" + provider.generateOneTimePassword()
                            + "\">Se connecter</a>");

            // TODO: once he clicked on "yes", update our database with its new informations
        }

        //If user is anonymous
        if (!authentication.getAuthorities().containsAll(PRE_AUTHENTICATED.getGrantedAuthorities())) {
            this.clinicAuthenticationService.doubleAuthentication(optionalStaff.get());
        } 
        
        //If the user is performing a second step authentication
        if(authentication.getAuthorities().containsAll(PRE_AUTHENTICATED.getGrantedAuthorities())){
            String givenOTP = request.getParameter("otp");
            boolean isOTPValid = false;

            try {
                isOTPValid = this.clinicAuthenticationService.verifyGivenOTP(givenOTP, optionalStaff.get());
            } catch(Exception e){
                throw new BadCredentialsException("Mauvais mot de passe à usage unique, reconnectez vous avec votre identifiant / Mot de passe");
            }

            this.clinicAuthenticationService.deleteUserOTP(optionalStaff.get());

            if (isOTPValid) {
                return this.createSuccessFullAuthenticationSecondStep(authentication, staffLdapDetails, optionalStaff.get());
                
            } else {
                throw new BadCredentialsException("Mauvais mot de passe à usage unique, reconnectez vous avec votre identifiant / Mot de passe");
            }

            
        }
        

        return this.createSuccessFullAuthenticationFirstStep(authentication, staffLdapDetails, optionalStaff.get());
    }

    /**
     * Authenticate user to Active Directory
     * 
     * @param authentication
     * @return true if user is autenticated from Active Directory otherwise false
     */
    private Authentication authenticateToActiveDirectory(Authentication authentication) {
        try {
            return this.ldapProvider.authenticate(authentication);
        } catch (AuthenticationException e) {
            return null;
        }
    }

    private boolean isSuspiciousConnection(HttpServletRequest request, Staff staff) {

        if (this.clinicAuthenticationService.isUserBrowserIsUsual(request, staff)) {
            return false;
        }

        if (this.clinicAuthenticationService.isUsuerIpIsUsual(request, staff)) {
            return false;
        }

        return true;
    }

    /**
     * Create successful authentication first step (user and password)
     * 
     * @param authentication
     * @param staffLdapDetails
     * @return
     */
    private Authentication createSuccessFullAuthenticationFirstStep(Authentication authentication, StaffLdapDetails staffLdapDetails, Staff staff) {
        staffLdapDetails.setStaff(staff);

        UsernamePasswordAuthenticationToken customToken = new UsernamePasswordAuthenticationToken(staffLdapDetails,
                authentication.getCredentials(), PRE_AUTHENTICATED.getGrantedAuthorities());
        customToken.setDetails(customToken.getDetails());

        return customToken;
    }

    /**
     * Create successful authentication for the second step (username, password and
     * OTP)
     * 
     * @param authentication
     * @param staffLdapDetails
     * @return
     */
    private Authentication createSuccessFullAuthenticationSecondStep(Authentication authentication, StaffLdapDetails staffLdapDetails, Staff staff) {
        staffLdapDetails.setStaff(staff);
                
        UsernamePasswordAuthenticationToken customToken = new UsernamePasswordAuthenticationToken(staffLdapDetails,
                authentication.getCredentials(), AUTHENTICATED.getGrantedAuthorities());
        customToken.setDetails(customToken.getDetails());

        return customToken;
    }

    
    /**
     * Create failed authentication
     * 
     * @param authentication
     * @param staffLdapDetails
     * @return
     */
    private Authentication createFailedAuthentication(Authentication authentication, StaffLdapDetails staffLdapDetails, Staff staff) {
        staffLdapDetails.setStaff(staff);

        UsernamePasswordAuthenticationToken customToken = new UsernamePasswordAuthenticationToken(staffLdapDetails,
                authentication.getCredentials(), new HashSet<SimpleGrantedAuthority>());
        customToken.setDetails(customToken.getDetails());
        customToken.setAuthenticated(false);

        return customToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}