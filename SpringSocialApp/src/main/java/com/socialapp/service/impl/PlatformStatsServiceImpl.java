package com.socialapp.service.impl;

import com.socialapp.pojo.DailyPlatformSummary;
import com.socialapp.pojo.PeriodicSummaryStats;
import com.socialapp.pojo.PeriodicSummaryStats.PeriodType;
import com.socialapp.repository.PeriodicStatsRepository;
import com.socialapp.repository.PlatformStatsRepository;
import com.socialapp.repository.PostRepository;
import com.socialapp.repository.UserRepository;
import com.socialapp.service.PlatformStatsService;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class PlatformStatsServiceImpl implements PlatformStatsService {

    private static final Logger logger = LoggerFactory.getLogger(PlatformStatsServiceImpl.class);

    @Autowired
    private PlatformStatsRepository statsRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private PeriodicStatsRepository periodicStatsRepo;

    @Override
    public void generateDailySummary() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.withHour(0).withMinute(0).withSecond(0).withNano(0);

        logger.info("Generating daily summary for date: {}", startOfDay.toLocalDate());

        DailyPlatformSummary summary = statsRepo.getSummaryByDate(startOfDay);

        long userCount = userRepo.countUsers();
        long postCount = postRepo.countPosts();
        int newUsersToday = userRepo.countUsersRegisteredToday();
        int newPostsToday = postRepo.countPostsCreatedToday();

        if (summary == null) {
            summary = new DailyPlatformSummary();
            summary.setSummaryDate(startOfDay);
            summary.setTotalUsers(userCount);
            summary.setTotalPosts(postCount);
            summary.setNewUsersRegisteredToday(newUsersToday);
            summary.setNewPostsCreatedToday(newPostsToday);
            summary.setLastCalculatedAt(now);

            statsRepo.createDailySummary(summary);
            logger.info("Created new daily summary for {}", startOfDay.toLocalDate());
        } else {
            summary.setTotalUsers(userCount);
            summary.setTotalPosts(postCount);
            summary.setNewUsersRegisteredToday(newUsersToday);
            summary.setNewPostsCreatedToday(newPostsToday);
            summary.setLastCalculatedAt(now);

            statsRepo.updateDailySummary(summary);
            logger.info("Updated daily summary for {}", startOfDay.toLocalDate());
        }
    }

    @Override
    public List<DailyPlatformSummary> getAllSummaries() {
        return statsRepo.getAllSummariesOrderByDateDesc();
    }

    @Override
    public List<PeriodicSummaryStats> getAllPeriodicSummaries() {
        List<PeriodicSummaryStats> list = periodicStatsRepo.getAllPeriodicStats();
        logger.info("📊 Periodic stats count: {}", list.size());
        return list;
    }

    @Override
    public void generatePeriodicSummaries() {
        LocalDateTime now = LocalDateTime.now();

        // MONTHLY
        YearMonth previousMonth = YearMonth.from(now).minusMonths(1);
        int prevMonthValue = previousMonth.getMonthValue();
        int prevMonthYear = previousMonth.getYear();

        PeriodicSummaryStats monthlySummary = periodicStatsRepo.findByPeriod(prevMonthYear, prevMonthValue, null, PeriodType.monthly);

        if (monthlySummary == null) {
            LocalDateTime startOfPrevMonth = previousMonth.atDay(1).atStartOfDay();
            LocalDateTime startOfCurrentMonth = YearMonth.from(now).atDay(1).atStartOfDay();

            Long newUsersLastMonth = statsRepo.sumNewUsersByDateRange(startOfPrevMonth, startOfCurrentMonth);
            Long newPostsLastMonth = statsRepo.sumNewPostsByDateRange(startOfPrevMonth, startOfCurrentMonth);

            if (newUsersLastMonth != null && newPostsLastMonth != null) {
                monthlySummary = new PeriodicSummaryStats();
                monthlySummary.setSummaryYear(prevMonthYear);
                monthlySummary.setSummaryMonth(prevMonthValue);
                monthlySummary.setSummaryQuarter(null);
                monthlySummary.setPeriodType(PeriodType.monthly);
                monthlySummary.setNewUsersCount(newUsersLastMonth.intValue());
                monthlySummary.setNewPostsCount(newPostsLastMonth.intValue());
                monthlySummary.setCalculatedAt(now);

                periodicStatsRepo.save(monthlySummary);
                logger.info("Generated monthly summary for {}", previousMonth);
            } else {
                logger.warn("No daily data available for monthly summary {}", previousMonth);
            }
        } else {
            logger.info("Monthly summary for {} already exists", previousMonth);
        }

        // QUARTERLY
        LocalDate today = LocalDate.now();
        int currentQuarterValue = today.get(java.time.temporal.IsoFields.QUARTER_OF_YEAR);
        int currentYear = today.getYear();

        int prevQuarterValue = currentQuarterValue == 1 ? 4 : currentQuarterValue - 1;
        int prevQuarterYear = currentQuarterValue == 1 ? currentYear - 1 : currentYear;

        PeriodicSummaryStats quarterlySummary = periodicStatsRepo.findByPeriod(prevQuarterYear, null, prevQuarterValue, PeriodType.quarterly);

        if (quarterlySummary == null) {
            LocalDate startOfPrevQuarter = LocalDate.of(prevQuarterYear, (prevQuarterValue - 1) * 3 + 1, 1);
            LocalDate startOfCurrentQuarter = LocalDate.of(currentYear, (currentQuarterValue - 1) * 3 + 1, 1);

            LocalDateTime startOfPrevQuarterTime = startOfPrevQuarter.atStartOfDay();
            LocalDateTime startOfCurrentQuarterTime = startOfCurrentQuarter.atStartOfDay();

            Long newUsersLastQuarter = statsRepo.sumNewUsersByDateRange(startOfPrevQuarterTime, startOfCurrentQuarterTime);
            Long newPostsLastQuarter = statsRepo.sumNewPostsByDateRange(startOfPrevQuarterTime, startOfCurrentQuarterTime);

            if (newUsersLastQuarter != null && newPostsLastQuarter != null) {
                quarterlySummary = new PeriodicSummaryStats();
                quarterlySummary.setSummaryYear(prevQuarterYear);
                quarterlySummary.setSummaryMonth(null);
                quarterlySummary.setSummaryQuarter(prevQuarterValue);
                quarterlySummary.setPeriodType(PeriodType.quarterly);
                quarterlySummary.setNewUsersCount(newUsersLastQuarter.intValue());
                quarterlySummary.setNewPostsCount(newPostsLastQuarter.intValue());
                quarterlySummary.setCalculatedAt(now);

                periodicStatsRepo.save(quarterlySummary);
                logger.info("Generated quarterly summary for Q{}/{}", prevQuarterValue, prevQuarterYear);
            } else {
                logger.warn("No daily data for quarterly summary Q{}/{}", prevQuarterValue, prevQuarterYear);
            }
        } else {
            logger.info("Quarterly summary for Q{}/{} already exists", prevQuarterValue, prevQuarterYear);
        }

        // YEARLY
        int prevYearValue = now.getYear() - 1;
        PeriodicSummaryStats yearlySummary = periodicStatsRepo.findByPeriod(prevYearValue, null, null, PeriodType.yearly);

        if (yearlySummary == null) {
            LocalDateTime startOfPrevYear = LocalDateTime.of(prevYearValue, 1, 1, 0, 0);
            LocalDateTime startOfCurrentYear = LocalDateTime.of(now.getYear(), 1, 1, 0, 0);

            Long newUsersLastYear = statsRepo.sumNewUsersByDateRange(startOfPrevYear, startOfCurrentYear);
            Long newPostsLastYear = statsRepo.sumNewPostsByDateRange(startOfPrevYear, startOfCurrentYear);

            if (newUsersLastYear != null && newPostsLastYear != null) {
                yearlySummary = new PeriodicSummaryStats();
                yearlySummary.setSummaryYear(prevYearValue);
                yearlySummary.setSummaryMonth(null);
                yearlySummary.setSummaryQuarter(null);
                yearlySummary.setPeriodType(PeriodType.yearly);
                yearlySummary.setNewUsersCount(newUsersLastYear.intValue());
                yearlySummary.setNewPostsCount(newPostsLastYear.intValue());
                yearlySummary.setCalculatedAt(now);

                periodicStatsRepo.save(yearlySummary);
                logger.info("Generated yearly summary for {}", prevYearValue);
            } else {
                logger.warn("No daily data for yearly summary {}", prevYearValue);
            }
        } else {
            logger.info("Yearly summary for {} already exists", prevYearValue);
        }
    }
}
