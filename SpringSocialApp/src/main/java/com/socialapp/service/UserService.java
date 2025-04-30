/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.service;

import com.socialapp.pojo.Users;

/**
 *
 * @author DELL G15
 */
public interface UserService {

    // Lấy thông tin người dùng bằng tên đăng nhập
    Users getUserByUsername(String username);

    // Lấy thông tin người dùng bằng email
    Users getUserByEmail(String email);

    // Lấy thông tin người dùng bằng ID
    Users getUserById(int id);

    // Thêm mới một người dùng
    Users addUser(Users user);

    // Xác thực người dùng với tên đăng nhập/email và mật khẩu
    boolean authenticate(String usernameOrEmail, String password);

    // Thay đổi thông tin người dùng
    Users updateUser(Users user);

    // Xóa người dùng bằng ID
    void deleteUser(int id);
}
