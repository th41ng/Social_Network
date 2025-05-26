// src/components/EditCommentModal.js
import React, { useState, useEffect } from 'react';
import { Modal, Button, Form } from 'react-bootstrap';
import MySpinner from './layouts/MySpinner';

const EditCommentModal = ({
    show,
    onHide,
    commentToEdit, // Will be an object like { postId, commentId, content, userId }
    onUpdateComment,
    isSubmitting
}) => {
    const [editCommentText, setEditCommentText] = useState("");

    useEffect(() => {
        if (commentToEdit) {
            setEditCommentText(commentToEdit.content || "");
        }
    }, [commentToEdit]);

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!commentToEdit || !editCommentText.trim()) {
            alert("Nội dung bình luận không được để trống.");
            return;
        }
        // The actual API call will be handled by onUpdateComment passed from Home.js
        onUpdateComment(commentToEdit.commentId, editCommentText.trim(), commentToEdit.postId);
    };

    if (!commentToEdit) return null;

    return (
        <Modal show={show} onHide={onHide} centered backdrop="static" keyboard={!isSubmitting}>
            <Modal.Header closeButton={!isSubmitting}>
                <Modal.Title>Chỉnh sửa bình luận</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form onSubmit={handleSubmit}>
                    <Form.Group className="mb-3">
                        <Form.Label>Nội dung bình luận</Form.Label>
                        <Form.Control
                            as="textarea" rows={3} value={editCommentText}
                            onChange={(e) => setEditCommentText(e.target.value)}
                            disabled={isSubmitting}
                            required
                        />
                    </Form.Group>
                    <div className="d-flex justify-content-end">
                        <Button variant="secondary" onClick={onHide} className="me-2" disabled={isSubmitting}>Hủy</Button>
                        <Button variant="primary" type="submit" disabled={isSubmitting}>
                            {isSubmitting ? <><MySpinner animation="border" size="sm" as="span" /> Đang lưu...</> : "Lưu thay đổi"}
                        </Button>
                    </div>
                </Form>
            </Modal.Body>
        </Modal>
    );
};

export default EditCommentModal;