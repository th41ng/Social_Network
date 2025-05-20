import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Form, Button, Spinner, Alert, Container} from 'react-bootstrap';
import Apis, { authApis, endpoints } from '../../configs/Apis'; // Đảm bảo đường dẫn này chính xác
import { MyUserContext } from '../../configs/Contexts'; // Đảm bảo đường dẫn này chính xác

const TakeSurveyPage = () => {
    const { surveyId } = useParams();
    const [surveyDetails, setSurveyDetails] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null); // Dùng chung cho lỗi fetch và lỗi validation
    const [answers, setAnswers] = useState({});
    const [submitting, setSubmitting] = useState(false);
    const [unansweredQuestionIds, setUnansweredQuestionIds] = useState([]); // State mới để lưu ID các câu hỏi bắt buộc chưa trả lời

    const currentUser = useContext(MyUserContext);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchSurveyDetails = async () => {
            setLoading(true);
            setError(null);
            setUnansweredQuestionIds([]); // Reset khi tải lại
            try {
                const api = currentUser ? authApis() : Apis;
                const response = await api.get(endpoints['survey_detail'](surveyId));
                setSurveyDetails(response.data);

                if (response.data && response.data.questions) {
                    const initialAnswers = {};
                    response.data.questions.forEach(q => {
                        initialAnswers[q.questionId] = '';
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
                        errorMsg = err.response.data?.error || "Bạn không có quyền truy cập hoặc đã hoàn thành khảo sát này.";
                    } else if (err.response.data && (err.response.data.error || err.response.data.message)) {
                        errorMsg = err.response.data.error || err.response.data.message;
                    }
                }
                setError(errorMsg); // Lỗi này là lỗi fetch
            } finally {
                setLoading(false);
            }
        };

        fetchSurveyDetails();
    }, [surveyId, currentUser]);

    const handleAnswerChange = (questionId, value) => {
        setAnswers(prevAnswers => ({
            ...prevAnswers,
            [questionId]: value
        }));
        if (error) { // Xóa lỗi validation cũ khi người dùng bắt đầu sửa
            setError(null);
        }
        if (unansweredQuestionIds.includes(questionId)) { // Xóa highlight lỗi cho câu hỏi cụ thể
            setUnansweredQuestionIds(prev => prev.filter(id => id !== questionId));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!currentUser) {
            alert("Vui lòng đăng nhập để gửi phản hồi.");
            navigate("/login", { state: { from: `/surveys/${surveyId}/take` } });
            return;
        }

        setUnansweredQuestionIds([]); // Reset danh sách câu hỏi lỗi trước mỗi lần submit
        setError(null); // Xóa lỗi chung trước khi submit

        if (surveyDetails && surveyDetails.questions) {
            const requiredQuestionsData = surveyDetails.questions.filter(q => q.isRequired === true);
            const unansweredMessages = [];
            const unansweredIds = [];

            for (const reqQuestion of requiredQuestionsData) {
                const answerValue = answers[reqQuestion.questionId];
                let isConsideredAnswered = false;

                if (reqQuestion.questionType === 'Multiple Choice') {
                    if (answerValue && answerValue.toString().trim() !== '') {
                        isConsideredAnswered = true;
                    }
                } else if (reqQuestion.questionType === 'TEXT_INPUT' || reqQuestion.questionType === 'Essay') {
                    if (typeof answerValue === 'string' && answerValue.trim() !== '') {
                        isConsideredAnswered = true;
                    }
                }

                if (!isConsideredAnswered) {
                    unansweredMessages.push(`"${reqQuestion.questionText}" (Câu ${surveyDetails.questions.findIndex(q => q.questionId === reqQuestion.questionId) + 1})`);
                    unansweredIds.push(reqQuestion.questionId);
                }
            }

            if (unansweredMessages.length > 0) {
                setError(`Vui lòng trả lời các câu hỏi bắt buộc sau:\n- ${unansweredMessages.join('\n- ')}`);
                setUnansweredQuestionIds(unansweredIds); // Lưu ID các câu hỏi lỗi để highlight
                setSubmitting(false);
                return;
            }
        }

        setSubmitting(true);
        //setError(null); // Đã dời lên trên

        const formattedResponses = Object.keys(answers).map(questionId => {
            const question = surveyDetails.questions.find(q => q.questionId === parseInt(questionId));
            const answerValue = answers[questionId];
            const item = { questionId: parseInt(questionId) };
            if (!question) return null;
            let hasValidAnswer = false;
            if (question.questionType === 'Multiple Choice' && answerValue && answerValue.toString().trim() !== '') {
                item.selectedOptionId = parseInt(answerValue);
                hasValidAnswer = true;
            } else if ((question.questionType === 'TEXT_INPUT' || question.questionType === 'Essay') && typeof answerValue === 'string' && answerValue.trim() !== '') {
                item.responseText = answerValue.trim();
                hasValidAnswer = true;
            }
            return hasValidAnswer ? item : null;
        }).filter(item => item !== null);

        if (formattedResponses.length === 0 && surveyDetails.questions.some(q => q.isRequired === true)) {
            setError("Vui lòng cung cấp câu trả lời hợp lệ cho các câu hỏi bắt buộc.");
            setSubmitting(false);
            return;
        } else if (formattedResponses.length === 0 && surveyDetails.questions.length > 0 && !surveyDetails.questions.some(q => q.isRequired === true)) {
            alert("Vui lòng trả lời ít nhất một câu hỏi, hoặc đảm bảo câu trả lời của bạn hợp lệ.");
            setSubmitting(false);
            return;
        }

        const payload = { responses: formattedResponses };
        try {
            await authApis().post(endpoints['survey_submit_responses'](surveyId), payload);
            alert("Cảm ơn bạn đã hoàn thành khảo sát!");
            navigate("/surveys");
        } catch (err) {
            console.error("Lỗi khi gửi phản hồi khảo sát:", err);
            let errorMsg = "Không thể gửi phản hồi của bạn.";
            if (err.response && err.response.data && (err.response.data.error || err.response.data.message)) {
                errorMsg = err.response.data.error || err.response.data.message;
            }
            setError(errorMsg); // Lỗi từ server khi submit
        } finally {
            setSubmitting(false);
        }
    };

    const renderQuestion = (question) => {
        const answerValue = answers[question.questionId] || '';
        const isInvalid = unansweredQuestionIds.includes(question.questionId); // Kiểm tra câu hỏi có đang bị lỗi không

        // Thêm prop isInvalid cho Form.Control hoặc Form.Check nếu React-Bootstrap hỗ trợ
        // Hoặc bạn có thể tự thêm class CSS để custom style
        // Ví dụ: className={isInvalid ? 'is-invalid' : ''} (cần CSS tương ứng)

        return (
            <Form.Group className={isInvalid ? 'question-error-highlight' : ''}> {/* Thêm class để highlight nếu muốn */}
                {(() => {
                    switch (question.questionType) {
                        case 'Multiple Choice':
                            return (Array.isArray(question.options) && question.options.length > 0) ? (
                                question.options.map(opt => (
                                    <Form.Check
                                        type="radio"
                                        key={opt.optionId}
                                        id={`q${question.questionId}-opt${opt.optionId}`}
                                        label={opt.optionText}
                                        value={opt.optionId.toString()}
                                        checked={answerValue === opt.optionId.toString()}
                                        onChange={(e) => handleAnswerChange(question.questionId, e.target.value)}
                                        name={`question-${question.questionId}`}
                                        isInvalid={isInvalid && !answerValue} // Highlight nếu bắt buộc, lỗi và chưa có giá trị
                                    />
                                ))
                            ) : <p className="text-muted small">Câu hỏi trắc nghiệm này hiện không có lựa chọn nào.</p>;
                        case 'Essay':
                            return (
                                <Form.Control
                                    as="textarea"
                                    rows={3}
                                    value={answerValue}
                                    onChange={(e) => handleAnswerChange(question.questionId, e.target.value)}
                                    placeholder="Nhập câu trả lời của bạn..."
                                    isInvalid={isInvalid} // Highlight nếu lỗi
                                />
                            );
                        case 'TEXT_INPUT':
                            return (
                                <Form.Control
                                    type="text"
                                    value={answerValue}
                                    onChange={(e) => handleAnswerChange(question.questionId, e.target.value)}
                                    placeholder="Nhập câu trả lời ngắn..."
                                    isInvalid={isInvalid} // Highlight nếu lỗi
                                />
                            );
                        default:
                            return <p className="text-danger small">Loại câu hỏi không được hỗ trợ: {question.questionType}</p>;
                    }
                })()}
            </Form.Group>
        );
    };

    // --- LOGIC RENDER ĐÃ ĐƯỢC SẮP XẾP LẠI ---
    if (loading) {
        return (
            <div className="text-center my-5">
                <Spinner animation="border" variant="primary" />
                <p className="mt-2">Đang tải chi tiết khảo sát...</p>
            </div>
        );
    }

    // Lỗi fetch nghiêm trọng, không có surveyDetails
    if (!surveyDetails && error) {
        return <Alert variant="danger" className="m-3 text-center">{error}</Alert>;
    }

    // Không có surveyDetails (sau khi loading xong và không có lỗi fetch nghiêm trọng)
    if (!surveyDetails) {
        return <Alert variant="warning" className="m-3 text-center">Không tìm thấy thông tin khảo sát.</Alert>;
    }
    
    // surveyDetails đã có, nhưng người dùng không thể trả lời
    // Thông báo này nên được ưu tiên hơn lỗi validation nếu có
    if (surveyDetails.canRespond === false) {
        return <Alert variant="info" className="m-3 text-center">Bạn không thể thực hiện khảo sát này (có thể đã hết hạn, bạn đã làm rồi, hoặc khảo sát không còn hoạt động).</Alert>;
    }

    // Nếu đến đây, surveyDetails có và người dùng có thể trả lời. Hiển thị form.
    // Lỗi validation (biến `error`) sẽ được hiển thị bên trong Card.Body.
    return (
        <Container className="my-4"> {/* Thêm Container cho khoảng cách và căn giữa tốt hơn */}
            <Card className="shadow-sm">
                <Card.Header as="h3" className="bg-primary text-white text-center">{surveyDetails.title}</Card.Header>
                <Card.Body>
                    {surveyDetails.description && <Card.Text className="mb-4 fst-italic text-center">{surveyDetails.description}</Card.Text>}
                    
                    {/* Hiển thị lỗi (validation hoặc lỗi submit từ server) */}
                    {error && (
                        <Alert variant="danger" onClose={() => { setError(null); setUnansweredQuestionIds([]); }} dismissible>
                            {error.split('\n').map((line, i) => (<React.Fragment key={i}>{line}<br/></React.Fragment>))}
                        </Alert>
                    )}

                    <Form onSubmit={handleSubmit}>
                        {surveyDetails.questions && surveyDetails.questions.map((question, index) => (
                            <Card 
                                key={question.questionId} 
                                className={`mb-4 ${unansweredQuestionIds.includes(question.questionId) ? 'border-danger shadow-danger' : ''}`}
                                id={`question-card-${question.questionId}`} // Thêm ID để có thể scroll tới
                            >
                                <Card.Header className={`${unansweredQuestionIds.includes(question.questionId) ? 'text-danger' : ''}`}>
                                    <strong>Câu {index + 1}:</strong> {question.questionText}
                                    {question.isRequired === true && <span className="text-danger ms-1 fw-bold">*</span>}
                                </Card.Header>
                                <Card.Body>
                                    {renderQuestion(question)}
                                </Card.Body>
                            </Card>
                        ))}
                        <Button 
                            type="submit" 
                            variant="success" 
                            disabled={submitting} // Chỉ disable khi đang submitting
                            className="w-100 py-2 mt-3"
                        >
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
        </Container>
    );
};

export default TakeSurveyPage;