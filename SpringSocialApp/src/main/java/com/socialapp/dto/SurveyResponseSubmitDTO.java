package com.socialapp.dto;

import java.util.List;

public class SurveyResponseSubmitDTO {

    private List<SurveyResponseItemDTO> responses;

    public List<SurveyResponseItemDTO> getResponses() {
        return responses;
    }

    public void setResponses(List<SurveyResponseItemDTO> responses) {
        this.responses = responses;
    }
}
