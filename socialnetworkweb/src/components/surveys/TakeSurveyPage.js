import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Form, Button, Spinner, Alert } from 'react-bootstrap';
import Apis, { authApis, endpoints } from '../../configs/Apis';
import { MyUserContext } from '../../configs/Contexts';

const TakeSurveyPage = () => {
    const { surveyId } = useParams();
    const [surveyDetails, setSurveyDetails] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [answers, setAnswers] = useState({});
    const [submitting, setSubmitting] = useState(false);
    const currentUser = useContext(MyUserContext);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchSurveyDetails = async () => {
            setLoading(true);
            setError(null);
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

    const handleAnswerChange = (questionId, value) => { 
        setAnswers(prevAnswers => ({
            ...prevAnswers,
            [questionId]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!currentUser) {
            alert("Vui lòng đăng nhập để gửi phản hồi.");
            navigate("/login", { state: { from: `/surveys/${surveyId}/take` } });
            return;
        }

        setSubmitting(true);
        setError(null);

        const formattedResponses = Object.keys(answers).map(questionId => {
            const question = surveyDetails.questions.find(q => q.questionId === parseInt(questionId));
            const answerValue = answers[questionId];
            const item = { questionId: parseInt(questionId) };

            if (!question) return null;

            
            if (question.questionType === 'Multiple Choice' && answerValue) {
                item.selectedOptionId = parseInt(answerValue);
            } else if (question.questionType === 'TEXT_INPUT' && typeof answerValue === 'string' && answerValue.trim() !== '') {
                item.responseText = answerValue.trim();
            } else if (question.questionType === 'Essay' && typeof answerValue === 'string' && answerValue.trim() !== '') {
                item.responseText = answerValue.trim();
            }
           


            if (item.selectedOptionId || item.responseText) {
                return item;
            }
            return null;
        }).filter(item => item !== null);

        if (formattedResponses.length === 0) {
            alert("Vui lòng trả lời ít nhất một câu hỏi.");
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
            setError(errorMsg);
        } finally {
            setSubmitting(false);
        }
    };

    const renderQuestion = (question) => {
        const answerValue = answers[question.questionId] || ''; 

        switch (question.questionType) {
            case 'Multiple Choice': 
                if (Array.isArray(question.options) && question.options.length > 0) {
                    return (
                        <Form.Group>
                            {question.options.map(opt => (
                                <Form.Check
                                    type="radio"
                                    key={opt.optionId}
                                    id={`q${question.questionId}-opt${opt.optionId}`}
                                    label={opt.optionText}
                                    value={opt.optionId.toString()} 
                                  
                                    checked={answerValue === opt.optionId.toString()}
                                    onChange={(e) => handleAnswerChange(question.questionId, e.target.value)}
                                />
                            ))}
                        </Form.Group>
                    );
                } else {
                    return <p>Câu hỏi trắc nghiệm này hiện không có lựa chọn nào.</p>;
                }

            case 'Essay':
                return (
                    <Form.Group>
                        <Form.Control
                            as="textarea"
                            rows={3}
                            value={answerValue}
                            onChange={(e) => handleAnswerChange(question.questionId, e.target.value)}
                            placeholder="Nhập câu trả lời của bạn..."
                        />
                    </Form.Group>
                );

            case 'TEXT_INPUT': 
                return (
                    <Form.Group>
                        <Form.Control
                            type="text"
                            value={answerValue}
                            onChange={(e) => handleAnswerChange(question.questionId, e.target.value)}
                            placeholder="Nhập câu trả lời ngắn..."
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
    
    if (surveyDetails.canRespond === false && !error) {
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