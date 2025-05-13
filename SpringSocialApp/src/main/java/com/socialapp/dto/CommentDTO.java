package com.socialapp.dto;

import java.util.Map;

public class CommentDTO {
    private Integer commentId;
    private String content;
    private String userFullName;
    private String userAvatar;

    // Add a field to store reactions count
    private Map<String, Long> reactions;

    // Constructor
    public CommentDTO(Integer commentId, String content, String userFullName, String userAvatar) {
        this.commentId = commentId;
        this.content = content;
        this.userFullName = userFullName;
        this.userAvatar = userAvatar;
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
}
