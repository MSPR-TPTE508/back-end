package fr.epsi.clinic.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
 
        HttpServletRequest req = (HttpServletRequest) request;
     
        System.out.println(req.getRequestURI() + "=>" + req.getMethod());


        chain.doFilter(request, response);
    }

    // @GetMapping("/")
    // public String index(
	// 	Model model,
	// 	@RequestParam(value = "femail", defaultValue = "") String email,
	// 	@RequestParam(value = "fpassword", defaultValue = "") String password
	// 	// HttpServletRequest request
	// ) {
	// 	// String browserName = request.getHeader("user-agent");
	// 	// String ipAddress = request.getHeader("host");

	// 	// model.addAttribute("message", "Browser: " + browserName + ", IP: " + ipAddress);
	// 	/*
	// 	if (!email.isEmpty() && !password.isEmpty()) {
	// 		// Interroger la BDD
			
	// 		List<String> users = new ArrayList<>();
			
	// 		if (users.contains(email) && users.contains(password)) {
	// 			// User recognized
	// 			String user = users.get(users.indexOf(email));
				
	// 			if (user == browserName && user == ipAddress) {
	// 				// Browser and Ip recognized

	// 				// TODO: try connection to 
	// 			} else {
	// 				// Browser and Ip not recognized

	// 				// TODO: send email

	// 				model.addAttribute("message", "Un email de connexion a était envoyé");
	// 			}
	// 		} else {
	// 			// User not recognized
	// 			model.addAttribute("message", "Identifiants non-reconnus");
	// 		}
			
	// 		// redirect to index.jsp
	// 		return "index";
	// 	} else {
	// 		model.addAttribute("message", "Veuillez saisir vos identifiants ci-dessus");

	// 		// redirect to index.jsp
	// 		return "index";
	// 	}*/

	// 	return "index";
    // }
}
