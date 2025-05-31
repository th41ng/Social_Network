package com.socialapp.dto;

import java.util.Date;

public class EventDTO {
    private Integer eventId;
    private Integer adminId;
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private String location;

    // Nếu cần thông tin notification kèm theo thì có thể thêm field notificationId hoặc EventNotificationDTO

    public EventDTO() {
    }

    // Constructor đầy đủ
    public EventDTO(Integer eventId, Integer adminId, String title, String description, Date startDate, Date endDate, String location) {
        this.eventId = eventId;
        this.adminId = adminId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
    }

    // getter/setter
    public Integer getEventId() {
        return eventId;
    }
    public void setEventId(Integer eventId) {
        this.eventId = eventId;
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
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
}
