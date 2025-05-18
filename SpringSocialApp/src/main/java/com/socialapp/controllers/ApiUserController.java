package com.socialapp.controllers;

import com.socialapp.pojo.User;
import com.socialapp.service.UserService;
import com.socialapp.utils.JwtUtils;
import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
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
                User userDetailService = this.userDetailService.getUserByUsername(u.getUsername());

                // Kiểm tra vai trò ROLE_ALUMNI và trạng thái xác thực
                if (userDetailService.getRole().contains("ROLE_ALUMNI") && !userDetailService.getIsVerified()) {
                    logger.warn("Người dùng ROLE_ALUMNI chưa được xác thực: {}", u.getUsername());
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tài khoản chưa được xác thực");
                }

                // Kiểm tra vai trò ROLE_LECTURER và thời gian đổi mật khẩu lần cuối
                if (userDetailService.getRole().contains("ROLE_LECTURER")) {
                    long hoursSinceCreation
                            = (new Date().getTime() - userDetailService.getCreatedAt().getTime()) / (1000 * 60 * 60);
                    if (hoursSinceCreation > 24 && userDetailService.getLastPasswordChange() == null) {
                        logger.warn("Người dùng ROLE_LECTURER chưa đổi mật khẩu trong vòng 24 giờ sau khi tạo tài khoản: {}",
                                u.getUsername());
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn cần đổi mật khẩu trước khi đăng nhập");
                    }
                }

                // Nếu tất cả kiểm tra hợp lệ, tạo token
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") int id) {
        try {
            logger.info("Đang cố gắng xoá người dùng với ID: {}", id);
            userDetailService.deleteUser(id);
            logger.info("Đã xoá thành công người dùng với ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Đã xảy ra lỗi khi xoá người dùng với ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @GetMapping("/profile")
//    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authorizationHeader) {
//        try {
//            // Lấy token từ header
//            String token = authorizationHeader.replace("Bearer ", "");
//
//            // Trích xuất username từ token
//            String username = JwtUtils.validateTokenAndGetUsername(token);
//            if (username == null) {
//                logger.warn("Token không hợp lệ hoặc không chứa username");
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
//            }
//
//            // Lấy thông tin người dùng từ database
//            User user = userDetailService.getUserByUsername(username);
//            if (user == null) {
//                logger.warn("Không tìm thấy người dùng với username: {}", username);
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
//            }
//
//            // Trả về thông tin người dùng
//            logger.info("Lấy thông tin profile thành công cho username: {}", username);
//            return ResponseEntity.ok(user);
//        } catch (Exception e) {
//            logger.error("Đã xảy ra lỗi khi lấy thông tin profile: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xử lý yêu cầu");
//        }
//    }
    @RequestMapping("/secure/profile")
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<?> getProfile(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Principal is null");
        }
        User user = this.userDetailService.getUserByUsername(principal.getName());
        return ResponseEntity.ok(user);
    }

}
