package com.socialapp.repository.impl;

import com.socialapp.pojo.SurveyOption;
import com.socialapp.pojo.SurveyQuestion;
import com.socialapp.repository.SurveyQuestionRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class SurveyQuestionRepositoryImpl implements SurveyQuestionRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<SurveyQuestion> getQuestionsBySurveyId(int surveyId) {
        Session session = factory.getObject().getCurrentSession();
        String hql = "SELECT DISTINCT sq FROM SurveyQuestion sq "
                + "LEFT JOIN FETCH sq.surveyOptions "
                + "WHERE sq.surveyId.surveyId = :surveyId "
                + "ORDER BY sq.questionOrder ASC, sq.questionId ASC";
        return session.createQuery(hql, SurveyQuestion.class)
                .setParameter("surveyId", surveyId)
                .getResultList();
    }

    @Override
    public SurveyQuestion addSurveyQuestion(SurveyQuestion question) {
        Session session = factory.getObject().getCurrentSession();
        session.persist(question);
        return question;
    }

    @Override
    public SurveyQuestion updateSurveyQuestion(SurveyQuestion question) {
        Session session = factory.getObject().getCurrentSession();
        // Vòng lặp xử lý options ở đây có thể không cần thiết nữa nếu controller
        // đã chuẩn bị đầy đủ collection question.getSurveyOptions()
        // session.merge(question) với CascadeType.ALL và orphanRemoval=true
        // sẽ xử lý việc thêm mới, cập nhật và xóa options.
        /*
    for (SurveyOption option : question.getSurveyOptions()) {
        if (option.getOptionId() != null) { // Option đã tồn tại
            session.merge(option);
        } else { // Option mới
            option.setQuestionId(question); // Đảm bảo liên kết
            session.persist(option);
        }
    }
         */
        session.merge(question); // Cập nhật câu hỏi và cascade tới các options
        return question;
    }

    @Override
    public void deleteSurveyQuestion(int questionId) {
        Session session = factory.getObject().getCurrentSession();
        SurveyQuestion question = session.get(SurveyQuestion.class, questionId);
        if (question != null) {
            session.remove(question);
        } else {
            throw new IllegalArgumentException("SurveyQuestion not found with ID: " + questionId);
        }
    }

    @Override
    public SurveyQuestion getSurveyQuestionById(int questionId) {
        Session session = factory.getObject().getCurrentSession();
        String hql = "SELECT sq FROM SurveyQuestion sq "
                + "LEFT JOIN FETCH sq.surveyOptions "
                + "WHERE sq.questionId = :questionId";
        List<SurveyQuestion> list = session.createQuery(hql, SurveyQuestion.class)
                .setParameter("questionId", questionId)
                .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }
}
