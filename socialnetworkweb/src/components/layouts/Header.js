import { useContext, useEffect, useState } from "react";
import { Button, Container, Nav, Navbar, NavDropdown } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import Apis, { endpoints } from "../../configs/Apis";
import { MyUserContext, MyDispatchContext } from "../../configs/Contexts";

const Header = () => {
    const [categories, setCategories] = useState([]);
    const user = useContext(MyUserContext); // Lấy thông tin người dùng
    const dispatch = useContext(MyDispatchContext);
    const navigate = useNavigate();

    const PROFILE_CATEGORY_ID = 5;
    const NOTIFICATIONS_CATEGORY_ID = 3;

    const loadCates = async () => {
        try {

            if (endpoints['categories']) {
                let res = await Apis.get(endpoints['categories']);
                setCategories(res.data);
            } else {
                console.warn("Endpoint 'categories' chưa được định nghĩa.");
                setCategories([]);
            }
        } catch (error) {
            console.error("Lỗi khi tải danh mục:", error);
        }
    };

    const handleLogout = () => {
        dispatch({ type: "logout" });
        navigate(user ? "/" : "/login");
    };

    useEffect(() => {
        loadCates();
    }, []);

    return (
        <Navbar expand="lg" className="bg-body-tertiary shadow-sm" sticky="top">
            <Container>
                <Navbar.Brand as={Link} to={user ? "/home" : "/"}>
                    DT's SocialNetwork
                </Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        {user && <Nav.Link as={Link} to="/home">Trang chủ</Nav.Link>}

                        {categories.length > 0 && (
                            <NavDropdown title="Danh mục" id="basic-nav-dropdown">
                                {categories.map((c) => {
                                    let path;
                                    // Xử lý cho các ID từ 1 đến 5
                                    if (c.id >= 1 && c.id <= 5) {
                                        switch (c.id) {
                                            case 1:
                                                path = '/';
                                                break;
                                            case 5:
                                                path = '/profile';
                                                break;
                                            case 4:
                                                path = '/surveys';
                                                break;
                                            case 3:
                                                path = '/notifications';
                                                break;
                                            case 2:
                                                path = '/posts';
                                                break;
                                            default:
                                                path = `/?cateId=${c.id}`;
                                                break;
                                        }
                                    } else {
                                        // Xử lý cho các ID ngoài khoảng 1-5
                                        path = `/?cateId=${c.id}`;
                                    }

                                    return (
                                        <NavDropdown.Item as={Link} key={c.id} to={path}>
                                            {c.name}
                                        </NavDropdown.Item>
                                    );
                                })}
                            </NavDropdown>
                        )}

                    </Nav>

                    <Nav>
                        {user ? (
                            <>
                                <Nav.Link as={Link} to="/profile" className="d-flex align-items-center">
                                    {user.avatar && (
                                        <img
                                            src={user.avatar}
                                            alt={user.username}
                                            style={{ width: "30px", height: "30px", borderRadius: "50%", marginRight: "8px" }}
                                        />
                                    )}
                                    {user.fullName || user.username}
                                </Nav.Link>
                                <Button variant="outline-danger" onClick={handleLogout} size="sm" className="ms-lg-2 align-self-center mt-2 mt-lg-0">
                                    Đăng xuất
                                </Button>
                            </>
                        ) : (
                            <>
                                <Nav.Link as={Link} to="/login" className="ms-lg-2">
                                    <Button variant="outline-primary" size="sm">Đăng nhập</Button>
                                </Nav.Link>
                                <Nav.Link as={Link} to="/register">
                                    <Button variant="primary" size="sm">Đăng ký</Button>
                                </Nav.Link>
                            </>
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
};

export default Header;
