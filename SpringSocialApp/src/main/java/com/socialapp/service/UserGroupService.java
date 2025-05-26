/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.service;

import com.socialapp.pojo.UserGroups;
import java.util.List;
import java.util.Map;

/**
 *
 * @author DELL G15
 */
public interface UserGroupService {

    // Lấy danh sách tất cả các nhóm với các tham số lọc
    List<UserGroups> getAllGroups(Map<String, String> params);

    // Lấy thông tin nhóm cụ thể theo ID
    UserGroups getGroupById(int groupId);
    


    // Thêm mới hoặc cập nhật thông tin nhóm
    UserGroups addOrUpdateGroup(UserGroups group);

    // Xóa nhóm theo ID
    void deleteGroup(int groupId);

   
}
