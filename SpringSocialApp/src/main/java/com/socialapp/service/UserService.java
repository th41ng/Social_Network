/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.service;

import com.socialapp.pojo.User;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author DELL G15
 */
public interface UserService extends UserDetailsService {

    Page<User> getAllUsersWithPagination(Map<String, String> params, Pageable pageable);

    List<User> getAllUsers(Map<String, String> params);

    User getUserByUsername(String username);

    User getUserByEmail(String email);

    User getUserById(int id);

    User updateUser(User user);

    boolean deleteUser(int id);

    User register(Map<String, String> params, MultipartFile avatar, MultipartFile coverImage);

    boolean authenticate(String username, String password);

    void verifyStudent(int userId);

    int countUsersRegisteredToday();

    long countUsers();

    User addUser(User user);

    void banUser(int userId);

    void updatePassword(String email, String newPassword);

    List<User> getAvailableUsersForGroup(int groupId);
}
