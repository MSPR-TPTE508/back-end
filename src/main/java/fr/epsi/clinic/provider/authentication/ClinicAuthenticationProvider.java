package fr.epsi.clinic.provider.authentication;

import static fr.epsi.clinic.configuration.ApplicationUserRole.AUTHENTICATED;
import static fr.epsi.clinic.configuration.ApplicationUserRole.PRE_AUTHENTICATED;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import fr.epsi.clinic.configuration.EmailServiceConfiguration;
import fr.epsi.clinic.mapper.StaffMapper;
import fr.epsi.clinic.model.Staff;
import fr.epsi.clinic.model.StaffLdapDetails;
import fr.epsi.clinic.model.VerificationInformationToken;
import fr.epsi.clinic.provider.totp.TotpProvider;
import fr.epsi.clinic.service.ClinicAuthenticationService;
import fr.epsi.clinic.service.StaffService;
import fr.epsi.clinic.service.VerificationInformationTokenService;

@Component
public class ClinicAuthenticationProvider implements AuthenticationProvider {

    private String applicationUrl;

    private ClinicAuthenticationService clinicAuthenticationService;
    private StaffService staffService;
    private VerificationInformationTokenService verificationInformationTokenService;

    private ActiveDirectoryLdapAuthenticationProvider ldapProvider;
    private final StaffMapper staffMapper = new StaffMapper();
    private final TotpProvider provider = new TotpProvider();
    private final EmailServiceConfiguration emailServiceConfiguration = new EmailServiceConfiguration();

    @Autowired
    LdapTemplate ldapTemplate;

    public ClinicAuthenticationProvider(String applicationUrl, String ldapDomain, String ldapUrl, StaffService staffService,
            ClinicAuthenticationService clinicAuthenticationService, VerificationInformationTokenService verificationInformationTokenService) {
        this.ldapProvider = new ActiveDirectoryLdapAuthenticationProvider(ldapDomain, ldapUrl);
        this.ldapProvider.setConvertSubErrorCodesToExceptions(true);
        this.ldapProvider.setUserDetailsContextMapper(staffMapper);

        this.applicationUrl = applicationUrl;
        this.staffService = staffService;
        this.clinicAuthenticationService = clinicAuthenticationService;
        this.verificationInformationTokenService = verificationInformationTokenService;
    }

    public ClinicAuthenticationProvider() {

    }

