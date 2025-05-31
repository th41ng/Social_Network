import { useEffect, useState, useCallback, useContext } from "react"; 
import { Alert, Button, Col, Row } from "react-bootstrap";
import Apis, { endpoints, authApis } from "../configs/Apis";
import { useNavigate, useSearchParams } from "react-router-dom";
import cookie from "react-cookies";
import MySpinner from "./layouts/MySpinner";
import { MyUserContext, MyDispatchContext } from "../configs/Contexts";


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

    const [isSubmitting, setIsSubmitting] = useState(false);

    const [editingPost, setEditingPost] = useState(null);
    const [showEditModal, setShowEditModal] = useState(false);

    const [editingComment, setEditingComment] = useState(null);
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

    const keyword = q.get("kw"); 

    const loadPosts = useCallback(async (pageToLoad, isNewSearch = false) => {
        if (!isNewSearch && !canLoadMore && pageToLoad > 1) return;
       
       


        setLoadingPosts(true);
        try {
            let url = endpoints['posts'];
            const params = new URLSearchParams();
            params.append('page', pageToLoad.toString());

            
            if (keyword) { 
                params.append('kw', keyword);
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
            setCanLoadMore(false);
        } finally {
            setLoadingPosts(false);
        }
    }, [keyword, canLoadMore]); 


    useEffect(() => {
        if (cookie.load("token") && currentUser) {
            if (currentPage > 0) {
                loadPosts(currentPage, currentPage === 1 && keyword !== null);
            }
        }
    }, [currentPage, currentUser, loadPosts, keyword]); 

    useEffect(() => {
        setCurrentPage(1);
        setCanLoadMore(true); 
      
    }, [keyword, currentUser]); 

    
    const formatDate = useCallback((dateStr) => {
        if (!dateStr) return "Không rõ thời gian";
        const date = new Date(dateStr);
        if (isNaN(date.getTime())) return "Thời gian không hợp lệ";
        return date.toLocaleString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
    }, []);

    const handleAuthAction = useCallback(async (actionFunc, errorMessageDefault, unauthorizedMessage) => {
        if (!cookie.load("token") || !currentUser) {
            alert(unauthorizedMessage || "Vui lòng đăng nhập để thực hiện hành động này.");
            if (!currentUser && cookie.load("token")) {
                cookie.remove("token");
                dispatch({ type: "logout" });
            }
            nav("/login");
            return null;
        }
        try {
            const response = await actionFunc();
            return response;
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
                        if (msg === errorMessageDefault) {
                            try { msg = `Lỗi Server: ${error.response.status} - ${JSON.stringify(errorData)}`; } catch (e) { /* ignore */ }
                        }
                    } else if (error.response.statusText && msg === errorMessageDefault) {
                        msg = `Lỗi: ${error.response.status} - ${error.response.statusText}`;
                    }
                }
            } else if (error.message) {
                msg = `${errorMessageDefault} (Chi tiết: ${error.message})`;
            }
            alert(msg);
            return error.response || { data: { error: msg }, status: error.code || 'NETWORK_ERROR' };
        }
    }, [currentUser, dispatch, nav]);

    
    const handlePostReactionClick = useCallback(async (postId, reactionType) => {
        const result = await handleAuthAction(
            () => authApis().post(endpoints['post-reactions'](postId), { type: reactionType }),
            "Có lỗi xảy ra khi tương tác với bài viết.",
            "Vui lòng đăng nhập để thích bài viết."
        );
        if (result && result.status === 200 && result.data) {
            setPosts(prev => prev.map(p => p.postId === postId ? { ...p, reactions: result.data } : p));
        }
    }, [handleAuthAction]);

    const handlePostCreated = useCallback((newPostDTO) => {
        setPosts(prevPosts => [newPostDTO, ...prevPosts]);
    }, []);


    const handleDeletePost = useCallback(async (postIdToDelete) => {
        if (!window.confirm("Bạn có chắc chắn muốn xóa bài viết này? Hành động này không thể hoàn tác.")) {
            return;
        }
        const result = await handleAuthAction(
            () => authApis().delete(endpoints['delete-post'](postIdToDelete)),
            "Có lỗi xảy ra khi xóa bài viết.",
            "Vui lòng đăng nhập để xóa bài viết."
        );
        if (result && (result.status === 204 || result.status === 200)) {
            setPosts(currentPosts => currentPosts.filter(p => p.postId !== postIdToDelete));
            alert("Xóa bài viết thành công!");
        }
    }, [handleAuthAction]);

    const openEditPostModalHandler = useCallback((postToEdit) => {
        if (!currentUser || !postToEdit.userId || currentUser.id !== postToEdit.userId) {
            alert("Bạn không có quyền sửa bài viết này.");
            return;
        }
        setEditingPost(postToEdit);
        setShowEditModal(true);
    }, [currentUser]);

    const closeEditPostModalHandler = useCallback(() => {
        setShowEditModal(false);
        setEditingPost(null);
    }, []);

    const handleUpdatePost = useCallback(async (formData) => {
        setIsSubmitting(true);
        const result = await handleAuthAction(
            () => authApis().post(endpoints['posts'], formData, {
                headers: { "Content-Type": "multipart/form-data" }
            }),
            "Có lỗi xảy ra khi cập nhật bài viết.",
            "Vui lòng đăng nhập để cập nhật bài viết."
        );
        setIsSubmitting(false);
        if (result && result.status === 200 && result.data && result.data.postId) {
            const updatedPostFromServer = result.data;
            setPosts(currentPosts => currentPosts.map(p =>
                p.postId === editingPost?.postId 
                    ? { ...updatedPostFromServer, userId: updatedPostFromServer.userId || editingPost?.userId }
                    : p
            ));
            alert("Cập nhật bài viết thành công!");
            closeEditPostModalHandler(); 
        }
    }, [handleAuthAction, editingPost, closeEditPostModalHandler]); 

    const handleToggleCommentLock = useCallback(async (postId) => {
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
        setIsSubmitting(true);
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
                    ? { ...updatedPostFromServer, userId: updatedPostFromServer.userId || p.userId }
                    : p
            ));
            alert(`Đã ${actionMessage} bình luận thành công!`);
        }
    }, [posts, currentUser, handleAuthAction]); 


   
    const handleCommentReactionClick = useCallback(async (postId, commentId, reactionType) => {
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
    }, [handleAuthAction]);

    const handleAddComment = useCallback(async (postId, content) => {
        const result = await handleAuthAction(
            () => authApis().post(endpoints['add-comment'](postId), { content: content }),
            "Lỗi không xác định khi thêm bình luận.",
            "Vui lòng đăng nhập để bình luận."
        );
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
            return true;
        }
        return false;
    }, [handleAuthAction]);

    const handleDeleteComment = useCallback(async (postId, commentIdToDelete) => {
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
    }, [handleAuthAction]);

    const openEditCommentModalHandler = useCallback((currentPost, commentToEdit) => {
        if (!currentUser || !commentToEdit.userId || currentUser.id !== commentToEdit.userId) {
            alert("Bạn không có quyền sửa bình luận này.");
            return;
        }
        setEditingComment({
            postId: currentPost.postId,
            commentId: commentToEdit.commentId,
            content: commentToEdit.content,
            userId: commentToEdit.userId
        });
        setShowEditCommentModal(true);
    }, [currentUser]);

    const closeEditCommentModalHandler = useCallback(() => {
        setShowEditCommentModal(false);
        setEditingComment(null);
    }, []);

  const handleUpdateComment = useCallback(async (commentId, content, postId) => {
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
            if (p.postId === postId) {
                return {
                    ...p,
                    comments: p.comments.map(c =>
                        c.commentId === commentId
                            ? { ...updatedCommentFromServer, reactions: c.reactions || {} } 
                            : c
                    )
                };
            }
            return p;
        }));

        alert("Cập nhật bình luận thành công!");
        closeEditCommentModalHandler(); 
    }
}, [handleAuthAction, editingComment, closeEditCommentModalHandler]);



 
    const loadMore = useCallback(() => {
        if (!loadingPosts && canLoadMore) {
            setCurrentPage(prevPage => prevPage + 1);
        }
    }, [loadingPosts, canLoadMore]);


    if (!currentUser && cookie.load("token")) {
        return <div className="text-center mt-5"><MySpinner /><p>Đang tải dữ liệu người dùng...</p></div>;
    }
    if (!currentUser && !cookie.load("token")) {
        return <div className="text-center mt-5"><MySpinner /><p>Đang kiểm tra đăng nhập...</p></div>;
    }

    if (loadingPosts && currentPage === 1 && posts.length === 0 && !keyword) { 
        return <div className="text-center mt-5"><MySpinner /></div>;
    }

    return (
        <>
            {currentUser && (
                <CreatePostForm
                    onPostCreated={handlePostCreated}
                    isSubmitting={isSubmitting}
                    setIsSubmitting={setIsSubmitting}
                    authApis={authApis}
                    endpoints={endpoints}
                    handleAuthAction={handleAuthAction}
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
                    onUpdatePost={handleUpdatePost}
                    isSubmitting={isSubmitting}
                />
            )}

            {editingComment && (
                <EditCommentModal
                    show={showEditCommentModal}
                    onHide={closeEditCommentModalHandler}
                    commentToEdit={editingComment}
                    onUpdateComment={handleUpdateComment}
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