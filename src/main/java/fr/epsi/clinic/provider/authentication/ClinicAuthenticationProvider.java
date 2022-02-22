package fr.epsi.clinic.provider.authentication;

import java.util.LinkedHashMap;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.stereotype.Component;

import fr.epsi.clinic.mapper.StaffMapper;
import fr.epsi.clinic.service.ClinicAuthenticationService;

@Component
public class ClinicAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private ClinicAuthenticationService clinicAuthenticationService;

    private ActiveDirectoryLdapAuthenticationProvider ldapProvider;
    private StaffMapper staffMapper = new StaffMapper();

    public ClinicAuthenticationProvider(String ldapDomain, String ldapUrl){
        this.ldapProvider = new ActiveDirectoryLdapAuthenticationProvider(ldapDomain, ldapUrl);
        this.ldapProvider.setConvertSubErrorCodesToExceptions(true);
        this.ldapProvider.setUserDetailsContextMapper(staffMapper);
    }

    public ClinicAuthenticationProvider(){

    }

    /**
     * Main authentication where business authentication is made
     */
    @Override
    public Authentication authenticate(Authentication authentication) 
      throws AuthenticationException {
        authentication = this.authenticateToActiveDirectory(authentication);

        if(Objects.isNull(authentication)){
            throw new BadCredentialsException("Wrong username or password");
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