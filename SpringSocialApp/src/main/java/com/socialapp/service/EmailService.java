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
   // Gửi email thông báo đã tạo tài khoản với mật khẩu mặc định
    void sendEmailtoLecturer(String to, String subject, String body, String defaultPassword);

    // Gửi email thông báo đã xác thực tài khoản
    void sendEmailtoStudent(String to, String subject, String body);
    
    // Gửi email mã xác thực
    void sendVerifyEmail(String to, String subject, String body);
    
    void sendNotiEmailtoUser(String to, String subject, String body);
    
    void sendNotiEmailtoGroup(String to, String subject, String body);
}
