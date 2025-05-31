package com.socialapp.pojo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author DELL G15
 */
@Entity
@Table(name = "survey_questions")
@NamedQueries({
    @NamedQuery(name = "SurveyQuestion.findAll", query = "SELECT sq FROM SurveyQuestion sq"),
    @NamedQuery(name = "SurveyQuestion.findById", query = "SELECT sq FROM SurveyQuestion sq WHERE sq.questionId = :questionId"),})
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

    @JoinColumn(name = "type_id", referencedColumnName = "type_id")
    @ManyToOne(optional = false)
    private QuestionType typeId;

    @OneToMany(mappedBy = "questionId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)

    private List<SurveyOption> surveyOptions = new ArrayList<>();

    public SurveyQuestion() {

    }

    public SurveyQuestion(Integer questionId) {

        this.questionId = questionId;

    }

    public SurveyQuestion(Integer questionId, Survey surveyId, String questionText, Boolean isRequired, Integer questionOrder, QuestionType typeId) {

        this.questionId = questionId;
        this.surveyId = surveyId;
        this.questionText = questionText;
        this.isRequired = isRequired;
        this.questionOrder = questionOrder;
        this.typeId = typeId;

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

    public List<SurveyOption> getSurveyOptions() {
        return surveyOptions;
    }

    public void setSurveyOptions(List<SurveyOption> surveyOptions) {
        this.surveyOptions = surveyOptions;
    }

    public QuestionType getTypeId() {
        return typeId;
    }

    public void setTypeId(QuestionType typeId) {
        this.typeId = typeId;
    }

    @Override
    public int hashCode() {
        return (questionId != null ? questionId.hashCode() : 0);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        SurveyQuestion other = (SurveyQuestion) object;
        return Objects.equals(this.questionId, other.questionId);
    }

    @Override
    public String toString() {
        return "com.socialapp.pojo.SurveyQuestion[ questionId=" + questionId + " ]";
    }
}
