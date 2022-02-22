package fr.epsi.clinic.configuration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.Test;

public class EmailServiceConfigurationTest {
    @Test
    void testSendMessageWithAttachment() {
        assertDoesNotThrow(() -> {
            new EmailServiceConfiguration().sendSimpleMessage("tristan.muller@epsi.fr", "no-reply@epsi.fr", "test", "test");
        });
    }

    @Test
    void testSendSimpleMessage() {
        final File file = new File("test.txt");

        if (!file.exists()) {
            try(FileWriter writer = new FileWriter(file)) {
                writer.write("Ceci est un test");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        assertDoesNotThrow(() -> {
            new EmailServiceConfiguration().sendMessageWithAttachment("tristan.muller@epsi.fr", "no-reply@epsi.fr", "test", "test", file);
        });
    }
}
