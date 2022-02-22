package fr.epsi.clinic.model;

import java.util.Collection;
import java.util.Objects;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapUserDetails;

public class StaffLdapDetails implements LdapUserDetails{

    private static final long serialVersionUID = 1L;
    private Staff staff;
    private LdapUserDetails details;
    private String activeDirectoryEmail;
    
    public StaffLdapDetails(LdapUserDetails details, Staff staff) {
        this.details = details;
        this.staff = staff;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public LdapUserDetails getDetails() {
        return details;
    }

    public void setDetails(LdapUserDetails details) {
        this.details = details;
    }

    public String getActiveDirectoryEmail() {
        return activeDirectoryEmail;
    }

    public void setActiveDirectoryEmail(String activeDirectoryEmail) {
        if(Objects.isNull(activeDirectoryEmail)) throw new BadCredentialsException("User do not have an email in active directory");

        this.activeDirectoryEmail = activeDirectoryEmail;
    }

    public StaffLdapDetails(){

    }
    
    public boolean isEnabled() {
        return true;
    }
    
    public String getDn() {
        return details.getDn();
    }
    
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return details.getAuthorities();
    }
    
    public String getPassword() {
        return details.getPassword();
    }
    
    public String getUsername() {
        return details.getUsername();
    }
    
    public boolean isAccountNonExpired() {
        return details.isAccountNonExpired();
    }
    
    public boolean isAccountNonLocked() {
        return details.isAccountNonLocked();
    }
    
    public boolean isCredentialsNonExpired() {
        return details.isCredentialsNonExpired();
    }

    @Override
    public void eraseCredentials() {
        // TODO Auto-generated method stub
        
    }
 
}
