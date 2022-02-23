package fr.epsi.clinic.configuration;

import java.io.File;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * SMTP Email sender
 * @see sendSimpleMessage()
 * @see sendMessageWithAttachment()
 */
@Configuration
public class EmailServiceConfiguration {

    @Value("${spring.mail.host}")
    private String host = "smtp.gmail.com";
    
    @Value("${spring.mail.port}")
    private int port = 587;

    @Value("${spring.mail.username}")
    private String username = "epsi.mspr.508@gmail.com";

    @Value("${spring.mail.password}")
    private String password = "canxawluuulpfita";

    @Value("${spring.mail.transport.protocol}")
    private String transportProtocol = "smtp";

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String smtpAuth = "true";

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String smtSstarttls = "true";

    @Value("${spring.mail.properties.debug}")
    private String debug = "true";

    private JavaMailSenderImpl mailSender;

    /**
     * Constructor
     */
    public EmailServiceConfiguration() {
        this.mailSender = new JavaMailSenderImpl();
        this.mailSender.setHost(host);
        this.mailSender.setPort(port);

        this.mailSender.setUsername(username);
        this.mailSender.setPassword(password);

        final Properties props = this.mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", transportProtocol);
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.starttls.enable", smtSstarttls);
        props.put("mail.debug", debug);
    }

    /**
     * Send a simple mail
     * @param to email
     * @param from email
     * @param subject
     * @param text content
     */
    public void sendSimpleMessage(String to, String from, String subject, String text) {
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        this.mailSender.send(message);
    }

    /**
     * Send an email with html text
     * @param to email
     * @param subject
     * @param from email
     * @param htmlText content
     */
    public void sendHtmlMessage(String to, String from, String subject, String htmlText) {
        MimeMessage message = this.mailSender.createMimeMessage();

        try {
            final MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlText, true);
            this.mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send an email with a file
     * @param to email
     * @param subject
     * @param from email
     * @param text content
     * @param attachmentFile file
     */
    public void sendMessageWithAttachment(String to, String from, String subject, String text, File attachmentFile) {
        MimeMessage message = this.mailSender.createMimeMessage();

        try {
            final MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
    
            final FileSystemResource file = new FileSystemResource(attachmentFile);
            helper.addAttachment(file.getFilename(), file);
    
            this.mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send an email with a file
     * @param to email
     * @param subject
     * @param from email
     * @param htmlText content
     * @param attachmentFile file
     */
    public void sendHtmlMessageWithAttachment(String to, String from, String subject, String htmlText, File attachmentFile) {
        MimeMessage message = this.mailSender.createMimeMessage();

        try {
            final MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlText, true);
    
            final FileSystemResource file = new FileSystemResource(attachmentFile);
            helper.addAttachment(file.getFilename(), file);
    
            this.mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
