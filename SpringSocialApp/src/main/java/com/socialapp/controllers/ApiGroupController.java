/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;

import static com.mysql.cj.conf.PropertyKey.logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.socialapp.service.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api")
public class ApiGroupController {

    @Autowired
    private UserGroupService userGroupService;
    private static final Logger logger = LoggerFactory.getLogger(ApiNotificationController.class);

    @DeleteMapping("/groups/delete/{GroupId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable("GroupId") int GroupId) {
        try {
            logger.info("Đang cố gắng xoá nhóm với ID: {}", GroupId);
            userGroupService.deleteGroup(GroupId);
            logger.info("Đã xoá thành công nhóm với ID: {}", GroupId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Đã xảy ra lỗi khi xoá nhóm với ID {}: ", GroupId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
