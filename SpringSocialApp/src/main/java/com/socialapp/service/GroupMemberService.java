/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.service;

import com.socialapp.pojo.GroupMembers;
import java.util.List;
import java.util.Map;

/**
 *
 * @author DELL G15
 */
public interface GroupMemberService {

    // Lấy tất cả thành viên theo tham số
    List<GroupMembers> getAllMembers(Map<String, String> params);

    // Lấy thông tin thành viên theo ID
    GroupMembers getMemberById(int memberId);

    // Lấy thông tin thành viên dựa trên ID nhóm và ID người dùng
    GroupMembers getMemberByGroupAndUserId(int groupId, int userId);

    // Thêm hoặc cập nhật thông tin thành viên
    GroupMembers addOrUpdateMember(GroupMembers groupMember);

    // Xóa thành viên theo ID
    void deleteMember(int memberId);
//
//    // Xóa thành viên khỏi nhóm dựa trên ID nhóm và ID người dùng
//    void deleteMemberByGroupAndUserId(int groupId, int userId);

    List<GroupMembers> getMembersByGroupId(int groupId);

}
