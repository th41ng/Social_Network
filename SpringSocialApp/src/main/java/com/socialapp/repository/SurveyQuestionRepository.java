package com.socialapp.repository;

import com.socialapp.pojo.SurveyQuestion;
import java.util.List;

public interface SurveyQuestionRepository {

    // Lấy danh sách câu hỏi của khảo sát theo surveyId
    List<SurveyQuestion> getQuestionsBySurveyId(int surveyId);

    // Thêm câu hỏi mới vào cơ sở dữ liệu
    SurveyQuestion addSurveyQuestion(SurveyQuestion question);

    // Cập nhật câu hỏi
    SurveyQuestion updateSurveyQuestion(SurveyQuestion question);

    // Xóa câu hỏi theo ID
    void deleteSurveyQuestion(int questionId);

    // Lấy câu hỏi theo ID
    SurveyQuestion getSurveyQuestionById(int questionId);
}
