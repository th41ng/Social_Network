/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.pojo;
import jakarta.persistence.*;
import java.io.Serializable;
/**
 *
 * @author DELL G15
 */
@Entity
@Table(name = "survey_options")
@NamedQueries({
    @NamedQuery(name = "SurveyOption.findAll", query = "SELECT so FROM SurveyOption so"),
    @NamedQuery(name = "SurveyOption.findById", query = "SELECT so FROM SurveyOption so WHERE so.optionId = :optionId"),
    @NamedQuery(name = "SurveyOption.findByQuestionId", query = "SELECT so FROM SurveyOption so WHERE so.questionId = :questionId")
})
public class SurveyOption implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "option_id")
    private Integer optionId;

    @JoinColumn(name = "question_id", referencedColumnName = "question_id")
    @ManyToOne(optional = false)
    private SurveyQuestion questionId;

    @Basic(optional = false)
    @Column(name = "option_text")
    private String optionText;

    public SurveyOption() {
    }

    public SurveyOption(Integer optionId) {
        this.optionId = optionId;
    }

    public SurveyOption(Integer optionId, SurveyQuestion questionId, String optionText) {
        this.optionId = optionId;
        this.questionId = questionId;
        this.optionText = optionText;
    }

    // Getters and Setters
    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }

    public SurveyQuestion getQuestionId() {
        return questionId;
    }

    public void setQuestionId(SurveyQuestion questionId) {
        this.questionId = questionId;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (optionId != null ? optionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SurveyOption)) {
            return false;
        }
        SurveyOption other = (SurveyOption) object;
        return (this.optionId != null || other.optionId == null) && (this.optionId == null || this.optionId.equals(other.optionId));
    }

    @Override
    public String toString() {
        return "com.socialapp.pojo.SurveyOption[ optionId=" + optionId + " ]";
    }
}