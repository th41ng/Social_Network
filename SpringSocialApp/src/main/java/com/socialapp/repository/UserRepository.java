package com.socialapp.repository;

import com.socialapp.pojo.User;

/**
 * Giao diện cho các thao tác với người dùng
 */
public interface UserRepository {
    // Lấy thông tin người dùng theo tên đăng nhập
    User getUserByUsername(String username);

    // Lấy thông tin người dùng theo email
    User getUserByEmail(String email);

    // Lấy thông tin người dùng theo ID
    User getUserById(int id);

    // Cập nhật thông tin người dùng
    User updateUser(User user);

    // Xóa người dùng theo ID
    void deleteUser(int id);
    
    User register(User u);
    
    boolean authenticate(String username, String password);
}
