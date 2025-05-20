import React, { useEffect, useState } from "react";
import { Alert, Button, Card, Col, Row, Container } from "react-bootstrap";
import { authApis, endpoints } from "../configs/Apis";
import cookie from "react-cookies";
import MySpinner from "./layouts/MySpinner"; // Đảm bảo đường dẫn đúng
import { useNavigate } from "react-router-dom";

const Profile = () => {
  const [profile, setProfile] = useState(null);
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const token = cookie.load("token");
        if (!token) {
          setError("Bạn chưa đăng nhập!");
          setLoading(false);
          return;
        }

        const api = authApis();

        // Lấy thông tin hồ sơ
        const profileRes = await api.get(endpoints["profile"]);
        const userProfile = profileRes.data;
        setProfile(userProfile);

        // Lấy bài viết dựa trên userId từ profile
        const postsRes = await api.get(endpoints.userposts(userProfile.id)); // Chú ý sử dụng userId từ profile
        setPosts(postsRes.data);
      } catch (err) {
        console.error("Lỗi khi lấy dữ liệu hồ sơ hoặc bài viết:", err);
        if (err.response?.status === 401) {
          navigate("/");
        } else {
          setError("Không thể tải dữ liệu. Vui lòng thử lại sau.");
        }
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [navigate]);
  if (loading) return <MySpinner />;

  if (error) {
    return (
      <Container className="text-center py-4">
        <Alert variant="danger">{error}</Alert>
        <Button variant="primary" onClick={() => navigate("/")}>
          Đi đến đăng nhập
        </Button>
      </Container>
    );
  }

  return (
    <Container className="py-4">
      {/* Profile Header */}
      <Card className="mb-4 shadow-sm">
        <Card.Body className="d-flex align-items-center">
          <img
            src={profile?.avatar || "https://via.placeholder.com/150"}
            alt="Avatar"
            className="rounded-circle me-3"
            style={{ width: "100px", height: "100px", objectFit: "cover" }}
          />
          <div>
            <h3 className="mb-0">{profile?.username || "Ẩn danh"}</h3>
            <p className="text-muted mb-1">{profile?.email || "Không có email"}</p>
            <p className="text-muted mb-0">
              Thành viên từ:{" "}
              {profile?.createdAt
                ? new Date(profile.createdAt).toLocaleDateString()
                : "Không xác định"}
            </p>
          </div>
        </Card.Body>
      </Card>

      {/* Posts Section */}
      <h4 className="mb-4">Bài viết của bạn</h4>
      <Row>
        {posts.length > 0 ? (
          posts.map((post) => (
            <Col key={post.postId || post.id} md={4} className="mb-4">
              <Card className="h-100 shadow-sm">
                <Card.Body>
                  <Card.Title>{post.content || "Không có nội dung"}</Card.Title>
                  <Card.Text>
                    {post.image ? <img src={post.image} alt="Post Image" style={{ maxWidth: "100%", height: "auto" }} /> : "Không có hình ảnh"}
                  </Card.Text>

                </Card.Body>
                <Card.Footer className="text-muted">
                  Đăng ngày:{" "}
                  {post.createdAt
                    ? new Date(post.createdAt).toLocaleDateString()
                    : "Không xác định"}
                </Card.Footer>
              </Card>
            </Col>
          ))
        ) : (
          <Col>
            <Alert variant="info">Bạn chưa có bài viết nào.</Alert>
          </Col>
        )}
      </Row>
    </Container>
  );
};

export default Profile;
