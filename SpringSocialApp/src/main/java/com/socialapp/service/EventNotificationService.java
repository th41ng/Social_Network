
package com.socialapp.service;

import com.socialapp.pojo.EventNotification;
import java.util.List;
import java.util.Map;


public interface EventNotificationService {
    List<EventNotification> getNotifications(Map<String, String> params);

    EventNotification getNotificationById(int id);

    EventNotification addOrUpdateNotification(EventNotification notification);

    void deleteNotification(int id);

    long countNotis();

    List<EventNotification> getNotificationsForUser(int UserId,Map<String, String> params);
}