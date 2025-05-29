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
import org.hibernate.query.Query; // Đảm bảo import đúng Query
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

    // PAGE_SIZE nên là static final hoặc lấy từ file properties
    public static final int PAGE_SIZE = 5; // Giữ nguyên hoặc di chuyển ra nơi cấu hình chung

    @Autowired
    private LocalSessionFactoryBean factory;

    private void applyCommonFilters(CriteriaBuilder b, Root<Survey> root, Map<String, String> params, List<Predicate> predicates) {
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
            try {
                 predicates.add(b.greaterThanOrEqualTo(root.get("createdAt"), java.sql.Timestamp.valueOf(fromDate + " 00:00:00")));
            } catch (IllegalArgumentException e) {
                // Xử lý lỗi nếu định dạng ngày không hợp lệ, ví dụ log hoặc bỏ qua filter này
                System.err.println("Invalid fromDate format: " + fromDate);
            }
        }

        String toDate = params.get("toDate");
        if (toDate != null && !toDate.isEmpty()) {
            try {
                predicates.add(b.lessThanOrEqualTo(root.get("createdAt"), java.sql.Timestamp.valueOf(toDate + " 23:59:59")));
            } catch (IllegalArgumentException e) {
                 // Xử lý lỗi nếu định dạng ngày không hợp lệ
                System.err.println("Invalid toDate format: " + toDate);
            }
        }
    }


    @Override
    public List<Survey> getSurveys(Map<String, String> params) {
        Session s = factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Survey> q = b.createQuery(Survey.class);
        Root<Survey> root = q.from(Survey.class);
        q.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();
            applyCommonFilters(b, root, params, predicates); // Sử dụng hàm chung
            q.where(predicates.toArray(Predicate[]::new));

            // Sắp xếp
            String orderBy = params.get("orderBy");
            if (orderBy != null && !orderBy.isEmpty()) {
                q.orderBy(b.desc(root.get(orderBy)));
            } else {
                q.orderBy(b.desc(root.get("createdAt"))); // Mặc định sắp xếp theo ngày tạo giảm dần
            }
        } else {
             q.orderBy(b.desc(root.get("createdAt"))); // Sắp xếp mặc định nếu không có params
        }


        Query<Survey> query = s.createQuery(q); // Sử dụng Query từ org.hibernate.query.Query

        // Phân trang
        if (params != null && params.containsKey("page")) {
            try {
                int page = Integer.parseInt(params.get("page"));
                if (page < 1) page = 1; // Đảm bảo page >= 1
                query.setMaxResults(PAGE_SIZE);
                query.setFirstResult((page - 1) * PAGE_SIZE);
            } catch (NumberFormatException e) {
                // Xử lý trường hợp "page" không phải là số, ví dụ, trả về trang đầu tiên
                 query.setMaxResults(PAGE_SIZE);
                 query.setFirstResult(0);
            }
        } else {
            // Mặc định hiển thị trang 1 nếu không có tham số page
            query.setMaxResults(PAGE_SIZE);
            query.setFirstResult(0);
        }
        return query.getResultList();
    }

    @Override
    public long countSurveys(Map<String, String> params) {
        Session s = factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Survey> root = q.from(Survey.class);
        q.select(b.count(root));

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();
            applyCommonFilters(b, root, params, predicates); // Sử dụng hàm chung
            q.where(predicates.toArray(Predicate[]::new));
        }
        
        Query<Long> query = s.createQuery(q);
        return query.getSingleResult();
    }

    @Override
    public Survey getSurveyById(int id) {
        Session s = factory.getObject().getCurrentSession();
        return s.get(Survey.class, id);
    }

    @Override
    public Survey addOrUpdateSurvey(Survey survey) { // Đổi tên biến s thành survey cho dễ hiểu
        Session session = factory.getObject().getCurrentSession();
        if (survey.getSurveyId() == null) {
            session.persist(survey);
        } else {
            session.merge(survey);
        }
        return survey;
    }
    
    @Override
    public void deleteSurvey(int id) {
        Session session = factory.getObject().getCurrentSession();
        Survey s = getSurveyById(id);
        if (s != null) {
            session.remove(s);
        } else {
            // Cân nhắc throw một exception cụ thể hơn hoặc log lỗi
            throw new jakarta.persistence.EntityNotFoundException("Survey not found with ID: " + id + " for deletion.");
        }
    }
}