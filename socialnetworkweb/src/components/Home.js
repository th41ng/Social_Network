// // src/components/Home.js
// import { useEffect, useState, useCallback, useRef, useContext } from "react";
// import { Alert, Button, Card, Col, Form, InputGroup, Row, Dropdown, Modal } from "react-bootstrap";
// import Apis, { endpoints, authApis } from "../configs/Apis";
// import { useNavigate, useSearchParams } from "react-router-dom";
// import cookie from "react-cookies";
// import MySpinner from "./layouts/MySpinner";
// import { MyUserContext, MyDispatchContext } from "../configs/Contexts";

// const Home = () => {
//     // State cho danh sách posts và pagination
//     const [posts, setPosts] = useState([]);
//     const [currentPage, setCurrentPage] = useState(1);
//     const [canLoadMore, setCanLoadMore] = useState(true);
//     const [loadingPosts, setLoadingPosts] = useState(false);
//     const [q] = useSearchParams();
//     const nav = useNavigate();

//     // State cho comment mới
//     const [showComments, setShowComments] = useState({});
//     const [newCommentContent, setNewCommentContent] = useState({});
//     const [submittingComment, setSubmittingComment] = useState({}); // Dùng khi gửi comment mới

//     // State cho việc tạo bài viết mới
//     const [newPostText, setNewPostText] = useState("");
//     const [newPostImage, setNewPostImage] = useState(null);
//     const imageInputRef = useRef(null);

//     const currentUser = useContext(MyUserContext);
//     const dispatch = useContext(MyDispatchContext);

//     // State chung cho việc submit (POST, PUT) - dùng cho cả post và comment modal
//     const [isSubmittingPost, setIsSubmittingPost] = useState(false);

//     // State cho việc sửa bài viết
//     const [editingPost, setEditingPost] = useState(null);
//     const [showEditModal, setShowEditModal] = useState(false);
//     const [editText, setEditText] = useState("");
//     const [editImageFile, setEditImageFile] = useState(null);
//     const [editImagePreview, setEditImagePreview] = useState(null);
//     const editImageInputRef = useRef(null);
//     const [isRequestingImageRemoval, setIsRequestingImageRemoval] = useState(false);

//     // State cho việc sửa bình luận (Đảm bảo chỉ khai báo một lần)
//     const [editingComment, setEditingComment] = useState(null);
//     const [showEditCommentModal, setShowEditCommentModal] = useState(false);
//     const [editCommentText, setEditCommentText] = useState("");

//     // --- useEffects ---
//     useEffect(() => {
//         const token = cookie.load("token");
//         if (!token && !currentUser) {
//             nav("/login");
//         } else if (!currentUser && token) {
//             const fetchCurrentUser = async () => {
//                 try {
//                     const userRes = await authApis().get(endpoints['profile']);
//                     dispatch({ type: "login", payload: userRes.data });
//                 } catch (ex) {
//                     console.error("Lỗi tự động lấy thông tin user:", ex);
//                     if (ex.response && ex.response.status === 401) {
//                         cookie.remove("token");
//                         dispatch({ type: "logout" });
//                         nav("/login");
//                     }
//                 }
//             };
//             fetchCurrentUser();
//         }
//     }, [nav, currentUser, dispatch]);

//     const loadPosts = useCallback(async (pageToLoad, isNewSearch = false) => {
//         if (!isNewSearch && !canLoadMore && pageToLoad > 1) return;
//         if (loadingPosts && !isNewSearch && pageToLoad > 1) return;

//         setLoadingPosts(true);
//         try {
//             let url = endpoints['posts'];
//             const params = new URLSearchParams();
//             params.append('page', pageToLoad.toString());

//             const currentKw = q.get("kw");
//             if (currentKw) {
//                 params.append('kw', currentKw);
//             }
//             const fullUrl = `${url}?${params.toString()}`;

//             const res = await Apis.get(fullUrl);

//             if (res.data && Array.isArray(res.data)) {
//                 const fetchedPosts = res.data;
//                 if (fetchedPosts.length === 0) {
//                     setCanLoadMore(false);
//                     if (pageToLoad === 1 || isNewSearch) setPosts([]);
//                 } else {
//                     setPosts(prev => (pageToLoad === 1 || isNewSearch) ? fetchedPosts : [...prev, ...fetchedPosts]);
//                     setCanLoadMore(true);
//                 }
//             } else {
//                 setCanLoadMore(false);
//                 if (pageToLoad === 1 || isNewSearch) setPosts([]);
//             }
//         } catch (ex) {
//             console.error("Lỗi khi tải danh sách bài viết:", ex);
//             setCanLoadMore(false);
//         } finally {
//             setLoadingPosts(false);
//         }
//         // eslint-disable-next-line react-hooks/exhaustive-deps
//     }, [q.get("kw"), canLoadMore]);

//     useEffect(() => {
//         if (cookie.load("token") && currentUser) {
//             if (currentPage > 0) {
//                 loadPosts(currentPage, currentPage === 1 && q.get("kw") !== null);
//             }
//         }
//         // eslint-disable-next-line react-hooks/exhaustive-deps
//     }, [currentPage, currentUser]);

//     useEffect(() => {
//         setCurrentPage(1);
//         setCanLoadMore(true);
//         if (cookie.load("token") && currentUser) {
//             loadPosts(1, true);
//         }
//         // eslint-disable-next-line react-hooks/exhaustive-deps
//     }, [q.get("kw"), currentUser]);

//     // --- Helper Functions ---
//     const formatDate = (dateStr) => {
//         if (!dateStr) return "Không rõ thời gian";
//         const date = new Date(dateStr);
//         if (isNaN(date.getTime())) return "Thời gian không hợp lệ";
//         return date.toLocaleString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
//     };

//     const toggleComments = (postId) => {
//         setShowComments(prevState => ({ ...prevState, [postId]: !prevState[postId] }));
//     };

//     const handleAuthAction = async (actionFunc, errorMessageDefault, unauthorizedMessage) => {
//         if (!cookie.load("token") || !currentUser) {
//             alert(unauthorizedMessage || "Vui lòng đăng nhập để thực hiện hành động này.");
//             if (!currentUser && cookie.load("token")) {
//                 cookie.remove("token");
//                 dispatch({ type: "logout" });
//             }
//             nav("/login");
//             return null;
//         }
//         try {
//             const response = await actionFunc();
//             return response;
//         } catch (error) {
//             console.error(errorMessageDefault, error);
//             let msg = errorMessageDefault;
//             if (error.response) {
//                 if (error.response.status === 401) {
//                     msg = "Phiên đăng nhập đã hết hạn hoặc không hợp lệ. Vui lòng đăng nhập lại!";
//                     cookie.remove("token");
//                     dispatch({ type: "logout" });
//                     nav("/login");
//                 } else {
//                     const errorData = error.response.data;
//                     if (errorData && (errorData.error || errorData.message || errorData.detail)) {
//                         msg = errorData.error || errorData.message || errorData.detail;
//                     } else if (typeof errorData === 'string' && errorData.length < 200 && !errorData.toLowerCase().includes("<html")) {
//                         msg = errorData;
//                     } else if (errorData && typeof errorData === 'object' && Object.keys(errorData).length > 0) {
//                         const keys = ['message', 'error', 'detail', 'title'];
//                         for (const key of keys) {
//                             if (typeof errorData[key] === 'string') {
//                                 msg = errorData[key];
//                                 break;
//                             }
//                         }
//                         if (msg === errorMessageDefault) {
//                             try { msg = `Lỗi Server: ${error.response.status} - ${JSON.stringify(errorData)}`; } catch (e) { /* ignore */ }
//                         }
//                     } else if (error.response.statusText && msg === errorMessageDefault) {
//                         msg = `Lỗi: ${error.response.status} - ${error.response.statusText}`;
//                     }
//                 }
//             } else if (error.message) {
//                 msg = `${errorMessageDefault} (Chi tiết: ${error.message})`;
//             }
//             alert(msg);
//             return error.response || { data: { error: msg }, status: error.code || 'NETWORK_ERROR' };
//         }
//     };

