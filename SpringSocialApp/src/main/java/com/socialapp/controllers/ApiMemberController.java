/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.controllers;

import com.socialapp.service.GroupMemberService;
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
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api")
public class ApiMemberController {

    @Autowired
    private GroupMemberService groupMemberService;

    private static final Logger logger = LoggerFactory.getLogger(ApiNotificationController.class);

    @DeleteMapping("/members/delete/{MemberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable("MemberId") int MemberId) {
        try {
            logger.info("Đang cố gắng xoá thành viên với ID: {}", MemberId);
            groupMemberService.deleteMember(MemberId);
            logger.info("Đã xoá thành công thành viên với ID: {}", MemberId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Đã xảy ra lỗi khi xoá thành viên với ID {}: ", MemberId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}