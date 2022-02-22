package fr.epsi.clinic.provider.totp;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

public class TotpProvider {
    private TimeBasedOneTimePasswordGenerator totp;

    public TotpProvider() {
        try {
            this.totp = new TimeBasedOneTimePasswordGenerator();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return Password || null
     */
    public String generateOneTimePassword()
    {
        String password = null;

        try {
            final KeyGenerator keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());

            // Key length should match the length of the HMAC output (160 bits for SHA-1, 256 bits
            // for SHA-256, and 512 bits for SHA-512). Note that while Mac#getMacLength() returns a
            // length in _bytes,_ KeyGenerator#init(int) takes a key length in _bits._
            final int macLengthInBytes = Mac.getInstance(totp.getAlgorithm()).getMacLength();
            keyGenerator.init(macLengthInBytes * 8);

            final Key key = keyGenerator.generateKey();
            final Instant now = Instant.now();

            password = totp.generateOneTimePasswordString(key, now);
        } catch(Exception  ex) {
            ex.printStackTrace();
        }

        return password;
    }
}
