// src/components/CreatePostForm.js
import React, { useState, useRef, useContext } from 'react';
import { Button, Card, Col, Form, Row } from 'react-bootstrap';
import MySpinner from './layouts/MySpinner';
import { MyUserContext } from '../configs/Contexts';

// Nhận thêm prop setIsSubmitting
const CreatePostForm = ({ onPostCreated, isSubmitting, setIsSubmitting, authApis, endpoints, handleAuthAction }) => {
    const currentUser = useContext(MyUserContext);
    const [newPostText, setNewPostText] = useState("");
    const [newPostImage, setNewPostImage] = useState(null);
    const [imagePreview, setImagePreview] = useState(null);
    const imageInputRef = useRef(null);

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        setNewPostImage(file);
        if (file) {
            setImagePreview(URL.createObjectURL(file));
        } else {
            setImagePreview(null);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!newPostText.trim() && !newPostImage) {
            alert("Vui lòng nhập nội dung hoặc chọn hình ảnh cho bài viết.");
            return;
        }

        const formData = new FormData();
        formData.append("content", newPostText.trim());
        if (newPostImage) {
            formData.append("imageFile", newPostImage);
        }

        if (setIsSubmitting) { // Kiểm tra xem prop có được truyền không
            setIsSubmitting(true); // <--- BẮT ĐẦU LOADING
        }

        try {
            const result = await handleAuthAction( // handleAuthAction là async và được truyền từ Home
                () => authApis().post(endpoints['posts'], formData, { headers: { "Content-Type": "multipart/form-data" } }),
                "Lỗi không xác định từ máy chủ khi đăng bài.",
                "Vui lòng đăng nhập để đăng bài."
            );

            if (result && (result.status === 201 || result.status === 200) && result.data && result.data.postId) {
                onPostCreated(result.data); 
                setNewPostText("");
                setNewPostImage(null);
                setImagePreview(null);
                if (imageInputRef.current) {
                    imageInputRef.current.value = "";
                }
                alert("Đăng bài viết thành công!");
            }
            // Nếu handleAuthAction đã alert lỗi rồi thì không cần alert thêm ở đây
        } catch (error) {
            // Lỗi từ handleAuthAction (nếu nó ném lỗi thay vì chỉ trả về object lỗi)
            // Hoặc lỗi không mong muốn khác
            console.error("Lỗi trong quá trình handleSubmit của CreatePostForm:", error);
            // Không cần alert lại nếu handleAuthAction đã alert
        } finally {
            if (setIsSubmitting) {
                setIsSubmitting(false); // <--- KẾT THÚC LOADING (dù thành công hay thất bại)
            }
        }
    };

    if (!currentUser) {
        return null; 
    }

    return (
        <Row className="justify-content-center mt-3">
            <Col md={8}>
                <Card className="mb-4 shadow-sm">
                    <Card.Header as="h5" className="d-flex align-items-center">
                        {currentUser.avatar && (
                            <img src={currentUser.avatar} alt={`${currentUser.username}'s Avatar`} style={{ width: "40px", height: "40px", borderRadius: "50%", marginRight: "15px" }} />
                        )}
                        Tạo bài viết
                    </Card.Header>
                    <Card.Body>
                        <Form onSubmit={handleSubmit}>
                            <Form.Group className="mb-3">
                                <Form.Control
                                    as="textarea" rows={3}
                                    placeholder={`Bạn đang nghĩ gì, ${currentUser.fullName || currentUser.username}?`}
                                    value={newPostText}
                                    onChange={(e) => setNewPostText(e.target.value)}
                                    disabled={isSubmitting} /> {/* Nút bị disable dựa trên prop isSubmitting */}
                            </Form.Group>
                            <Form.Group controlId="formFilePostImageHome" className="mb-3">
                                <Form.Label>Thêm ảnh (tùy chọn)</Form.Label>
                                <Form.Control
                                    type="file" accept="image/*"
                                    ref={imageInputRef}
                                    onChange={handleImageChange}
                                    disabled={isSubmitting} />
                            </Form.Group>
                            {imagePreview && (
                                <div className="mb-3 text-center">
                                    <img src={imagePreview} alt="Xem trước ảnh" style={{ maxWidth: '100%', maxHeight: '200px', objectFit: 'contain', borderRadius: '0.25rem' }} />
                                </div>
                            )}
                            <Button variant="primary" type="submit" disabled={isSubmitting} className="w-100">
                                {/* Hiển thị spinner và text dựa trên prop isSubmitting */}
                                {isSubmitting ? <><MySpinner animation="border" size="sm" as="span" /> Đang đăng...</> : "Đăng bài"}
                            </Button>
                        </Form>
                    </Card.Body>
                </Card>
            </Col>
        </Row>
    );
};

export default CreatePostForm;