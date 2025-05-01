package com.socialapp.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

public class EventNotification implements Serializable{
    private static long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "notification_id")
    private Integer notificationId;

    @Basic(optional = false)
    @Column(name = "admin_id")
    private Integer adminId;

    @Basic(optional = false)
    @Column(name = "title")
    private String title;

    @Column(name = "event_id")
    private Integer eventId;

    @Column(name = "receiver_user_id")
    private Integer receiverUserId;

    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "content")
    private String content;

    @Column(name = "sent_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sentAt;

    //@Column(name = "is_deleted")
    //private boolean isDeleted = false;  // Trường isDeleted để đánh dấu xóa mềm

    // Quan hệ
    @ManyToOne
    @JoinColumn(name = "admin_id", referencedColumnName = "user_id")
    private User admin;

    @ManyToOne
    @JoinColumn(name = "receiver_user_id", referencedColumnName = "user_id")
    private User receiverUser;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "group_id")
    private UserGroups group;

    @OneToOne
    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    private Event event;


    // Các getter, setter khác

    /**
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * @param aSerialVersionUID the serialVersionUID to set
     */
    public static void setSerialVersionUID(long aSerialVersionUID) {
        serialVersionUID = aSerialVersionUID;
    }

    /**
     * @return the notificationId
     */
    public Integer getNotificationId() {
        return notificationId;
    }

    /**
     * @param notificationId the notificationId to set
     */
    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    /**
     * @return the adminId
     */
    public Integer getAdminId() {
        return adminId;
    }

    /**
     * @param adminId the adminId to set
     */
    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the eventId
     */
    public Integer getEventId() {
        return eventId;
    }

    /**
     * @param eventId the eventId to set
     */
    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    /**
     * @return the receiverUserId
     */
    public Integer getReceiverUserId() {
        return receiverUserId;
    }

    /**
     * @param receiverUserId the receiverUserId to set
     */
    public void setReceiverUserId(Integer receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    /**
     * @return the groupId
     */
    public Integer getGroupId() {
        return groupId;
    }

    /**
     * @param groupId the groupId to set
     */
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the sentAt
     */
    public Date getSentAt() {
        return sentAt;
    }

    /**
     * @param sentAt the sentAt to set
     */
    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

  

    /**
     * @return the admin
     */
    public User getAdmin() {
        return admin;
    }

    /**
     * @param admin the admin to set
     */
    public void setAdmin(User admin) {
        this.admin = admin;
    }

    /**
     * @return the receiverUser
     */
    public User getReceiverUser() {
        return receiverUser;
    }

    /**
     * @param receiverUser the receiverUser to set
     */
    public void setReceiverUser(User receiverUser) {
        this.receiverUser = receiverUser;
    }

    /**
     * @return the group
     */
    public UserGroups getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(UserGroups group) {
        this.group = group;
    }

    /**
     * @return the event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(Event event) {
        this.event = event;
    }
}
