package com.socialapp.schedule;

import com.socialapp.service.PlatformStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class StatisticsScheduler {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsScheduler.class);

    @Autowired
    private PlatformStatsService platformStatsService;

    // Tác vụ này tạo thống kê hàng ngày, chạy lúc 1 giờ sáng mỗi ngày
    @Scheduled(cron = "0 0 1 * * *")
    public void runDailySummaryTask() {
        logger.info("Scheduler: Starting daily summary generation...");
        platformStatsService.generateDailySummary();
        logger.info("Scheduler: Finished daily summary generation.");
    }

    // Tác vụ này tạo/cập nhật thống kê theo chu kỳ (tháng, quý, năm)
    // Chạy vào 02:00:00 sáng, ngày 1 hàng tháng
    @Scheduled(cron = "0 0 2 1 * *")
    public void runPeriodicSummariesTask() {
        logger.info("Scheduler: Starting periodic summaries generation (Monthly/Quarterly/Yearly)...");
        platformStatsService.generatePeriodicSummaries();
        logger.info("Scheduler: Finished periodic summaries generation (Monthly/Quarterly/Yearly).");
    }
}