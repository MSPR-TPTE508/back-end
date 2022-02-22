package fr.epsi.clinic.mapper;

import java.util.Collection;

import org.apache.catalina.User;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import fr.epsi.clinic.model.Staff;
import fr.epsi.clinic.model.StaffLdapDetails;

public class StaffMapper implements UserDetailsContextMapper{

    private UserDetailsContextMapper ldapUserDetailsMapper = new LdapUserDetailsMapper();

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
            Collection<? extends GrantedAuthority> authorities) {

        Staff staff = new Staff();
        UserDetails details = this.ldapUserDetailsMapper.mapUserFromContext(ctx, username, authorities);
        
        StaffLdapDetails staffLdapDetails = new StaffLdapDetails((LdapUserDetails) details, staff);
        staffLdapDetails.setActiveDirectoryEmail(ctx.getStringAttribute("mail"));
        return staffLdapDetails;
    }

    @Override
    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
        // TODO Auto-generated method stub
        
    }
}
