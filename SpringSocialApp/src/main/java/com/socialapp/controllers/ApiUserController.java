package com.socialapp.controllers;

import com.socialapp.pojo.User;
import com.socialapp.service.UserService;
import com.socialapp.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ApiUserController {

    @Autowired
    private UserService userDetailService;

    private static final Logger logger = LoggerFactory.getLogger(ApiUserController.class);

    @PostMapping(path = "/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestParam Map<String, String> params,
            @RequestParam(value = "avatar") MultipartFile avatar) {
        try {
            User user = this.userDetailService.register(params, avatar);
            logger.info("Đăng ký thành công người dùng mới: {}", user.getUsername());
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Đã xảy ra lỗi khi đăng ký người dùng mới: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User u) {
        try {
            if (u.getUsername() == null || u.getPassword() == null) {
                logger.warn("Thông tin đăng nhập không đầy đủ.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thiếu username hoặc password");
            }

            if (this.userDetailService.authenticate(u.getUsername(), u.getPassword())) {
                String token = JwtUtils.generateToken(u.getUsername());
                logger.info("Đăng nhập thành công: {}", u.getUsername());
                return ResponseEntity.ok().body(Collections.singletonMap("token", token));
            } else {
                logger.warn("Đăng nhập thất bại cho username: {}", u.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai thông tin đăng nhập");
            }
        } catch (Exception e) {
            logger.error("Đã xảy ra lỗi khi xử lý đăng nhập: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xử lý đăng nhập");
        }
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") int userId) {
        try {
            logger.info("Đang cố gắng xoá người dùng với ID: {}", userId);
            userDetailService.deleteUser(userId);
            logger.info("Đã xoá thành công người dùng với ID: {}", userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Đã xảy ra lỗi khi xoá người dùng với ID {}: {}", userId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/verify/{userId}")
    public ResponseEntity<?> verifyStudent(@PathVariable int userId) {
        try {
            boolean isVerified = this.userDetailService.verifyStudent(userId);
            if (isVerified) {
                logger.info("Xác thực thành công cho người dùng ID: {}", userId);
                return ResponseEntity.ok().body(Collections.singletonMap("message", "Xác thực thành công"));
            } else {
                logger.warn("Không tìm thấy người dùng với ID: {}", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
            }
        } catch (Exception e) {
            logger.error("Đã xảy ra lỗi khi xác thực người dùng ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xác thực");
        }
    }

}
