package fr.epsi.clinic.provider.authentication;

import java.util.ArrayList;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.stereotype.Component;

@Component
public class ClinicAuthenticationProvider implements AuthenticationProvider {
    private ActiveDirectoryLdapAuthenticationProvider ldapProvider;

    public ClinicAuthenticationProvider(String ldapDomain, String ldapUrl){
        this.ldapProvider = new ActiveDirectoryLdapAuthenticationProvider(ldapDomain, ldapUrl);
        this.ldapProvider.setConvertSubErrorCodesToExceptions(true);
    }

    public ClinicAuthenticationProvider(){

    }

    @Override
    public Authentication authenticate(Authentication authentication) 
      throws AuthenticationException {
        final boolean isUserAuthenticateToActiveDirectory = this.authenticateToActiveDirectory(authentication);
        
        if(!isUserAuthenticateToActiveDirectory){
            throw new BadCredentialsException("Wrong username or password");
        }

        return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),authentication.getCredentials(), authentication.getAuthorities());
    }

    private boolean authenticateToActiveDirectory(Authentication authentication){
        try {
            Authentication auth = this.ldapProvider.authenticate(authentication);
            return true;
        } catch (AuthenticationException e){
            return false;
        }
        
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}