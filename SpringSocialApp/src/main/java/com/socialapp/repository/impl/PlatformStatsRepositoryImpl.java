package com.socialapp.repository.impl;

import com.socialapp.pojo.DailyPlatformSummary;
import com.socialapp.repository.PlatformStatsRepository;
import java.time.LocalDateTime;
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

    private Session getCurrentSession() {

        return sessionFactory.getCurrentSession();
    }

    @Override
    public void createDailySummary(DailyPlatformSummary summary) {
        Session session = sessionFactory.getCurrentSession();
        session.save(summary);
    }

    @Override
    public boolean isSummaryExistsForDate(LocalDateTime dateTime) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT COUNT(s.id) FROM DailyPlatformSummary s WHERE s.summaryDate = :date";
        Query<Long> query = session.createQuery(hql, Long.class);

        query.setParameter("date", dateTime);

        Long count = query.uniqueResult();
        return count != null && count > 0;
    }

    @Override
    public List<DailyPlatformSummary> getAllSummaries() {
        Session session = sessionFactory.getCurrentSession();
        Query<DailyPlatformSummary> query = session.createQuery(
                "FROM DailyPlatformSummary ORDER BY summaryDate DESC", DailyPlatformSummary.class);
        return query.getResultList();
    }

    @Override
    public DailyPlatformSummary getSummaryByDate(LocalDateTime summaryDate) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM DailyPlatformSummary s WHERE s.summaryDate = :date";
        Query<DailyPlatformSummary> query = session.createQuery(hql, DailyPlatformSummary.class);

        query.setParameter("date", summaryDate);

        return query.uniqueResult();
    }

    @Override
    public void updateDailySummary(DailyPlatformSummary summary) {
        Session session = sessionFactory.getCurrentSession();
        session.update(summary);
    }

    @Override
    public List<DailyPlatformSummary> getAllSummariesOrderByDateDesc() {
        Session session = getCurrentSession();
        String hql = "FROM DailyPlatformSummary ORDER BY lastCalculatedAt DESC, summaryDate DESC";
        Query<DailyPlatformSummary> query = session.createQuery(hql, DailyPlatformSummary.class);
        return query.getResultList();
    }

    @Override
    public Long sumNewUsersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        Session session = getCurrentSession();
        String hql = "SELECT SUM(s.newUsersRegisteredToday) FROM DailyPlatformSummary s "
                + "WHERE s.summaryDate >= :startDate AND s.summaryDate < :endDate";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        Long result = query.uniqueResult();
        return result != null ? result : 0L;
    }

    @Override
    public Long sumNewPostsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        Session session = getCurrentSession();
        String hql = "SELECT SUM(s.newPostsCreatedToday) FROM DailyPlatformSummary s "
                + "WHERE s.summaryDate >= :startDate AND s.summaryDate < :endDate";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        Long result = query.uniqueResult();
        return result != null ? result : 0L;
    }
}
