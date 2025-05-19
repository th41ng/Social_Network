/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;

import com.socialapp.pojo.Event;
import com.socialapp.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api")
public class ApiEventController {

    @Autowired
    private EventService eventService;
    private static final Logger logger = LoggerFactory.getLogger(ApiNotificationController.class);

    @DeleteMapping("/deleteEvent/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") int id) {
        try {
            logger.info("Đang cố gắng xoá sự kiện với ID: {}", id);
            eventService.deleteEvent(id);
            logger.info("Đã xoá thành công sự kiện với ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Đã xảy ra lỗi khi xoá sự kiện với ID {}: ", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API lấy chi tiết sự kiện theo ID
    @GetMapping("/event/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable("id") int id) {
        try {
            logger.info("Đang lấy thông tin sự kiện với ID: {}", id);
            Event event = eventService.getEventById(id);
            if (event == null) {
                logger.warn("Không tìm thấy sự kiện với ID: {}", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(event, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy thông tin sự kiện với ID {}: ", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
