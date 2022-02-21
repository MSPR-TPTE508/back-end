package fr.epsi.clinic.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import fr.epsi.clinic.provider.authentication.ClinicAuthenticationProvider;

@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${ldap.url}")
    private String ldapUrl;

    @Value("${ldap.domain}")
    private String ldapDomain;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/login").permitAll()
            .antMatchers(HttpMethod.GET, "/").fullyAuthenticated()
            .antMatchers("/**").denyAll()
            .and()
        .formLogin().loginPage("/login");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        ClinicAuthenticationProvider provider = new ClinicAuthenticationProvider(ldapDomain, ldapUrl);

        auth.authenticationProvider(provider);
    }

}
