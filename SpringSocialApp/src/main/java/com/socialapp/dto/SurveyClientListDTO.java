package com.socialapp.dto;

import java.time.LocalDateTime;

public class SurveyClientListDTO {

    private Integer surveyId;
    private String title;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Integer questionCount;
    private Boolean isRespondedByCurrentUser;
    private String status;

    public SurveyClientListDTO() {
    }

    public Integer getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId) {
        this.surveyId = surveyId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public Boolean getIsRespondedByCurrentUser() {
        return isRespondedByCurrentUser;
    }

    public void setIsRespondedByCurrentUser(Boolean respondedByCurrentUser) {
        isRespondedByCurrentUser = respondedByCurrentUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
