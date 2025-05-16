package com.socialapp.controllers;

import com.socialapp.pojo.DailyPlatformSummary;
import com.socialapp.pojo.PeriodicSummaryStats;
import com.socialapp.repository.PlatformStatsRepository;
import com.socialapp.service.CategoryService;
import com.socialapp.service.PlatformStatsService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@ControllerAdvice // Vẫn còn annotation này, xem xét lại nếu không cần thiết cho controller này
@RequestMapping("/stats")
public class PlatformStatsController {

    private static final Logger logger = LoggerFactory.getLogger(PlatformStatsController.class); // Đã có

    @Autowired
    private PlatformStatsService statsService; 

    
    @Autowired
    private CategoryService categoryService; // Giữ lại nếu cần

    @ModelAttribute
    public void commonAttributes(Model model) {
        logger.debug("Setting common attributes (categories)");
        model.addAttribute("categories", this.categoryService.getCategories());
    }

    @GetMapping
    public String listStats(Model model) {
        logger.info("====== BẮT ĐẦU PlatformStatsController.listStats() ======");
        System.out.println("!!!!!!!!!!!!!!!!!!!! PlatformStatsController.listStats() ĐÃ ĐƯỢC GỌI !!!!!!!!!!!!!!!!!!!!");
        logger.info("====== BẮT ĐẦU PlatformStatsController.listStats() ======");

        statsService.generateDailySummary(); 

        
        List<DailyPlatformSummary> stats = statsService.getAllSummaries(); // Gọi qua service đã có @Transactional

        model.addAttribute("stats", stats);
        logger.info("====== ĐÃ LẤY XONG dailyStats, số lượng: {} ======", (stats != null ? stats.size() : "null"));
        logger.info("====== Chuẩn bị lấy periodicStats ======");

        List<PeriodicSummaryStats> periodicStats = statsService.getAllPeriodicSummaries();         logger.info("====== ĐÃ GỌI statsService.getAllPeriodicSummaries() XONG ======");
        if (periodicStats != null) {
            logger.info("📊 Periodic stats fetched in controller: {}", periodicStats.size());
        } else {
            logger.info("📊 Periodic stats fetched in controller: null");
        }
        model.addAttribute("periodicStats", periodicStats);

        logger.info("====== KẾT THÚC PlatformStatsController.listStats() ======");
        return "stats_management";
    }
}
