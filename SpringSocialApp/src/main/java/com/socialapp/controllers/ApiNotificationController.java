package com.socialapp.controllers;

import com.socialapp.service.EventNotificationService;
import com.socialapp.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Bộ điều khiển để xử lý các yêu cầu API cho Sự kiện.
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api")
public class ApiNotificationController {

    @Autowired
    private EventNotificationService eventNotificaionService;

    private static final Logger logger = LoggerFactory.getLogger(ApiNotificationController.class);

    /**
     * Endpoint để xoá sự kiện theo ID.
     *
     * @param id ID của sự kiện cần xoá
     * @return ResponseEntity chỉ ra kết quả của hoạt động
     */
    @DeleteMapping("/notification/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable("notificationId") int id) {
        try {
            logger.info("Đang cố gắng xoá sự kiện với ID: {}", id);
            eventNotificaionService.deleteNotification(id);
            logger.info("Đã xoá thành công sự kiện với ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Đã xảy ra lỗi khi xoá sự kiện với ID {}: ", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
