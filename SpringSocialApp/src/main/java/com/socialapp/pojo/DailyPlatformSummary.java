/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.socialapp.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 *
 * @author DELL G15
 */
@Entity
@Table(name = "daily_platform_summary")
public class DailyPlatformSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "summary_date")
    private Date summaryDate;

    @Column(name = "total_users")
    private Long totalUsers;

    @Column(name = "total_posts")
    private Long totalPosts;

    @Column(name = "new_users_registered_today")
    private int newUsersRegisteredToday;

    @Column(name = "new_posts_created_today")
    private int newPostsCreatedToday;

    @Column(name = "last_calculated_at")
    private Date lastCalculatedAt;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the summaryDate
     */
    public Date getSummaryDate() {
        return summaryDate;
    }

    /**
     * @param summaryDate the summaryDate to set
     */
    public void setSummaryDate(Date summaryDate) {
        this.summaryDate = summaryDate;
    }

    /**
     * @return the totalUsers
     */
    public long getTotalUsers() {
        return totalUsers;
    }

    /**
     * @param totalUsers the totalUsers to set
     */
    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    /**
     * @return the totalPosts
     */
    public long getTotalPosts() {
        return totalPosts;
    }

    /**
     * @param totalPosts the totalPosts to set
     */
    public void setTotalPosts(long totalPosts) {
        this.totalPosts = totalPosts;
    }

    /**
     * @return the newUsersRegisteredToday
     */
    public int getNewUsersRegisteredToday() {
        return newUsersRegisteredToday;
    }

    /**
     * @param newUsersRegisteredToday the newUsersRegisteredToday to set
     */
    public void setNewUsersRegisteredToday(int newUsersRegisteredToday) {
        this.newUsersRegisteredToday = newUsersRegisteredToday;
    }

    /**
     * @return the newPostsCreatedToday
     */
    public int getNewPostsCreatedToday() {
        return newPostsCreatedToday;
    }

    /**
     * @param newPostsCreatedToday the newPostsCreatedToday to set
     */
    public void setNewPostsCreatedToday(int newPostsCreatedToday) {
        this.newPostsCreatedToday = newPostsCreatedToday;
    }

    /**
     * @return the lastCalculatedAt
     */
    public Date getLastCalculatedAt() {
        return lastCalculatedAt;
    }

    /**
     * @param lastCalculatedAt the lastCalculatedAt to set
     */
    public void setLastCalculatedAt(Date lastCalculatedAt) {
        this.lastCalculatedAt = lastCalculatedAt;
    }

}
