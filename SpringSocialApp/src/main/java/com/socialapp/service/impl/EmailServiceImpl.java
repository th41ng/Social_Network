package com.socialapp.service.impl;

import com.socialapp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender; // Inject Bean JavaMailSender

    private final String fromEmail = "nguyenlethanhthang@gmail.com";  // Địa chỉ email người gửi

    @Override
    public void sendEmailtoLecturer(String to, String subject, String body, String defaultPassword) {
        String fullBody = body + "\n\nMật khẩu mặc định của bạn là: " + defaultPassword;
        sendEmail(to, subject, fullBody);
    }

    @Override
    public void sendEmailtoStudent(String to, String subject, String body) {
        sendEmail(to, subject, body);
    }

     @Override
    public void sendVerifyEmail(String to, String subject, String body) {
       sendEmail(to, subject, body);
    }
    
    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            System.out.println("Email sent successfully to: " + to);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public void sendNotiEmailtoUser(String to, String subject, String body) {
      sendEmail(to, subject, body);
    }

    @Override
    public void sendNotiEmailtoGroup(String to, String subject, String body) {
       sendEmail(to, subject, body);
    }

   
}