//     // --- Post Actions ---
//     const handlePostReactionClick = async (postId, reactionType) => {
//         const result = await handleAuthAction(
//             () => authApis().post(endpoints['post-reactions'](postId), { type: reactionType }),
//             "Có lỗi xảy ra khi tương tác với bài viết.",
//             "Vui lòng đăng nhập để thích bài viết."
//         );
//         if (result && result.status === 200 && result.data) {
//             setPosts(prev => prev.map(p => p.postId === postId ? { ...p, reactions: result.data } : p));
//         }
//     };

//     const handleCreateNewPost = async (e) => {
//         e.preventDefault();
//         if (!newPostText.trim() && !newPostImage) {
//             alert("Vui lòng nhập nội dung hoặc chọn hình ảnh cho bài viết."); return;
//         }

//         setIsSubmittingPost(true);
//         const formData = new FormData();
//         formData.append("content", newPostText.trim());
//         if (newPostImage) formData.append("imageFile", newPostImage);

//         const result = await handleAuthAction(
//             () => authApis().post(endpoints['posts'], formData, { headers: { "Content-Type": "multipart/form-data" } }),
//             "Lỗi không xác định từ máy chủ khi đăng bài.",
//             "Vui lòng đăng nhập để đăng bài."
//         );

//         if (result && (result.status === 201 || result.status === 200) && result.data && result.data.postId) {
//             const newPostDTO = result.data;
//             setPosts(prevPosts => [newPostDTO, ...prevPosts]);
//             setNewPostText("");
//             setNewPostImage(null);
//             if (imageInputRef.current) imageInputRef.current.value = "";
//             alert("Đăng bài viết thành công!");
//         }
//         setIsSubmittingPost(false);
//     };

//     const handleDeletePost = async (postIdToDelete) => {
//         if (!window.confirm("Bạn có chắc chắn muốn xóa bài viết này? Hành động này không thể hoàn tác.")) {
//             return;
//         }
//         const result = await handleAuthAction(
//             () => authApis().delete(endpoints['delete-post'](postIdToDelete)),
//             "Có lỗi xảy ra khi xóa bài viết.",
//             "Vui lòng đăng nhập để xóa bài viết."
//         );
//         if (result && (result.status === 204 || result.status === 200)) {
//             setPosts(currentPosts => currentPosts.filter(p => p.postId !== postIdToDelete));
//             alert("Xóa bài viết thành công!");
//         }
//     };

//     const openEditModal = (postToEdit) => {
//         if (!currentUser || !postToEdit.userId || currentUser.id !== postToEdit.userId) {
//             alert("Bạn không có quyền sửa bài viết này.");
//             return;
//         }
//         setEditingPost(postToEdit);
//         setEditText(postToEdit.content);
//         setEditImageFile(null);
//         setEditImagePreview(postToEdit.image || null);
//         setIsRequestingImageRemoval(false);
//         setShowEditModal(true);
//     };

//     const closeEditModal = () => {
//         setShowEditModal(false);
//         setEditingPost(null);
//         setEditText("");
//         setEditImageFile(null);
//         setEditImagePreview(null);
//         setIsRequestingImageRemoval(false);
//         if (editImageInputRef.current) {
//             editImageInputRef.current.value = "";
//         }
//     };

//     const handleEditImageChange = (e) => {
//         const file = e.target.files[0];
//         if (file) {
//             setEditImageFile(file);
//             setEditImagePreview(URL.createObjectURL(file));
//             setIsRequestingImageRemoval(false);
//         } else {
//             setEditImageFile(null);
//             setEditImagePreview(isRequestingImageRemoval ? null : (editingPost ? editingPost.image : null));
//         }
//     };

//     const handleToggleImageRemovalInEditModal = () => {
//         if (isRequestingImageRemoval) {
//             setIsRequestingImageRemoval(false);
//             setEditImagePreview(editingPost.image || null);
//         } else {
//             setIsRequestingImageRemoval(true);
//             setEditImagePreview(null);
//         }
//         setEditImageFile(null);
//         if (editImageInputRef.current) {
//             editImageInputRef.current.value = "";
//         }
//     };

//     const handleUpdatePost = async (e) => {
//         e.preventDefault();
//         if (!editingPost) return;

//         if (!editText.trim() && !editImageFile && isRequestingImageRemoval && editingPost.image) {
//             alert("Nội dung bài viết không được để trống nếu bạn chọn xóa ảnh.");
//             return;
//         }
//         if (!editText.trim() && !editImagePreview && !editImageFile && !isRequestingImageRemoval) {
//             alert("Nội dung bài viết hoặc hình ảnh không được để trống.");
//             return;
//         }

//         setIsSubmittingPost(true);
//         const formData = new FormData();
//         formData.append("postId", editingPost.postId);
//         formData.append("content", editText.trim());

//         if (editImageFile) {
//             formData.append("imageFile", editImageFile);
//         } else if (isRequestingImageRemoval && editingPost.image) {
//             formData.append("removeCurrentImage", "true");
//         }

//         const result = await handleAuthAction(
//             () => authApis().post(endpoints['posts'], formData, {
//                 headers: { "Content-Type": "multipart/form-data" }
//             }),
//             "Có lỗi xảy ra khi cập nhật bài viết.",
//             "Vui lòng đăng nhập để cập nhật bài viết."
//         );
//         setIsSubmittingPost(false);

//         if (result && result.status === 200 && result.data && result.data.postId) {
//             const updatedPostFromServer = result.data;
//             setPosts(currentPosts => currentPosts.map(p =>
//                 p.postId === editingPost.postId
//                     ? { ...updatedPostFromServer, userId: updatedPostFromServer.userId || editingPost.userId }
//                     : p
//             ));
//             alert("Cập nhật bài viết thành công!");
//             closeEditModal();
//         }
//     };

//     const handleToggleCommentLock = async (postId) => {
//         const currentPost = posts.find(p => p.postId === postId);
//         if (!currentPost || !currentUser || currentUser.id !== currentPost.userId) {
//             alert("Bạn không có quyền thực hiện hành động này.");
//             return;
//         }
//         const isCurrentlyLocked = currentPost.commentLocked;
//         const actionMessage = isCurrentlyLocked ? "mở khóa" : "khóa";
//         if (!window.confirm(`Bạn có chắc muốn ${actionMessage} bình luận cho bài viết này?`)) {
//             return;
//         }
//         setIsSubmittingPost(true);
//         const result = await handleAuthAction(
//             () => authApis().post(endpoints['toggle-comment-lock'](postId)),
//             `Có lỗi khi ${actionMessage} bình luận.`,
//             "Vui lòng đăng nhập."
//         );
//         setIsSubmittingPost(false);
//         if (result && result.status === 200 && result.data) {
//             const updatedPostFromServer = result.data;
//             setPosts(currentPosts => currentPosts.map(p =>
//                 p.postId === postId
//                     ? { ...updatedPostFromServer, userId: updatedPostFromServer.userId || p.userId }
//                     : p
//             ));
//             alert(`Đã ${actionMessage} bình luận thành công!`);
//         }
//     };

