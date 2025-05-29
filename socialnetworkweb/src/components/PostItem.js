import React, { useState, useContext } from 'react';
import { Card, Button, Form, InputGroup, Dropdown, Alert } from 'react-bootstrap';
import MySpinner from './layouts/MySpinner'; // Assuming MySpinner is in layouts
import { MyUserContext } from '../configs/Contexts'; // For currentUser

import '../styles/PostItem.css'

// Bọc component PostItem với React.memo để tối ưu hiệu suất
const PostItem = React.memo(({
    post,
    currentUser,
    formatDate,
    onPostReaction,
    onDeletePost,
    onOpenEditModal,
    onToggleCommentLock,
    onCommentReaction,
    onAddComment,
    onDeleteComment,
    onOpenEditCommentModal,
    handleAuthAction, // Pass this down if actions are initiated from PostItem directly
    authApis, // Pass this down
    endpoints // Pass this down
}) => {
    const [showComments, setShowComments] = useState(false);
    const [newCommentContent, setNewCommentContent] = useState('');
    const [submittingComment, setSubmittingComment] = useState(false);

    const toggleCommentsDisplay = () => setShowComments(prev => !prev);

    const handleLocalAddComment = async () => {
        if (post && post.commentLocked) {
            alert("Bình luận đã bị khóa cho bài viết này.");
            return;
        }
        if (!newCommentContent.trim()) {
            alert("Vui lòng nhập nội dung bình luận.");
            return;
        }
        setSubmittingComment(true);
        await onAddComment(post.postId, newCommentContent.trim());
        setNewCommentContent(''); // Clear input after successful submission (parent should handle this state update)
        setSubmittingComment(false);
    };

    return (
        <Card className="mb-3 shadow-sm rounded position-relative">
            {currentUser && post.userId && currentUser.id === post.userId && (
                <div className="position-absolute top-0 end-0 p-2" style={{ zIndex: 10 }}>
                    <Dropdown>
                        <Dropdown.Toggle variant="link" id={`dropdown-post-${post.postId}`} bsPrefix="p-0" style={{ color: '#6c757d', textDecoration: 'none' }}>
                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" className="bi bi-three-dots-vertical" viewBox="0 0 16 16">
                                <path d="M9.5 13a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0zm0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0zm0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0z" />
                            </svg>
                        </Dropdown.Toggle>
                        <Dropdown.Menu align="end">
                            <Dropdown.Item onClick={() => onOpenEditModal(post)}>
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-pencil-fill me-2" viewBox="0 0 16 16"><path d="M12.854.146a.5.5 0 0 0-.707 0L10.5 1.793 14.207 5.5l1.647-1.646a.5.5 0 0 0 0-.708l-3-3zm.646 6.061L9.793 2.5 3.293 9H3.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.207l6.5-6.5zm-7.468 7.468A.5.5 0 0 1 6 13.5V13h-.5a.5.5 0 0 1-.5-.5V12h-.5a.5.5 0 0 1-.5-.5V11h-.5a.5.5 0 0 1-.5-.5V10h-.5a.499.499 0 0 1-.175-.032l-.179.178a.5.5 0 0 0-.11.168l-2 5a.5.5 0 0 0 .65.65l5-2a.5.5 0 0 0 .168-.11l.178-.178z" /></svg>
                                Sửa bài viết
                            </Dropdown.Item>
                            <Dropdown.Item onClick={() => onToggleCommentLock(post.postId)}>
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className={`bi ${post.commentLocked ? "bi-unlock-fill" : "bi-lock-fill"} me-2`} viewBox="0 0 16 16">
                                    {post.commentLocked ?
                                        <path d="M11 1a2 2 0 0 0-2 2v4a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V9a2 2 0 0 1 2-2h5V3a3 3 0 0 1 6 0v4a.5.5 0 0 1-1 0V3a2 2 0 0 0-2-2z" /> :
                                        <path d="M8 1a2 2 0 0 1 2 2v4H6V3a2 2 0 0 1 2-2zm3 6V3a3 3 0 0 0-6 0v4a2 2 0 0 0-2 2v5a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2z" />
                                    }
                                </svg>
                                {post.commentLocked ? "Mở khóa bình luận" : "Khóa bình luận"}
                            </Dropdown.Item>
                            <Dropdown.Divider />
                            <Dropdown.Item onClick={() => onDeletePost(post.postId)} className="text-danger">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-trash3-fill me-2" viewBox="0 0 16 16"><path d="M11 1.5v1h3.5a.5.5 0 0 1 0 1h-.538l-.853 10.66A2 2 0 0 1 11.115 16h-6.23a2 2 0 0 1-1.994-1.84L2.038 3.5H1.5a.5.5 0 0 1 0-1H5v-1A1.5 1.5 0 0 1 6.5 0h3A1.5 1.5 0 0 1 11 1.5Zm-5 0v1h4v-1a.5.5 0 0 0-.5-.5h-3a.5.5 0 0 0-.5.5ZM4.5 5.029l.5 8.5a.5.5 0 1 0 .998-.06l-.5-8.5a.5.5 0 1 0-.998.06Zm6.53-.528a.5.5 0 0 0-.528.47l-.5 8.5a.5.5 0 0 0 .998.058l.5-8.5a.5.5 0 0 0-.47-.058ZM8 4.5a.5.5 0 0 0-.5.5v8.5a.5.5 0 0 0 1 0V5a.5.5 0 0 0-.5-.5Z" /></svg>
                                Xóa bài viết
                            </Dropdown.Item>
                        </Dropdown.Menu>
                    </Dropdown>
                </div>
            )}
            <Card.Body>
                <div className="d-flex align-items-center mb-3">
                    <img src={post.userAvatar || "https://via.placeholder.com/40?text=User"} alt={`${post.userFullName || 'User'}'s avatar`} width="40" height="40" className="rounded-circle me-3" />
                    <div>
                        <strong>{post.userFullName || "Ẩn danh"}</strong><br />
                        <small className="text-muted">
                            {post.updatedAt && formatDate(post.updatedAt) !== formatDate(post.createdAt)
                                ? `Cập nhật lúc: ${formatDate(post.updatedAt)}`
                                : `Đăng lúc: ${formatDate(post.createdAt)}`}
                        </small>
                    </div>
                </div>
                <Card.Text style={{ whiteSpace: "pre-wrap" }}>{post.content}</Card.Text>
                {post.image && (<Card.Img variant="bottom" src={post.image} alt={`Ảnh bài viết ${post.postId}`} className="mt-2" style={{ maxHeight: "450px", objectFit: "contain", borderRadius: "0.25rem" }} />)}

                <div className="mt-3">
                    {post.reactions && Object.keys(post.reactions).length > 0 && (
                        <div className="mb-2 d-flex align-items-center">
                            {Object.entries(post.reactions).map(([reaction, count]) => (
                                <span key={reaction} className="me-3" style={{ fontSize: "0.9em" }}>
                                    {reaction === 'like' && '👍'} {reaction === 'haha' && '😂'} {reaction === 'heart' && '❤️'}
                                    <small className="text-muted ps-1">({count})</small>
                                </span>
                            ))}
                        </div>
                    )}
                    <div className="d-flex justify-content-start align-items-center mb-2 border-top pt-2">
                        <Button variant="outline-primary" size="sm" className="me-2" onClick={() => onPostReaction(post.postId, 'like')}>👍 Thích</Button>
                        <Button variant="outline-primary" size="sm" className="me-2" onClick={() => onPostReaction(post.postId, 'haha')}>😂 Haha</Button>
                        <Button variant="outline-primary" size="sm" className="me-3" onClick={() => onPostReaction(post.postId, 'heart')}>❤️ Tim</Button>
                        <Button variant="outline-secondary" size="sm" onClick={toggleCommentsDisplay}>
                            💬 Bình luận ({post.commentCount !== undefined ? post.commentCount : (Array.isArray(post.comments) ? post.comments.length : 0)})
                        </Button>
                    </div>
                </div>

                {showComments && (
                    <div className="mt-3 border-top pt-3">
                        <h6 className="mb-3">Bình luận {post.commentLocked && <small className="text-danger ms-2">(Đã khóa)</small>}</h6>
                        {!post.commentLocked && (
                            <Form className="mb-3" onSubmit={(e) => { e.preventDefault(); handleLocalAddComment(); }}>
                                <InputGroup>
                                    <Form.Control as="textarea" rows={2} placeholder="Viết bình luận của bạn..." value={newCommentContent} onChange={(e) => setNewCommentContent(e.target.value)} required disabled={submittingComment} />
                                    <Button variant="primary" type="submit" disabled={submittingComment}> {submittingComment ? <MySpinner animation="border" size="sm" /> : 'Gửi'} </Button>
                                </InputGroup>
                            </Form>
                        )}
                        {(Array.isArray(post.comments) && post.comments.length > 0) ? (
                            post.comments.map((comment) => (
                                <div key={comment.commentId} className="mb-3 p-2 bg-light rounded comment-item position-relative">
                                    {currentUser && comment.userId &&
                                        (currentUser.id === comment.userId || (post && post.userId && currentUser.id === post.userId)) && (
                                            <div className="position-absolute top-0 end-0 p-1" style={{ zIndex: 5 }}>
                                                <Dropdown>
                                                    <Dropdown.Toggle variant="link" bsPrefix="p-0" size="sm" style={{ color: '#6c757d', textDecoration: 'none', lineHeight: 1 }}>
                                                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" className="bi bi-three-dots" viewBox="0 0 16 16">
                                                            <path d="M3 9.5a1.5 1.5 0 1 1 0-3 1.5 1.5 0 0 1 0 3zm5 0a1.5 1.5 0 1 1 0-3 1.5 1.5 0 0 1 0 3zm5 0a1.5 1.5 0 1 1 0-3 1.5 1.5 0 0 1 0 3z" />
                                                        </svg>
                                                    </Dropdown.Toggle>
                                                    <Dropdown.Menu align="end">
                                                        {currentUser.id === comment.userId &&
                                                            <Dropdown.Item onClick={() => onOpenEditCommentModal(post, comment)}>
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-pencil-fill me-2" viewBox="0 0 16 16"><path d="M12.854.146a.5.5 0 0 0-.707 0L10.5 1.793 14.207 5.5l1.647-1.646a.5.5 0 0 0 0-.708l-3-3zm.646 6.061L9.793 2.5 3.293 9H3.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.207l6.5-6.5zm-7.468 7.468A.5.5 0 0 1 6 13.5V13h-.5a.5.5 0 0 1-.5-.5V12h-.5a.5.5 0 0 1-.5-.5V11h-.5a.5.5 0 0 1-.5-.5V10h-.5a.499.499 0 0 1-.175-.032l-.179.178a.5.5 0 0 0-.11.168l-2 5a.5.5 0 0 0 .65.65l5-2a.5.5 0 0 0 .168-.11l.178-.178z" /></svg>
                                                                Sửa
                                                            </Dropdown.Item>
                                                        }
                                                        <Dropdown.Item onClick={() => onDeleteComment(post.postId, comment.commentId)} className="text-danger">
                                                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-trash3-fill me-2" viewBox="0 0 16 16"><path d="M11 1.5v1h3.5a.5.5 0 0 1 0 1h-.538l-.853 10.66A2 2 0 0 1 11.115 16h-6.23a2 2 0 0 1-1.994-1.84L2.038 3.5H1.5a.5.5 0 0 1 0-1H5v-1A1.5 1.5 0 0 1 6.5 0h3A1.5 1.5 0 0 1 11 1.5Zm-5 0v1h4v-1a.5.5 0 0 0-.5-.5h-3a.5.5 0 0 0-.5.5ZM4.5 5.029l.5 8.5a.5.5 0 1 0 .998-.06l-.5-8.5a.5.5 0 1 0-.998.06Zm6.53-.528a.5.5 0 0 0-.528.47l-.5 8.5a.5.5 0 0 0 .998.058l.5-8.5a.5.5 0 0 0-.47-.058ZM8 4.5a.5.5 0 0 0-.5.5v8.5a.5.5 0 0 0 1 0V5a.5.5 0 0 0-.5-.5Z" /></svg>
                                                            Xóa
                                                        </Dropdown.Item>
                                                    </Dropdown.Menu>
                                                </Dropdown>
                                            </div>
                                        )}
                                    <div className="d-flex align-items-start mb-1">
                                        <img src={comment.userAvatar || "https://via.placeholder.com/30?text=U"} alt={`${comment.userFullName || 'User'}'s avatar`} width="30" height="30" className="rounded-circle me-2 mt-1" />
                                        <div className="flex-grow-1">
                                            <strong>{comment.userFullName || "Người dùng"}</strong>
                                            <p style={{ marginBottom: '0.25rem', whiteSpace: "pre-wrap", fontSize: "0.95em" }}>{comment.content}</p>
                                            <small className="text-muted">
                                                {comment.updatedAt && formatDate(comment.updatedAt) !== formatDate(comment.createdAt)
                                                    ? `Cập nhật lúc: ${formatDate(comment.updatedAt)}`
                                                    : `Đăng lúc: ${formatDate(comment.createdAt)}`}
                                            </small>

                                            {comment.reactions && Object.keys(comment.reactions).length > 0 && (
                                                <div className="mt-1 d-flex align-items-center">
                                                    {Object.entries(comment.reactions).map(([type, count]) => (<span key={type} className="me-2" style={{ fontSize: '0.8em' }}> {type === 'like' && '👍'} {type === 'haha' && '😂'} {type === 'heart' && '❤️'} <small className="text-muted ps-1">({count})</small> </span>))}
                                                </div>
                                            )}
                                            <div className="mt-1 comment-action-buttons">
                                                <Button variant="link" size="sm" className="p-0 me-2 text-decoration-none reaction-button-comment" onClick={() => onCommentReaction(post.postId, comment.commentId, 'like')}>Thích</Button>
                                                <Button variant="link" size="sm" className="p-0 me-2 text-decoration-none reaction-button-comment" onClick={() => onCommentReaction(post.postId, comment.commentId, 'haha')}>Haha</Button>
                                                <Button variant="link" size="sm" className="p-0 text-decoration-none reaction-button-comment" onClick={() => onCommentReaction(post.postId, comment.commentId, 'heart')}>Tim</Button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            ))
                        ) : (<p className="text-muted small">Chưa có bình luận nào.</p>)}
                    </div>
                )}
            </Card.Body>
        </Card>
    );
});

export default PostItem;
