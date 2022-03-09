package fr.epsi.clinic.configuration;

import static fr.epsi.clinic.configuration.ApplicationUserRole.AUTHENTICATED;

import java.net.http.HttpClient;

import javax.net.ssl.SSLContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import fr.epsi.clinic.handler.CustomAccessDeniedHandler;
import fr.epsi.clinic.handler.LoginSuccessHandler;
import fr.epsi.clinic.provider.authentication.ClinicAuthenticationProvider;
import fr.epsi.clinic.service.ClinicAuthenticationService;
import fr.epsi.clinic.service.StaffService;
import fr.epsi.clinic.service.VerificationInformationTokenService;

@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${application.url}")
    private String applicationUrl;

    @Value("${ldap.url}")
    private String ldapUrl;

    @Value("${ldap.domain}")
    private String ldapDomain;

    @Autowired
    private StaffService staffService;

    @Autowired
    private ClinicAuthenticationService clinicAuthenticationService;

    @Autowired
    private VerificationInformationTokenService verificationInformationTokenService;

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requiresChannel(channel -> channel.anyRequest().requiresSecure());

        http.exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler());

        http
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/login").permitAll()
                .antMatchers(HttpMethod.GET, "/validation-identity").permitAll()
                .antMatchers(HttpMethod.GET, "/").hasRole(AUTHENTICATED.name())
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/css/*").permitAll()
                .antMatchers("/**").denyAll()
                .and()
                .csrf().ignoringAntMatchers("/h2-console/**")
                .and()
                .headers().frameOptions().sameOrigin()
                .and()
                .formLogin()
                .loginPage("/login")
                .successHandler(loginSuccessHandler)
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/logout.done").deleteCookies("JSESSIONID")
                .invalidateHttpSession(true);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        ClinicAuthenticationProvider provider = new ClinicAuthenticationProvider(applicationUrl, ldapDomain, ldapUrl,
                staffService,
                clinicAuthenticationService, verificationInformationTokenService);

        auth.authenticationProvider(provider);
    }

}
