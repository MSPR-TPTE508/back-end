package fr.epsi.clinic.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Staff {
    @Id
    private String email;

    private String browser;
    private String lastIpAddress;
    private Integer failedConnections;
    private String OTP;

    public String getOTP() {
        return OTP;
    }
    public void setOTP(String oTP) {
        OTP = oTP;
    }
    public Integer getFailedConnections() {
        return failedConnections;
    }
    public void setFailedConnections(Integer failedConnections) {
        this.failedConnections = failedConnections;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getBrowser() {
        return browser;
    }
    public void setBrowser(String browser) {
        this.browser = browser;
    }
    public String getLastIpAddress() {
        return lastIpAddress;
    }
    public void setLastIpAddress(String lastIpAddress) {
        this.lastIpAddress = lastIpAddress;
    }
   
}
