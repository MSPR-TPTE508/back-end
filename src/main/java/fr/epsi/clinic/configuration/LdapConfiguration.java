package fr.epsi.clinic.configuration;


import java.util.List;

import javax.naming.Name;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.support.LdapNameBuilder;

import fr.epsi.clinic.mapper.StaffAttributesMapper;
import fr.epsi.clinic.model.Staff;

@Configuration
public class LdapConfiguration {

    @Bean
    public LdapContextSource ldapContextSource(){
        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl("ldap://88.163.249.230:16900/");
        ldapContextSource.setBase("dc=sposcar,dc=local");

        //To Change for security reasons
        ldapContextSource.setUserDn("CN=vagrant,CN=Users,DC=sposcar,DC=local");
        ldapContextSource.setPassword("vagrant");

        return ldapContextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate(){
        LdapTemplate ldapTemplate = new LdapTemplate(ldapContextSource());
        ldapTemplate.setIgnorePartialResultException(true);

        return ldapTemplate;
    }

}
