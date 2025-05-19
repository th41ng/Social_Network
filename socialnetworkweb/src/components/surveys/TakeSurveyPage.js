import React, { useState, useEffect, useContext } from 'react'; // THÊM "from 'react';"
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Form, Button, Spinner, Alert } from 'react-bootstrap'; // Bỏ ListGroup nếu không dùng ở đây
import Apis, { authApis, endpoints } from '../../configs/Apis'; // Đảm bảo Apis được import nếu dùng
import { MyUserContext } from '../../configs/Contexts';

const TakeSurveyPage = () => {
    const { surveyId } = useParams();
    const [surveyDetails, setSurveyDetails] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [answers, setAnswers] = useState({}); // Lưu trữ câu trả lời: { questionId: answerValue }
    const [submitting, setSubmitting] = useState(false);
    const currentUser = useContext(MyUserContext);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchSurveyDetails = async () => {
            setLoading(true);
            setError(null);
            try {
                // API này có thể cần xác thực để biết canRespond dựa trên currentUser
                const api = currentUser ? authApis() : Apis; // Apis nếu cho phép anonymous xem
                const response = await api.get(endpoints['survey_detail'](surveyId));
                setSurveyDetails(response.data);

                // Khởi tạo state cho answers
                if (response.data && response.data.questions) {
                    const initialAnswers = {};
                    response.data.questions.forEach(q => {
                        if (q.questionType === 'MULTIPLE_CHOICE') {
                            initialAnswers[q.questionId] = []; // Mảng cho checkbox
                        } else {
                            initialAnswers[q.questionId] = '';  // Chuỗi rỗng cho radio hoặc text
                        }
                    });
                    setAnswers(initialAnswers);
                }

            } catch (err) {
                console.error(`Lỗi khi tải chi tiết khảo sát ${surveyId}:`, err);
                let errorMsg = `Không thể tải chi tiết khảo sát.`;
                 if (err.response) {
                    if (err.response.status === 404) {
                        errorMsg = "Khảo sát không tồn tại hoặc đã bị xóa.";
                    } else if (err.response.status === 403) {
                        errorMsg = err.response.data.error || "Bạn không có quyền truy cập hoặc đã hoàn thành khảo sát này.";
                    } else if (err.response.data && (err.response.data.error || err.response.data.message)) {
                        errorMsg = err.response.data.error || err.response.data.message;
                    }
                }
                setError(errorMsg);
            } finally {
                setLoading(false);
            }
        };

        fetchSurveyDetails();
    }, [surveyId, currentUser]);

    const handleAnswerChange = (questionId, value, questionType) => {
    setAnswers(prevAnswers => {
        if (questionType === 'MULTIPLE_CHOICE') {
            // Đối với câu hỏi trắc nghiệm, chỉ lưu một giá trị duy nhất
            return { ...prevAnswers, [questionId]: value };
        }
        return { ...prevAnswers, [questionId]: value }; // Cập nhật câu trả lời cho các loại câu hỏi khác
    });
};

const handleSubmit = async (e) => {
    e.preventDefault();
    if (!currentUser) { // Yêu cầu đăng nhập để submit
        alert("Vui lòng đăng nhập để gửi phản hồi.");
        navigate("/login", { state: { from: `/surveys/${surveyId}/take` } });
        return;
    }

    setSubmitting(true);
    setError(null);

    // Kiểm tra và chuẩn bị các câu trả lời
    const formattedResponses = Object.keys(answers).map(questionId => {
        const question = surveyDetails.questions.find(q => q.questionId === parseInt(questionId));
        const answerValue = answers[questionId];
        const item = { questionId: parseInt(questionId) };

        // Kiểm tra câu trả lời và xử lý các câu hỏi
        if (question.questionType === 'SINGLE_CHOICE' && answerValue) {
            item.selectedOptionId = parseInt(answerValue); // Dạng single choice
        } else if (question.questionType === 'MULTIPLE_CHOICE' && answerValue) {
            item.selectedOptionId = parseInt(answerValue); // Lưu một lựa chọn duy nhất cho multiple choice
        } else if (question.questionType === 'TEXT_INPUT' && typeof answerValue === 'string' && answerValue.trim() !== '') {
            item.responseText = answerValue.trim(); // Dạng text input
        } else if (question.questionType === 'Essay' && typeof answerValue === 'string' && answerValue.trim() !== '') {
            item.responseText = answerValue.trim();  // Dạng Essay
        }

        // Chỉ thêm vào mảng nếu có câu trả lời hợp lệ
        if (item.selectedOptionId || item.responseText) {
            return item;
        }
        return null;
    }).filter(item => item !== null); // Loại bỏ các câu trả lời rỗng

    // Kiểm tra xem có câu trả lời hợp lệ hay không
    if (formattedResponses.length === 0) {
        alert("Vui lòng trả lời ít nhất một câu hỏi.");
        setSubmitting(false);
        return;
    }

    const payload = { responses: formattedResponses };

    try {
        // Gửi phản hồi khảo sát
        await authApis().post(endpoints['survey_submit_responses'](surveyId), payload);
        alert("Cảm ơn bạn đã hoàn thành khảo sát!");
        navigate("/surveys"); // Hoặc trang cảm ơn
    } catch (err) {
        console.error("Lỗi khi gửi phản hồi khảo sát:", err);
        let errorMsg = "Không thể gửi phản hồi của bạn.";
        if (err.response && err.response.data && (err.response.data.error || err.response.data.message)) {
            errorMsg = err.response.data.error || err.response.data.message;
        }
        setError(errorMsg);
    } finally {
        setSubmitting(false);
    }
};



