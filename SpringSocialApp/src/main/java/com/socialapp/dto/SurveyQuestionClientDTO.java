package com.socialapp.dto;

import java.util.List;

public class SurveyQuestionClientDTO {
    private Integer questionId;
    private String questionText;
    private String questionType; // Ví dụ: "SINGLE_CHOICE", "MULTIPLE_CHOICE", "TEXT_INPUT"
    private List<SurveyOptionClientDTO> options; // Chỉ có giá trị cho câu hỏi dạng lựa chọn

    // Constructors, Getters and Setters
    public SurveyQuestionClientDTO() {}

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public List<SurveyOptionClientDTO> getOptions() {
        return options;
    }

    public void setOptions(List<SurveyOptionClientDTO> options) {
        this.options = options;
    }
}