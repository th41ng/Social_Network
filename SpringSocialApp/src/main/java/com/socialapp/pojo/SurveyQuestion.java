package com.socialapp.pojo;

import jakarta.persistence.*;
import java.io.Serializable;
// THAY ĐỔI IMPORT: Từ Set/HashSet sang List/ArrayList
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
    @NamedQuery(name = "SurveyQuestion.findById", query = "SELECT sq FROM SurveyQuestion sq WHERE sq.questionId = :questionId"),
    // @NamedQuery(name = "SurveyQuestion.findBySurveyId", query = "SELECT sq FROM SurveyQuestion sq WHERE sq.surveyId = :surveyId")
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

    @JoinColumn(name = "type_id", referencedColumnName = "type_id")
    @ManyToOne(optional = false)
    private QuestionType typeId;

    // --- THAY ĐỔI QUAN TRỌNG Ở ĐÂY ---
    // Chuyển từ Set<SurveyOption> sang List<SurveyOption>
    // Khởi tạo bằng ArrayList để Spring MVC có thể "auto-grow" khi binding indexed parameters
    @OneToMany(mappedBy = "questionId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // @OrderColumn(name = "option_order_in_question") // TÙY CHỌN: Nếu bạn muốn duy trì thứ tự của các option trong DB
    private List<SurveyOption> surveyOptions = new ArrayList<>();

    public SurveyQuestion() {
        // surveyOptions đã được khởi tạo tại dòng khai báo trường ở trên.
        // Nếu bạn muốn, có thể tường minh khởi tạo ở đây:
        // if (this.surveyOptions == null) {
        //     this.surveyOptions = new ArrayList<>();
        // }
    }

    public SurveyQuestion(Integer questionId) {
        // this(); // Gọi constructor mặc định nếu bạn khởi tạo surveyOptions trong constructor mặc định
        this.questionId = questionId;
        // surveyOptions sẽ được khởi tạo từ khai báo trường.
    }

    public SurveyQuestion(Integer questionId, Survey surveyId, String questionText, Boolean isRequired, Integer questionOrder, QuestionType typeId) {
        // this(); // Gọi constructor mặc định
        this.questionId = questionId;
        this.surveyId = surveyId;
        this.questionText = questionText;
        this.isRequired = isRequired;
        this.questionOrder = questionOrder;
        this.typeId = typeId;
        // surveyOptions sẽ được khởi tạo từ khai báo trường.
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

    // --- CẬP NHẬT GETTER VÀ SETTER CHO surveyOptions ---
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