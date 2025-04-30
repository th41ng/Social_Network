package com.socialapp.repository;

import com.socialapp.pojo.Users;

/**
 * Giao diện cho các thao tác với người dùng
 */
public interface UserRepository {
    // Lấy thông tin người dùng theo tên đăng nhập
    Users getUserByUsername(String username);

    // Lấy thông tin người dùng theo email
    Users getUserByEmail(String email);

    // Lấy thông tin người dùng theo ID
    Users getUserById(int id);

    // Thêm mới một người dùng
    Users addUser(Users user);

    // Cập nhật thông tin người dùng
    Users updateUser(Users user);

    // Xóa người dùng theo ID
    void deleteUser(int id);

    // Xác thực người dùng theo tên đăng nhập hoặc email và mật khẩu
    boolean authenticate(String usernameOrEmail, String password);
}