    /**
     * Main authentication where business authentication is made
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        HttpServletRequest request = getCurrentRequest(RequestContextHolder.getRequestAttributes());
        Authentication dynamicAuthentication = retrieveAuthenticationDependingOnStaffStep(SecurityContextHolder.getContext().getAuthentication(), authentication);
        
        StaffLdapDetails staffLdapDetails = null;
        String username = authentication.getPrincipal().toString();

        // Create specific userDetails to ensure that the user has been authenticated by
        // the Active directory
        if (Objects.nonNull(dynamicAuthentication)) {
            staffLdapDetails = (StaffLdapDetails) dynamicAuthentication.getPrincipal();
            username = staffLdapDetails.getUsername();
        } 

        // Get staff informations from active directory
        Staff activeDirectoryStaff = this.staffService.findStaffInActiveDirectory(username);

        // Use active directory staff informations to get the user from our DB even if he's not authenticated to be able to check the number of failed connections
        Optional<Staff> optionalStaff = staffService.findUserByEmail(activeDirectoryStaff.getEmail());

        if (optionalStaff.isPresent()) {
            // Check if antibruteforce is enabled for the staff
            boolean isUserAntiBruteForceDisabled = this.clinicAuthenticationService.isUserAntiBruteForceDisabled(optionalStaff.get());

            if (!isUserAntiBruteForceDisabled) {
                throw new BadCredentialsException(
                        "Account locked, please try again in few minutes");
            }
        }

        // Add a Staff if the staff he's not already in our Database whatever he is authenticated or not.
        if (optionalStaff.isEmpty() && Objects.nonNull(activeDirectoryStaff)) {
            optionalStaff = this.clinicAuthenticationService.saveStaff(request, activeDirectoryStaff);
        }

        // If active directory cannot authenticate the user
        if (Objects.isNull(staffLdapDetails)) {
            if (optionalStaff.isPresent()) {
                this.clinicAuthenticationService.incrementStaffFailedConnection(optionalStaff.get());
            }

            throw new BadCredentialsException("Wrong username or password");
        }

        // Check for suspicious connection
        this.connectionChecker(request, optionalStaff.get());

        //If user is anonymous
        if (!this.isStaffPreAuthenticated(dynamicAuthentication)) {
            this.clinicAuthenticationService.doubleAuthentication(optionalStaff.get());
        } 
        
        //If the user is performing a second step authentication
        if(this.isStaffPreAuthenticated(dynamicAuthentication)){
            return this.authenticateUserWithOTP(dynamicAuthentication, request, optionalStaff.get(), staffLdapDetails);
        }
        
        return this.createSuccessFullAuthenticationFirstStep(dynamicAuthentication, staffLdapDetails, optionalStaff.get());
    }

    /**
     * Check user's authentication status from authorities
     * @param authentication
     * @return true if the user is pre authenticated, else false
     */
    public boolean isStaffPreAuthenticated(Authentication authentication){
        return authentication.getAuthorities().containsAll(PRE_AUTHENTICATED.getGrantedAuthorities());
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

    /**
     * Authenticate user from its second step authentication
     * @param authentication
     * @param request
     * @param staff
     * @param staffLdapDetails
     * @return Successfull Authentication instance only if its OTP is equal to the one in our DB, else throw a BadCredentialsException
     */
    private Authentication authenticateUserWithOTP(Authentication authentication, HttpServletRequest request, Staff staff, StaffLdapDetails staffLdapDetails){
        String givenOTP = request.getParameter("otp");
        boolean isOTPValid = false;

        try {
            isOTPValid = this.clinicAuthenticationService.verifyGivenOTP(givenOTP, staff);
        } catch(Exception e){
            throw new BadCredentialsException("Mauvais mot de passe à usage unique, reconnectez vous avec votre identifiant / Mot de passe");
        }

        this.clinicAuthenticationService.deleteUserOTP(staff);

        if (isOTPValid) {
            this.clinicAuthenticationService.resetStaffFailedConnections(staff);
            return this.createSuccessFullAuthenticationSecondStep(authentication, staffLdapDetails, staff);
            
        } else {
            throw new BadCredentialsException("Mauvais mot de passe à usage unique, reconnectez vous avec votre identifiant / Mot de passe");
        }
    }

    private boolean connectionChecker(HttpServletRequest request, Staff staff) {

        if (!this.clinicAuthenticationService.isUserBrowserIsUsual(request, staff)) {
            VerificationInformationToken token = this.verificationInformationTokenService.createVerificationInformationToken(request, staff);
            this.verificationInformationTokenService.saveVerificationInformationToken(token);
            this.sendIdentityValidationEmail(token.getSecret(), staff);
            
            throw new BadCredentialsException("Une anomalie a été detectée à votre connexion, un email vous a été envoyé pour confirmer votre identité");
        }

        if(this.clinicAuthenticationService.isUserIpAddressIsFromDomain(request)){
            return false;
        }
        
        if(this.clinicAuthenticationService.isUserIpAddressConform(request)){
            if (!this.clinicAuthenticationService.isUserIpIsUsual(request, staff)) {
                this.sendAlertEmail(staff);
                this.clinicAuthenticationService.updateStaffIpAddress(staff, request);
            }

            return false;
        } else {
            this.clinicAuthenticationService.lockStaffAccount(staff);
        }


        return true;
    }

    private void sendAlertEmail(Staff staff){

        emailServiceConfiguration.sendHtmlMessage(
                staff.getEmail(),
                "no-reply@epsi.fr",
                "Email de connection",
                "<p>Une nouvelle tentative de connexion a été détectée recemment à partir d'un source inconnue.</p>" +
                "<p>Si vous êtes à l'origine de cette connexion, vous pouvez ignorer ce méssage.</p>" +
                "<p>Sinon, il est vivement conseillé d'effectuer un changement de mot de passe rapidement.</p>" +
                "<p>Pour protéger votre compte, il est conseillé de ne jamais divulger vos identifiants et changer de mot de passe régulièrement.</p>"
        );

        // TODO: once he clicked on "yes", update our database with its new informations
    }

    private void sendIdentityValidationEmail(String otp, Staff staff){

        // Send an email that will ask the user to confirm its identity
        emailServiceConfiguration.sendHtmlMessage(
                staff.getEmail(),
                "no-reply@epsi.fr",
                "Email de verification d'identité",
                "<h1>Email de verification d'identité</h1>" +
                        "<h2>Connection à partir d'une nouvelle configuration</h2>" +
                        "<p>Une connexion avec une nouvelle configuration a été detecté, merci de confirmer votre identité en cliquant sur le lien ci-dessous</p>"
                        +"<em>Le compte est bloqué momentanément, en cliquant sur le lien ci-dessous il se débloquera immédiatement</em>"
                        +
                        // TODO: Rendre l'adresse persistente et affiner la méthode d'envoie du OTP
                        "<a href=\""+ this.applicationUrl +"/validation-identity?otp=" + otp
                        + "\">Confirmer mon identité</a>");

        // TODO: once he clicked on "yes", update our database with its new informations
    }

    /**
     * Retrieve authentication based on the staff authentication step
     * @param previousAuthentication
     * @param currentAuthentication
     * @return successfull authentication from active directory if the user hasn't previously been authenticated, else return previous authentication object
     */
    private Authentication retrieveAuthenticationDependingOnStaffStep(Authentication previousAuthentication, Authentication currentAuthentication){
        if(Objects.nonNull(previousAuthentication)){
            return previousAuthentication;
        }
        
        return this.authenticateToActiveDirectory(currentAuthentication);
    }

    /**
     * Retrieve current request from RequestAttributes Object
     * @param requestAttributes
     * @return current request from RequestAttributes Object
     */
    private HttpServletRequest getCurrentRequest(RequestAttributes requestAttributes){

        if (Objects.isNull(requestAttributes)) {
            throw new NullPointerException("Attributes cannot be found");
        }
        return ((ServletRequestAttributes) requestAttributes).getRequest();
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

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}