package com.socialapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class CommentDTO {

    private Integer commentId;
    private String content;
    private String userFullName;
    private String userAvatar;

    // Add a field to store reactions count
    private Map<String, Long> reactions;
    private LocalDateTime createdAt;

    // Constructor
    public CommentDTO(Integer commentId, String content, String userFullName, String userAvatar) {
        this.commentId = commentId;
        this.content = content;
        this.userFullName = userFullName;
        this.userAvatar = userAvatar;
    }

    public CommentDTO() {
        this.reactions = new HashMap<>(); // Khởi tạo reactions để tránh NullPointerException
    }

    // Getters and Setters
    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public Map<String, Long> getReactions() {
        return reactions;
    }

    public void setReactions(Map<String, Long> reactions) {
        this.reactions = reactions;
    }

    /**
     * @return the createdAt
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // Rất quan trọng

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