const renderQuestion = (question) => {
    const answerValue = answers[question.questionId];

    // Kiểm tra nếu question.options là null hoặc undefined trước khi gọi map
    if (!question.options) {
        if (question.questionType === 'Essay') {
            return (
                <Form.Group>
                    <Form.Control
                        as="textarea"
                        rows={3}
                        value={answerValue || ''} // Giá trị trả lời
                        onChange={(e) => handleAnswerChange(question.questionId, e.target.value, question.questionType)}
                        placeholder="Nhập câu trả lời của bạn..."
                    />
                </Form.Group>
            );
        }
        return <p>Không có lựa chọn cho câu hỏi này.</p>;
    }

    switch (question.questionType) {
        case 'Multiple Choice': // Dạng câu hỏi Multiple Choice (Radio button)
            return (
                <Form.Group>
                    {question.options.map(opt => (
                        <Form.Check
                            type="radio"
                            key={opt.optionId}
                            id={`q${question.questionId}-opt${opt.optionId}`}
                            label={opt.optionText}
                            value={opt.optionId}
                            checked={parseInt(answerValue) === opt.optionId}
                            onChange={(e) => handleAnswerChange(question.questionId, e.target.value, question.questionType)} // Chỉ lưu một lựa chọn duy nhất
                        />
                    ))}
                </Form.Group>
            );

        case 'Essay': // Dạng câu hỏi Essay (Text Input)
            return (
                <Form.Group>
                    <Form.Control
                        as="textarea"
                        rows={3}
                        value={answerValue || ''}
                        onChange={(e) => handleAnswerChange(question.questionId, e.target.value, question.questionType)}
                        placeholder="Nhập câu trả lời của bạn..."
                    />
                </Form.Group>
            );

        default:
            return <p>Loại câu hỏi không được hỗ trợ: {question.questionType}</p>;
    }
};







    if (loading) {
        return (
            <div className="text-center my-5">
                <Spinner animation="border" variant="primary" />
                <p>Đang tải chi tiết khảo sát...</p>
            </div>
        );
    }

    if (error) {
        return <Alert variant="danger" className="m-3 text-center">{error}</Alert>;
    }

    if (!surveyDetails) {
        return <Alert variant="warning" className="m-3 text-center">Không tìm thấy thông tin khảo sát.</Alert>;
    }
    
    if (surveyDetails.canRespond === false && !error) { // Thêm !error để không hiển thị cùng lúc với lỗi từ API
         return <Alert variant="warning" className="m-3 text-center">Bạn không thể thực hiện khảo sát này (có thể đã hết hạn, bạn đã làm rồi, hoặc khảo sát không còn hoạt động).</Alert>;
    }


    return (
        <Card className="mt-4 shadow-sm">
            <Card.Header as="h3" className="text-primary">{surveyDetails.title}</Card.Header>
            <Card.Body>
                {surveyDetails.description && <Card.Text className="mb-4">{surveyDetails.description}</Card.Text>}
                
                <Form onSubmit={handleSubmit}>
                    {surveyDetails.questions && surveyDetails.questions.map((question, index) => (
                        <Card key={question.questionId} className="mb-4">
                            <Card.Header><strong>Câu {index + 1}:</strong> {question.questionText}</Card.Header>
                            <Card.Body>
                                {renderQuestion(question)}
                            </Card.Body>
                        </Card>
                    ))}
                    <Button type="submit" variant="success" disabled={submitting || !surveyDetails.canRespond} className="w-100">
                        {submitting ? <><Spinner as="span" animation="border" size="sm" role="status" aria-hidden="true" /> Đang gửi...</> : "Gửi Phản Hồi"}
                    </Button>
                </Form>
            </Card.Body>
             {surveyDetails.expiresAt && 
                <Card.Footer className="text-muted text-center">
                    <small>Khảo sát hết hạn vào: {new Date(surveyDetails.expiresAt).toLocaleString('vi-VN')}</small>
                </Card.Footer>
            }
        </Card>
    );
};

export default TakeSurveyPage;