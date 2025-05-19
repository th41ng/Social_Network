package com.socialapp.dto;

import java.util.List;

public class SurveyResponseSubmitDTO {
    // surveyId sẽ được lấy từ path variable của API endpoint
    private List<SurveyResponseItemDTO> responses;

    // Getters and Setters
    public List<SurveyResponseItemDTO> getResponses() {
        return responses;
    }

    public void setResponses(List<SurveyResponseItemDTO> responses) {
        this.responses = responses;
    }
}