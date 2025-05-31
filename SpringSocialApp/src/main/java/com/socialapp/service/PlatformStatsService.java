/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.service;

import com.socialapp.pojo.DailyPlatformSummary;
import com.socialapp.pojo.PeriodicSummaryStats;
import java.util.List;

/**
 *
 * @author DELL G15
 */
public interface PlatformStatsService {

    void generateDailySummary();

    List<DailyPlatformSummary> getAllSummaries();

    void generatePeriodicSummaries();

    List<PeriodicSummaryStats> getAllPeriodicSummaries();
}
