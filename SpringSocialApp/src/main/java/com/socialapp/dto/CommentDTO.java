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
    private Integer userId; 

    private Map<String, Long> reactions;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") 
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") 
    private LocalDateTime updatedAt; 

    
    public CommentDTO(Integer commentId, String content, String userFullName, String userAvatar, Integer userId) {
        this.commentId = commentId;
        this.content = content;
        this.userFullName = userFullName;
        this.userAvatar = userAvatar;
        this.userId = userId; 
        this.reactions = new HashMap<>(); 
    }

    // Constructor 
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

   
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
  

    public Map<String, Long> getReactions() {
        return reactions;
    }

    public void setReactions(Map<String, Long> reactions) {
        this.reactions = reactions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return the updatedAt
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt the updatedAt to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}