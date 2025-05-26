// src/components/CreatePostForm.js
import React, { useState, useRef, useContext } from 'react';
import { Button, Card, Col, Form, Row } from 'react-bootstrap';
import MySpinner from './layouts/MySpinner';
import { MyUserContext } from '../configs/Contexts'; // Assuming MyUserContext provides currentUser

const CreatePostForm = ({ onPostCreated, isSubmitting, authApis, endpoints, handleAuthAction }) => {
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

        // Parent component will handle the submission logic via onPostCreated
        // or we can directly call handleAuthAction if preferred
        const result = await handleAuthAction(
            () => authApis().post(endpoints['posts'], formData, { headers: { "Content-Type": "multipart/form-data" } }),
            "Lỗi không xác định từ máy chủ khi đăng bài.",
            "Vui lòng đăng nhập để đăng bài."
        );

        if (result && (result.status === 201 || result.status === 200) && result.data && result.data.postId) {
            onPostCreated(result.data); // Callback to update posts list in Home.js
            setNewPostText("");
            setNewPostImage(null);
            setImagePreview(null);
            if (imageInputRef.current) {
                imageInputRef.current.value = "";
            }
            alert("Đăng bài viết thành công!");
        }
    };

    if (!currentUser) {
        return null; // Or some placeholder if you want to show something when not logged in
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
                                    disabled={isSubmitting} />
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