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
            new EmailServiceConfiguration().sendSimpleMessage(
                "tristan.muller@epsi.fr",
                "no-reply@epsi.fr",
                "test",
                "Bonjour, ceci est un test");
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

    @Test
    void testSendHtmlMessageWithAttachment() {
        final File file = new File("test.txt");

        if (!file.exists()) {
            try(FileWriter writer = new FileWriter(file)) {
                writer.write("Ceci est un test");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        assertDoesNotThrow(() -> {
            new EmailServiceConfiguration().sendHtmlMessageWithAttachment(
                "tristan.muller@epsi.fr",
                "no-reply@epsi.fr",
                "test",
                "<h1>Email de test</h1>" +
                "<h2>Envoi d'un email au format html</h2>" +
                "<p>Ceci est un test comprenant deux titres, un paragraphe et un lien ci-dessous</p>" +
                "<a href=\"https://google.com/\">Google</a>",
                file);
        });
    }

    @Test
    void testSendHtmlMessage() {
        assertDoesNotThrow(() -> {
            new EmailServiceConfiguration().sendHtmlMessage(
                "tristan.muller@epsi.fr",
                "no-reply@epsi.fr",
                "test",
                "<h1>Email de test</h1>" +
                "<h2>Envoi d'un email au format html</h2>" +
                "<p>Ceci est un test comprenant deux titres, un paragraphe et un lien ci-dessous</p>" +
                "<a href=\"https://google.com/\">Google</a>"
            );
        });
    }
}
