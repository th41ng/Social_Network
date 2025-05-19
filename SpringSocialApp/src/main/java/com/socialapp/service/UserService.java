/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.service;

import com.socialapp.pojo.User;
import jakarta.mail.Multipart;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author DELL G15
 */
public interface UserService extends UserDetailsService {

    List<User> getAllUsers(Map<String, String> params);

    // Lấy thông tin người dùng bằng tên đăng nhập
    User getUserByUsername(String username);

    // Lấy thông tin người dùng bằng email
    User getUserByEmail(String email);

    // Lấy thông tin người dùng bằng ID
    User getUserById(int id);

    // Thay đổi thông tin người dùng
    User updateUser(User user);

    // Xóa người dùng bằng ID
    void deleteUser(int id);

    User register(Map<String, String> params, MultipartFile avatar);

    boolean authenticate(String username, String password);

    void verifyStudent(int userId);

//    boolean verifyStudent(int userId);
    int countUsersRegisteredToday();

    User addUser(User user);
    
    void updatePassword(String email, String newPassword);
}
