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
     // Gửi email đến người dùng
    void sendEmail(String to, String subject, String body);
    
    // Gửi email xác thực tài khoản (ví dụ: để kích hoạt tài khoản)
    void sendVerificationEmail(String to, String verificationLink);
    
    // Gửi email thông báo sự kiện
    void sendEventNotification(String to, String eventDetails);
}
