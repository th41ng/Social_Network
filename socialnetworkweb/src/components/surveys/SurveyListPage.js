// SurveyListPage.js
import React, { useState, useEffect, useCallback, useContext } from 'react'; // Thêm useCallback
import { Link } from 'react-router-dom';
import { ListGroup, Spinner, Alert, Button } from 'react-bootstrap'; // Thêm Button
import Apis, { endpoints, authApis } from '../../configs/Apis';
import { MyUserContext } from '../../configs/Contexts';

const SurveyListPage = () => {
    const [surveys, setSurveys] = useState([]);
    const [loading, setLoading] = useState(true);       // Trạng thái loading ban đầu
    const [loadingMore, setLoadingMore] = useState(false); // Trạng thái loading khi tải thêm
    const [error, setError] = useState(null);
    const [currentPage, setCurrentPage] = useState(1);   // State cho trang hiện tại
    const [canLoadMore, setCanLoadMore] = useState(true); // Còn có thể tải thêm không
    const currentUser = useContext(MyUserContext);

    const fetchSurveysData = useCallback(async (pageToLoad) => {
        if (pageToLoad === 1) {
            setLoading(true); // Loading cho lần tải đầu
        } else {
            setLoadingMore(true); // Loading cho các lần tải thêm
        }
        setError(null);

        try {
            const api = currentUser ? authApis() : Apis;
            // Gửi tham số 'page' lên API
            const response = await api.get(endpoints['surveys_list'], { 
                params: { page: pageToLoad } 
            });
            
            if (response.data && Array.isArray(response.data)) {
                const fetchedSurveys = response.data;
                if (fetchedSurveys.length === 0) {
                    setCanLoadMore(false); // Không còn survey để tải
                } else {
                    setSurveys(prevSurveys => 
                        pageToLoad === 1 ? fetchedSurveys : [...prevSurveys, ...fetchedSurveys]
                    );
                    // Giả sử API trả về ít hơn PAGE_SIZE của backend nếu đó là trang cuối
                    // Hoặc backend cần trả về thông tin page size để biết có nên setCanLoadMore(false) không
                    // Ví dụ: nếu backend PAGE_SIZE là 6, và API trả về < 6 surveys thì là trang cuối
                    // if (fetchedSurveys.length < YOUR_BACKEND_PAGE_SIZE_FOR_SURVEYS) { 
                    //    setCanLoadMore(false);
                    // }
                }
            } else {
                setSurveys(prev => pageToLoad === 1 ? [] : prev); // Giữ lại survey cũ nếu tải thêm lỗi
                setCanLoadMore(false); // Nếu API trả về không đúng định dạng
            }
        } catch (err) {
            console.error(`Lỗi khi tải danh sách khảo sát (trang ${pageToLoad}):`, err);
            let errorMsg = "Không thể tải danh sách khảo sát.";
            if (err.response && err.response.data && (err.response.data.error || err.response.data.message)) {
                errorMsg = err.response.data.error || err.response.data.message;
            }
            setError(errorMsg); // Chỉ set error, không xóa surveys cũ nếu đang tải thêm
            setCanLoadMore(false); // Dừng tải thêm nếu có lỗi
        } finally {
            if (pageToLoad === 1) {
                setLoading(false);
            } else {
                setLoadingMore(false);
            }
        }
    }, [currentUser]); // Thêm các dependency nếu có (ví dụ: filters, sortOrder)

    useEffect(() => {
        fetchSurveysData(1); // Tải trang đầu tiên khi component mount
    }, [fetchSurveysData]); // Chạy lại khi fetchSurveysData thay đổi (thường là khi currentUser thay đổi)

    const handleLoadMore = () => {
        if (!loadingMore && canLoadMore) {
            setCurrentPage(prevPage => {
                const nextPage = prevPage + 1;
                fetchSurveysData(nextPage); // Gọi fetchSurveysData với trang tiếp theo
                return nextPage;
            });
        }
    };
    
    const formatDate = (dateStr) => {
        if (!dateStr) return "Không rõ";
        return new Date(dateStr).toLocaleString('vi-VN');
    };

    if (loading && currentPage === 1) { // Chỉ hiển thị spinner loading lớn cho lần tải đầu tiên
        return (
            <div className="text-center my-5">
                <Spinner animation="border" variant="primary" />
                <p>Đang tải danh sách khảo sát...</p>
            </div>
        );
    }

    if (error && surveys.length === 0) { // Chỉ hiển thị lỗi lớn nếu không có survey nào để hiển thị
        return <Alert variant="danger" className="m-3 text-center">{error}</Alert>;
    }

    return (
        <div className="mt-4">
            <h2 className="mb-4 text-center text-primary">Danh Sách Khảo Sát Hiện Có</h2>
            {error && <Alert variant="warning" className="text-center my-2">{error}</Alert>} {/* Hiển thị lỗi nhỏ nếu có surveys đã tải */}
            
            {surveys.length === 0 && !loading && ( // Kiểm tra !loading để không hiển thị khi đang tải lần đầu
                <Alert variant="info" className="text-center">Hiện tại không có khảo sát nào.</Alert>
            )}

            {surveys.length > 0 && (
                <ListGroup>
                    {surveys.map(survey => (
                        <ListGroup.Item 
                            key={survey.surveyId} 
                            action 
                            as={Link} 
                            to={`/surveys/${survey.surveyId}/take`}
                            // Bạn cần lấy isRespondedByCurrentUser và status từ API cho mỗi survey
                            // Ví dụ: disabled={survey.status === 'EXPIRED' || survey.isRespondedByCurrentUser}
                            className="mb-3 shadow-sm"
                        >
                            <div className="d-flex w-100 justify-content-between">
                                <h5 className="mb-1">{survey.title}</h5>
                                <small className={`text-${survey.status === 'ACTIVE' ? 'success' : (survey.status === 'EXPIRED' ? 'danger' : 'muted')}`}>
                                    {/* API của bạn cần trả về 'status' cho survey */}
                                    {survey.status === 'ACTIVE' ? 'Đang diễn ra' : (survey.status === 'EXPIRED' ? 'Đã hết hạn' : 'Không hoạt động')}
                                </small>
                            </div>
                            <p className="mb-1">{survey.description || "Không có mô tả."}</p>
                            <small className="text-muted">
                                {/* API của bạn cần trả về 'questionCount' và 'expiresAt' */}
                                Số câu hỏi: {survey.questionCount || 0}
                                {survey.expiresAt && ` | Hết hạn: ${formatDate(survey.expiresAt)}`}
                            </small>
                            {/* API của bạn cần trả về 'isRespondedByCurrentUser' */}
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

            {canLoadMore && !loadingMore && surveys.length > 0 && ( // Chỉ hiển thị nút nếu còn có thể tải và không đang tải
                <div className="text-center mt-3">
                    <Button onClick={handleLoadMore} variant="primary">Tải thêm khảo sát</Button>
                </div>
            )}
            {loadingMore && ( // Spinner nhỏ khi đang tải thêm
                <div className="text-center my-3">
                    <Spinner animation="border" variant="secondary" size="sm" />
                    <span className="ms-2">Đang tải thêm...</span>
                </div>
            )}
            {!canLoadMore && surveys.length > 0 && !loading && ( // Thông báo khi đã hết survey
                 <Alert variant="light" className="text-center mt-3 mb-0">Đã hiển thị tất cả khảo sát.</Alert>
            )}
        </div>
    );
};

export default SurveyListPage;