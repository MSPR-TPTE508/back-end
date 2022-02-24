package fr.epsi.clinic.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import static fr.epsi.clinic.configuration.ApplicationUserRole.*;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
 
     
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        if(authentication.getAuthorities().containsAll(PRE_AUTHENTICATED.getGrantedAuthorities())){
            response.sendRedirect("/login");
        }
         
        super.onAuthenticationSuccess(request, response, authentication);
    }
 
}
