/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.service;

/**
 *
 * @author DELL G15
 */
public interface EmailService {
   
    void sendEmailtoLecturer(String to, String subject, String body, String defaultPassword);

   
    void sendEmailtoStudent(String to, String subject, String body);
    
    
    void sendVerifyEmail(String to, String subject, String body);
}
