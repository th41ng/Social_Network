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
import java.util.Map;

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
        long postCount = postRepo.countPosts(Map.of());
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

        // --- MONTHLY ---
        YearMonth previousMonthPeriod = YearMonth.from(now).minusMonths(1); // Kỳ tháng trước
        int prevMonthValue = previousMonthPeriod.getMonthValue();
        int prevMonthYear = previousMonthPeriod.getYear();

        // Luôn tính toán dữ liệu cho kỳ tháng trước
        LocalDateTime startOfPrevMonth = previousMonthPeriod.atDay(1).atStartOfDay();
        // Ngày đầu tiên của tháng hiện tại (để làm mốc cuối cho tháng trước)
        LocalDateTime endOfPrevMonthExclusive = YearMonth.from(now).atDay(1).atStartOfDay();

        Long newUsersLastMonth = statsRepo.sumNewUsersByDateRange(startOfPrevMonth, endOfPrevMonthExclusive);
        Long newPostsLastMonth = statsRepo.sumNewPostsByDateRange(startOfPrevMonth, endOfPrevMonthExclusive);

        if (newUsersLastMonth != null && newPostsLastMonth != null) {
            PeriodicSummaryStats monthlySummary = periodicStatsRepo.findByPeriod(prevMonthYear, prevMonthValue, null, PeriodType.monthly);

            String actionType = "UPDATING";
            if (monthlySummary == null) {
                monthlySummary = new PeriodicSummaryStats();
                monthlySummary.setSummaryYear(prevMonthYear);
                monthlySummary.setSummaryMonth(prevMonthValue);
                monthlySummary.setSummaryQuarter(null); // Cho monthly thì quarter là null
                monthlySummary.setPeriodType(PeriodType.monthly);
                actionType = "CREATING";
            }

            monthlySummary.setNewUsersCount(newUsersLastMonth.intValue());
            monthlySummary.setNewPostsCount(newPostsLastMonth.intValue());
            monthlySummary.setCalculatedAt(now); // Luôn cập nhật thời điểm tính toán

            periodicStatsRepo.save(monthlySummary);
            logger.info("{} monthly summary for {}: Users={}, Posts={}", actionType, previousMonthPeriod, newUsersLastMonth, newPostsLastMonth);

        } else {
            logger.warn("No daily data available to generate/update monthly summary for {}", previousMonthPeriod);
        }

        // --- QUARTERLY ---
        LocalDate today = LocalDate.now();
        int currentQuarterValue = today.get(java.time.temporal.IsoFields.QUARTER_OF_YEAR);
        int currentYearForQuarter = today.getYear();

        // Xác định quý trước đó
        int prevQuarterValue;
        int prevQuarterYear;
        if (currentQuarterValue == 1) {
            prevQuarterValue = 4;
            prevQuarterYear = currentYearForQuarter - 1;
        } else {
            prevQuarterValue = currentQuarterValue - 1;
            prevQuarterYear = currentYearForQuarter;
        }
        String prevQuarterId = "Q" + prevQuarterValue + "/" + prevQuarterYear;

        // Tính toán dữ liệu cho quý trước
        LocalDate startOfPrevQuarterDate = LocalDate.of(prevQuarterYear, (prevQuarterValue - 1) * 3 + 1, 1);
        // Ngày đầu tiên của quý hiện tại (làm mốc cuối cho quý trước)
        LocalDate startOfCurrentQuarterDate = LocalDate.of(currentYearForQuarter, (currentQuarterValue - 1) * 3 + 1, 1);

        LocalDateTime startOfPrevQuarterTime = startOfPrevQuarterDate.atStartOfDay();
        LocalDateTime endOfPrevQuarterTimeExclusive = startOfCurrentQuarterDate.atStartOfDay();

        Long newUsersLastQuarter = statsRepo.sumNewUsersByDateRange(startOfPrevQuarterTime, endOfPrevQuarterTimeExclusive);
        Long newPostsLastQuarter = statsRepo.sumNewPostsByDateRange(startOfPrevQuarterTime, endOfPrevQuarterTimeExclusive);

        if (newUsersLastQuarter != null && newPostsLastQuarter != null) {
            PeriodicSummaryStats quarterlySummary = periodicStatsRepo.findByPeriod(prevQuarterYear, null, prevQuarterValue, PeriodType.quarterly);
            String actionType = "UPDATING";
            if (quarterlySummary == null) {
                quarterlySummary = new PeriodicSummaryStats();
                quarterlySummary.setSummaryYear(prevQuarterYear);
                quarterlySummary.setSummaryMonth(null); // Cho quarterly thì month là null
                quarterlySummary.setSummaryQuarter(prevQuarterValue);
                quarterlySummary.setPeriodType(PeriodType.quarterly);
                actionType = "CREATING";
            }

            quarterlySummary.setNewUsersCount(newUsersLastQuarter.intValue());
            quarterlySummary.setNewPostsCount(newPostsLastQuarter.intValue());
            quarterlySummary.setCalculatedAt(now);

            periodicStatsRepo.save(quarterlySummary);
            logger.info("{} quarterly summary for {}: Users={}, Posts={}", actionType, prevQuarterId, newUsersLastQuarter, newPostsLastQuarter);
        } else {
            logger.warn("No daily data available to generate/update quarterly summary for {}", prevQuarterId);
        }


        // --- YEARLY ---
        int prevYearValue = now.getYear() - 1; // Năm trước đó

        // Tính toán dữ liệu cho năm trước
        LocalDateTime startOfPrevYear = LocalDateTime.of(prevYearValue, 1, 1, 0, 0);
        // Ngày đầu tiên của năm hiện tại (làm mốc cuối cho năm trước)
        LocalDateTime endOfPrevYearExclusive = LocalDateTime.of(now.getYear(), 1, 1, 0, 0);

        Long newUsersLastYear = statsRepo.sumNewUsersByDateRange(startOfPrevYear, endOfPrevYearExclusive);
        Long newPostsLastYear = statsRepo.sumNewPostsByDateRange(startOfPrevYear, endOfPrevYearExclusive);

        if (newUsersLastYear != null && newPostsLastYear != null) {
            PeriodicSummaryStats yearlySummary = periodicStatsRepo.findByPeriod(prevYearValue, null, null, PeriodType.yearly);
            String actionType = "UPDATING";
            if (yearlySummary == null) {
                yearlySummary = new PeriodicSummaryStats();
                yearlySummary.setSummaryYear(prevYearValue);
                yearlySummary.setSummaryMonth(null);
                yearlySummary.setSummaryQuarter(null);
                yearlySummary.setPeriodType(PeriodType.yearly);
                actionType = "CREATING";
            }

            yearlySummary.setNewUsersCount(newUsersLastYear.intValue());
            yearlySummary.setNewPostsCount(newPostsLastYear.intValue());
            yearlySummary.setCalculatedAt(now);

            periodicStatsRepo.save(yearlySummary);
            logger.info("{} yearly summary for {}: Users={}, Posts={}", actionType, prevYearValue, newUsersLastYear, newPostsLastYear);
        } else {
            logger.warn("No daily data available to generate/update yearly summary for {}", prevYearValue);
        }
    }
}