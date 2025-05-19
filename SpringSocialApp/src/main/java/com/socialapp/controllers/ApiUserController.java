package com.socialapp.controllers;

import com.socialapp.pojo.Post;
import com.socialapp.pojo.User;
import com.socialapp.service.EmailService;
import com.socialapp.service.PostService;
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
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ApiUserController {

    @Autowired
    private UserService userDetailService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PostService postService;
    // Tạm lưu mã xác thực trong memory (nên dùng Redis hoặc DB trong thực tế)
    private Map<String, String> verificationCodes = new ConcurrentHashMap<>();
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
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("notVerifed");
                }

                // Kiểm tra vai trò ROLE_LECTURER và thời gian đổi mật khẩu lần cuối
                if (userDetailService.getRole().contains("ROLE_LECTURER")) {
                    long hoursSinceCreation
                            = (new Date().getTime() - userDetailService.getCreatedAt().getTime()) / (1000 * 60 * 60);
                    if (hoursSinceCreation > 24 && userDetailService.getLastPasswordChange() == null) {
                        logger.warn("Người dùng ROLE_LECTURER chưa đổi mật khẩu trong vòng 24 giờ sau khi tạo tài khoản: {}",
                                u.getUsername());
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("expired");
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

    @PostMapping("/send-verification-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email không được để trống");
        }

        User user = userDetailService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng với email này");
        }

        // Tạo mã xác thực 6 chữ số ngẫu nhiên
        String code = String.format("%06d", new Random().nextInt(999999));

        // Lưu mã tạm thời theo email (thời hạn hết hạn có thể thêm xử lý riêng)
        verificationCodes.put(email, code);

        // Gửi email (cần config EmailService)
        String subject = "Mã xác thực đặt lại mật khẩu";
        String body = "Mã xác thực của bạn là: " + code + ". Mã này có hiệu lực trong 10 phút.";
        emailService.sendVerifyEmail(email, subject, body);

        return ResponseEntity.ok("Mã xác thực đã được gửi tới email của bạn.");
    }

    // Sửa lại API reset-password nhận thêm email + verificationCode
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String newPassword = request.get("newPassword");
            String verificationCode = request.get("verificationCode");

            if (email == null || newPassword == null || verificationCode == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thiếu thông tin bắt buộc");
            }

            User user = userDetailService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng.");
            }

            // Kiểm tra mã xác thực
            String storedCode = verificationCodes.get(email);
            if (storedCode == null || !storedCode.equals(verificationCode)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Mã xác thực không hợp lệ hoặc đã hết hạn.");
            }

            // Xóa mã xác thực sau khi dùng
            verificationCodes.remove(email);

            // Cập nhật mật khẩu
            userDetailService.updatePassword(user.getEmail(), newPassword);

            return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi đặt lại mật khẩu.");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUserPosts(Principal principal) {
        try {
            // Kiểm tra Principal
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Principal is null");
            }

            // Lấy thông tin người dùng từ Principal
            User user = this.userDetailService.getUserByUsername(principal.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng.");
            }
         
            // Lấy danh sách bài viết của người dùng
            List<Post> posts = postService.getPostsByUserId(user.getId());
            if (posts == null || posts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng chưa có bài viết nào.");
            }

            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách bài viết của người dùng hiện tại: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lấy danh sách bài viết.");
        }
    }
}
