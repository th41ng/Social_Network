/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.pojo;

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
    private int summaryId;

    private int summaryYear;
    private Integer summaryQuarter;
    private Integer summaryMonth;

    @Enumerated(EnumType.STRING)
    private PeriodType periodType;

    private int newPostsCount;
    private int newUsersCount;

    private LocalDateTime calculatedAt;

    // Enum for period type
    public enum PeriodType {
        yearly, quarterly, monthly
    }

    /**
     * @return the summaryId
     */
    public int getSummaryId() {
        return summaryId;
    }

    /**
     * @param summaryId the summaryId to set
     */
    public void setSummaryId(int summaryId) {
        this.summaryId = summaryId;
    }

    /**
     * @return the summaryYear
     */
    public int getSummaryYear() {
        return summaryYear;
    }

    /**
     * @param summaryYear the summaryYear to set
     */
    public void setSummaryYear(int summaryYear) {
        this.summaryYear = summaryYear;
    }

    /**
     * @return the summaryQuarter
     */
    public Integer getSummaryQuarter() {
        return summaryQuarter;
    }

    /**
     * @param summaryQuarter the summaryQuarter to set
     */
    public void setSummaryQuarter(Integer summaryQuarter) {
        this.summaryQuarter = summaryQuarter;
    }

    /**
     * @return the summaryMonth
     */
    public Integer getSummaryMonth() {
        return summaryMonth;
    }

    /**
     * @param summaryMonth the summaryMonth to set
     */
    public void setSummaryMonth(Integer summaryMonth) {
        this.summaryMonth = summaryMonth;
    }

    /**
     * @return the periodType
     */
    public PeriodType getPeriodType() {
        return periodType;
    }

    /**
     * @param periodType the periodType to set
     */
    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    /**
     * @return the newPostsCount
     */
    public int getNewPostsCount() {
        return newPostsCount;
    }

    /**
     * @param newPostsCount the newPostsCount to set
     */
    public void setNewPostsCount(int newPostsCount) {
        this.newPostsCount = newPostsCount;
    }

    /**
     * @return the newUsersCount
     */
    public int getNewUsersCount() {
        return newUsersCount;
    }

    /**
     * @param newUsersCount the newUsersCount to set
     */
    public void setNewUsersCount(int newUsersCount) {
        this.newUsersCount = newUsersCount;
    }

    /**
     * @return the calculatedAt
     */
    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    /**
     * @param calculatedAt the calculatedAt to set
     */
    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

}
