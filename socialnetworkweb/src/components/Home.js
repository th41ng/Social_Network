// src/components/Home.js
import { useEffect, useState, useCallback, useRef } from "react";
import { Alert, Button, Card, Col, Form, InputGroup, Row } from "react-bootstrap";
import Apis, { endpoints, authApis } from "../configs/Apis";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import MySpinner from "./layouts/MySpinner";
// import { MyUserContext } from "../configs/Contexts"; // Bỏ comment nếu bạn dùng UserContext

const Home = () => {
    const [posts, setPosts] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [canLoadMore, setCanLoadMore] = useState(true);
    const [loadingPosts, setLoadingPosts] = useState(false); // Loading cho danh sách bài viết
    const [q] = useSearchParams();
    const nav = useNavigate();
    const [showComments, setShowComments] = useState({});
    const [newCommentContent, setNewCommentContent] = useState({}); 
    const [submittingComment, setSubmittingComment] = useState({});

    // === STATE CHO VIỆC TẠO BÀI VIẾT MỚI ===
    const [newPostText, setNewPostText] = useState(""); 
    const [newPostImage, setNewPostImage] = useState(null); 
    const [isSubmittingPost, setIsSubmittingPost] = useState(false); 
    const imageInputRef = useRef(null); 
    // const currentUser = useContext(MyUserContext); 
    // === KẾT THÚC STATE CHO TẠO BÀI VIẾT MỚI ===

    const loadPosts = useCallback(async (pageToLoad) => {
        if (!canLoadMore && pageToLoad > 1) return;
        setLoadingPosts(true);
        try {
            let url = `${endpoints['posts']}?page=${pageToLoad}`;
            const kw = q.get("kw");
            if (kw) url += `&kw=${kw}`;
            
            const res = await Apis.get(url);
            if (res.data && Array.isArray(res.data)) {
                if (res.data.length === 0) {
                    setCanLoadMore(false);
                    if (pageToLoad === 1) setPosts([]);
                } else {
                    if (pageToLoad === 1) {
                        setPosts(res.data);
                    } else {
                        setPosts(prevPosts => [...prevPosts, ...res.data]);
                    }
                    setCanLoadMore(true); 
                }
            } else {
                console.error("Dữ liệu API /posts trả về không phải là mảng hoặc không hợp lệ:", res.data);
                setCanLoadMore(false);
                if (pageToLoad === 1) setPosts([]);
            }
        } catch (ex) {
            console.error("Lỗi khi tải danh sách bài viết:", ex);
            setCanLoadMore(false);
        } finally {
            setLoadingPosts(false);
        }
    }, [q, canLoadMore]);

    const formatDate = (dateStr) => {
        if (!dateStr) return "Không rõ thời gian";
        const date = new Date(dateStr);
        if (isNaN(date.getTime())) {
            return "Thời gian không hợp lệ";
        }
        return date.toLocaleString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
    };

    const toggleComments = (postId) => {
        setShowComments((prevState) => ({
            ...prevState,
            [postId]: !prevState[postId],
        }));
    };

    const handlePostReactionClick = async (postId, reactionType) => {
        try {
            const res = await authApis().post(endpoints['post-reactions'](postId), { type: reactionType });
            setPosts(prev => prev.map(p => p.postId === postId ? {...p, reactions: res.data} : p));
        } catch (error) { 
            console.error("Lỗi reaction bài viết:", error);
            if (error.response && error.response.status === 401) {
                alert("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!");
                nav("/login");
            } else {
                alert("Có lỗi xảy ra khi tương tác với bài viết.");
            }
        }
    };
    
    const handleCommentReactionClick = async (postId, commentId, reactionType) => {
        try {
            const res = await authApis().post(endpoints['comment-reactions'](commentId), { type: reactionType });
            setPosts(prevPosts => prevPosts.map(p => {
                if (p.postId === postId) {
                    return {...p, comments: p.comments?.map(c => 
                            c.commentId === commentId ? { ...c, reactions: res.data } : c
                        )};
                }
                return p;
            }));
        } catch (error) {
            console.error(`Lỗi reaction cho bình luận ${commentId}:`, error);
            if (error.response && error.response.status === 401) {
                alert("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!");
                nav("/login");
            } else {
                alert("Có lỗi xảy ra khi tương tác với bình luận.");
            }
        }
    };

    const handleAddComment = async (postId) => {
        const content = newCommentContent[postId];
        if (!content || content.trim() === "") { alert("Vui lòng nhập nội dung bình luận."); return; }
        setSubmittingComment(prev => ({...prev, [postId]: true}));
        try {
            const res = await authApis().post(endpoints['add-comment'](postId), { content: content.trim() });
            const newCommentDTO = res.data;
            if (newCommentDTO && newCommentDTO.commentId) {
                setPosts(prev => prev.map(p => {
                    if (p.postId === postId) {
                        const currentComments = p.comments || [];
                        return { 
                            ...p, 
                            comments: [newCommentDTO, ...currentComments], 
                            commentCount: (Number(p.commentCount) || currentComments.length) + 1 
                        };
                    }
                    return p;
                }));
                setNewCommentContent(prev => ({ ...prev, [postId]: '' }));
            } else { throw new Error("Dữ liệu bình luận trả về không hợp lệ từ server."); }
        } catch (error) {
            console.error(`Lỗi khi thêm bình luận cho bài viết ${postId}:`, error);
            const errorMsg = error.response?.data?.message || error.response?.data || (error.message || "Lỗi không xác định khi thêm bình luận.");
            if (error.response && error.response.status === 401) { alert("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!"); nav("/login"); } 
            else { alert(`Lỗi thêm bình luận: ${typeof errorMsg === 'string' ? errorMsg : JSON.stringify(errorMsg)}`); }
        } finally { setSubmittingComment(prev => ({...prev, [postId]: false})); }
    };

    // === HÀM XỬ LÝ TẠO BÀI VIẾT MỚI ===
    const handleCreateNewPost = async (e) => {
        e.preventDefault(); 

        if (!newPostText.trim() && !newPostImage) {
            alert("Vui lòng nhập nội dung hoặc chọn hình ảnh cho bài viết.");
            return;
        }
        setIsSubmittingPost(true); 
        try {
            const formData = new FormData();
            formData.append("content", newPostText.trim());
            if (newPostImage) {
                formData.append("imageFile", newPostImage); 
            }

            const res = await authApis().post(endpoints['posts'], formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
            });
            const newPostDTO = res.data; 

            if (newPostDTO && newPostDTO.postId) {
                setPosts(prevPosts => [newPostDTO, ...prevPosts]);
                setNewPostText("");
                setNewPostImage(null);
                if (imageInputRef.current) { 
                    imageInputRef.current.value = "";
                }
                alert("Đăng bài viết thành công!");
                // Tùy chọn: Mở rộng phần comment của bài viết mới nếu muốn
                // toggleComments(newPostDTO.postId); 
            } else {
                throw new Error("Không thể tạo bài viết do lỗi dữ liệu trả về từ máy chủ.");
            }
        } catch (error) {
            console.error("Lỗi khi tạo bài viết mới:", error);
            const errorMsg = error.response?.data?.message || error.response?.data || (error.message || "Lỗi không xác định từ máy chủ.");
            if (error.response && error.response.status === 401) {
                alert("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!");
                nav("/login");
            } else {
                alert(`Lỗi tạo bài viết: ${typeof errorMsg === 'string' ? errorMsg : JSON.stringify(errorMsg)}`);
            }
        } finally {
            setIsSubmittingPost(false); 
        }
    };
    // === KẾT THÚC HÀM TẠO BÀI VIẾT MỚI ===

    useEffect(() => {
        setCurrentPage(1); 
        setCanLoadMore(true);
    }, [q]);

    useEffect(() => {
        if (currentPage > 0) {
            loadPosts(currentPage);
        } else if (currentPage === 0 && q.get("kw")) {
            setPosts([]);
        }
    }, [currentPage, loadPosts, q]);


    const loadMore = () => {
        if (!loadingPosts && canLoadMore) {
            setCurrentPage(prevPage => prevPage + 1);
        }
    };
    
    if (loadingPosts && currentPage === 1 && posts.length === 0 && !q.get("kw")) {
        return <MySpinner />;
    }

    return (
        <>
            {/* === FORM TẠO BÀI VIẾT MỚI === */}
            <Row className="justify-content-center mt-3">
                <Col md={8}>
                    <Card className="mb-4 shadow-sm">
                        <Card.Header as="h5">Tạo bài viết</Card.Header>
                        <Card.Body>
                            <Form onSubmit={handleCreateNewPost}>
                                <Form.Group className="mb-3">
                                    {/* Optional: Display current user's avatar here if available from context */}
                                    {/* {currentUser && <img src={currentUser.avatar || "..."} />} */}
                                    <Form.Control
                                        as="textarea"
                                        rows={3}
                                        placeholder="Bạn đang nghĩ gì?"
                                        value={newPostText}
                                        onChange={(e) => setNewPostText(e.target.value)}
                                        disabled={isSubmittingPost}
                                    />
                                </Form.Group>
                                <Form.Group controlId="formFilePostImage" className="mb-3">
                                    <Form.Label>Thêm ảnh (tùy chọn)</Form.Label>
                                    <Form.Control 
                                        type="file" 
                                        accept="image/*"
                                        ref={imageInputRef}
                                        onChange={(e) => setNewPostImage(e.target.files[0])}
                                        disabled={isSubmittingPost}
                                    />
                                </Form.Group>
                                {newPostImage && (
                                    <div className="mb-3 text-center">
                                        <img 
                                            src={URL.createObjectURL(newPostImage)} 
                                            alt="Xem trước ảnh tải lên" 
                                            style={{maxWidth: '100%', maxHeight: '200px', marginTop: '10px', border: '1px solid #dee2e6', borderRadius: '0.25rem', objectFit: 'contain'}} 
                                        />
                                    </div>
                                )}
                                <Button variant="primary" type="submit" disabled={isSubmittingPost} className="w-100">
                                    {isSubmittingPost ? <><MySpinner animation="border" size="sm" as="span" role="status" aria-hidden="true" /> Đang đăng...</> : "Đăng bài"}
                                </Button>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
            {/* === KẾT THÚC FORM TẠO BÀI VIẾT MỚI === */}

            {posts.length === 0 && !loadingPosts && !canLoadMore && ( 
                <Alert variant="info" className="m-2 text-center">Không có bài viết nào.</Alert> 
            )}
            
            <Row className="justify-content-center">
                <Col md={8}>
                    {posts.map((post) => (
                        <Card key={post.postId} className="mb-3 shadow-sm rounded">
                            <Card.Body>
                                {/* Phần hiển thị thông tin user và nội dung bài viết */}
                                <div className="d-flex align-items-center mb-3">
                                    <img src={post.userAvatar || "https://via.placeholder.com/40"} alt={`${post.userFullName || 'User'}'s avatar`} width="40" height="40" className="rounded-circle me-2"/>
                                    <div>
                                        <strong>{post.userFullName || "Ẩn danh"}</strong><br />
                                        <small className="text-muted">{formatDate(post.createdAt)}</small>
                                    </div>
                                </div>
                                <Card.Text style={{ whiteSpace: "pre-wrap" }}>{post.content}</Card.Text>
                                {post.image && (<Card.Img variant="bottom" src={post.image} alt={`Ảnh bài viết ${post.postId}`} className="mt-2" style={{ maxHeight: "450px", objectFit: "contain", borderRadius: "0.25rem" }}/> )}

                                {/* Phần reactions và các nút của bài viết */}
                                <div className="mt-3">
                                    {post.reactions && Object.keys(post.reactions).length > 0 && (
                                        <div className="mb-2">
                                            {Object.entries(post.reactions).map(([reaction, count]) => (
                                                <span key={reaction} className="me-3">
                                                    {reaction === 'like' && '👍'}
                                                    {reaction === 'haha' && '😂'}
                                                    {reaction === 'heart' && '❤️'}
                                                    {' '}<small className="text-muted">({count})</small>
                                                </span>
                                            ))}
                                        </div>
                                    )}
                                    <div className="d-flex justify-content-start align-items-center mb-2">
                                        <Button variant="outline-primary" size="sm" className="me-2" onClick={() => handlePostReactionClick(post.postId, 'like')}>👍 Thích</Button>
                                        <Button variant="outline-primary" size="sm" className="me-2" onClick={() => handlePostReactionClick(post.postId, 'haha')}>😂 Haha</Button>
                                        <Button variant="outline-primary" size="sm" onClick={() => handlePostReactionClick(post.postId, 'heart')}>❤️ Tim</Button>
                                    </div>
                                    <Button variant="outline-secondary" size="sm" onClick={() => toggleComments(post.postId)}>
                                        💬 Bình luận ({post.commentCount !== undefined ? post.commentCount : (post.comments ? post.comments.length : 0)})
                                    </Button>
                                </div>

                                {/* Phần hiển thị bình luận và form thêm bình luận */}
                                {showComments[post.postId] && (
                                    <div className="mt-3 border-top pt-3">
                                        <h5 className="mb-3">Bình luận</h5>
                                        <Form className="mb-3" onSubmit={(e) => { e.preventDefault(); handleAddComment(post.postId); }}>
                                            <InputGroup>
                                                <Form.Control as="textarea" rows={2} placeholder="Viết bình luận của bạn..." value={newCommentContent[post.postId] || ''} onChange={(e) => setNewCommentContent(prev => ({...prev, [post.postId]: e.target.value }))} required disabled={submittingComment[post.postId]} />
                                                <Button variant="primary" type="submit" disabled={submittingComment[post.postId]}> {submittingComment[post.postId] ? <MySpinner animation="border" size="sm" /> : 'Gửi'} </Button>
                                            </InputGroup>
                                        </Form>
                                        {post.comments && post.comments.length > 0 ? (
                                            post.comments.map((comment) => (
                                                <div key={comment.commentId} className="mb-3 p-2 bg-light rounded comment-item">
                                                    <div className="d-flex align-items-start mb-1">
                                                        <img src={comment.userAvatar || "https://via.placeholder.com/30"} alt={`${comment.userFullName || 'User'}'s avatar`} width="30" height="30" className="rounded-circle me-2 mt-1" />
                                                        <div className="flex-grow-1">
                                                            <strong>{comment.userFullName || "Người dùng"}</strong>
                                                            <p style={{ marginBottom: '0.25rem', whiteSpace: "pre-wrap" }}>{comment.content}</p>
                                                            <small className="text-muted">{formatDate(comment.createdAt)}</small>
                                                            {comment.reactions && Object.keys(comment.reactions).length > 0 && ( <div className="mt-1"> {Object.entries(comment.reactions).map(([type, count]) => ( <span key={type} className="me-2" style={{fontSize: '0.8em'}}> {type === 'like' && '👍'} {type === 'haha' && '😂'} {type === 'heart' && '❤️'} {' '}({count}) </span> ))} </div> )}
                                                            <div className="mt-1 comment-action-buttons">
                                                                <Button variant="link" size="sm" className="p-0 me-2 text-decoration-none reaction-button-comment" onClick={() => handleCommentReactionClick(post.postId, comment.commentId, 'like')}>Thích</Button>
                                                                <Button variant="link" size="sm" className="p-0 me-2 text-decoration-none reaction-button-comment" onClick={() => handleCommentReactionClick(post.postId, comment.commentId, 'haha')}>Haha</Button>
                                                                <Button variant="link" size="sm" className="p-0 text-decoration-none reaction-button-comment" onClick={() => handleCommentReactionClick(post.postId, comment.commentId, 'heart')}>Tim</Button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            ))
                                        ) : ( <p>Chưa có bình luận nào.</p> )}
                                    </div>
                                )}
                            </Card.Body>
                        </Card>
                    ))}
                </Col>
            </Row>

            {/* Phần hiển thị nút Xem thêm và MySpinner khi load more */}
            {loadingPosts && currentPage > 1 && <div className="text-center my-3"><MySpinner /></div>}
            {!loadingPosts && canLoadMore && posts.length > 0 && (
                <div className="text-center my-3">
                    <Button variant="primary" onClick={loadMore}>Xem thêm...</Button>
                </div>
            )}
            {!loadingPosts && !canLoadMore && posts.length > 0 && currentPage > 0 && (
                 <Alert variant="light" className="text-center m-2">Đã xem hết bài viết.</Alert>
            )}
        </>
    );
};

export default Home;