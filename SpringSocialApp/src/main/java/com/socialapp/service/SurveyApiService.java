package com.socialapp.service;

// Các DTO mới cho client
import com.socialapp.dto.SurveyClientListDTO;
import com.socialapp.dto.SurveyDetailClientDTO;
// DTO cho submit response (giữ nguyên)
import com.socialapp.dto.SurveyResponseSubmitDTO;
import com.socialapp.pojo.User;
import java.util.List;
import java.util.Map;

public interface SurveyApiService {

    /**
     * Lấy danh sách các khảo sát đang hoạt động cho client.
     * @param params Các tham số truy vấn (ví dụ: phân trang).
     * @param currentUser Người dùng hiện tại (để kiểm tra đã trả lời chưa).
     * @return Danh sách các SurveyClientListDTO.
     */
    List<SurveyClientListDTO> getActiveSurveysForClient(Map<String, String> params, User currentUser);

    /**
     * Lấy chi tiết một khảo sát để client trả lời.
     * @param surveyId ID của khảo sát.
     * @param currentUser Người dùng hiện tại.
     * @return SurveyDetailClientDTO chứa thông tin chi tiết và câu hỏi.
     */
    SurveyDetailClientDTO getSurveyDetailsForClient(int surveyId, User currentUser);

    /**
     * Client gửi phản hồi cho một khảo sát.
     * @param surveyId ID của khảo sát.
     * @param responseSubmitDTO DTO chứa các câu trả lời.
     * @param currentUser Người dùng gửi phản hồi.
     * @return true nếu gửi thành công, false nếu thất bại (hoặc throw exception).
     */
    boolean submitSurveyResponseForClient(int surveyId, SurveyResponseSubmitDTO responseSubmitDTO, User currentUser);
}