//     // --- Comment Actions ---
//     const handleCommentReactionClick = async (postId, commentId, reactionType) => {
//         const result = await handleAuthAction(
//             () => authApis().post(endpoints['comment-reactions'](commentId), { type: reactionType }),
//             "Có lỗi xảy ra khi tương tác với bình luận.",
//             "Vui lòng đăng nhập để thích bình luận."
//         );
//         if (result && result.status === 200 && result.data) {
//             setPosts(prevPosts => prevPosts.map(p => {
//                 if (p.postId === postId) {
//                     return { ...p, comments: p.comments?.map(c => c.commentId === commentId ? { ...c, reactions: result.data } : c) };
//                 }
//                 return p;
//             }));
//         }
//     };

//     const handleAddComment = async (postId) => {
//         const currentPost = posts.find(p => p.postId === postId);
//         if (currentPost && currentPost.commentLocked) {
//             alert("Bình luận đã bị khóa cho bài viết này.");
//             return;
//         }

//         const content = newCommentContent[postId];
//         if (!content || content.trim() === "") { alert("Vui lòng nhập nội dung bình luận."); return; }

//         setSubmittingComment(prev => ({ ...prev, [postId]: true }));
//         const result = await handleAuthAction(
//             () => authApis().post(endpoints['add-comment'](postId), { content: content.trim() }),
//             "Lỗi không xác định khi thêm bình luận.",
//             "Vui lòng đăng nhập để bình luận."
//         );
//         if (result && result.status === 201 && result.data && result.data.commentId) {
//             const newCommentDTO = result.data;
//             setPosts(prev => prev.map(p => {
//                 if (p.postId === postId) {
//                     const currentComments = Array.isArray(p.comments) ? p.comments : [];
//                     return {
//                         ...p,
//                         comments: [newCommentDTO, ...currentComments],
//                         commentCount: (p.commentCount != null ? p.commentCount : currentComments.length) + 1
//                     };
//                 }
//                 return p;
//             }));
//             setNewCommentContent(prev => ({ ...prev, [postId]: '' }));
//         }
//         setSubmittingComment(prev => ({ ...prev, [postId]: false }));
//     };

//     const handleDeleteComment = async (postId, commentIdToDelete) => {
//         if (!window.confirm("Bạn có chắc chắn muốn xóa bình luận này?")) {
//             return;
//         }
//         const result = await handleAuthAction(
//             () => authApis().delete(endpoints['delete-comment'](commentIdToDelete)),
//             "Có lỗi xảy ra khi xóa bình luận.",
//             "Vui lòng đăng nhập để xóa bình luận."
//         );
//         if (result && (result.status === 204 || result.status === 200)) {
//             setPosts(currentPosts => currentPosts.map(p => {
//                 if (p.postId === postId) {
//                     const updatedComments = Array.isArray(p.comments) ? p.comments.filter(c => c.commentId !== commentIdToDelete) : [];
//                     return {
//                         ...p,
//                         comments: updatedComments,
//                         commentCount: Math.max(0, (p.commentCount !== undefined ? p.commentCount : updatedComments.length + 1) - 1)
//                     };
//                 }
//                 return p;
//             }));
//             alert("Xóa bình luận thành công!");
//         }
//     };

//     const openEditCommentModal = (currentPost, commentToEdit) => {
//         if (!currentUser || !commentToEdit.userId || currentUser.id !== commentToEdit.userId) {
//             alert("Bạn không có quyền sửa bình luận này.");
//             return;
//         }
//         setEditingComment({
//             postId: currentPost.postId,
//             commentId: commentToEdit.commentId,
//             content: commentToEdit.content,
//             userId: commentToEdit.userId
//         });
//         setEditCommentText(commentToEdit.content);
//         setShowEditCommentModal(true);
//     };

//     const closeEditCommentModal = () => {
//         setShowEditCommentModal(false);
//         setEditingComment(null);
//         setEditCommentText("");
//     };

//     const handleUpdateComment = async (e) => {
//         e.preventDefault();
//         if (!editingComment || !editCommentText.trim()) {
//             alert("Nội dung bình luận không được để trống.");
//             return;
//         }
//         setIsSubmittingPost(true);
//         const result = await handleAuthAction(
//             () => authApis().put(endpoints['update-comment'](editingComment.commentId), { content: editCommentText.trim() }),
//             "Có lỗi xảy ra khi cập nhật bình luận.",
//             "Vui lòng đăng nhập để cập nhật bình luận."
//         );
//         setIsSubmittingPost(false);
//         if (result && result.status === 200 && result.data) {
//             const updatedCommentFromServer = result.data;
//             setPosts(currentPosts => currentPosts.map(p => {
//                 if (p.postId === editingComment.postId) {
//                     return {
//                         ...p,
//                         comments: p.comments.map(c =>
//                             c.commentId === editingComment.commentId
//                                 ? { ...updatedCommentFromServer, userId: updatedCommentFromServer.userId || editingComment.userId }
//                                 : c
//                         )
//                     };
//                 }
//                 return p;
//             }));
//             alert("Cập nhật bình luận thành công!");
//             closeEditCommentModal();
//         }
//     };

//     // --- Load More ---
//     const loadMore = () => {
//         if (!loadingPosts && canLoadMore) {
//             setCurrentPage(prevPage => prevPage + 1);
//         }
//     };

//     // --- Render Logic ---
//     if (!currentUser && cookie.load("token")) {
//         return <div className="text-center mt-5"><MySpinner /><p>Đang tải dữ liệu người dùng...</p></div>;
//     }
//     if (!currentUser && !cookie.load("token")) {
//         return <div className="text-center mt-5"><MySpinner /><p>Đang kiểm tra đăng nhập...</p></div>;
//     }

//     if (loadingPosts && currentPage === 1 && posts.length === 0 && !q.get("kw")) {
//         return <div className="text-center mt-5"><MySpinner /></div>;
//     }

//     return (
//         <>
//             {/* Form Tạo Bài Viết Mới */}
//             {currentUser && (
//                 <Row className="justify-content-center mt-3">
//                     <Col md={8}>
//                         <Card className="mb-4 shadow-sm">
//                             <Card.Header as="h5" className="d-flex align-items-center">
//                                 {currentUser.avatar && (
//                                     <img src={currentUser.avatar} alt={`${currentUser.username}'s Avatar`} style={{ width: "40px", height: "40px", borderRadius: "50%", marginRight: "15px" }} />
//                                 )}
//                                 Tạo bài viết
//                             </Card.Header>
//                             <Card.Body>
//                                 <Form onSubmit={handleCreateNewPost}>
//                                     <Form.Group className="mb-3">
//                                         <Form.Control
//                                             as="textarea" rows={3}
//                                             placeholder={`Bạn đang nghĩ gì, ${currentUser.fullName || currentUser.username}?`}
//                                             value={newPostText}
//                                             onChange={(e) => setNewPostText(e.target.value)}
//                                             disabled={isSubmittingPost} />
//                                     </Form.Group>
//                                     <Form.Group controlId="formFilePostImageHome" className="mb-3">
//                                         <Form.Label>Thêm ảnh (tùy chọn)</Form.Label>
//                                         <Form.Control
//                                             type="file" accept="image/*"
//                                             ref={imageInputRef}
//                                             onChange={(e) => setNewPostImage(e.target.files[0])}
//                                             disabled={isSubmittingPost} />
//                                     </Form.Group>
//                                     {newPostImage && (
//                                         <div className="mb-3 text-center">
//                                             <img src={URL.createObjectURL(newPostImage)} alt="Xem trước ảnh" style={{ maxWidth: '100%', maxHeight: '200px', objectFit: 'contain', borderRadius: '0.25rem' }} />
//                                         </div>
//                                     )}
//                                     <Button variant="primary" type="submit" disabled={isSubmittingPost} className="w-100">
//                                         {isSubmittingPost ? <><MySpinner animation="border" size="sm" as="span" /> Đang đăng...</> : "Đăng bài"}
//                                     </Button>
//                                 </Form>
//                             </Card.Body>
//                         </Card>
//                     </Col>
//                 </Row>
//             )}

