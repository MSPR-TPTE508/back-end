package fr.epsi.clinic;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SpringBootApplication
@Controller
public class ClinicApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClinicApplication.class, args);
	}

	@GetMapping("/")
    public String index(
		Model model,
		@RequestParam(value = "femail", defaultValue = "") String email,
		@RequestParam(value = "fpassword", defaultValue = "") String password
		// HttpServletRequest request
	) {
		// String browserName = request.getHeader("user-agent");
		// String ipAddress = request.getHeader("host");

		// model.addAttribute("message", "Browser: " + browserName + ", IP: " + ipAddress);
		/*
		if (!email.isEmpty() && !password.isEmpty()) {
			// Interroger la BDD
			
			List<String> users = new ArrayList<>();
			
			if (users.contains(email) && users.contains(password)) {
				// User recognized
				String user = users.get(users.indexOf(email));
				
				if (user == browserName && user == ipAddress) {
					// Browser and Ip recognized

					// TODO: try connection to 
				} else {
					// Browser and Ip not recognized

					// TODO: send email

					model.addAttribute("message", "Un email de connexion a était envoyé");
				}
			} else {
				// User not recognized
				model.addAttribute("message", "Identifiants non-reconnus");
			}
			
			// redirect to index.jsp
			return "index";
		} else {
			model.addAttribute("message", "Veuillez saisir vos identifiants ci-dessus");

			// redirect to index.jsp
			return "index";
		}*/

		return "index";
    }

}
