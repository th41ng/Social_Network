// SurveyListPage.js
import React, { useState, useEffect, useCallback, useContext, useRef } from 'react';
import { Link } from 'react-router-dom';
import { ListGroup, Spinner, Alert, Button } from 'react-bootstrap';
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

    // Ref để lưu trữ ID của trang đang được fetch (cho page > 1)
    // Giúp ngăn chặn việc gửi nhiều yêu cầu cho cùng một trang nếu handleLoadMore được kích hoạt nhanh
    const fetchingPageRef = useRef(null);

    const fetchSurveysData = useCallback(async (pageToLoad) => {
        // Nếu đang fetch chính trang này rồi (và không phải là trang 1) thì bỏ qua
        if (pageToLoad > 1 && fetchingPageRef.current === pageToLoad) {
            // console.log(`Đang fetch trang ${pageToLoad}, bỏ qua yêu cầu lặp lại.`);
            return;
        }

        if (pageToLoad === 1) {
            setLoading(true); // Loading cho lần tải đầu
            fetchingPageRef.current = null; // Reset khi tải lại từ đầu
        } else {
            setLoadingMore(true); // Loading cho các lần tải thêm
        }

        // Đánh dấu trang này đang được fetch (chỉ cho page > 1)
        if (pageToLoad > 1) {
            fetchingPageRef.current = pageToLoad;
        }
        setError(null); // Xóa lỗi cũ trước mỗi lần fetch

        try {
            const api = currentUser ? authApis() : Apis;
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
                    // Kiểm tra xem có phải trang cuối không dựa trên số lượng item trả về
                    // Giả sử PAGE_SIZE ở backend là 6
                    const BACKEND_PAGE_SIZE = 5;
                    if (fetchedSurveys.length < BACKEND_PAGE_SIZE) {
                        setCanLoadMore(false);
                    }
                }
            } else {
                // API trả về không đúng định dạng hoặc không có data
                if (pageToLoad === 1) {
                    setSurveys([]); // Nếu là trang đầu thì xóa surveys cũ
                }
                setCanLoadMore(false); // Ngừng tải thêm nếu API trả về không đúng
                // setError("Dữ liệu khảo sát không hợp lệ."); // Có thể set lỗi cụ thể
            }
        } catch (err) {
            console.error(`Lỗi khi tải danh sách khảo sát (trang ${pageToLoad}):`, err);
            let errorMsg = "Không thể tải danh sách khảo sát.";
            if (err.response && err.response.data && (err.response.data.error || err.response.data.message)) {
                errorMsg = err.response.data.error || err.response.data.message;
            }
            setError(errorMsg);
            // Nếu lỗi khi đang tải thêm, không nên setCanLoadMore(false) ngay,
            // trừ khi đó là lỗi nghiêm trọng (ví dụ 404 cho endpoint)
            // setCanLoadMore(false);
        } finally {
            if (pageToLoad === 1) {
                setLoading(false);
            } else {
                setLoadingMore(false);
            }
            // Xóa đánh dấu khi fetch xong cho trang này (nếu nó đã được đánh dấu)
            if (pageToLoad > 1 && fetchingPageRef.current === pageToLoad) {
                fetchingPageRef.current = null;
            }
        }
    }, [currentUser]); // Phụ thuộc vào currentUser để fetch lại nếu user thay đổi

    useEffect(() => {
        // Đảm bảo reset fetchingPageRef khi effect này chạy lại (ví dụ currentUser thay đổi)
        // để tránh trường hợp fetchingPageRef bị kẹt giá trị cũ.
        fetchingPageRef.current = null;
        setCurrentPage(1); // Reset về trang 1 khi currentUser thay đổi
        setSurveys([]);    // Xóa surveys cũ
        setCanLoadMore(true); // Reset khả năng load more
        fetchSurveysData(1); // Tải trang đầu tiên khi component mount hoặc fetchSurveysData (do currentUser) thay đổi

        return () => { // Cleanup function
            fetchingPageRef.current = null; // Reset khi component unmount
        };
    }, [fetchSurveysData]); // useEffect này phụ thuộc vào fetchSurveysData (mà fetchSurveysData phụ thuộc currentUser)

    const handleLoadMore = () => {
        // Guard này vẫn quan trọng để quản lý trạng thái UI của nút "Tải thêm"
        // và ngăn việc gọi khi không cần thiết.
        if (loadingMore || !canLoadMore) {
            return;
        }
        // fetchSurveysData giờ đã có cơ chế tự bảo vệ bằng fetchingPageRef.current
        // để tránh thực hiện logic fetch nếu cùng trang đó đang được fetch.
        setCurrentPage(prevPage => {
            const nextPage = prevPage + 1;
            fetchSurveysData(nextPage);
            return nextPage;
        });
    };

    const formatDate = (dateStr) => { // Giữ lại hàm này nếu bạn có dùng trường expiresAt
        if (!dateStr) return "Không rõ";
        // Định dạng của createdAt từ API là một mảng [năm, tháng, ngày, giờ, phút, giây?]
        // Cần chuyển đổi nó thành một đối tượng Date hợp lệ
        if (Array.isArray(dateStr) && dateStr.length >= 5) {
            // Tháng trong new Date() là 0-indexed (0-11), nên trừ 1
            return new Date(dateStr[0], dateStr[1] - 1, dateStr[2], dateStr[3], dateStr[4], dateStr[5] || 0).toLocaleString('vi-VN');
        }
        // Nếu là chuỗi date ISO hoặc tương tự
        const date = new Date(dateStr);
        if (isNaN(date.getTime())) { // Kiểm tra nếu date không hợp lệ
            return "Ngày không hợp lệ";
        }
        return date.toLocaleString('vi-VN');
    };


    if (loading && currentPage === 1) { // Chỉ hiển thị spinner loading lớn cho lần tải đầu tiên
        return (
            <div className="text-center my-5">
                <Spinner animation="border" variant="primary" />
                <p>Đang tải danh sách khảo sát...</p>
            </div>
        );
    }

    // Hiển thị lỗi lớn nếu không có survey nào để hiển thị và có lỗi (thường là lỗi tải lần đầu)
    if (error && surveys.length === 0 && !loading) {
        return <Alert variant="danger" className="m-3 text-center">{error}</Alert>;
    }

    return (
        <div className="mt-4 container"> {/* Thêm class container cho layout tốt hơn */}
            <h2 className="mb-4 text-center text-primary">Danh Sách Khảo Sát Hiện Có</h2>
            
            {/* Hiển thị lỗi nhỏ nếu có surveys đã tải và sau đó gặp lỗi khi tải thêm */}
            {error && surveys.length > 0 && <Alert variant="warning" className="text-center my-2">{error}</Alert>}
            
            {/* Thông báo không có khảo sát nào, chỉ hiển thị khi không loading lần đầu và không có lỗi */}
            {surveys.length === 0 && !loading && !error && (
                <Alert variant="info" className="text-center">Hiện tại không có khảo sát nào.</Alert>
            )}

            {surveys.length > 0 && (
                <ListGroup>
                    {surveys.map(survey => (
                        <ListGroup.Item
                            key={survey.surveyId} // Key phải là unique
                            action
                            as={Link}
                            to={`/surveys/${survey.surveyId}/take`}
                            className="mb-3 shadow-sm"
                            // Ví dụ: disabled={survey.status === 'EXPIRED' || survey.isRespondedByCurrentUser}
                        >
                            <div className="d-flex w-100 justify-content-between">
                                <h5 className="mb-1">{survey.title}</h5>
                                <small className={`text-${survey.status === 'ACTIVE' ? 'success' : (survey.status === 'EXPIRED' ? 'danger' : 'muted')}`}>
                                    {survey.status === 'ACTIVE' ? 'Đang diễn ra' : (survey.status === 'EXPIRED' ? 'Đã hết hạn' : 'Không hoạt động')}
                                </small>
                            </div>
                            <p className="mb-1">{survey.description || "Không có mô tả."}</p>
                            <small className="text-muted">
                                Số câu hỏi: {survey.questionCount !== undefined ? survey.questionCount : "N/A"}
                                {survey.createdAt && ` | Tạo lúc: ${formatDate(survey.createdAt)}`}
                                {/* {survey.expiresAt && ` | Hết hạn: ${formatDate(survey.expiresAt)}`} */}
                            </small>
                            {currentUser && survey.isRespondedByCurrentUser && ( // Chỉ hiển thị nếu đã đăng nhập
                                <p className="mt-2 mb-0 text-success"><small><em>Bạn đã hoàn thành khảo sát này.</em></small></p>
                            )}
                            {survey.status === 'EXPIRED' && (!currentUser || !survey.isRespondedByCurrentUser) && (
                                <p className="mt-2 mb-0 text-danger"><small><em>Khảo sát này đã hết hạn.</em></small></p>
                            )}
                        </ListGroup.Item>
                    ))}
                </ListGroup>
            )}

            {/* Nút tải thêm */}
            {canLoadMore && !loadingMore && surveys.length > 0 && (
                <div className="text-center mt-3 mb-5"> {/* Thêm mb-5 cho khoảng cách */}
                    <Button onClick={handleLoadMore} variant="primary">Tải thêm khảo sát</Button>
                </div>
            )}

            {/* Spinner nhỏ khi đang tải thêm */}
            {loadingMore && (
                <div className="text-center my-3">
                    <Spinner animation="border" variant="secondary" size="sm" />
                    <span className="ms-2">Đang tải thêm...</span>
                </div>
            )}

            {/* Thông báo khi đã hết survey */}
            {!canLoadMore && surveys.length > 0 && !loading && (
                 <Alert variant="light" className="text-center mt-3 mb-0">Đã hiển thị tất cả khảo sát.</Alert>
            )}
        </div>
    );
};

export default SurveyListPage;