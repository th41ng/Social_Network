import React, { useEffect, useState } from "react";
import { Alert, Button, Card, Col, Row, Container, Form, Modal } from "react-bootstrap";
import { authApis, endpoints } from "../configs/Apis";
import cookie from "react-cookies";
import MySpinner from "./layouts/MySpinner";
import { useNavigate } from "react-router-dom";

const Profile = () => {
  const [profile, setProfile] = useState(null);
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showEdit, setShowEdit] = useState(false);
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
  });
  const [avatarFile, setAvatarFile] = useState(null);
  const [coverImageFile, setCoverImageFile] = useState(null);
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

        const profileRes = await api.get(endpoints["profile"]);
        const userProfile = profileRes.data;
        setProfile(userProfile);
        setFormData({
          fullName: userProfile.fullName || "",
          email: userProfile.email || "",
        });

        const postsRes = await api.get(endpoints.userposts(userProfile.id));
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

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleFileChange = (e) => {
    const { name, files } = e.target;
    if (files && files[0]) {
      if (name === "avatar") {
        setAvatarFile(files[0]);
      } else if (name === "coverImage") {
        setCoverImageFile(files[0]);
      }
    }
  };

  const handleSave = async () => {
    try {
      const form = new FormData();
      form.append("fullName", formData.fullName);
      form.append("email", formData.email);
      if (avatarFile) form.append("avatar", avatarFile);
      if (coverImageFile) form.append("coverImage", coverImageFile);

      const updatedProfile = await authApis().post(endpoints["update-profile"], form, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      setProfile(updatedProfile.data);
      setShowEdit(false);
      alert("Cập nhật hồ sơ thành công!");
    } catch (err) {
      console.error("Lỗi khi cập nhật hồ sơ:", err);
      alert("Đã xảy ra lỗi. Vui lòng thử lại sau.");
    }
  };

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
      {/* Cover Image */}
      <div
        className="mb-5"
        style={{
          height: "200px",
          backgroundImage: `url(${profile?.coverImage || "https://via.placeholder.com/1200x200"})`,
          backgroundSize: "cover",
          backgroundPosition: "center",
          borderRadius: "8px",
          position: "relative",
        }}
      >
        <img
          src={profile?.avatar || "https://via.placeholder.com/150"}
          alt="Avatar"
          className="rounded-circle"
          style={{
            width: "100px",
            height: "100px",
            objectFit: "cover",
            border: "4px solid white",
            position: "absolute",
            bottom: "-50px",
            left: "50%",
            transform: "translateX(-50%)",
          }}
        />
      </div>

      <div className="text-center mb-3">
        <h3 className="mb-0">{profile?.username || "Ẩn danh"}</h3>
        <p className="text-muted mb-0">{profile?.email || "Không có email"}</p>
      </div>

      <div className="text-center mb-4">
        <Button variant="primary" onClick={() => setShowEdit(true)}>
          Sửa thông tin hồ sơ
        </Button>
      </div>

      {/* Modal sửa thông tin hồ sơ */}
      <Modal show={showEdit} onHide={() => setShowEdit(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>Sửa thông tin hồ sơ</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group className="mb-3" controlId="formFullName">
              <Form.Label>Họ và tên</Form.Label>
              <Form.Control
                type="text"
                name="fullName"
                value={formData.fullName}
                onChange={handleChange}
                placeholder="Nhập họ và tên"
              />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formEmail">
              <Form.Label>Email</Form.Label>
              <Form.Control
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="Nhập email"
              />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formAvatar">
              <Form.Label>Ảnh đại diện</Form.Label>
              <Form.Control
                type="file"
                accept="image/*"
                name="avatar"
                onChange={handleFileChange}
              />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formCoverImage">
              <Form.Label>Ảnh bìa</Form.Label>
              <Form.Control
                type="file"
                accept="image/*"
                name="coverImage"
                onChange={handleFileChange}
              />
            </Form.Group>
            <div className="d-grid mt-3">
              <Button variant="warning" onClick={() => {
                  setShowEdit(false);
                  navigate("/reset-password");  // Đảm bảo bạn đã import useNavigate và có route reset-password
                }}
              >
                Đổi mật khẩu
              </Button>
            </div>

          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowEdit(false)}>
            Hủy
          </Button>
          <Button variant="primary" onClick={handleSave}>
            Lưu
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Posts Section */}
      <div className="mt-5">
        <h4 className="mb-4">Bài viết của bạn</h4>
        <Row>
          {posts.length > 0 ? (
            posts.map((post) => (
              <Col key={post.postId || post.id} md={4} className="mb-4">
                <Card className="h-100 shadow-sm">
                  <Card.Body>
                    <Card.Title>{post.content || "Không có nội dung"}</Card.Title>
                    <Card.Text>
                      {post.image ? (
                        <img
                          src={post.image}
                          alt="Post Image"
                          style={{
                            maxWidth: "100%",
                            height: "auto",
                            borderRadius: "4px",
                          }}
                        />
                      ) : (
                        "Không có hình ảnh"
                      )}
                    </Card.Text>
                  </Card.Body>
                  <Card.Footer className="text-muted text-center">
                    Đăng ngày: {post.createdAt ? new Date(post.createdAt).toLocaleDateString() : "Không xác định"}
                  </Card.Footer>
                </Card>
              </Col>
            ))
          ) : (
            <Col>
              <Alert variant="info" className="text-center">
                Bạn chưa có bài viết nào.
              </Alert>
            </Col>
          )}
        </Row>
      </div>
    </Container>
  );
};

export default Profile;
