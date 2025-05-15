/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.repository.impl;

import com.socialapp.pojo.Survey;
import com.socialapp.repository.SurveyRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author DELL G15
 */
@Repository
@Transactional
public class SurveyRepositoryImpl implements SurveyRepository {

    private static final int PAGE_SIZE = 6;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Survey> getSurveys(Map<String, String> params) {
        Session s = factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Survey> q = b.createQuery(Survey.class);
        Root<Survey> root = q.from(Survey.class);
        q.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();

            // Tìm theo từ khóa tiêu đề
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                predicates.add(b.like(root.get("title"), "%" + kw + "%"));
            }

            // Lọc theo adminId
            String adminId = params.get("adminId");
            if (adminId != null && !adminId.isEmpty()) {
                predicates.add(b.equal(root.get("adminId").get("userId"), Integer.parseInt(adminId)));
            }

            // Lọc theo ngày tạo
            String fromDate = params.get("fromDate");
            if (fromDate != null && !fromDate.isEmpty()) {
                predicates.add(b.greaterThanOrEqualTo(root.get("createdAt"), java.sql.Timestamp.valueOf(fromDate + " 00:00:00")));
            }

            String toDate = params.get("toDate");
            if (toDate != null && !toDate.isEmpty()) {
                predicates.add(b.lessThanOrEqualTo(root.get("createdAt"), java.sql.Timestamp.valueOf(toDate + " 23:59:59")));
            }

            q.where(predicates.toArray(Predicate[]::new));

            // Sắp xếp
            String orderBy = params.get("orderBy");
            if (orderBy != null && !orderBy.isEmpty()) {
                q.orderBy(b.desc(root.get(orderBy)));
            } else {
                q.orderBy(b.desc(root.get("createdAt")));
            }
        }

        var query = s.createQuery(q);

        // Phân trang
        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            query.setMaxResults(PAGE_SIZE);
            query.setFirstResult((page - 1) * PAGE_SIZE);
        }

        return query.getResultList();
    }

    @Override
    public Survey getSurveyById(int id) {
        Session s = factory.getObject().getCurrentSession();
        return s.get(Survey.class, id);
    }

    @Override
    public Survey addOrUpdateSurvey(Survey s) {
        Session session = factory.getObject().getCurrentSession();
        if (s.getSurveyId() == null) {
            session.persist(s);
        } else {
            session.merge(s);
        }
        return s;
    }
    
    @Override
    public void deleteSurvey(int id) {
        Session session = factory.getObject().getCurrentSession();
        Survey s = getSurveyById(id);
        if (s != null) {
            session.remove(s);
        } else {
            throw new IllegalArgumentException("Survey not found with ID: " + id);
        }
    }
}
