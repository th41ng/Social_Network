/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.service;

import com.socialapp.pojo.User;

/**
 *
 * @author DELL G15
 */
public interface UserService {

    // Lấy thông tin người dùng bằng tên đăng nhập
    User getUserByUsername(String username);

    // Lấy thông tin người dùng bằng email
    User getUserByEmail(String email);

    // Lấy thông tin người dùng bằng ID
    User getUserById(int id);

    // Thêm mới một người dùng
    User addUser(User user);

    // Xác thực người dùng với tên đăng nhập/email và mật khẩu
    boolean authenticate(String usernameOrEmail, String password);

    // Thay đổi thông tin người dùng
    User updateUser(User user);

    // Xóa người dùng bằng ID
    void deleteUser(int id);
}
