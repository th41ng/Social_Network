package com.socialapp.repository.impl;

import com.socialapp.pojo.DailyPlatformSummary;
import com.socialapp.repository.PlatformStatsRepository;
import java.util.Date;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PlatformStatsRepositoryImpl implements PlatformStatsRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void createDailySummary(DailyPlatformSummary summary) {
        Session session = sessionFactory.getCurrentSession(); // Sử dụng session từ SessionFactory
        session.save(summary);
    }

    @Override
    public boolean isSummaryExistsForDate(Date date) {
        Session session = sessionFactory.getCurrentSession(); // Sử dụng session từ SessionFactory
        String hql = "SELECT COUNT(s.id) FROM DailyPlatformSummary s WHERE s.summaryDate = :date";
        Query query = session.createQuery(hql);
        query.setParameter("date", date);

        Long count = (Long) query.uniqueResult();
        return count != null && count > 0;
    }

    @Override
    public List<DailyPlatformSummary> getAllSummaries() {
        Session session = sessionFactory.getCurrentSession(); // Sử dụng session từ SessionFactory
        Query<DailyPlatformSummary> query = session.createQuery("FROM DailyPlatformSummary", DailyPlatformSummary.class);
        return query.getResultList();
    }
}
