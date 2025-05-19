package com.socialapp.dto;

import java.util.List;

public class SurveyResponseItemDTO {
    private Integer questionId;
    private Integer selectedOptionId; // Cho câu hỏi SINGLE_CHOICE
    private List<Integer> selectedOptionIds; // Cho câu hỏi MULTIPLE_CHOICE
    private String responseText; // Cho câu hỏi TEXT_INPUT

    // Getters and Setters
    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(Integer selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }

    public List<Integer> getSelectedOptionIds() {
        return selectedOptionIds;
    }

    public void setSelectedOptionIds(List<Integer> selectedOptionIds) {
        this.selectedOptionIds = selectedOptionIds;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }
}