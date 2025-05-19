import React, { useState, useEffect, useContext } from 'react';
import { Link } from 'react-router-dom';
import { Card, ListGroup, Spinner, Alert, Button } from 'react-bootstrap';
import Apis, { endpoints, authApis } from '../../configs/Apis'; // Điều chỉnh đường dẫn nếu cần
import { MyUserContext } from '../../configs/Contexts'; // Để lấy thông tin người dùng hiện tại

const SurveyListPage = () => {
    const [surveys, setSurveys] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const currentUser = useContext(MyUserContext);

    useEffect(() => {
        const fetchSurveys = async () => {
            setLoading(true);
            setError(null);
            try {
                // Nếu API yêu cầu xác thực để xem isRespondedByCurrentUser, dùng authApis()
                // Nếu API cho phép xem công khai, dùng Apis.get()
                // Giả sử API /api/surveys có thể được gọi mà không cần token,
                // nhưng nếu có token, backend sẽ dùng nó để check isRespondedByCurrentUser
                const api = currentUser ? authApis() : Apis;
                const response = await api.get(endpoints['surveys_list']);
                
                if (response.data && Array.isArray(response.data)) {
                    setSurveys(response.data);
                } else {
                    setSurveys([]); // Xử lý trường hợp data không phải mảng hoặc null/undefined
                }
            } catch (err) {
                console.error("Lỗi khi tải danh sách khảo sát:", err);
                let errorMsg = "Không thể tải danh sách khảo sát.";
                if (err.response && err.response.data && (err.response.data.error || err.response.data.message)) {
                    errorMsg = err.response.data.error || err.response.data.message;
                }
                setError(errorMsg);
                setSurveys([]); // Đặt lại surveys thành mảng rỗng khi có lỗi
            } finally {
                setLoading(false);
            }
        };

        fetchSurveys();
    }, [currentUser]); // Tải lại khi currentUser thay đổi (nếu cần check isResponded)

    const formatDate = (dateStr) => {
        if (!dateStr) return "Không rõ";
        return new Date(dateStr).toLocaleString('vi-VN');
    };

    if (loading) {
        return (
            <div className="text-center my-5">
                <Spinner animation="border" variant="primary" />
                <p>Đang tải danh sách khảo sát...</p>
            </div>
        );
    }

    if (error) {
        return <Alert variant="danger" className="m-3 text-center">{error}</Alert>;
    }

    return (
        <div className="mt-4">
            <h2 className="mb-4 text-center text-primary">Danh Sách Khảo Sát Hiện Có</h2>
            {surveys.length === 0 ? (
                <Alert variant="info" className="text-center">Hiện tại không có khảo sát nào.</Alert>
            ) : (
                <ListGroup>
                    {surveys.map(survey => (
                        <ListGroup.Item key={survey.surveyId} action as={Link} to={`/surveys/${survey.surveyId}/take`} 
                                        disabled={survey.status === 'EXPIRED' || survey.isRespondedByCurrentUser}
                                        className="mb-3 shadow-sm">
                            <div className="d-flex w-100 justify-content-between">
                                <h5 className="mb-1">{survey.title}</h5>
                                <small className={`text-${survey.status === 'ACTIVE' ? 'success' : (survey.status === 'EXPIRED' ? 'danger' : 'muted')}`}>
                                    {survey.status === 'ACTIVE' ? 'Đang diễn ra' : (survey.status === 'EXPIRED' ? 'Đã hết hạn' : 'Không hoạt động')}
                                </small>
                            </div>
                            <p className="mb-1">{survey.description || "Không có mô tả."}</p>
                            <small className="text-muted">
                                Số câu hỏi: {survey.questionCount || 0}
                                {survey.expiresAt && ` | Hết hạn: ${formatDate(survey.expiresAt)}`}
                            </small>
                            {survey.isRespondedByCurrentUser && (
                                <p className="mt-2 mb-0 text-success"><small><em>Bạn đã hoàn thành khảo sát này.</em></small></p>
                            )}
                             {survey.status === 'EXPIRED' && !survey.isRespondedByCurrentUser && (
                                <p className="mt-2 mb-0 text-danger"><small><em>Khảo sát này đã hết hạn.</em></small></p>
                            )}
                        </ListGroup.Item>
                    ))}
                </ListGroup>
            )}
        </div>
    );
};

export default SurveyListPage;