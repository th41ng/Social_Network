package com.socialapp.controllers;

import com.socialapp.pojo.User;
import com.socialapp.service.UserService;
import com.socialapp.utils.JwtUtils; // Import JwtUtils
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ApiUserController {

    @Autowired
    private UserService userService; // Đổi tên userDetailService thành userService cho nhất quán

    @Autowired
    private JwtUtils jwtUtils; // Inject JwtUtils

    private static final Logger logger = LoggerFactory.getLogger(ApiUserController.class);

    @PostMapping(path = "/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestParam Map<String, String> params,
                                      @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        try {
            User user = this.userService.register(params, avatar); // Sử dụng userService
            logger.info("Đăng ký thành công người dùng mới: {}", user.getUsername());
            
            User responseUser = new User(); // Tạo đối tượng mới để không trả về password
            responseUser.setId(user.getId());
            responseUser.setUsername(user.getUsername());
            responseUser.setFullName(user.getFullName());
            responseUser.setEmail(user.getEmail());
            responseUser.setAvatar(user.getAvatar());
            responseUser.setRole(user.getRole());
            // ... các trường an toàn khác ...
            return new ResponseEntity<>(responseUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
             logger.warn("Lỗi đăng ký (dữ liệu không hợp lệ): {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e) {
            logger.error("Đã xảy ra lỗi hệ thống khi đăng ký người dùng mới: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống, không thể đăng ký người dùng.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User u) {
        try {
            if (u.getUsername() == null || u.getPassword() == null || u.getUsername().trim().isEmpty() || u.getPassword().isEmpty()) {
                logger.warn("Thông tin đăng nhập không đầy đủ hoặc không hợp lệ.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thiếu username hoặc password.");
            }

            if (this.userService.authenticate(u.getUsername(), u.getPassword())) { // Sử dụng userService
                User authenticatedUser = this.userService.getUserByUsername(u.getUsername());

                if (authenticatedUser == null) {
                     logger.error("Xác thực thành công nhưng không thể lấy thông tin cho người dùng: {}", u.getUsername());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lấy thông tin người dùng sau khi xác thực.");
                }

                // Kiểm tra vai trò ROLE_ALUMNI và trạng thái xác thực
                if (authenticatedUser.getRole() != null && authenticatedUser.getRole().contains("ROLE_ALUMNI") && (authenticatedUser.getIsVerified() == null || !authenticatedUser.getIsVerified())) {
                    logger.warn("Người dùng ROLE_ALUMNI '{}' chưa được xác thực.", u.getUsername());
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tài khoản cựu sinh viên của bạn chưa được quản trị viên xác thực.");
                }

                // Kiểm tra vai trò ROLE_LECTURER và thời gian đổi mật khẩu lần cuối
                if (authenticatedUser.getRole() != null && authenticatedUser.getRole().contains("ROLE_LECTURER")) {
                    if (authenticatedUser.getCreatedAt() != null) {
                        long hoursSinceCreation = (new Date().getTime() - authenticatedUser.getCreatedAt().getTime()) / (1000 * 60 * 60);
                        if (hoursSinceCreation > 24 && authenticatedUser.getLastPasswordChange() == null) {
                            logger.warn("Người dùng ROLE_LECTURER '{}' chưa đổi mật khẩu sau 24h tạo tài khoản.", u.getUsername());
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn cần đổi mật khẩu lần đầu trước khi đăng nhập. Vui lòng liên hệ quản trị viên nếu cần hỗ trợ.");
                        }
                    } else {
                        logger.warn("Người dùng ROLE_LECTURER '{}' không có thông tin ngày tạo tài khoản.", u.getUsername());
                        // Cân nhắc cách xử lý trường hợp này, có thể là lỗi dữ liệu
                    }
                }

                // Nếu tất cả kiểm tra hợp lệ, tạo token
                String token = this.jwtUtils.generateToken(authenticatedUser.getUsername()); // Gọi qua instance
                logger.info("Đăng nhập thành công cho người dùng: {}", authenticatedUser.getUsername());
                return ResponseEntity.ok().body(Collections.singletonMap("token", token));
            } else {
                logger.warn("Đăng nhập thất bại cho username: {}", u.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai thông tin đăng nhập.");
            }
        } catch (RuntimeException e) { // Bắt cụ thể RuntimeException từ jwtUtils.generateToken
             logger.error("Lỗi khi tạo token JWT trong quá trình đăng nhập cho {}: {}", u.getUsername(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống khi tạo phiên đăng nhập.");
        }
        catch (Exception e) {
            logger.error("Đã xảy ra lỗi không xác định khi xử lý đăng nhập cho {}: {}", u.getUsername(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống khi xử lý đăng nhập.");
        }
    }
    
    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal().toString())) {
            logger.warn("API /current-user: Không có thông tin xác thực hoặc phiên không hợp lệ.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Yêu cầu xác thực để thực hiện hành động này.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            
            // Lấy thông tin User đầy đủ từ service
            User currentUser = this.userService.getUserByUsername(username); 

            if (currentUser != null) {
                User userResponse = new User(); 
                userResponse.setId(currentUser.getId());
                userResponse.setUsername(currentUser.getUsername());
                userResponse.setFullName(currentUser.getFullName()); // Sử dụng fullName
                userResponse.setAvatar(currentUser.getAvatar());
                userResponse.setRole(currentUser.getRole());
                userResponse.setEmail(currentUser.getEmail());
                userResponse.setIsVerified(currentUser.getIsVerified());
                userResponse.setStudentId(currentUser.getStudentId());
                // Thêm các trường an toàn khác nếu cần
                
                logger.info("API /current-user: Trả về thông tin cho người dùng '{}'", username);
                return ResponseEntity.ok(userResponse);
            } else {
                logger.error("API /current-user: Người dùng '{}' đã xác thực nhưng không tìm thấy trong database.", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy thông tin người dùng.");
            }
        } else if (principal != null) {
            logger.warn("API /current-user: Principal không phải là UserDetails. Loại Principal: {}", principal.getClass().getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể xác định thông tin người dùng từ principal.");
        }

        logger.error("API /current-user: Principal là null sau khi đã kiểm tra authentication.isAuthenticated(). Điều này không nên xảy ra.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi không xác định khi lấy thông tin người dùng.");
    }

    @DeleteMapping("/users/{userId}")
    // Cân nhắc thêm @PreAuthorize("hasRole('ADMIN')") hoặc kiểm tra quyền trong method
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") int userId) {
        try {
            logger.info("Yêu cầu xoá người dùng với ID: {}", userId);
            // Thêm logic kiểm tra quyền hạn ở đây nếu cần
            boolean deleted = userService.deleteUser(userId); // Giả sử phương thức này trả về boolean
            if (deleted) {
                logger.info("Đã xoá thành công người dùng với ID: {}", userId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                logger.warn("Không thể xoá người dùng với ID: {}. Có thể người dùng không tồn tại.", userId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Đã xảy ra lỗi khi xoá người dùng với ID {}: {}", userId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}