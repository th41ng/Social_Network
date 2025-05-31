package com.socialapp.service;


public interface EmailService {
    void sendEmailtoLecturer(String to, String subject, String body, String defaultPassword);

    void sendEmailtoStudent(String to, String subject, String body);

    void sendVerifyEmail(String to, String subject, String body);
    
    void sendNotiEmailtoUser(String to, String subject, String body);
    
    void sendNotiEmailtoGroup(String to, String subject, String body);
}