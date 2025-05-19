package com.socialapp.dto;

public class SurveyOptionClientDTO {
    private Integer optionId;
    private String optionText;

    // Constructors, Getters and Setters
    public SurveyOptionClientDTO() {}

    public SurveyOptionClientDTO(Integer optionId, String optionText) {
        this.optionId = optionId;
        this.optionText = optionText;
    }

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }
}