package com.socialapp.repository;


import com.socialapp.pojo.User;
import java.util.List;
import java.util.Map;

public interface UserRepository {

    
    List<User> getAllUsers(Map<String, String> params);


    User getUserByUsername(String username);

    User getUserByEmail(String email);

    User getUserById(int id);

    User updateUser(User user);
    
    User addUser(User user);

    boolean deleteUser(int id); 

    User register(User u);
    
    void banUser(int userId);
    
    void verifyStudent(int userId);
    
    
    boolean authenticate(String username, String password);
    
    long countUsers();
    
    int countUsersRegisteredToday();

    
    void updatePassword(String email, String newPassword);

    
    
     List<User> findAvailableUsersForGroup(int groupId);
     
     
     
}




