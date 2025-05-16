package com.socialapp.repository;

import com.socialapp.pojo.SurveyOption;

public interface SurveyOptionRepository {

    // Thêm lựa chọn mới vào cơ sở dữ liệu
    SurveyOption addSurveyOption(SurveyOption option);

    // Cập nhật lựa chọn trắc nghiệm
    SurveyOption updateSurveyOption(SurveyOption option);

    // Xóa lựa chọn theo ID
    void deleteSurveyOption(int optionId);

    // Lấy lựa chọn theo ID
    SurveyOption getSurveyOptionById(int optionId);
}