//             {/* Danh sách bài viết */}
//             {posts.length === 0 && !loadingPosts && currentPage === 1 && !canLoadMore && (
//                 <Alert variant="info" className="m-3 text-center">Không có bài viết nào để hiển thị.</Alert>
//             )}

//             <Row className="justify-content-center">
//                 <Col md={8}>
//                     {posts.map((post) => (
//                         <Card key={post.postId} className="mb-3 shadow-sm rounded position-relative">
//                             {currentUser && post.userId && currentUser.id === post.userId && (
//                                 <div className="position-absolute top-0 end-0 p-2" style={{ zIndex: 10 }}>
//                                     <Dropdown>
//                                         <Dropdown.Toggle variant="link" id={`dropdown-post-${post.postId}`} bsPrefix="p-0" style={{ color: '#6c757d', textDecoration: 'none' }}>
//                                             <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" className="bi bi-three-dots-vertical" viewBox="0 0 16 16">
//                                                 <path d="M9.5 13a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0zm0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0zm0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0z" />
//                                             </svg>
//                                         </Dropdown.Toggle>
//                                         <Dropdown.Menu align="end">
//                                             <Dropdown.Item onClick={() => openEditModal(post)}>
//                                                 <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-pencil-fill me-2" viewBox="0 0 16 16"><path d="M12.854.146a.5.5 0 0 0-.707 0L10.5 1.793 14.207 5.5l1.647-1.646a.5.5 0 0 0 0-.708l-3-3zm.646 6.061L9.793 2.5 3.293 9H3.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.207l6.5-6.5zm-7.468 7.468A.5.5 0 0 1 6 13.5V13h-.5a.5.5 0 0 1-.5-.5V12h-.5a.5.5 0 0 1-.5-.5V11h-.5a.5.5 0 0 1-.5-.5V10h-.5a.499.499 0 0 1-.175-.032l-.179.178a.5.5 0 0 0-.11.168l-2 5a.5.5 0 0 0 .65.65l5-2a.5.5 0 0 0 .168-.11l.178-.178z" /></svg>
//                                                 Sửa bài viết
//                                             </Dropdown.Item>
//                                             <Dropdown.Item onClick={() => handleToggleCommentLock(post.postId)}>
//                                                 <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className={`bi ${post.commentLocked ? "bi-unlock-fill" : "bi-lock-fill"} me-2`} viewBox="0 0 16 16">
//                                                     {post.commentLocked ?
//                                                         <path d="M11 1a2 2 0 0 0-2 2v4a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V9a2 2 0 0 1 2-2h5V3a3 3 0 0 1 6 0v4a.5.5 0 0 1-1 0V3a2 2 0 0 0-2-2z" /> :
//                                                         <path d="M8 1a2 2 0 0 1 2 2v4H6V3a2 2 0 0 1 2-2zm3 6V3a3 3 0 0 0-6 0v4a2 2 0 0 0-2 2v5a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2z" />
//                                                     }
//                                                 </svg>
//                                                 {post.commentLocked ? "Mở khóa bình luận" : "Khóa bình luận"}
//                                             </Dropdown.Item>
//                                             <Dropdown.Divider />
//                                             <Dropdown.Item onClick={() => handleDeletePost(post.postId)} className="text-danger">
//                                                 <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-trash3-fill me-2" viewBox="0 0 16 16"><path d="M11 1.5v1h3.5a.5.5 0 0 1 0 1h-.538l-.853 10.66A2 2 0 0 1 11.115 16h-6.23a2 2 0 0 1-1.994-1.84L2.038 3.5H1.5a.5.5 0 0 1 0-1H5v-1A1.5 1.5 0 0 1 6.5 0h3A1.5 1.5 0 0 1 11 1.5Zm-5 0v1h4v-1a.5.5 0 0 0-.5-.5h-3a.5.5 0 0 0-.5.5ZM4.5 5.029l.5 8.5a.5.5 0 1 0 .998-.06l-.5-8.5a.5.5 0 1 0-.998.06Zm6.53-.528a.5.5 0 0 0-.528.47l-.5 8.5a.5.5 0 0 0 .998.058l.5-8.5a.5.5 0 0 0-.47-.058ZM8 4.5a.5.5 0 0 0-.5.5v8.5a.5.5 0 0 0 1 0V5a.5.5 0 0 0-.5-.5Z" /></svg>
//                                                 Xóa bài viết
//                                             </Dropdown.Item>
//                                         </Dropdown.Menu>
//                                     </Dropdown>
//                                 </div>
//                             )}
//                             <Card.Body>
//                                 <div className="d-flex align-items-center mb-3">
//                                     <img src={post.userAvatar || "https://via.placeholder.com/40?text=User"} alt={`${post.userFullName || 'User'}'s avatar`} width="40" height="40" className="rounded-circle me-3" />
//                                     <div>
//                                         <strong>{post.userFullName || "Ẩn danh"}</strong><br />
//                                         <small className="text-muted">
//                                             {post.updatedAt && formatDate(post.updatedAt) !== formatDate(post.createdAt)
//                                                 ? `Cập nhật lúc: ${formatDate(post.updatedAt)}`
//                                                 : `Đăng lúc: ${formatDate(post.createdAt)}`}
//                                         </small>
//                                     </div>
//                                 </div>
//                                 <Card.Text style={{ whiteSpace: "pre-wrap" }}>{post.content}</Card.Text>
//                                 {post.image && (<Card.Img variant="bottom" src={post.image} alt={`Ảnh bài viết ${post.postId}`} className="mt-2" style={{ maxHeight: "450px", objectFit: "contain", borderRadius: "0.25rem" }} />)}

//                                 <div className="mt-3">
//                                     {post.reactions && Object.keys(post.reactions).length > 0 && (
//                                         <div className="mb-2 d-flex align-items-center">
//                                             {Object.entries(post.reactions).map(([reaction, count]) => (
//                                                 <span key={reaction} className="me-3" style={{ fontSize: "0.9em" }}>
//                                                     {reaction === 'like' && '👍'} {reaction === 'haha' && '😂'} {reaction === 'heart' && '❤️'}
//                                                     <small className="text-muted ps-1">({count})</small>
//                                                 </span>
//                                             ))}
//                                         </div>
//                                     )}
//                                     <div className="d-flex justify-content-start align-items-center mb-2 border-top pt-2">
//                                         <Button variant="outline-primary" size="sm" className="me-2" onClick={() => handlePostReactionClick(post.postId, 'like')}>👍 Thích</Button>
//                                         <Button variant="outline-primary" size="sm" className="me-2" onClick={() => handlePostReactionClick(post.postId, 'haha')}>😂 Haha</Button>
//                                         <Button variant="outline-primary" size="sm" className="me-3" onClick={() => handlePostReactionClick(post.postId, 'heart')}>❤️ Tim</Button>
//                                         <Button variant="outline-secondary" size="sm" onClick={() => toggleComments(post.postId)}>
//                                             💬 Bình luận ({post.commentCount !== undefined ? post.commentCount : (Array.isArray(post.comments) ? post.comments.length : 0)})
//                                         </Button>
//                                     </div>
//                                 </div>

