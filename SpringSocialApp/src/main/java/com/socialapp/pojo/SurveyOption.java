package com.socialapp.pojo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SurveyOption that = (SurveyOption) o;

        if (this.optionId == null) {
            return false;
        }

        return Objects.equals(this.optionId, that.optionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(optionId);
    }

    @Override
    public String toString() {
        return "com.socialapp.pojo.SurveyOption[ optionId=" + optionId + " ]";
    }
}
