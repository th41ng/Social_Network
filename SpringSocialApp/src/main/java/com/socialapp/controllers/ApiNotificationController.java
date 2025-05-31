package com.socialapp.controllers;

import com.socialapp.pojo.EventNotification;
import com.socialapp.pojo.User;
import com.socialapp.service.EventNotificationService;
import com.socialapp.service.UserService;
import com.socialapp.utils.JwtUtils;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiNotificationController {

    @Autowired
    private EventNotificationService eventNotificationService;
    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(ApiNotificationController.class);

    @DeleteMapping("/delete/{notificationId}")
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
    public ResponseEntity<?> getUserNotifications(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam Map<String, String> params) {
        try {
            // Step 1: Validate Authorization Header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Invalid or missing Authorization header.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is invalid or missing.");
            }

            String token = authHeader.substring(7);

            // Step 2: Validate JWT and Extract Username
            String username = JwtUtils.validateTokenAndGetUsername(token);
            if (username == null) {
                logger.warn("Invalid or expired JWT.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT is invalid or expired.");
            }

            // Step 3: Retrieve User by Username
            User user = userService.getUserByUsername(username);
            if (user == null) {
                logger.warn("No user found for username: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            // Step 4: Ensure User ID is Available
            Integer userId = user.getId();
            if (userId == null) {
                logger.warn("User ID is null for username: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User ID is unavailable.");
            }

            // Step 5: Fetch Notifications for the User
            logger.info("Fetching notifications for userId: {}", userId);
            List<EventNotification> notifications = eventNotificationService.getNotificationsForUser(userId, params);
            
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            // Step 6: Handle Unexpected Errors
            logger.error("An error occurred while fetching notifications: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

}
