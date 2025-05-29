/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.repository;

import com.socialapp.pojo.EventNotification;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
public interface EventNotificationRepository {
    // Lấy danh sách thông báo dựa trên các tham số

    List<EventNotification> getNotifications(Map<String, String> params);

    // Lấy thông báo dựa trên ID
    EventNotification getNotificationById(int id);

    // Thêm hoặc cập nhật thông báo
    EventNotification addOrUpdateNotification(EventNotification notification);

    // Xóa thông báo dựa trên ID
    void deleteNotification(int id);
    long countNotis();

    List<EventNotification> getNotificationsForUser(int userId,Map<String, String> params);
}
