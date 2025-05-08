package com.socialapp.pojo;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "survey_responses")
@NamedQueries({
    @NamedQuery(name = "SurveyResponse.findAll", query = "SELECT sr FROM SurveyResponse sr"),
    @NamedQuery(name = "SurveyResponse.findById", query = "SELECT sr FROM SurveyResponse sr WHERE sr.responseId = :responseId"),
    @NamedQuery(name = "SurveyResponse.findBySurveyId", query = "SELECT sr FROM SurveyResponse sr WHERE sr.surveyId = :surveyId"),
    @NamedQuery(name = "SurveyResponse.findByUserId", query = "SELECT sr FROM SurveyResponse sr WHERE sr.userId = :userId")
})
public class SurveyResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "response_id")
    private Integer responseId;

    @JoinColumn(name = "survey_id", referencedColumnName = "survey_id")
    @ManyToOne(optional = false)
    private Survey surveyId;

    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private User userId;

    @JoinColumn(name = "question_id", referencedColumnName = "question_id")
    @ManyToOne(optional = false)
    private SurveyQuestion questionId;

    @Column(name = "option_id")
    private Integer optionId;

    @Column(name = "response_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date responseAt;

    // Thêm cột để lưu câu trả lời tự luận
    @Column(name = "response_text")
    private String responseText;

    public SurveyResponse() {
    }

    public SurveyResponse(Integer responseId) {
        this.responseId = responseId;
    }

    public SurveyResponse(Integer responseId, Survey surveyId, User userId, SurveyQuestion questionId, Integer optionId, Date responseAt, String responseText) {
        this.responseId = responseId;
        this.surveyId = surveyId;
        this.userId = userId;
        this.questionId = questionId;
        this.optionId = optionId;
        this.responseAt = responseAt;
        this.responseText = responseText; 
    }

    
    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

   
    public Integer getResponseId() {
        return responseId;
    }

    public void setResponseId(Integer responseId) {
        this.responseId = responseId;
    }

    public Survey getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Survey surveyId) {
        this.surveyId = surveyId;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public SurveyQuestion getQuestionId() {
        return questionId;
    }

    public void setQuestionId(SurveyQuestion questionId) {
        this.questionId = questionId;
    }

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }

    public Date getResponseAt() {
        return responseAt;
    }

    public void setResponseAt(Date responseAt) {
        this.responseAt = responseAt;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (responseId != null ? responseId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SurveyResponse)) {
            return false;
        }
        SurveyResponse other = (SurveyResponse) object;
        return (this.responseId != null || other.responseId == null) && (this.responseId == null || this.responseId.equals(other.responseId));
    }

    @Override
    public String toString() {
        return "com.socialapp.pojo.SurveyResponse[ responseId=" + responseId + " ]";
    }
}
