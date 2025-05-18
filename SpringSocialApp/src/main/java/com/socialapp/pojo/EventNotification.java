    package com.socialapp.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.Basic;
    import jakarta.persistence.Column;
    import jakarta.persistence.Entity;
    import jakarta.persistence.GeneratedValue;
    import jakarta.persistence.GenerationType;
    import jakarta.persistence.Id;
    import jakarta.persistence.JoinColumn;
    import jakarta.persistence.ManyToOne;
    import jakarta.persistence.OneToOne;
    import jakarta.persistence.Table;
    import jakarta.persistence.Temporal;
    import jakarta.persistence.TemporalType;
    import java.io.Serializable;
    import java.util.Date;

    @Entity
    @Table(name = "event_notifications")
    public class EventNotification implements Serializable {
        private static long serialVersionUID = 1L;

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Basic(optional = false)
        @Column(name = "notification_id")
        private Integer notificationId;

        @Column(name = "admin_id", insertable = false, updatable = false)
        private Integer adminId;

        @Basic(optional = false)
        @Column(name = "title")
        private String title;

        @Column(name = "event_id", insertable = false, updatable = false)
        private Integer eventId;

        @Column(name = "receiver_user_id", insertable = false, updatable = false)
        private Integer receiverUserId;

        @Column(name = "group_id", insertable = false, updatable = false)
        private Integer groupId;

        @Column(name = "content")
        private String content;

        @Column(name = "sent_at")
        @Temporal(TemporalType.TIMESTAMP)
        private Date sentAt;

        // Quan hệ
        @ManyToOne
        @JsonIgnore
        @JoinColumn(name = "admin_id", referencedColumnName = "user_id")
        private User admin;

        @ManyToOne
        @JsonIgnore
        @JoinColumn(name = "receiver_user_id", referencedColumnName = "user_id")
        private User receiverUser;

        @ManyToOne
        @JsonIgnore
        @JoinColumn(name = "group_id", referencedColumnName = "group_id")
        private UserGroups group;

        @OneToOne
        @JsonIgnore
        @JoinColumn(name = "event_id", referencedColumnName = "event_id")
        private Event event;


        // Các getter, setter khác

        public static long getSerialVersionUID() {
            return serialVersionUID;
        }

        public static void setSerialVersionUID(long aSerialVersionUID) {
            serialVersionUID = aSerialVersionUID;
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

        public User getAdmin() {
            return admin;
        }

        public void setAdmin(User admin) {
            this.admin = admin;
        }

        public User getReceiverUser() {
            return receiverUser;
        }

        public void setReceiverUser(User receiverUser) {
            this.receiverUser = receiverUser;
        }

        public UserGroups getGroup() {
            return group;
        }

        public void setGroup(UserGroups group) {
            this.group = group;
        }

        public Event getEvent() {
            return event;
        }

        public void setEvent(Event event) {
            this.event = event;
        }
    }
