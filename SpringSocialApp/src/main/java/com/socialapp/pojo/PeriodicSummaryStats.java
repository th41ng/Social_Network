/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 *
 * @author DELL G15
 */
@Entity
@Table(name = "periodic_summary_stats")
public class PeriodicSummaryStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "summary_id")
    private int summaryId;

    @Column(name = "summary_year")     
    private int summaryYear;

    @Column(name = "summary_quarter")  
    private Integer summaryQuarter;

    @Column(name = "summary_month")     
    private Integer summaryMonth;

    @Enumerated(EnumType.STRING)
    @Column(name = "period_type")       
    private PeriodType periodType;

    @Column(name = "new_posts_count")   
    private int newPostsCount;

    @Column(name = "new_users_count")  
    private int newUsersCount;

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;

    
    public enum PeriodType {
        yearly, quarterly, monthly
    }

   
    public int getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(int summaryId) {
        this.summaryId = summaryId;
    }

    public int getSummaryYear() {
        return summaryYear;
    }

    public void setSummaryYear(int summaryYear) {
        this.summaryYear = summaryYear;
    }

    public Integer getSummaryQuarter() {
        return summaryQuarter;
    }

    public void setSummaryQuarter(Integer summaryQuarter) {
        this.summaryQuarter = summaryQuarter;
    }

    public Integer getSummaryMonth() {
        return summaryMonth;
    }

    public void setSummaryMonth(Integer summaryMonth) {
        this.summaryMonth = summaryMonth;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    public int getNewPostsCount() {
        return newPostsCount;
    }

    public void setNewPostsCount(int newPostsCount) {
        this.newPostsCount = newPostsCount;
    }

    public int getNewUsersCount() {
        return newUsersCount;
    }

    public void setNewUsersCount(int newUsersCount) {
        this.newUsersCount = newUsersCount;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
}