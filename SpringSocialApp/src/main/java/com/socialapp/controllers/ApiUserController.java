package com.socialapp.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.socialapp.configs.UserRole;
import com.socialapp.pojo.Post;
import com.socialapp.pojo.User;
import com.socialapp.service.EmailService;
import com.socialapp.service.PostApiService;
import com.socialapp.service.PostService;
import com.socialapp.service.UserService;
import com.socialapp.utils.JwtUtils;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ApiUserController {

    @Autowired
    private UserService userDetailService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PostApiService postApiService;
    @Autowired
    private Cloudinary cloudinary;

    private Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(ApiUserController.class);

    @PostMapping(path = "/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestParam Map<String, String> params,
            @RequestParam(value = "avatar") MultipartFile avatar,
            @RequestParam(value = "coverImage") MultipartFile coverImage) {
        try {
            User user = this.userDetailService.register(params, avatar, coverImage);
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
                User userDetail = this.userDetailService.getUserByUsername(u.getUsername());

                // Kiểm tra nếu tài khoản bị khóa
                if (userDetail.getIsLocked()) {
                    logger.warn("Tài khoản đã bị khóa: {}", u.getUsername());
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("accountLocked");
                }

                // Kiểm tra vai trò và trạng thái của người dùng
                if (userDetail.getRole() == UserRole.ROLE_ALUMNI && !userDetail.getIsVerified()) {
                    logger.warn("Người dùng ROLE_ALUMNI chưa được xác thực: {}", u.getUsername());
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("notVerified");
                }

                if (userDetail.getRole() == UserRole.ROLE_LECTURER) {
                    long hoursSinceCreation
                            = (new Date().getTime() - userDetail.getCreatedAt().getTime()) / (1000 * 60 * 60);
                    if (hoursSinceCreation > 24 && userDetail.getLastPasswordChange() == null) {
                        logger.warn("Người dùng ROLE_LECTURER chưa đổi mật khẩu trong vòng 24 giờ sau khi tạo tài khoản: {}",
                                u.getUsername());
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("expired");
                    }
                }

                // Tạo token nếu hợp lệ
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

        String code = String.format("%06d", new Random().nextInt(999999));
        verificationCodes.put(email, code);

        String subject = "Mã xác thực đặt lại mật khẩu";
        String body = "Mã xác thực của bạn là: " + code + ". Mã này có hiệu lực trong 10 phút.";
        emailService.sendVerifyEmail(email, subject, body);

        return ResponseEntity.ok("Mã xác thực đã được gửi tới email của bạn.");
    }

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

            String storedCode = verificationCodes.get(email);
            if (storedCode == null || !storedCode.equals(verificationCode)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Mã xác thực không hợp lệ hoặc đã hết hạn.");
            }

            verificationCodes.remove(email);
            userDetailService.updatePassword(user.getEmail(), newPassword);

            return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi đặt lại mật khẩu.");
        }
    }

    @GetMapping("/user-posts/{userId}")
    public ResponseEntity<?> getUserPosts(@PathVariable("userId") int userId) {
        try {
            List<Post> userPosts = postApiService.getPostsByUserId(userId);

            if (userPosts == null || userPosts.isEmpty()) {
                logger.info("No posts found for user with ID: {}", userId);
                return ResponseEntity.ok(Collections.emptyList());
            }
            logger.info("Found {} posts for user with ID: {}", userPosts.size(), userId);
            return ResponseEntity.ok(userPosts);
        } catch (Exception e) {
            logger.error("Error while fetching posts for user with ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while fetching posts");
        }
    }

    @PostMapping(path = "/updateProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập!");
            }

            User user = userDetailService.getUserByUsername(principal.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng!");
            }

            // Cập nhật thông tin cơ bản
            user.setFullName(fullName);
            user.setEmail(email);

            // Cập nhật avatar nếu có
            if (avatar != null && !avatar.isEmpty()) {
                try {
                    Map<String, Object> avatarResult = cloudinary.uploader().upload(
                            avatar.getBytes(),
                            ObjectUtils.asMap("resource_type", "image", "folder", "user_avatars")
                    );
                    String avatarUrl = avatarResult.get("secure_url").toString();
                    user.setAvatar(avatarUrl);
                } catch (Exception ex) {
                    logger.error("Lỗi khi tải lên ảnh avatar: {}", ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi tải lên ảnh avatar!");
                }
            }

            // Cập nhật ảnh bìa nếu có
            if (coverImage != null && !coverImage.isEmpty()) {
                try {
                    Map<String, Object> coverResult = cloudinary.uploader().upload(
                            coverImage.getBytes(),
                            ObjectUtils.asMap("resource_type", "image", "folder", "user_cover_images")
                    );
                    String coverImageUrl = coverResult.get("secure_url").toString();
                    user.setCoverImage(coverImageUrl);
                } catch (Exception ex) {
                    logger.error("Lỗi khi tải lên ảnh bìa: {}", ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi tải lên ảnh bìa!");
                }
            }

            // Lưu thay đổi vào database
            userDetailService.updateUser(user);
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật hồ sơ: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi cập nhật hồ sơ!");
        }
    }

}
