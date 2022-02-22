package fr.epsi.clinic.provider.totp;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class TotpProviderTest {
    @Test
    void testGenerateOneTimePassword() {
        // GIVEN a TotpProvider
        final TotpProvider provider = new TotpProvider();

        // WHEN it generate a one time password
        String password = provider.generateOneTimePassword();
        System.out.print(password);

        // THEN the password is not null
        assertNotNull(password);
    }
}
