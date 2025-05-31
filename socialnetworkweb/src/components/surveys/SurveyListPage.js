
import React, { useState, useEffect, useCallback, useContext, useRef } from 'react';
import { Link } from 'react-router-dom';
import { ListGroup, Spinner, Alert, Button } from 'react-bootstrap';
import Apis, { endpoints, authApis } from '../../configs/Apis';
import { MyUserContext } from '../../configs/Contexts';

const SurveyListPage = () => {
    const [surveys, setSurveys] = useState([]);
    const [loading, setLoading] = useState(true);      
    const [loadingMore, setLoadingMore] = useState(false); 
    const [error, setError] = useState(null);
    const [currentPage, setCurrentPage] = useState(1);   
    const [canLoadMore, setCanLoadMore] = useState(true); 
    const currentUser = useContext(MyUserContext);

    const fetchingPageRef = useRef(null);

    const fetchSurveysData = useCallback(async (pageToLoad) => {
       
        if (pageToLoad > 1 && fetchingPageRef.current === pageToLoad) {
           
            return;
        }

        if (pageToLoad === 1) {
            setLoading(true); 
            fetchingPageRef.current = null; 
        } else {
            setLoadingMore(true); 
        }

      
        if (pageToLoad > 1) {
            fetchingPageRef.current = pageToLoad;
        }
        setError(null); 

        try {
            const api = currentUser ? authApis() : Apis;
            const response = await api.get(endpoints['surveys_list'], {
                params: { page: pageToLoad }
            });

            if (response.data && Array.isArray(response.data)) {
                const fetchedSurveys = response.data;
                if (fetchedSurveys.length === 0) {
                    setCanLoadMore(false); 
                } else {
                    setSurveys(prevSurveys =>
                        pageToLoad === 1 ? fetchedSurveys : [...prevSurveys, ...fetchedSurveys]
                    );
                   
                    const BACKEND_PAGE_SIZE = 5;
                    if (fetchedSurveys.length < BACKEND_PAGE_SIZE) {
                        setCanLoadMore(false);
                    }
                }
            } else {
              
                if (pageToLoad === 1) {
                    setSurveys([]); 
                }
                setCanLoadMore(false); 
                
            }
        } catch (err) {
            console.error(`Lỗi khi tải danh sách khảo sát (trang ${pageToLoad}):`, err);
            let errorMsg = "Không thể tải danh sách khảo sát.";
            if (err.response && err.response.data && (err.response.data.error || err.response.data.message)) {
                errorMsg = err.response.data.error || err.response.data.message;
            }
            setError(errorMsg);
            
        } finally {
            if (pageToLoad === 1) {
                setLoading(false);
            } else {
                setLoadingMore(false);
            }
           
            if (pageToLoad > 1 && fetchingPageRef.current === pageToLoad) {
                fetchingPageRef.current = null;
            }
        }
    }, [currentUser]); 

    useEffect(() => {
        
        fetchingPageRef.current = null;
        setCurrentPage(1); 
        setSurveys([]);    
        setCanLoadMore(true); 
        fetchSurveysData(1); 

        return () => { 
            fetchingPageRef.current = null; 
        };
    }, [fetchSurveysData]); 

    const handleLoadMore = () => {
       
        if (loadingMore || !canLoadMore) {
            return;
        }
        
        setCurrentPage(prevPage => {
            const nextPage = prevPage + 1;
            fetchSurveysData(nextPage);
            return nextPage;
        });
    };

    const formatDate = (dateStr) => { 
        if (!dateStr) return "Không rõ";
       
        if (Array.isArray(dateStr) && dateStr.length >= 5) {
          
            return new Date(dateStr[0], dateStr[1] - 1, dateStr[2], dateStr[3], dateStr[4], dateStr[5] || 0).toLocaleString('vi-VN');
        }
      
        const date = new Date(dateStr);
        if (isNaN(date.getTime())) { 
            return "Ngày không hợp lệ";
        }
        return date.toLocaleString('vi-VN');
    };


    if (loading && currentPage === 1) { 
        return (
            <div className="text-center my-5">
                <Spinner animation="border" variant="primary" />
                <p>Đang tải danh sách khảo sát...</p>
            </div>
        );
    }

    
    if (error && surveys.length === 0 && !loading) {
        return <Alert variant="danger" className="m-3 text-center">{error}</Alert>;
    }

    return (
        <div className="mt-4 container"> 
            <h2 className="mb-4 text-center text-primary">Danh Sách Khảo Sát Hiện Có</h2>
            
            
            {error && surveys.length > 0 && <Alert variant="warning" className="text-center my-2">{error}</Alert>}
            
           
            {surveys.length === 0 && !loading && !error && (
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
                            className="mb-3 shadow-sm"
                           
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
                                
                            </small>
                            {currentUser && survey.isRespondedByCurrentUser && ( 
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

            {/* Spinner  */}
            {loadingMore && (
                <div className="text-center my-3">
                    <Spinner animation="border" variant="secondary" size="sm" />
                    <span className="ms-2">Đang tải thêm...</span>
                </div>
            )}

           
            {!canLoadMore && surveys.length > 0 && !loading && (
                 <Alert variant="light" className="text-center mt-3 mb-0">Đã hiển thị tất cả khảo sát.</Alert>
            )}
        </div>
    );
};

export default SurveyListPage;