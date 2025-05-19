package com.socialapp.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SurveyDetailClientDTO {
    private Integer surveyId;
    private String title;
    private String description;
    private LocalDateTime expiresAt;
    private List<SurveyQuestionClientDTO> questions;
    private Boolean canRespond; // Client có thể trả lời khảo sát này không? (Dựa trên status, expiry, đã trả lời chưa)

    // Constructors, Getters and Setters
    public SurveyDetailClientDTO() {}

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

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public List<SurveyQuestionClientDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SurveyQuestionClientDTO> questions) {
        this.questions = questions;
    }

    public Boolean getCanRespond() {
        return canRespond;
    }

    public void setCanRespond(Boolean canRespond) {
        this.canRespond = canRespond;
    }
}