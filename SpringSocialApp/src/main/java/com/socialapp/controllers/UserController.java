package com.socialapp.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.socialapp.configs.UserRole;
import com.socialapp.pojo.User;
import com.socialapp.repository.impl.UserRepositoryImpl;
import com.socialapp.service.UserService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/Users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(ApiUserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginView() {
        return "login"; 
    }

    @GetMapping("/listUser")
    public String listUsers(@RequestParam Map<String, String> params, Model model) {
        String pageParam = params.get("page");
        int page = (pageParam == null || pageParam.trim().isEmpty()) ? 1 : Integer.parseInt(pageParam);
        if (page < 1) {
            page = 1;
        }
     
        params.put("page", String.valueOf(page));

        List<User> users = this.userService.getAllUsers(params);
        long totalUsers = this.userService.countUsers();
        
        int pageSize = UserRepositoryImpl.PAGE_SIZE; 
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);
        
        model.addAttribute("users", users);
        model.addAttribute("params", params);
          model.addAttribute("currentPage", page);
           model.addAttribute("totalPages", totalPages);
        model.addAttribute("roles", UserRole.values()); 
        return "user_management";
    }

    
    @GetMapping("/editUser/{id}")
    public String editUserForm(@PathVariable("id") int id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "edit_user";
    }

    @PostMapping("/editUser/{id}")
    public String updateUser(
            @PathVariable("id") int id,
            @ModelAttribute User user,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "coverImageFile", required = false) MultipartFile coverImageFile) throws IOException {

        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            throw new IllegalArgumentException("User không tồn tại với ID: " + id);
        }

        // Cập nhật thông tin cơ bản
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setFullName(user.getFullName());
        existingUser.setRole(user.getRole());
        existingUser.setIsVerified(user.getIsVerified());
        existingUser.setIsLocked(user.getIsLocked());
        // Mã hóa mật khẩu nếu thay đổi
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            existingUser.setPassword(encodedPassword);
        }

        // Xử lý avatar
        if (avatarFile != null && !avatarFile.isEmpty()) {
            Map<String, Object> res = cloudinary.uploader().upload(avatarFile.getBytes(), ObjectUtils.asMap(
                    "resource_type", "image"
            ));
            String avatarUrl = (String) res.get("secure_url");
            existingUser.setAvatar(avatarUrl);
        }

        // Xử lý coverImage
        if (coverImageFile != null && !coverImageFile.isEmpty()) {
            Map<String, Object> res = cloudinary.uploader().upload(coverImageFile.getBytes(), ObjectUtils.asMap(
                    "resource_type", "image"
            ));
            String coverImageUrl = (String) res.get("secure_url");
            existingUser.setCoverImage(coverImageUrl);
        }

        // Lưu vào database
        userService.updateUser(existingUser);

        return "redirect:/?categoryId=5";
    }

  
    @PostMapping("/{userId}/verify")
    public String verifyUser(@PathVariable("userId") int userId) {
        userService.verifyStudent(userId);
        return "redirect:/?categoryId=5"; 
    }

    @PostMapping("/banUser/{userId}")
    public String banUser(@PathVariable("userId") int userId) {
        userService.banUser(userId);
        return "redirect:/?categoryId=5";
    }

    @GetMapping("/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", UserRole.values());
        return "add_user"; 
    }

    @PostMapping(path = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String addUser(@RequestParam Map<String, String> params,
            @RequestParam(value = "avatar", required = true) MultipartFile avatar,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            Model model) {
        try {
            User user = this.userService.register(params, avatar, coverImage);
            logger.info("Đăng ký thành công người dùng mới: {}", user.getUsername());
           
            model.addAttribute("successMessage", "Người dùng đã được đăng ký thành công!");
            return "redirect:/Users/add"; // Quay lại trang /Users/add
        } catch (Exception e) {
            logger.error("Đã xảy ra lỗi khi đăng ký người dùng mới: {}", e.getMessage());
          
            model.addAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
            return "redirect:/?categoryId=5"; 
        }
    }
}
