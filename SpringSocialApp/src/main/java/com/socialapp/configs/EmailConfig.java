package com.socialapp.configs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587); // Nếu dùng SSL, đổi thành 465
        mailSender.setUsername("nguyenlethanhthang@gmail.com");  // Thay bằng email của bạn
        mailSender.setPassword("rote wmxz wern qnac");  // Thay bằng mật khẩu ứng dụng

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // Thêm nếu cần
        props.put("mail.debug", "true"); // Log chi tiết gửi email

        return mailSender;
    }
}
