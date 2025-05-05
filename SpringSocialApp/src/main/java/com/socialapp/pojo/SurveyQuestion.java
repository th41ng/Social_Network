/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.pojo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author DELL G15
 */
@Entity
@Table(name = "survey_questions")
@NamedQueries({
    @NamedQuery(name = "SurveyQuestion.findAll", query = "SELECT sq FROM SurveyQuestion sq"),
    @NamedQuery(name = "SurveyQuestion.findById", query = "SELECT sq FROM SurveyQuestion sq WHERE sq.questionId = :questionId"),
    @NamedQuery(name = "SurveyQuestion.findBySurveyId", query = "SELECT sq FROM SurveyQuestion sq WHERE sq.surveyId = :surveyId")
})
public class SurveyQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "question_id")
    private Integer questionId;

    @JoinColumn(name = "survey_id", referencedColumnName = "survey_id")
    @ManyToOne(optional = false)
    private Survey surveyId;

    @Basic(optional = false)
    @Column(name = "question_text")
    private String questionText;

    @Column(name = "is_required")
    private Boolean isRequired;

    @Column(name = "question_order")
    private Integer questionOrder;

    @OneToMany(mappedBy = "questionId", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<SurveyOption> surveyOptions; // Hoặc List

    public SurveyQuestion() {
    }

    public SurveyQuestion(Integer questionId) {
        this.questionId = questionId;
    }

    public SurveyQuestion(Integer questionId, Survey surveyId, String questionText, Boolean isRequired, Integer questionOrder) {
        this.questionId = questionId;
        this.surveyId = surveyId;
        this.questionText = questionText;
        this.isRequired = isRequired;
        this.questionOrder = questionOrder;
    }

    // Getters and Setters
    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Survey getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Survey surveyId) {
        this.surveyId = surveyId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }

    public Set<SurveyOption> getSurveyOptions() {
     return surveyOptions;
}
    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (questionId != null ? questionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SurveyQuestion)) {
            return false;
        }
        SurveyQuestion other = (SurveyQuestion) object;
        return (this.questionId != null || other.questionId == null) && (this.questionId == null || this.questionId.equals(other.questionId));
    }

    @Override
    public String toString() {
        return "com.socialapp.pojo.SurveyQuestion[ questionId=" + questionId + " ]";
    }
}
