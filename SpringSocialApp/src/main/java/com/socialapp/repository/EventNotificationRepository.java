/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.repository;

import com.socialapp.pojo.EventNotification;
import java.util.List;
import java.util.Map;


public interface EventNotificationRepository {
 
    List<EventNotification> getNotifications(Map<String, String> params);

    EventNotification getNotificationById(int id);

    EventNotification addOrUpdateNotification(EventNotification notification);

    void deleteNotification(int id);
    long countNotis();

    List<EventNotification> getNotificationsForUser(int userId,Map<String, String> params);
}
