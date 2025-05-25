// src/components/EditPostModal.js
import React, { useState, useEffect, useRef } from 'react';
import { Modal, Button, Form } from 'react-bootstrap';
import MySpinner from './layouts/MySpinner';

const EditPostModal = ({
    show,
    onHide,
    postToEdit,
    onUpdatePost,
    isSubmitting,
    authApis, // Pass if needed for direct API calls, or handle in parent
    endpoints // Pass if needed
}) => {
    const [editText, setEditText] = useState("");
    const [editImageFile, setEditImageFile] = useState(null);
    const [editImagePreview, setEditImagePreview] = useState(null);
    const [isRequestingImageRemoval, setIsRequestingImageRemoval] = useState(false);
    const editImageInputRef = useRef(null);

    useEffect(() => {
        if (postToEdit) {
            setEditText(postToEdit.content || "");
            setEditImagePreview(postToEdit.image || null);
            setEditImageFile(null);
            setIsRequestingImageRemoval(false);
        }
    }, [postToEdit]);

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setEditImageFile(file);
            setEditImagePreview(URL.createObjectURL(file));
            setIsRequestingImageRemoval(false); // If new image is selected, don't remove old one by default
        } else {
            // If file selection is cancelled, revert to original/current state
            setEditImageFile(null);
            setEditImagePreview(isRequestingImageRemoval ? null : (postToEdit ? postToEdit.image : null));
        }
    };

    const toggleImageRemoval = () => {
        if (isRequestingImageRemoval) { // If currently set to remove, undo it
            setIsRequestingImageRemoval(false);
            setEditImagePreview(postToEdit.image || null); // Show original image
        } else { // If not set to remove, set it
            setIsRequestingImageRemoval(true);
            setEditImagePreview(null); // Clear preview
        }
        setEditImageFile(null); // Clear any newly selected file
        if (editImageInputRef.current) {
            editImageInputRef.current.value = "";
        }
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!postToEdit) return;

        if (!editText.trim() && !editImageFile && isRequestingImageRemoval && postToEdit.image) {
            alert("Nội dung bài viết không được để trống nếu bạn chọn xóa ảnh.");
            return;
        }
        if (!editText.trim() && !editImagePreview && !editImageFile && !isRequestingImageRemoval) {
             alert("Nội dung bài viết hoặc hình ảnh không được để trống.");
            return;
        }

        const formData = new FormData();
        formData.append("postId", postToEdit.postId);
        formData.append("content", editText.trim());

        if (editImageFile) {
            formData.append("imageFile", editImageFile);
        } else if (isRequestingImageRemoval && postToEdit.image) {
            formData.append("removeCurrentImage", "true");
        }
        // The actual API call will be handled by onUpdatePost passed from Home.js
        onUpdatePost(formData);
    };

    if (!postToEdit) return null;

    return (
        <Modal show={show} onHide={onHide} centered backdrop="static" keyboard={!isSubmitting}>
            <Modal.Header closeButton={!isSubmitting}>
                <Modal.Title>Chỉnh sửa bài viết</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form onSubmit={handleSubmit}>
                    <Form.Group className="mb-3">
                        <Form.Label>Nội dung</Form.Label>
                        <Form.Control
                            as="textarea" rows={5} value={editText}
                            onChange={(e) => setEditText(e.target.value)}
                            disabled={isSubmitting}
                        />
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Hình ảnh</Form.Label>
                        {editImagePreview && (
                            <div className="mb-2 text-center">
                                <img src={editImagePreview} alt="Xem trước" style={{ maxWidth: '100%', maxHeight: '200px', objectFit: 'contain', borderRadius: '0.25rem' }} />
                            </div>
                        )}
                        <Form.Control type="file" accept="image/*" ref={editImageInputRef}
                            onChange={handleImageChange} disabled={isSubmitting} />

                        {postToEdit.image && ( // Show remove option only if there was an original image
                            <Button
                                variant="link" size="sm"
                                className={`p-0 mt-1 d-block text-center ${isRequestingImageRemoval ? 'text-danger fw-bold' : 'text-muted'}`}
                                onClick={toggleImageRemoval}
                                disabled={isSubmitting} >
                                {isRequestingImageRemoval ? "Hoàn tác (giữ lại ảnh gốc)" : "Xóa ảnh này khỏi bài viết"}
                            </Button>
                        )}
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

export default EditPostModal;