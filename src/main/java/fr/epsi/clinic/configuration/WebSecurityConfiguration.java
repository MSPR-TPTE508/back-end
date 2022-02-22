package fr.epsi.clinic.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import fr.epsi.clinic.provider.authentication.ClinicAuthenticationProvider;
import fr.epsi.clinic.service.ClinicAuthenticationService;
import fr.epsi.clinic.service.StaffService;

@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${ldap.url}")
    private String ldapUrl;

    @Value("${ldap.domain}")
    private String ldapDomain;

    @Autowired
    private StaffService staffService;

    @Autowired
    private ClinicAuthenticationService clinicAuthenticationService;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/login").permitAll()
            .antMatchers(HttpMethod.GET, "/").fullyAuthenticated()
            .antMatchers("/h2-console/**").permitAll()
            .antMatchers("/**").denyAll()
            .and()
            .csrf().ignoringAntMatchers("/h2-console/**")
            .and()
            .headers().frameOptions().sameOrigin()
            .and()
            .formLogin().loginPage("/login");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        ClinicAuthenticationProvider provider = new ClinicAuthenticationProvider(ldapDomain, ldapUrl, staffService, clinicAuthenticationService);

        auth.authenticationProvider(provider);
    }

}
