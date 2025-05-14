package com.socialapp.controllers;

import com.socialapp.pojo.User;
import com.socialapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/Users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(ApiUserController.class);
    @Autowired
    private UserService userService;

    // Hiển thị trang đăng nhập
    @GetMapping("/login")
    public String loginView() {
        return "login"; // Chuyển tới trang login
    }

    // Hiển thị danh sách người dùng với các bộ lọc
    @GetMapping("/listUser")
    public String listUsers(@RequestParam Map<String, String> params, Model model) {
        List<User> users = this.userService.getAllUsers(params);
        model.addAttribute("users", users);
        model.addAttribute("params", params); 
        return "user_management"; 
    }



    // Sửa thông tin người dùng
    @GetMapping("/editUser/{id}")
    public String editUserForm(@PathVariable("id") int id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user); 
        return "user_edit"; 
    }

    @PostMapping("/editUser/{id}")
    public String updateUser(@PathVariable("id") int id, @ModelAttribute User user) {
        user.setId(id); // Cập nhật thông tin người dùng
        userService.updateUser(user);
        return "redirect:/Users/listUser"; 
    }
    
   

    
}
