package com.socialapp.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_platform_summary")
public class DailyPlatformSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "summary_date")
    private LocalDateTime summaryDate;

    @Column(name = "total_users")
    private Long totalUsers;

    @Column(name = "total_posts")
    private Long totalPosts;

    @Column(name = "new_users_registered_today")
    private int newUsersRegisteredToday;

    @Column(name = "new_posts_created_today")
    private int newPostsCreatedToday;

    @Column(name = "last_calculated_at")
    private LocalDateTime lastCalculatedAt;

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getSummaryDate() {
        return summaryDate;
    }

    public void setSummaryDate(LocalDateTime summaryDate) {
        this.summaryDate = summaryDate;
    }

    public long getTotalUsers() {
        return (this.totalUsers != null) ? this.totalUsers.longValue() : 0L; // Trả về 0 nếu null
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(Long totalPosts) {
        this.totalPosts = totalPosts;
    }

    public int getNewUsersRegisteredToday() {
        return newUsersRegisteredToday;
    }

    public void setNewUsersRegisteredToday(int newUsersRegisteredToday) {
        this.newUsersRegisteredToday = newUsersRegisteredToday;
    }

    public int getNewPostsCreatedToday() {
        return newPostsCreatedToday;
    }

    public void setNewPostsCreatedToday(int newPostsCreatedToday) {
        this.newPostsCreatedToday = newPostsCreatedToday;
    }

    public LocalDateTime getLastCalculatedAt() {
        return lastCalculatedAt;
    }

    public void setLastCalculatedAt(LocalDateTime lastCalculatedAt) {
        this.lastCalculatedAt = lastCalculatedAt;
    }
}
