package com.socialapp.repository;


import com.socialapp.pojo.User;
import java.util.List;
import java.util.Map;

/**
 * Giao diện cho các thao tác với người dùng
 */
public interface UserRepository {

    
    List<User> getAllUsers(Map<String, String> params);

    
    // Lấy thông tin người dùng theo tên đăng nhập
    User getUserByUsername(String username);

    // Lấy thông tin người dùng theo email
    User getUserByEmail(String email);

    // Lấy thông tin người dùng theo ID
    User getUserById(int id);

    // Cập nhật thông tin người dùng
    User updateUser(User user);
    
    User addUser(User user);
    
    // Xóa người dùng theo ID
    boolean deleteUser(int id); // THAY ĐỔI: từ void thành boolean

    User register(User u);
    
    void banUser(int userId);
    
    void verifyStudent(int userId);
    
    
    boolean authenticate(String username, String password);
    
    long countUsers();
    
    int countUsersRegisteredToday();

    
    void updatePassword(String email, String newPassword);

}
