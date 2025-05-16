package com.socialapp.repository;

import com.socialapp.pojo.DailyPlatformSummary;
import java.time.LocalDateTime;
import java.util.List;

public interface PlatformStatsRepository {

    void createDailySummary(DailyPlatformSummary summary);

    boolean isSummaryExistsForDate(LocalDateTime dateTime);

    List<DailyPlatformSummary> getAllSummaries();

    DailyPlatformSummary getSummaryByDate(LocalDateTime summaryDate);

    void updateDailySummary(DailyPlatformSummary summary);

    List<DailyPlatformSummary> getAllSummariesOrderByDateDesc();

    Long sumNewUsersByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Long sumNewPostsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
