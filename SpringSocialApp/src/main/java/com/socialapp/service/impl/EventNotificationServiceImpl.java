package com.socialapp.service.impl;

import com.socialapp.pojo.EventNotification;
import com.socialapp.repository.EventNotificationRepository;
import com.socialapp.service.EventNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class EventNotificationServiceImpl implements EventNotificationService {

    @Autowired
    private EventNotificationRepository eventNotificationRepository;

    @Override
    public List<EventNotification> getNotifications(Map<String, String> params) {
        return this.eventNotificationRepository.getNotifications(params);
    }

    @Override
    public EventNotification getNotificationById(int id) {
        return this.eventNotificationRepository.getNotificationById(id);
    }

    @Override
    public EventNotification addOrUpdateNotification(EventNotification notification) {
        return this.eventNotificationRepository.addOrUpdateNotification(notification);
    }

    @Override
    public void deleteNotification(int id) {
        this.eventNotificationRepository.deleteNotification(id);
    }

    @Override
    public List<EventNotification> getNotificationsForUser(int userId, Map<String, String> params) {
        return eventNotificationRepository.getNotificationsForUser(userId, params);
    }

    @Override
    public long countNotis() {
        return this.eventNotificationRepository.countNotis();
    }
}
