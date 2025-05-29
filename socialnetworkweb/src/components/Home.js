
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
    
    const [posts, setPosts] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [canLoadMore, setCanLoadMore] = useState(true);
    const [loadingPosts, setLoadingPosts] = useState(false);
    const [q] = useSearchParams();
    const nav = useNavigate();
    const currentUser = useContext(MyUserContext);
    const dispatch = useContext(MyDispatchContext);
    const [isSubmitting, setIsSubmitting] = useState(false); // Renamed for clarity, used by multiple actions
    const [editingPost, setEditingPost] = useState(null); // This is the post object
    const [showEditModal, setShowEditModal] = useState(false);
    const [editingComment, setEditingComment] = useState(null); // { postId, commentId, content, userId }
    const [showEditCommentModal, setShowEditCommentModal] = useState(false);
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
    const formatDate = (dateStr) => {
        if (!dateStr) return "Không rõ thời gian";
        const date = new Date(dateStr);
        if (isNaN(date.getTime())) return "Thời gian không hợp lệ";
        return date.toLocaleString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
    };
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
                    setIsSubmitting={setIsSubmitting} 
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