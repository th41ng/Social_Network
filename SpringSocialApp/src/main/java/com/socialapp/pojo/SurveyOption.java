package com.socialapp.pojo; // Hoặc package tương ứng của bạn

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects; // QUAN TRỌNG: Thêm import này

/**
 *
 * @author DELL G15 (hoặc tên tác giả của bạn)
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
    private SurveyQuestion questionId; // Tham chiếu đến SurveyQuestion

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
        // Sử dụng getClass() != o.getClass() để xử lý đúng proxy của Hibernate nếu có.
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SurveyOption that = (SurveyOption) o;

        // Nếu optionId của đối tượng hiện tại là null (đối tượng mới, chưa được lưu),
        // nó không bằng bất kỳ đối tượng nào khác (trừ chính nó).
        // Điều này đảm bảo các đối tượng mới khác nhau sẽ không bị coi là bằng nhau bởi Set.
        if (this.optionId == null) {
            return false;
        }

        // Nếu optionId không null, so sánh dựa trên optionId.
        // that.optionId phải được kiểm tra để đảm bảo nó không null nếu this.optionId không null
        // và logic so sánh này được sử dụng.
        // Objects.equals() xử lý null an toàn.
        return Objects.equals(this.optionId, that.optionId);
    }

    @Override
    public int hashCode() {
        if (this.optionId != null) {
            // Nếu đã có ID, sử dụng hashCode của ID
            return this.optionId.hashCode();
        }
        // Nếu chưa có ID (đối tượng mới), tính hashCode dựa trên các trường nghiệp vụ.
        // Điều này an toàn vì SurveyQuestion.hashCode() không tạo vòng lặp.
        // Cần đảm bảo optionText và questionId (tham chiếu SurveyQuestion) đã được thiết lập
        // trước khi đối tượng này được thêm vào một Set hoặc Map.
        return Objects.hash(optionText, questionId);
    }

    @Override
    public String toString() {
        return "com.socialapp.pojo.SurveyOption[ optionId=" + optionId + " ]";
    }
}