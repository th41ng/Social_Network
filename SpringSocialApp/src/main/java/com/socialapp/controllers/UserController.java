package com.socialapp.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.socialapp.pojo.User;
import com.socialapp.service.UserService;
import jakarta.validation.Valid;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/Users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(ApiUserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private Cloudinary cloudinary;

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

    // Thêm phương thức để xử lý việc xác nhận 0 thành 1
    @PostMapping("/{userId}/verify")
    public String verifyUser(@PathVariable("userId") int userId) {
        userService.verifyStudent(userId);
        return "redirect:/?categoryId=5"; // Chuyển hướng lại trang html
    }

    @GetMapping("/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        return "add_user"; // Tên file html: user_add.html
    }

    @PostMapping(path = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String addUser(@RequestParam Map<String, String> params,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            Model model) {
        try {
            User user = this.userService.register(params, avatar);
            logger.info("Đăng ký thành công người dùng mới: {}", user.getUsername());
            // Gửi thông báo thành công
            model.addAttribute("successMessage", "Người dùng đã được đăng ký thành công!");
            return "redirect:/Users/add"; // Quay lại trang /Users/add
        } catch (Exception e) {
            logger.error("Đã xảy ra lỗi khi đăng ký người dùng mới: {}", e.getMessage());
            // Gửi thông báo lỗi
            model.addAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
            return "redirect:/?categoryId=5"; // Quay lại trang hiện tại với thông báo lỗi
        }
    }
}
