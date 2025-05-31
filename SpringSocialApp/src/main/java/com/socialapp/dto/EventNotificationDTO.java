package com.socialapp.dto;

import java.util.Date;

public class EventNotificationDTO {
    private Integer notificationId;
    private Integer adminId;
    private String title;
    private Integer eventId;
    private Integer receiverUserId;
    private Integer groupId;
    private String content;
    private Date sentAt;

    public EventNotificationDTO() {
    }

    public EventNotificationDTO(Integer notificationId, Integer adminId, String title, Integer eventId,
                                Integer receiverUserId, Integer groupId, String content, Date sentAt) {
        this.notificationId = notificationId;
        this.adminId = adminId;
        this.title = title;
        this.eventId = eventId;
        this.receiverUserId = receiverUserId;
        this.groupId = groupId;
        this.content = content;
        this.sentAt = sentAt;
    }

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(Integer receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }
}
