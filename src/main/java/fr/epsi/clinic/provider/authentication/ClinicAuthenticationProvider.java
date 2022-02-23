package fr.epsi.clinic.provider.authentication;

import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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

    public ClinicAuthenticationProvider(String ldapDomain, String ldapUrl, StaffService staffService, ClinicAuthenticationService clinicAuthenticationService){
        this.ldapProvider = new ActiveDirectoryLdapAuthenticationProvider(ldapDomain, ldapUrl);
        this.ldapProvider.setConvertSubErrorCodesToExceptions(true);
        this.ldapProvider.setUserDetailsContextMapper(staffMapper);

        this.staffService = staffService;
        this.clinicAuthenticationService = clinicAuthenticationService;
    }

    public ClinicAuthenticationProvider(){

    }

    /**
     * Main authentication where business authentication is made
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(attributes)) {
            throw new NullPointerException("Attributes cannot be found");
        }

        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        authentication = this.authenticateToActiveDirectory(authentication);

        if(Objects.isNull(authentication)){
            throw new BadCredentialsException("Wrong username or password");
        }

        StaffLdapDetails staffLdapDetails = (StaffLdapDetails)authentication.getPrincipal();

        if(Objects.isNull(staffLdapDetails)){
            throw new BadCredentialsException("Unexpected error during authentication, user principal is null");
        }

        Optional<Staff> optionalStaff = staffService.findUserByEmail(staffLdapDetails.getActiveDirectoryEmail());

        //Add a Staff if the he's not already in our Database
        if(optionalStaff.isEmpty()){
            this.clinicAuthenticationService.addUser(request, staffLdapDetails);

            //return successfull Authentication
            return authentication;
        }

        //Check if antibruteforce is enabled for the staff
        boolean isUserAntiBruteForceDisabled = this.clinicAuthenticationService.isUserAntiBruteForceDisabled(optionalStaff.get());

        if(!isUserAntiBruteForceDisabled){
            //TODO show to the user that the account is locked and the remaining time to be unlocked
        }

        //Check for suspicious connection
        boolean isSuspiciousConnection = this.isSuspiciousConnection(request, optionalStaff.get());

        if(isSuspiciousConnection){
            System.out.println("UNUSUAL CONNECTION !!");
            
            // Send an email that will ask the user to confirm its identity
            final TotpProvider provider = new TotpProvider();

            final EmailServiceConfiguration emailServiceConfiguration = new EmailServiceConfiguration();
            emailServiceConfiguration.sendHtmlMessage(
                optionalStaff.get().getEmail(),
                "Email de connection",
                "no-reply@epsi.fr",
                "<h1>E-mail de connexion</h1>" +
                "<h2>Connection à partir d'une nouvelle configuration</h2>" +
                "<p>Vous venez de vous connecter à partir d'une nouvelle configuration, veuillez saisir le code ci-dessous pour vous connecter:</p>" +
                // TODO: Rendre l'adresse persistente et affiner la méthode d'envoie du OTP
                "<a href=\"http://localhost:8080?totp=" + provider.generateOneTimePassword() + "\">Se connecter</a>"
            );

            // TODO: once he clicked on "yes", update our database with its new informations
        }

        //Return successfull authentication
        return authentication;
    }

    /**
     * Authenticate user to Active Directory
     * @param authentication
     * @return true if user is autenticated from Active Directory otherwise false
     */
    private Authentication authenticateToActiveDirectory(Authentication authentication){
        try {
            return this.ldapProvider.authenticate(authentication);
        } catch (AuthenticationException e){
            return null;
        }
    }

    private boolean isSuspiciousConnection(HttpServletRequest request, Staff staff){

        if(this.clinicAuthenticationService.isUserBrowserIsUsual(request, staff)){
            return false;
        }

        if(this.clinicAuthenticationService.isUsuerIpIsUsual(request, staff)){
            return false;
        }
        
        return true;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}