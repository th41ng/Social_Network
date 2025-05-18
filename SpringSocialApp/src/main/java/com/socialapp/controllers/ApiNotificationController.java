package com.socialapp.controllers;

import com.socialapp.pojo.EventNotification;
import com.socialapp.pojo.User;
import com.socialapp.service.EventNotificationService;
import com.socialapp.service.UserService;
import com.socialapp.utils.JwtUtils;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiNotificationController {

    @Autowired
    private EventNotificationService eventNotificationService;
    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(ApiNotificationController.class);

    @DeleteMapping("/deleteNotification/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable("notificationId") int id) {
        try {
            logger.info("Đang cố gắng xoá sự kiện với ID: {}", id);
            eventNotificationService.deleteNotification(id);
            logger.info("Đã xoá thành công sự kiện với ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Đã xảy ra lỗi khi xoá sự kiện với ID {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<EventNotification>> getUserNotifications(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Authorization header không hợp lệ hoặc không được cung cấp.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String token = authHeader.substring(7);
            String username = JwtUtils.validateTokenAndGetUsername(token);

            if (username == null) {
                logger.warn("JWT không hợp lệ hoặc đã hết hạn.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            logger.info("Đang lấy user theo username: {}", username);
            User user = userService.getUserByUsername(username);

            if (user == null) {
                logger.warn("Không tìm thấy user cho username: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Integer userId = user.getId();
            if (userId == null) {
                logger.warn("User ID bị null cho username: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            logger.info("Đang lấy danh sách thông báo cho userId: {}", userId);
            List<EventNotification> notifications = eventNotificationService.getNotificationsForUser(userId);

            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách thông báo: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
