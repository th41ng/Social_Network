package com.socialapp.controllers;

import com.socialapp.pojo.DailyPlatformSummary;
import com.socialapp.repository.PlatformStatsRepository;
import com.socialapp.service.CategoryService;
import com.socialapp.service.PlatformStatsService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@ControllerAdvice
@RequestMapping("/stats")
public class PlatformStatsController {

    @Autowired
    private PlatformStatsService statsService;

    @Autowired
    private PlatformStatsRepository statsRepo;

    @Autowired
    private CategoryService categoryService;

    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("categories", this.categoryService.getCategories());
    }

    @GetMapping
    public String listStats(Model model) {
        statsService.generateDailySummary(); // đảm bảo mỗi ngày có 1 dòng

        List<DailyPlatformSummary> stats = statsRepo.getAllSummaries();
        model.addAttribute("stats", stats);
        return "stats_management";
    }
}
