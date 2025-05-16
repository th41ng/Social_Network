package com.socialapp.repository.impl;

import com.socialapp.pojo.PeriodicSummaryStats;
// import com.socialapp.pojo.PeriodicSummaryStats.PeriodType; // Không cần cho phương thức này
import com.socialapp.repository.PeriodicStatsRepository;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PeriodicStatsRepositoryImpl implements PeriodicStatsRepository {

    private static final Logger logger = LoggerFactory.getLogger(PeriodicStatsRepositoryImpl.class); // Khởi tạo logger

    @Autowired
    private SessionFactory sessionFactory;

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<PeriodicSummaryStats> getAllPeriodicStats() {
        Session session = getCurrentSession();
        String hql = "FROM PeriodicSummaryStats ORDER BY summaryYear DESC, summaryMonth DESC";
        logger.info("Executing HQL for getAllPeriodicStats: {}", hql);

        Query<PeriodicSummaryStats> query = session.createQuery(hql, PeriodicSummaryStats.class);
        List<PeriodicSummaryStats> results = query.getResultList();

        logger.info("PeriodicStatsRepositoryImpl: getAllPeriodicStats() found {} records.", (results != null ? results.size() : "null"));
        if (results != null) {
            // Log chi tiết một vài bản ghi đầu tiên để kiểm tra (ví dụ 5 bản ghi)
            int count = 0;
            for (PeriodicSummaryStats stat : results) {
                logger.debug("Repo fetched stat: ID={}, Year={}, Month={}, Quarter={}, Type={}",
                        stat.getSummaryId(),
                        stat.getSummaryYear(),
                        stat.getSummaryMonth(),
                        stat.getSummaryQuarter(),
                        stat.getPeriodType());
                count++;
                if (count >= 5 && results.size() > 5) { 
                    logger.debug("Repo fetched stat: ... and {} more records.", results.size() - count);
                    break;
                }
            }
        }
        return results;
    }

    @Override
    public PeriodicSummaryStats findByPeriod(int year, Integer month, Integer quarter, PeriodicSummaryStats.PeriodType periodType) {
        Session session = getCurrentSession();
        StringBuilder hqlBuilder = new StringBuilder("FROM PeriodicSummaryStats s WHERE s.summaryYear = :year AND s.periodType = :periodType");

        if (periodType == PeriodicSummaryStats.PeriodType.monthly && month != null) {
            hqlBuilder.append(" AND s.summaryMonth = :month");
        } else if (periodType == PeriodicSummaryStats.PeriodType.quarterly && quarter != null) {
            hqlBuilder.append(" AND s.summaryQuarter = :quarter");
        }
        

        Query<PeriodicSummaryStats> query = session.createQuery(hqlBuilder.toString(), PeriodicSummaryStats.class);
        query.setParameter("year", year);
        query.setParameter("periodType", periodType);

        if (periodType == PeriodicSummaryStats.PeriodType.monthly && month != null) {
            query.setParameter("month", month);
        } else if (periodType == PeriodicSummaryStats.PeriodType.quarterly && quarter != null) {
            query.setParameter("quarter", quarter);
        }
        return query.uniqueResult();
    }

    @Override
    public void save(PeriodicSummaryStats summary) {
        getCurrentSession().saveOrUpdate(summary);
    }
}
