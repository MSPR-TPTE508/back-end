package fr.epsi.clinic.configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.epsi.clinic.configuration.ApplicationUserPermission.*;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum ApplicationUserRole {
    PRE_AUTHENTICATED(new HashSet<ApplicationUserPermission>()),
    AUTHENTICATED(new HashSet<ApplicationUserPermission>(Arrays.asList(USER_WRITE, USER_READ)));

    private Set<ApplicationUserPermission> permissions;

    ApplicationUserRole(Set<ApplicationUserPermission> permissions){
        this.permissions = permissions;
    }

    public Set<ApplicationUserPermission> getPermissions() {
        return permissions;
    }

    /**
     * Generate granted authorities from role and permissions
     * 
     * @See SimpleGrantedAuthority
     * @return a list of simpleGranthedAuthority object
     */
    public Set<SimpleGrantedAuthority> getGrantedAuthorities(){
        Set<SimpleGrantedAuthority> grantedAuthorities = permissions
            .stream()
            .map( permission -> new SimpleGrantedAuthority(permission.getPermission()))
            .collect(Collectors.toSet());
        
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_"+this.name()));
        System.out.println(grantedAuthorities.toString());
        return grantedAuthorities;
    }


}
