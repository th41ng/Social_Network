/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.service.impl;

import com.socialapp.service.EmailService;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author DELL G15
 */
@Service
public class EmailServiceImpl implements EmailService{
     private final String fromEmail = "your-email@example.com";  // Địa chỉ email người gửi
    private final String emailPassword = "your-email-password"; // Mật khẩu email người gửi

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            Properties properties = new Properties();
            properties.put("mail.smtp.host", "smtp.example.com"); // Địa chỉ SMTP server
            properties.put("mail.smtp.port", "587"); // Cổng SMTP
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, emailPassword);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            Logger.getLogger(EmailServiceImpl.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public void sendVerificationEmail(String to, String verificationLink) {
        String subject = "Please Verify Your Email Address";
        String body = "Click the following link to verify your email address: " + verificationLink;
        sendEmail(to, subject, body);
    }

    @Override
    public void sendEventNotification(String to, String eventDetails) {
        String subject = "Event Notification";
        String body = "You have an upcoming event: " + eventDetails;
        sendEmail(to, subject, body);
    }
}
