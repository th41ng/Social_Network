/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.socialapp.repository;

import com.socialapp.pojo.PeriodicSummaryStats;
import com.socialapp.pojo.PeriodicSummaryStats.PeriodType;
import java.util.List;

/**
 *
 * @author DELL G15
 */
public interface PeriodicStatsRepository {

    List<PeriodicSummaryStats> getAllPeriodicStats();

    PeriodicSummaryStats findByPeriod(int year, Integer month, Integer quarter, PeriodType periodType);

    void save(PeriodicSummaryStats summary); // Thêm phương thức save
}
