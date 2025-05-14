/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.schedule;
import com.socialapp.service.PlatformStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
/**
 *
 * @author DELL G15
 */
@Component
public class StatisticsScheduler {

    @Autowired
    private PlatformStatsService platformStatsService;

    // Chạy lúc 1:00 sáng mỗi ngày
    @Scheduled(cron = "0 0 1 * * *")
    public void runDailySummary() {
        platformStatsService.generateDailySummary();
    }
}