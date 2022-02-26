package fr.epsi.clinic.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.epsi.clinic.model.Staff;
import fr.epsi.clinic.model.VerificationInformationToken;
import fr.epsi.clinic.provider.totp.TotpProvider;
import fr.epsi.clinic.repository.VerificationInformationTokenRepository;
import net.bytebuddy.asm.Advice.Local;

@Service
public class VerificationInformationTokenService {
    
    @Autowired
    VerificationInformationTokenRepository verificationInformationTokenRepository;

    @Autowired
    StaffService staffService;

    private final TotpProvider totpProvider = new TotpProvider();

    public VerificationInformationToken saveVerificationInformationToken(VerificationInformationToken token){
        return this.verificationInformationTokenRepository.save(token);
    }

    /**
     * Create a verificationInformationToken fullfilled
     * @param request
     * @param staff
     * @return verificationInformationToken
     */
    public VerificationInformationToken createVerificationInformationToken(HttpServletRequest request, Staff staff){
        VerificationInformationToken token = new VerificationInformationToken();
        String newBrowser = request.getHeader("user-agent");
        LocalDate expirationDate = LocalDate.now().plusDays(1);

        token.setEmail(staff.getEmail());
        token.setNewBrowser(newBrowser);
        token.setSecret(totpProvider.generateOneTimePassword());
        token.setExpirationDate(expirationDate);
        

        return token;
    }

    public void deleteVerificationInformationToken(VerificationInformationToken token){
        this.verificationInformationTokenRepository.delete(token);
    }

    public Optional<VerificationInformationToken> getVerificationInformationToken(String email){
        return this.verificationInformationTokenRepository.findById(email);
    }

    public Optional<VerificationInformationToken> getVerificationInformationTokenBySecret(String secret){
        return this.verificationInformationTokenRepository.findBySecret(secret);
    }

    public boolean isOTPValid(String otp) {
        Optional<VerificationInformationToken> token = this.getVerificationInformationTokenBySecret(otp);
        LocalDate currentDate = LocalDate.now();

        if(token.isPresent()){
            //If the token has not expired
            if(token.get().getExpirationDate().isAfter(currentDate)){
                return true;
            }
        }

        return false;
    }

    /**
     * Update staff informations from token new informations
     * @param otp
     * @return 
     */
    public Staff updateStaffWithTokenInformation(String otp){
        Optional<VerificationInformationToken> token = this.getVerificationInformationTokenBySecret(otp);

        if(token.isPresent()){
            Optional<Staff> staff = this.staffService.findUserByEmail(token.get().getEmail());

            if(staff.isPresent()){
                staff.get().setBrowser(token.get().getNewBrowser());
                this.deleteVerificationInformationToken(token.get());
                return this.staffService.saveStaff(staff.get()).get();
            }
        }
        return null;
    }
}
