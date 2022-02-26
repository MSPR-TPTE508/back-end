package fr.epsi.clinic.model;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class VerificationInformationToken {

    @Id
    private String email;

    private String secret;
    private LocalDate expirationDate;
    private String newBrowser;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getNewBrowser() {
        return newBrowser;
    }

    public void setNewBrowser(String newBrowser) {
        this.newBrowser = newBrowser;
    }

}
