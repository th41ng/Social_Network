package com.socialapp.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.socialapp.pojo.User;
import com.socialapp.repository.UserRepository;
import com.socialapp.service.UserService;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("userDetailsService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private Cloudinary cloudinary;

    @Override
    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Override
    public User getUserById(int id) {
        return userRepository.getUserById(id);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.updateUser(user);
    }

    @Override
    public void deleteUser(int id) {
        userRepository.deleteUser(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return this.userRepository.getUserByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = this.getUserByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException("Invalid username!");
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(u.getRole()));
        System.out.println("Found user: " + u.getUsername());
        return new org.springframework.security.core.userdetails.User(
                u.getUsername(), u.getPassword(), authorities);
    }

    @Override
    public User register(Map<String, String> params, MultipartFile avatar) {
        User user = new User();
        user.setFullName(params.get("fullName"));
        user.setEmail(params.get("email"));
        user.setStudentId(params.get("studentId"));
        user.setUsername(params.get("username"));
        user.setPassword(this.passwordEncoder.encode(params.get("password")));
        user.setRole(params.get("role"));
        user.setCreatedAt(new Date());
        user.setLastPasswordChange(null);
        user.setIsVerified(false);
        user.setIsLocked(false);

        if (avatar != null && !avatar.isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(avatar.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                user.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return this.userRepository.register(user);
    }

    @Override
    public boolean authenticate(String username, String password) {
        return this.userRepository.authenticate(username, password);
    }

    @Override
    public List<User> getAllUsers(Map<String, String> params) {
        return this.userRepository.getAllUsers(params);
    }

    @Override
    public void verifyStudent(int userId) {
        this.userRepository.verifyStudent(userId);
    }

    @Override
    public int countUsersRegisteredToday() {
        return this.userRepository.countUsersRegisteredToday();
    }

    @Override
    public User addUser(User user) {
        return this.userRepository.addUser(user);
    }

    @Override
    public void updatePassword(String email, String newPassword) {
        this.userRepository.updatePassword(email, newPassword);
    }
}
