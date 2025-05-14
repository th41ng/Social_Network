package com.socialapp.service.impl;

import com.socialapp.pojo.DailyPlatformSummary;
import com.socialapp.repository.PlatformStatsRepository;
import com.socialapp.repository.PostRepository;
import com.socialapp.repository.UserRepository;
import com.socialapp.service.PlatformStatsService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlatformStatsServiceImpl implements PlatformStatsService {

    @Autowired
    private PlatformStatsRepository statsRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PostRepository postRepo;

    @Transactional
    @Override
    public void generateDailySummary() {
        Date today = new Date();

        // Kiểm tra xem đã có tóm tắt cho ngày hôm nay chưa
        if (statsRepo.isSummaryExistsForDate(today)) {
            return; // Nếu đã có, không tạo lại
        }

        // Tổng số người dùng và bài viết
        long userCount = userRepo.countUsers();
        long postCount = postRepo.countPosts();

        // Người dùng mới và bài viết mới hôm nay
        int newUsersToday = userRepo.countUsersRegisteredToday();   // TODO: cần triển khai trong UserRepository
        int newPostsToday = postRepo.countPostsCreatedToday();      // TODO: cần triển khai trong PostRepository

        // Tạo đối tượng tóm tắt
        DailyPlatformSummary summary = new DailyPlatformSummary();
        summary.setSummaryDate(today);
        summary.setTotalUsers(userCount);
        summary.setTotalPosts(postCount);
        summary.setNewUsersRegisteredToday(newUsersToday);
        summary.setNewPostsCreatedToday(newPostsToday);
        summary.setLastCalculatedAt(today);

        // Lưu vào CSDL
        statsRepo.createDailySummary(summary);
    }

    @Override
    public List<DailyPlatformSummary> getAllSummaries() {
        return this.statsRepo.getAllSummaries();
    }
}
