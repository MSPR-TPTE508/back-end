package fr.epsi.clinic.provider.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.stereotype.Component;

import fr.epsi.clinic.mapper.StaffMapper;
import fr.epsi.clinic.model.Staff;
import fr.epsi.clinic.service.ClinicAuthenticationService;

@Component
public class ClinicAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private ClinicAuthenticationService clinicAuthenticationService;

    private ActiveDirectoryLdapAuthenticationProvider ldapProvider;

    public ClinicAuthenticationProvider(String ldapDomain, String ldapUrl){
        this.ldapProvider = new ActiveDirectoryLdapAuthenticationProvider(ldapDomain, ldapUrl);
        this.ldapProvider.setConvertSubErrorCodesToExceptions(true);
    }

    public ClinicAuthenticationProvider(){

    }

    /**
     * Main authentication where business authentication is made
     */
    @Override
    public Authentication authenticate(Authentication authentication) 
      throws AuthenticationException {
        final boolean isUserAuthenticateToActiveDirectory = this.authenticateToActiveDirectory(authentication);
        
        if(!isUserAuthenticateToActiveDirectory){
            throw new BadCredentialsException("Wrong username or password");
        }
        return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),authentication.getCredentials(), authentication.getAuthorities());
    }

    /**
     * Authenticate user to Active Directory
     * @param authentication
     * @return true if user is autenticated from Active Directory otherwise false
     */
    private boolean authenticateToActiveDirectory(Authentication authentication){
        try {
            this.ldapProvider.authenticate(authentication);
            return true;
        } catch (AuthenticationException e){
            return false;
        }
    }

    private Staff getStaffFromActiveDirectory(Authentication authentication){
        return null;
    }

    /**
     * Populate a Staff with active directory user's values
     * @param ctx
     * @return a populated Staff from the active directory user's values
     */
    private Staff populateStaffFromActiveDirectoryValues(DirContextOperations ctx){
        return StaffMapper.mapUserFromContext(ctx);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}