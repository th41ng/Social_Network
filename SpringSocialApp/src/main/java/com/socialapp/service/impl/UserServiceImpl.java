package com.socialapp.service.impl;

import com.socialapp.pojo.Users;
import com.socialapp.repository.UserRepository;
import com.socialapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Users getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    @Override
    public Users getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Override
    public Users getUserById(int id) {
        return userRepository.getUserById(id);
    }

    @Override
    public Users addUser(Users user) {
        return userRepository.addUser(user);
    }

    @Override
    public boolean authenticate(String usernameOrEmail, String password) {
        return userRepository.authenticate(usernameOrEmail, password);
    }

    @Override
    public Users updateUser(Users user) {
        return userRepository.updateUser(user);
    }

    @Override
    public void deleteUser(int id) {
        userRepository.deleteUser(id);
    }
}
