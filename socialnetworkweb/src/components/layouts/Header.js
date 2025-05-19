import { useContext, useEffect, useState } from "react";
import { Button, Container, Nav, Navbar, NavDropdown } from "react-bootstrap"; // Thêm Nav vào import
import { Link, useNavigate } from "react-router-dom";
import Apis, { endpoints } from "../../configs/Apis"; // Đảm bảo đường dẫn này chính xác
import { MyUserContext, MyDispatchContext } from "../../configs/Contexts"; // Đảm bảo đường dẫn này chính xác

const Header = () => {
    const [categories, setCategories] = useState([]);
    const user = useContext(MyUserContext); // Lấy thông tin người dùng
    const dispatch = useContext(MyDispatchContext);
    const navigate = useNavigate();

    const SURVEY_CATEGORY_ID = 4; // ID cho danh mục "Surveys"
    const NOTIFICATIONS_CATEGORY_ID = 3; // Giữ nguyên ID cho "Notifications" nếu có

    const loadCates = async () => {
        try {
            // Đảm bảo endpoints['categories'] đã được định nghĩa trong file configs/Apis.js
            if (endpoints['categories']) {
                let res = await Apis.get(endpoints['categories']);
                setCategories(res.data);
            } else {
                console.warn("Endpoint 'categories' chưa được định nghĩa.");
                setCategories([]); // Đặt là mảng rỗng nếu không có endpoint
            }
        } catch (error) {
            console.error("Lỗi khi tải danh mục:", error);
        }
    };

    const handleLogout = () => {
        dispatch({ type: "logout" }); // Gửi hành động logout
        // Chuyển hướng về trang chủ hoặc trang đăng nhập sau khi logout
        // Nếu user là null, có thể '/' là trang login, còn '/home' là trang chính sau khi login
        navigate(user ? "/" : "/login"); 
    };

    useEffect(() => {
        loadCates();
    }, []);

    return (
        <Navbar expand="lg" className="bg-body-tertiary shadow-sm" sticky="top"> {/* Thêm shadow và sticky */}
            <Container>
                <Navbar.Brand as={Link} to={user ? "/home" : "/"}> {/* Điều hướng dựa trên trạng thái đăng nhập */}
                    DT's SocialNetwork
                </Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto"> {/* Gom các link điều hướng chính vào Nav */}
                        {user && <Nav.Link as={Link} to="/home">Trang chủ</Nav.Link>}
                        
                        {categories.length > 0 && (
                            <NavDropdown title="Danh mục" id="basic-nav-dropdown">
                                {categories.map((c) => {
                                    let path;
                                    if (c.id === NOTIFICATIONS_CATEGORY_ID) {
                                        path = '/notifications';
                                    } else if (c.id === SURVEY_CATEGORY_ID) {
                                        // === THAY ĐỔI QUAN TRỌNG Ở ĐÂY ===
                                        path = '/surveys'; // Điều hướng đến trang danh sách khảo sát
                                    } else {
                                        // Giữ nguyên cho các danh mục khác, có thể dùng để lọc bài đăng ở Home
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
                        {/* Bạn có thể thêm một liên kết tĩnh đến Khảo sát ở đây nếu muốn */}
                        {/* <Nav.Link as={Link} to="/surveys">Khảo Sát</Nav.Link> */}
                    </Nav>
                    
                    <Nav> {/* Nav cho phần thông tin người dùng và đăng xuất/đăng nhập */}
                        {user ? (
                            <>
                                <Nav.Link as={Link} to="/profile" className="d-flex align-items-center"> {/* Thêm link tới profile nếu có */}
                                    {user.avatar && (
                                        <img 
                                            src={user.avatar} 
                                            alt={user.username} 
                                            style={{width: "30px", height: "30px", borderRadius: "50%", marginRight: "8px"}}
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