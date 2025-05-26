/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.repository;

import com.socialapp.pojo.UserGroups;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
public interface UserGroupsRepository {
    List<UserGroups> getAllGroups(Map<String, String> params);
    UserGroups getGroupById(int groupId);
   
    UserGroups addOrUpdateGroup(UserGroups group);
    void deleteGroup(int groupId);
}
