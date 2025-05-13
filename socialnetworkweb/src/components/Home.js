import { useEffect, useState } from "react";
import { Alert, Button, Card, Col, Row } from "react-bootstrap";
import Apis, { endpoints } from "../configs/Apis";
import { useSearchParams } from "react-router-dom";
import MySpinner from "./layouts/MySpinner";

const Home = () => {
    const [posts, setPosts] = useState([]);
    const [page, setPage] = useState(1);
    const [loading, setLoading] = useState(false);
    const [q] = useSearchParams();

    // Hàm lấy dữ liệu bài viết
    const loadPosts = async () => {
        try {
            setLoading(true);
            let url = `${endpoints['posts']}?page=${page}`;
            let res = await Apis.get(url);

            if (res.data.length === 0) {
                setPage(0);
            } else {
                if (page === 1) {
                    setPosts(res.data);
                } else {
                    setPosts([...posts, ...res.data]);
                }
            }
        } catch (ex) {
            console.error(ex);
        } finally {
            setLoading(false);
        }
    };

    // Hàm chuyển đổi và định dạng thời gian
    const formatDate = (dateStr) => {
        const date = new Date(dateStr);
        return date.toLocaleString(); // Hiển thị theo định dạng của người dùng
    };

    // Thêm trạng thái để theo dõi bình luận được hiển thị hay không
    const [showComments, setShowComments] = useState({});

    // Xử lý sự kiện khi người dùng nhấn nút "Bình luận"
    const toggleComments = (postId) => {
        setShowComments((prevState) => ({
            ...prevState,
            [postId]: !prevState[postId], // Lật trạng thái hiển thị/ẩn bình luận cho bài viết
        }));
    };

    // Hàm xử lý khi người dùng nhấn vào các reactions
    const handleReactionClick = async (postId, reactionType) => {
        try {
            // Gửi yêu cầu update reaction tới backend
            const res = await Apis.post(`${endpoints['posts']}/${postId}/reactions`, { type: reactionType });

            // Cập nhật lại reactions cho bài viết
            const updatedReactions = res.data.reactions;
            setPosts((prevPosts) =>
                prevPosts.map((post) =>
                    post.postId === postId
                        ? { ...post, reactions: updatedReactions }
                        : post
                )
            );
        } catch (error) {
            console.error("Error updating reaction:", error);
        }
    };

    useEffect(() => {
        if (page > 0) loadPosts();
    }, [page, q]);

    const loadMore = () => {
        if (!loading && page > 0) setPage(page + 1);
    };

    return (
        <>
            {posts.length === 0 && !loading && (
                <Alert variant="info" className="m-2">Không có bài viết nào!</Alert>
            )}

            <Row className="justify-content-center">
                <Col md={8}>
                    {posts.map((post) => (
                        <Card key={post.postId} className="mb-3 shadow-sm rounded">
                            <Card.Body>
                                <div className="d-flex align-items-center mb-3">
                                    <img
                                        src={post.userAvatar || "https://via.placeholder.com/40"}
                                        alt="avatar"
                                        width="40"
                                        height="40"
                                        className="rounded-circle me-2"
                                    />
                                    <div>
                                        <strong>{post.userFullName || "Ẩn danh"}</strong><br />
                                        <small className="text-muted">{formatDate(post.createdAt) || "Vừa xong"}</small>
                                    </div>
                                </div>

                                <Card.Title>{post.content}</Card.Title>

                                {/* Hiển thị hình ảnh nếu có */}
                                {post.image && (
                                    <Card.Img
                                        variant="bottom"
                                        src={post.image} 
                                        className="mt-2"
                                        style={{ maxHeight: "400px", objectFit: "cover" }}
                                    />
                                )}

                                <div className="mt-3">
                                    {/* Hiển thị reactions cho bài viết */}
                                    {post.reactions && Object.keys(post.reactions).length > 0 && (
                                        <div className="mb-2">
                                            <strong>Reactions:</strong>
                                            {Object.entries(post.reactions).map(([reaction, count]) => (
                                                <span key={reaction} className="me-2">
                                                    {reaction === 'like' && '👍'}
                                                    {reaction === 'haha' && '😂'}
                                                    {reaction === 'heart' && '❤️'}
                                                    <strong>{count}</strong>
                                                </span>
                                            ))}
                                        </div>
                                    )}

                                    {/* Các nút reactions */}
                                    <div className="d-flex">
                                        <Button
                                            variant="outline-primary"
                                            size="sm"
                                            className="me-2"
                                            onClick={() => handleReactionClick(post.postId, 'like')}
                                        >
                                            👍 Thích
                                        </Button>
                                        <Button
                                            variant="outline-warning"
                                            size="sm"
                                            className="me-2"
                                            onClick={() => handleReactionClick(post.postId, 'haha')}
                                        >
                                            😂 Haha
                                        </Button>
                                        <Button
                                            variant="outline-danger"
                                            size="sm"
                                            onClick={() => handleReactionClick(post.postId, 'heart')}
                                        >
                                            ❤️ Heart
                                        </Button>
                                    </div>

                                    <Button
                                        variant="outline-secondary"
                                        size="sm"
                                        onClick={() => toggleComments(post.postId)}
                                    >
                                        💬 Bình luận
                                    </Button>
                                </div>

                                {/* Hiển thị comment khi showComments[postId] là true */}
                                {showComments[post.postId] && post.comments && post.comments.length > 0 && (
                                    <div className="mt-3">
                                        {post.comments.map((comment) => (
                                            <div key={comment.commentId} className="mb-2">
                                                <strong>{comment.userFullName}:</strong> {comment.content}

                                                {/* Hiển thị reactions cho bình luận */}
                                                {comment.reactions && Object.keys(comment.reactions).length > 0 && (
                                                    <div className="mt-2">
                                                        <strong>Reactions:</strong>
                                                        {Object.entries(comment.reactions).map(([reaction, count]) => (
                                                            <span key={reaction} className="me-2">
                                                                {reaction === 'like' && '👍'}
                                                                {reaction === 'haha' && '😂'}
                                                                {reaction === 'heart' && '❤️'}
                                                                <strong>{count}</strong>
                                                            </span>
                                                        ))}
                                                    </div>
                                                )}
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </Card.Body>
                        </Card>
                    ))}
                </Col>
            </Row>

            {page > 0 && (
                <div className="text-center">
                    <Button className="btn btn-primary mt-2 mb-2" onClick={loadMore}>
                        Xem thêm...
                    </Button>
                </div>
            )}

            {loading && <MySpinner />}
        </>
    );
};

export default Home;