//                                 {showComments[post.postId] && (
//                                     <div className="mt-3 border-top pt-3">
//                                         <h6 className="mb-3">Bình luận {post.commentLocked && <small className="text-danger ms-2">(Đã khóa)</small>}</h6>
//                                         {!post.commentLocked && (
//                                             <Form className="mb-3" onSubmit={(e) => { e.preventDefault(); handleAddComment(post.postId); }}>
//                                                 <InputGroup>
//                                                     <Form.Control as="textarea" rows={2} placeholder="Viết bình luận của bạn..." value={newCommentContent[post.postId] || ''} onChange={(e) => setNewCommentContent(prev => ({ ...prev, [post.postId]: e.target.value }))} required disabled={submittingComment[post.postId]} />
//                                                     <Button variant="primary" type="submit" disabled={submittingComment[post.postId]}> {submittingComment[post.postId] ? <MySpinner animation="border" size="sm" /> : 'Gửi'} </Button>
//                                                 </InputGroup>
//                                             </Form>
//                                         )}
//                                         {(Array.isArray(post.comments) && post.comments.length > 0) ? (
//                                             post.comments.map((comment) => (
//                                                 <div key={comment.commentId} className="mb-3 p-2 bg-light rounded comment-item position-relative">
//                                                     {currentUser && comment.userId &&
//                                                         (currentUser.id === comment.userId || (post && post.userId && currentUser.id === post.userId)) && (
//                                                             <div className="position-absolute top-0 end-0 p-1" style={{ zIndex: 5 }}>
//                                                                 <Dropdown>
//                                                                     <Dropdown.Toggle variant="link" bsPrefix="p-0" size="sm" style={{ color: '#6c757d', textDecoration: 'none', lineHeight: 1 }}>
//                                                                         <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" className="bi bi-three-dots" viewBox="0 0 16 16">
//                                                                             <path d="M3 9.5a1.5 1.5 0 1 1 0-3 1.5 1.5 0 0 1 0 3zm5 0a1.5 1.5 0 1 1 0-3 1.5 1.5 0 0 1 0 3zm5 0a1.5 1.5 0 1 1 0-3 1.5 1.5 0 0 1 0 3z" />
//                                                                         </svg>
//                                                                     </Dropdown.Toggle>
//                                                                     <Dropdown.Menu align="end">
//                                                                         {currentUser.id === comment.userId &&
//                                                                             <Dropdown.Item onClick={() => openEditCommentModal(post, comment)}>
//                                                                                 <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-pencil-fill me-2" viewBox="0 0 16 16"><path d="M12.854.146a.5.5 0 0 0-.707 0L10.5 1.793 14.207 5.5l1.647-1.646a.5.5 0 0 0 0-.708l-3-3zm.646 6.061L9.793 2.5 3.293 9H3.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.207l6.5-6.5zm-7.468 7.468A.5.5 0 0 1 6 13.5V13h-.5a.5.5 0 0 1-.5-.5V12h-.5a.5.5 0 0 1-.5-.5V11h-.5a.5.5 0 0 1-.5-.5V10h-.5a.499.499 0 0 1-.175-.032l-.179.178a.5.5 0 0 0-.11.168l-2 5a.5.5 0 0 0 .65.65l5-2a.5.5 0 0 0 .168-.11l.178-.178z" /></svg>
//                                                                                 Sửa
//                                                                             </Dropdown.Item>
//                                                                         }
//                                                                         <Dropdown.Item onClick={() => handleDeleteComment(post.postId, comment.commentId)} className="text-danger">
//                                                                             <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-trash3-fill me-2" viewBox="0 0 16 16"><path d="M11 1.5v1h3.5a.5.5 0 0 1 0 1h-.538l-.853 10.66A2 2 0 0 1 11.115 16h-6.23a2 2 0 0 1-1.994-1.84L2.038 3.5H1.5a.5.5 0 0 1 0-1H5v-1A1.5 1.5 0 0 1 6.5 0h3A1.5 1.5 0 0 1 11 1.5Zm-5 0v1h4v-1a.5.5 0 0 0-.5-.5h-3a.5.5 0 0 0-.5.5ZM4.5 5.029l.5 8.5a.5.5 0 1 0 .998-.06l-.5-8.5a.5.5 0 1 0-.998.06Zm6.53-.528a.5.5 0 0 0-.528.47l-.5 8.5a.5.5 0 0 0 .998.058l.5-8.5a.5.5 0 0 0-.47-.058ZM8 4.5a.5.5 0 0 0-.5.5v8.5a.5.5 0 0 0 1 0V5a.5.5 0 0 0-.5-.5Z" /></svg>
//                                                                             Xóa
//                                                                         </Dropdown.Item>
//                                                                     </Dropdown.Menu>
//                                                                 </Dropdown>
//                                                             </div>
//                                                         )}
//                                                     <div className="d-flex align-items-start mb-1">
//                                                         <img src={comment.userAvatar || "https://via.placeholder.com/30?text=U"} alt={`${comment.userFullName || 'User'}'s avatar`} width="30" height="30" className="rounded-circle me-2 mt-1" />
//                                                         <div className="flex-grow-1">
//                                                             <strong>{comment.userFullName || "Người dùng"}</strong>
//                                                             <p style={{ marginBottom: '0.25rem', whiteSpace: "pre-wrap", fontSize: "0.95em" }}>{comment.content}</p>
//                                                             <small className="text-muted">
//                                                                 {comment.updatedAt && formatDate(comment.updatedAt) !== formatDate(comment.createdAt)
//                                                                     ? `Cập nhật lúc: ${formatDate(comment.updatedAt)}`
//                                                                     : `Đăng lúc: ${formatDate(comment.createdAt)}`}
//                                                             </small>

//                                                             {comment.reactions && Object.keys(comment.reactions).length > 0 && (
//                                                                 <div className="mt-1 d-flex align-items-center">
//                                                                     {Object.entries(comment.reactions).map(([type, count]) => (<span key={type} className="me-2" style={{ fontSize: '0.8em' }}> {type === 'like' && '👍'} {type === 'haha' && '😂'} {type === 'heart' && '❤️'} <small className="text-muted ps-1">({count})</small> </span>))}
//                                                                 </div>
//                                                             )}
//                                                             <div className="mt-1 comment-action-buttons">
//                                                                 <Button variant="link" size="sm" className="p-0 me-2 text-decoration-none reaction-button-comment" onClick={() => handleCommentReactionClick(post.postId, comment.commentId, 'like')}>Thích</Button>
//                                                                 <Button variant="link" size="sm" className="p-0 me-2 text-decoration-none reaction-button-comment" onClick={() => handleCommentReactionClick(post.postId, comment.commentId, 'haha')}>Haha</Button>
//                                                                 <Button variant="link" size="sm" className="p-0 text-decoration-none reaction-button-comment" onClick={() => handleCommentReactionClick(post.postId, comment.commentId, 'heart')}>Tim</Button>
//                                                             </div>
//                                                         </div>
//                                                     </div>
//                                                 </div>
//                                             ))
//                                         ) : (<p className="text-muted small">Chưa có bình luận nào.</p>)}
//                                     </div>
//                                 )}
//                             </Card.Body>
//                         </Card>
//                     ))}
//                 </Col>
//             </Row>

//             {/* Modal Sửa Bài Viết */}
//             {editingPost && (
//                 <Modal show={showEditModal} onHide={closeEditModal} centered backdrop="static" keyboard={!isSubmittingPost}>
//                     <Modal.Header closeButton={!isSubmittingPost}>
//                         <Modal.Title>Chỉnh sửa bài viết</Modal.Title>
//                     </Modal.Header>
//                     <Modal.Body>
//                         <Form onSubmit={handleUpdatePost}>
//                             <Form.Group className="mb-3">
//                                 <Form.Label>Nội dung</Form.Label>
//                                 <Form.Control
//                                     as="textarea" rows={5} value={editText}
//                                     onChange={(e) => setEditText(e.target.value)}
//                                     disabled={isSubmittingPost}
//                                 />
//                             </Form.Group>
//                             <Form.Group className="mb-3">
//                                 <Form.Label>Hình ảnh</Form.Label>
//                                 {editImagePreview && (
//                                     <div className="mb-2 text-center">
//                                         <img src={editImagePreview} alt="Xem trước" style={{ maxWidth: '100%', maxHeight: '200px', objectFit: 'contain', borderRadius: '0.25rem' }} />
//                                     </div>
//                                 )}
//                                 <Form.Control type="file" accept="image/*" ref={editImageInputRef}
//                                     onChange={handleEditImageChange} disabled={isSubmittingPost} />

