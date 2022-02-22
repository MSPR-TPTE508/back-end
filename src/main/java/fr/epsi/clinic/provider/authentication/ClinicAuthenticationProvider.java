package fr.epsi.clinic.provider.authentication;

import java.util.Enumeration;
import java.util.Objects;
import java.util.Optional;

import javax.mail.AuthenticationFailedException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import fr.epsi.clinic.mapper.StaffMapper;
import fr.epsi.clinic.model.Staff;
import fr.epsi.clinic.model.StaffLdapDetails;
import fr.epsi.clinic.repository.StaffRepository;
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
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
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
        }
        
        return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),authentication.getCredentials(), authentication.getAuthorities());
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

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}