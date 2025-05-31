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

    List<UserGroups> getAllGroups(Map<String, String> params);

    UserGroups getGroupById(int groupId);

    long countGroup();

    UserGroups addOrUpdateGroup(UserGroups group);

    void deleteGroup(int groupId);

}