//                                 {editingPost.image && (
//                                     <Button
//                                         variant="link" size="sm"
//                                         className={`p-0 mt-1 d-block text-center ${isRequestingImageRemoval ? 'text-danger fw-bold' : 'text-muted'}`}
//                                         onClick={handleToggleImageRemovalInEditModal}
//                                         disabled={isSubmittingPost} >
//                                         {isRequestingImageRemoval ? "Hoàn tác (giữ lại ảnh gốc)" : "Xóa ảnh này khỏi bài viết"}
//                                     </Button>
//                                 )}
//                             </Form.Group>
//                             <div className="d-flex justify-content-end">
//                                 <Button variant="secondary" onClick={closeEditModal} className="me-2" disabled={isSubmittingPost}>Hủy</Button>
//                                 <Button variant="primary" type="submit" disabled={isSubmittingPost}>
//                                     {isSubmittingPost ? <><MySpinner animation="border" size="sm" as="span" /> Đang lưu...</> : "Lưu thay đổi"}
//                                 </Button>
//                             </div>
//                         </Form>
//                     </Modal.Body>
//                 </Modal>
//             )}

//             {/* Modal Sửa Bình Luận */}
//             {editingComment && (
//                 <Modal show={showEditCommentModal} onHide={closeEditCommentModal} centered backdrop="static" keyboard={!isSubmittingPost}>
//                     <Modal.Header closeButton={!isSubmittingPost}>
//                         <Modal.Title>Chỉnh sửa bình luận</Modal.Title>
//                     </Modal.Header>
//                     <Modal.Body>
//                         <Form onSubmit={handleUpdateComment}>
//                             <Form.Group className="mb-3">
//                                 <Form.Label>Nội dung bình luận</Form.Label>
//                                 <Form.Control
//                                     as="textarea" rows={3} value={editCommentText}
//                                     onChange={(e) => setEditCommentText(e.target.value)}
//                                     disabled={isSubmittingPost}
//                                     required
//                                 />
//                             </Form.Group>
//                             <div className="d-flex justify-content-end">
//                                 <Button variant="secondary" onClick={closeEditCommentModal} className="me-2" disabled={isSubmittingPost}>Hủy</Button>
//                                 <Button variant="primary" type="submit" disabled={isSubmittingPost}>
//                                     {isSubmittingPost ? <><MySpinner animation="border" size="sm" as="span" /> Đang lưu...</> : "Lưu thay đổi"}
//                                 </Button>
//                             </div>
//                         </Form>
//                     </Modal.Body>
//                 </Modal>
//             )}

//             {/* Nút Xem thêm */}
//             {loadingPosts && currentPage > 1 && <div className="text-center my-3"><MySpinner /></div>}
//             {!loadingPosts && canLoadMore && posts.length > 0 && (
//                 <div className="text-center my-3">
//                     <Button variant="primary" onClick={loadMore} disabled={loadingPosts}>Xem thêm...</Button>
//                 </div>
//             )}
//             {!loadingPosts && !canLoadMore && posts.length > 0 && (
//                 <Alert variant="light" className="text-center m-2">Đã xem hết bài viết.</Alert>
//             )}
//         </>
//     );
// };

// export default Home;




// src/components/Home.js
import { useEffect, useState, useCallback, useRef, useContext } from "react";
import { Alert, Button, Col, Row } // Removed Form, InputGroup, Dropdown, Modal as they are now in child components primarily
    from "react-bootstrap";
import Apis, { endpoints, authApis } from "../configs/Apis";
import { useNavigate, useSearchParams } from "react-router-dom";
import cookie from "react-cookies";
import MySpinner from "./layouts/MySpinner";
import { MyUserContext, MyDispatchContext } from "../configs/Contexts";

// Import the new components
import CreatePostForm from "./CreatePostForm";
import PostItem from "./PostItem";
import EditPostModal from "./EditPostModal";
import EditCommentModal from "./EditCommentModal";

