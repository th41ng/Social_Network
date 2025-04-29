/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.repository;

import com.socialapp.pojo.User;

/**
 *
 * @author DELL G15
 */
public interface UserRepository {
     User getUserByUsername(String username);
    User getUserByEmail(String email);
    User getUserById(int id);
    User addUser(User user);
}