const Home = () => {
    // State cho danh sách posts và pagination
    const [posts, setPosts] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [canLoadMore, setCanLoadMore] = useState(true);
    const [loadingPosts, setLoadingPosts] = useState(false);
    const [q] = useSearchParams();
    const nav = useNavigate();

    // State cho comment mới - This will be managed within PostItem or passed to it
    // const [showComments, setShowComments] = useState({}); // Managed by PostItem
    // const [newCommentContent, setNewCommentContent] = useState({}); // Managed by PostItem

    // State cho việc tạo bài viết mới - now mostly managed by CreatePostForm
    // const [newPostText, setNewPostText] = useState("");
    // const [newPostImage, setNewPostImage] = useState(null);
    // const imageInputRef = useRef(null); // Managed by CreatePostForm

    const currentUser = useContext(MyUserContext);
    const dispatch = useContext(MyDispatchContext);

    // State chung cho việc submit (POST, PUT) - dùng cho cả post và comment modal
    const [isSubmitting, setIsSubmitting] = useState(false); // Renamed for clarity, used by multiple actions

    // State cho việc sửa bài viết
    const [editingPost, setEditingPost] = useState(null); // This is the post object
    const [showEditModal, setShowEditModal] = useState(false);
    // editText, editImageFile, etc., are now within EditPostModal

    // State cho việc sửa bình luận
    const [editingComment, setEditingComment] = useState(null); // { postId, commentId, content, userId }
    const [showEditCommentModal, setShowEditCommentModal] = useState(false);
    // editCommentText is now within EditCommentModal


    // --- useEffects ---
    useEffect(() => {
        const token = cookie.load("token");
        if (!token && !currentUser) {
            nav("/login");
        } else if (!currentUser && token) {
            const fetchCurrentUser = async () => {
                try {
                    const userRes = await authApis().get(endpoints['profile']);
                    dispatch({ type: "login", payload: userRes.data });
                } catch (ex) {
                    console.error("Lỗi tự động lấy thông tin user:", ex);
                    if (ex.response && ex.response.status === 401) {
                        cookie.remove("token");
                        dispatch({ type: "logout" });
                        nav("/login");
                    }
                }
            };
            fetchCurrentUser();
        }
    }, [nav, currentUser, dispatch]);

    const loadPosts = useCallback(async (pageToLoad, isNewSearch = false) => {
        if (!isNewSearch && !canLoadMore && pageToLoad > 1) return;
        if (loadingPosts && !isNewSearch && pageToLoad > 1) return;

        setLoadingPosts(true);
        try {
            let url = endpoints['posts'];
            const params = new URLSearchParams();
            params.append('page', pageToLoad.toString());

            const currentKw = q.get("kw");
            if (currentKw) {
                params.append('kw', currentKw);
            }
            const fullUrl = `${url}?${params.toString()}`;

            const res = await Apis.get(fullUrl);

            if (res.data && Array.isArray(res.data)) {
                const fetchedPosts = res.data;
                if (fetchedPosts.length === 0) {
                    setCanLoadMore(false);
                    if (pageToLoad === 1 || isNewSearch) setPosts([]);
                } else {
                    setPosts(prev => (pageToLoad === 1 || isNewSearch) ? fetchedPosts : [...prev, ...fetchedPosts]);
                    setCanLoadMore(true);
                }
            } else {
                setCanLoadMore(false);
                if (pageToLoad === 1 || isNewSearch) setPosts([]);
            }
        } catch (ex) {
            console.error("Lỗi khi tải danh sách bài viết:", ex);
            setCanLoadMore(false); // Ensure loading indicator stops on error
        } finally {
            setLoadingPosts(false);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [q.get("kw"), canLoadMore]); // Removed loadingPosts from dependencies as it causes re-runs

    useEffect(() => {
        if (cookie.load("token") && currentUser) {
            if (currentPage > 0) {
                loadPosts(currentPage, currentPage === 1 && q.get("kw") !== null);
            }
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [currentPage, currentUser]); // Removed loadPosts, it will be stable due to useCallback

    useEffect(() => {
        setCurrentPage(1);
        setCanLoadMore(true);
        if (cookie.load("token") && currentUser) {
            loadPosts(1, true); // isNewSearch = true
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [q.get("kw"), currentUser]); // Removed loadPosts

    // --- Helper Functions ---
    const formatDate = (dateStr) => {
        if (!dateStr) return "Không rõ thời gian";
        const date = new Date(dateStr);
        if (isNaN(date.getTime())) return "Thời gian không hợp lệ";
        return date.toLocaleString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
    };

    // Generic action handler
    const handleAuthAction = async (actionFunc, errorMessageDefault, unauthorizedMessage) => {
        if (!cookie.load("token") || !currentUser) {
            alert(unauthorizedMessage || "Vui lòng đăng nhập để thực hiện hành động này.");
            if (!currentUser && cookie.load("token")) { // Edge case: token exists but user not in context
                cookie.remove("token");
                dispatch({ type: "logout" });
            }
            nav("/login");
            return null; // Indicate failure or redirection
        }
        try {
            const response = await actionFunc();
            return response; // Return the response for the caller to process
        } catch (error) {
            console.error(errorMessageDefault, error);
            let msg = errorMessageDefault;
            if (error.response) {
                if (error.response.status === 401) {
                    msg = "Phiên đăng nhập đã hết hạn hoặc không hợp lệ. Vui lòng đăng nhập lại!";
                    cookie.remove("token");
                    dispatch({ type: "logout" });
                    nav("/login");
                } else {
                    const errorData = error.response.data;
                    // Simplified error message extraction
                    if (errorData && (errorData.error || errorData.message || errorData.detail)) {
                        msg = errorData.error || errorData.message || errorData.detail;
                    } else if (typeof errorData === 'string' && errorData.length < 200 && !errorData.toLowerCase().includes("<html")) {
                        msg = errorData;
                    } else if (errorData && typeof errorData === 'object' && Object.keys(errorData).length > 0) {
                        const keys = ['message', 'error', 'detail', 'title'];
                        for (const key of keys) {
                            if (typeof errorData[key] === 'string') {
                                msg = errorData[key];
                                break;
                            }
                        }
                        if (msg === errorMessageDefault) { // Fallback if no specific message found
                            try { msg = `Lỗi Server: ${error.response.status} - ${JSON.stringify(errorData)}`; } catch (e) { /* ignore */ }
                        }
                    } else if (error.response.statusText && msg === errorMessageDefault) {
                        msg = `Lỗi: ${error.response.status} - ${error.response.statusText}`;
                    }
                }
            } else if (error.message) { // Network error or other client-side error
                msg = `${errorMessageDefault} (Chi tiết: ${error.message})`;
            }
            alert(msg);
            return error.response || { data: { error: msg }, status: error.code || 'NETWORK_ERROR' }; // Return error structure
        }
    };

    // --- Post Actions ---
    const handlePostReactionClick = async (postId, reactionType) => {
        const result = await handleAuthAction(
            () => authApis().post(endpoints['post-reactions'](postId), { type: reactionType }),
            "Có lỗi xảy ra khi tương tác với bài viết.",
            "Vui lòng đăng nhập để thích bài viết."
        );
        if (result && result.status === 200 && result.data) {
            setPosts(prev => prev.map(p => p.postId === postId ? { ...p, reactions: result.data } : p));
        }
    };

    const handlePostCreated = (newPostDTO) => {
        setPosts(prevPosts => [newPostDTO, ...prevPosts]);
        // Resetting form fields is now handled within CreatePostForm
    };


    const handleDeletePost = async (postIdToDelete) => {
        if (!window.confirm("Bạn có chắc chắn muốn xóa bài viết này? Hành động này không thể hoàn tác.")) {
            return;
        }
        const result = await handleAuthAction(
            () => authApis().delete(endpoints['delete-post'](postIdToDelete)),
            "Có lỗi xảy ra khi xóa bài viết.",
            "Vui lòng đăng nhập để xóa bài viết."
        );
        if (result && (result.status === 204 || result.status === 200)) { // 204 No Content is common for DELETE
            setPosts(currentPosts => currentPosts.filter(p => p.postId !== postIdToDelete));
            alert("Xóa bài viết thành công!");
        }
    };

    const openEditPostModalHandler = (postToEdit) => {
        if (!currentUser || !postToEdit.userId || currentUser.id !== postToEdit.userId) {
            alert("Bạn không có quyền sửa bài viết này.");
            return;
        }
        setEditingPost(postToEdit);
        setShowEditModal(true);
    };

    const closeEditPostModalHandler = () => {
        setShowEditModal(false);
        setEditingPost(null); // Clear editing state
    };

    const handleUpdatePost = async (formData) => { // formData is now prepared by EditPostModal
        setIsSubmitting(true);
        const result = await handleAuthAction(
            () => authApis().post(endpoints['posts'], formData, { // Assumes your update endpoint is same as create but uses POST with postId
                headers: { "Content-Type": "multipart/form-data" }
            }),
            "Có lỗi xảy ra khi cập nhật bài viết.",
            "Vui lòng đăng nhập để cập nhật bài viết."
        );
        setIsSubmitting(false);
        if (result && result.status === 200 && result.data && result.data.postId) {
            const updatedPostFromServer = result.data;
            setPosts(currentPosts => currentPosts.map(p =>
                p.postId === editingPost.postId // Use editingPost.postId from state
                    ? { ...updatedPostFromServer, userId: updatedPostFromServer.userId || editingPost.userId } // Ensure userId is preserved
                    : p
            ));
            alert("Cập nhật bài viết thành công!");
            closeEditPostModalHandler();
        }
    };

    const handleToggleCommentLock = async (postId) => {
        const currentPost = posts.find(p => p.postId === postId);
        if (!currentPost || !currentUser || currentUser.id !== currentPost.userId) {
            alert("Bạn không có quyền thực hiện hành động này.");
            return;
        }
        const isCurrentlyLocked = currentPost.commentLocked;
        const actionMessage = isCurrentlyLocked ? "mở khóa" : "khóa";
        if (!window.confirm(`Bạn có chắc muốn ${actionMessage} bình luận cho bài viết này?`)) {
            return;
        }
        setIsSubmitting(true); // Use the general submitting state
        const result = await handleAuthAction(
            () => authApis().post(endpoints['toggle-comment-lock'](postId)),
            `Có lỗi khi ${actionMessage} bình luận.`,
            "Vui lòng đăng nhập."
        );
        setIsSubmitting(false);
        if (result && result.status === 200 && result.data) {
            const updatedPostFromServer = result.data;
            setPosts(currentPosts => currentPosts.map(p =>
                p.postId === postId
                    ? { ...updatedPostFromServer, userId: updatedPostFromServer.userId || p.userId } // Ensure userId is preserved
                    : p
            ));
            alert(`Đã ${actionMessage} bình luận thành công!`);
        }
    };


    // --- Comment Actions (many will be passed to PostItem) ---
    const handleCommentReactionClick = async (postId, commentId, reactionType) => {
        const result = await handleAuthAction(
            () => authApis().post(endpoints['comment-reactions'](commentId), { type: reactionType }),
            "Có lỗi xảy ra khi tương tác với bình luận.",
            "Vui lòng đăng nhập để thích bình luận."
        );
        if (result && result.status === 200 && result.data) {
            setPosts(prevPosts => prevPosts.map(p => {
                if (p.postId === postId) {
                    return { ...p, comments: p.comments?.map(c => c.commentId === commentId ? { ...c, reactions: result.data } : c) };
                }
                return p;
            }));
        }
    };

    const handleAddComment = async (postId, content) => { // Content now passed from PostItem
        // setIsSubmitting(true); // PostItem can manage its own submitting state for comments or use a shared one
        const result = await handleAuthAction(
            () => authApis().post(endpoints['add-comment'](postId), { content: content }),
            "Lỗi không xác định khi thêm bình luận.",
            "Vui lòng đăng nhập để bình luận."
        );
        // setIsSubmitting(false);
        if (result && result.status === 201 && result.data && result.data.commentId) {
            const newCommentDTO = result.data;
            setPosts(prev => prev.map(p => {
                if (p.postId === postId) {
                    const currentComments = Array.isArray(p.comments) ? p.comments : [];
                    return {
                        ...p,
                        comments: [newCommentDTO, ...currentComments],
                        commentCount: (p.commentCount != null ? p.commentCount : currentComments.length) + 1
                    };
                }
                return p;
            }));
            // Clearing input is handled in PostItem
            return true; // Indicate success
        }
        return false; // Indicate failure
    };

    const handleDeleteComment = async (postId, commentIdToDelete) => {
        if (!window.confirm("Bạn có chắc chắn muốn xóa bình luận này?")) {
            return;
        }
        const result = await handleAuthAction(
            () => authApis().delete(endpoints['delete-comment'](commentIdToDelete)),
            "Có lỗi xảy ra khi xóa bình luận.",
            "Vui lòng đăng nhập để xóa bình luận."
        );
        if (result && (result.status === 204 || result.status === 200)) {
            setPosts(currentPosts => currentPosts.map(p => {
                if (p.postId === postId) {
                    const updatedComments = Array.isArray(p.comments) ? p.comments.filter(c => c.commentId !== commentIdToDelete) : [];
                    return {
                        ...p,
                        comments: updatedComments,
                        commentCount: Math.max(0, (p.commentCount !== undefined ? p.commentCount : updatedComments.length + 1) - 1)
                    };
                }
                return p;
            }));
            alert("Xóa bình luận thành công!");
        }
    };

    const openEditCommentModalHandler = (currentPost, commentToEdit) => { // currentPost needed to get postId
        if (!currentUser || !commentToEdit.userId || currentUser.id !== commentToEdit.userId) {
            alert("Bạn không có quyền sửa bình luận này.");
            return;
        }
        setEditingComment({ // Store all necessary info
            postId: currentPost.postId,
            commentId: commentToEdit.commentId,
            content: commentToEdit.content,
            userId: commentToEdit.userId
        });
        setShowEditCommentModal(true);
    };

    const closeEditCommentModalHandler = () => {
        setShowEditCommentModal(false);
        setEditingComment(null);
    };

    const handleUpdateComment = async (commentId, content, postId) => { // postId needed to update the correct post's comments
        setIsSubmitting(true);
        const result = await handleAuthAction(
            () => authApis().put(endpoints['update-comment'](commentId), { content: content }),
            "Có lỗi xảy ra khi cập nhật bình luận.",
            "Vui lòng đăng nhập để cập nhật bình luận."
        );
        setIsSubmitting(false);
        if (result && result.status === 200 && result.data) {
            const updatedCommentFromServer = result.data;
            setPosts(currentPosts => currentPosts.map(p => {
                if (p.postId === postId) { // Use postId from the editingComment state
                    return {
                        ...p,
                        comments: p.comments.map(c =>
                            c.commentId === commentId
                                ? { ...updatedCommentFromServer, userId: updatedCommentFromServer.userId || editingComment.userId }
                                : c
                        )
                    };
                }
                return p;
            }));
            alert("Cập nhật bình luận thành công!");
            closeEditCommentModalHandler();
        }
    };


    // --- Load More ---
    const loadMore = () => {
        if (!loadingPosts && canLoadMore) {
            setCurrentPage(prevPage => prevPage + 1);
        }
    };

    // --- Render Logic ---
    if (!currentUser && cookie.load("token")) {
        return <div className="text-center mt-5"><MySpinner /><p>Đang tải dữ liệu người dùng...</p></div>;
    }
    if (!currentUser && !cookie.load("token")) {
        // This case might mean the initial auth check is still pending or failed silently,
        // or the user is genuinely logged out.
        // Depending on your app flow, you might want to redirect to login or show a message.
        // For now, keeping the spinner.
        return <div className="text-center mt-5"><MySpinner /><p>Đang kiểm tra đăng nhập...</p></div>;
    }

    // Show main loading spinner only on initial load of first page without search query
    if (loadingPosts && currentPage === 1 && posts.length === 0 && !q.get("kw")) {
        return <div className="text-center mt-5"><MySpinner /></div>;
    }


    return (
        <>
            {currentUser && (
                <CreatePostForm
                    onPostCreated={handlePostCreated}
                    isSubmitting={isSubmitting} // Pass the general submitting state
                    authApis={authApis}
                    endpoints={endpoints}
                    handleAuthAction={handleAuthAction} // Pass this if CreatePostForm handles its own submission fully
                />
            )}

            {posts.length === 0 && !loadingPosts && currentPage === 1 && !canLoadMore && (
                <Alert variant="info" className="m-3 text-center">Không có bài viết nào để hiển thị.</Alert>
            )}

            <Row className="justify-content-center">
                <Col md={8}>
                    {posts.map((post) => (
                        <PostItem
                            key={post.postId}
                            post={post}
                            currentUser={currentUser}
                            formatDate={formatDate}
                            onPostReaction={handlePostReactionClick}
                            onDeletePost={handleDeletePost}
                            onOpenEditModal={openEditPostModalHandler}
                            onToggleCommentLock={handleToggleCommentLock}
                            onCommentReaction={handleCommentReactionClick}
                            onAddComment={handleAddComment}
                            onDeleteComment={handleDeleteComment}
                            onOpenEditCommentModal={openEditCommentModalHandler}
                            // Pass down authApis, endpoints, and handleAuthAction if needed by PostItem for its internal logic
                            authApis={authApis}
                            endpoints={endpoints}
                            handleAuthAction={handleAuthAction}
                        />
                    ))}
                </Col>
            </Row>

            {editingPost && (
                <EditPostModal
                    show={showEditModal}
                    onHide={closeEditPostModalHandler}
                    postToEdit={editingPost}
                    onUpdatePost={handleUpdatePost} // This function now takes formData
                    isSubmitting={isSubmitting}
                // authApis and endpoints can be passed if EditPostModal needs to make its own calls
                // but it's better to handle the call in Home.js via onUpdatePost
                />
            )}

            {editingComment && (
                <EditCommentModal
                    show={showEditCommentModal}
                    onHide={closeEditCommentModalHandler}
                    commentToEdit={editingComment} // This is an object like { postId, commentId, content, userId }
                    onUpdateComment={handleUpdateComment} // Takes commentId, newContent, postId
                    isSubmitting={isSubmitting}
                />
            )}

            {loadingPosts && currentPage > 1 && <div className="text-center my-3"><MySpinner /></div>}
            {!loadingPosts && canLoadMore && posts.length > 0 && (
                <div className="text-center my-3">
                    <Button variant="primary" onClick={loadMore} disabled={loadingPosts}>Xem thêm...</Button>
                </div>
            )}
            {!loadingPosts && !canLoadMore && posts.length > 0 && (
                <Alert variant="light" className="text-center m-2">Đã xem hết bài viết.</Alert>
            )}
        </>
    );
};

export default Home;