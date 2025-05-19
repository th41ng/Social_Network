import { useContext, useEffect, useState } from "react";
import { Button, Container, Navbar, NavDropdown } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import Apis, { endpoints } from "../../configs/Apis";
import { MyUserContext, MyDispatchContext } from "../../configs/Contexts";

const Header = () => {
    const [categories, setCategories] = useState([]);
    const user = useContext(MyUserContext); // Lấy thông tin người dùng
    const dispatch = useContext(MyDispatchContext);
    const navigate = useNavigate();

    const loadCates = async () => {
        try {
            let res = await Apis.get(endpoints["categories"]);
            setCategories(res.data);
        } catch (error) {
            console.error("Lỗi khi tải danh mục:", error);
        }
    };

    const handleLogout = () => {
        dispatch({ type: "logout" }); // Gửi hành động logout
        navigate("/"); // Chuyển hướng về trang chính
    };

    useEffect(() => {
        loadCates();
    }, []);

    return (
        <Navbar expand="lg" className="bg-body-tertiary">
            <Container>
                <Navbar.Brand as={Link} to="/home">
                    DT's SocialNetwork
                </Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <NavDropdown title="Danh mục" id="basic-nav-dropdown">
                        {categories.map((c) => {
                            let path;
                            if (c.id === 3) {
                                path = "/notifications";
                            } else if (c.id === 5) {
                                path = "/profile";
                            } else {
                                path = `/?cateId=${c.id}`;
                            }
                            return (
                                <NavDropdown.Item as={Link} key={c.id} to={path}>
                                    {c.name}
                                </NavDropdown.Item>
                            );
                        })}
                    </NavDropdown>
                    {user && (
                        <div className="ms-auto d-flex align-items-center">
                            <span className="me-3">Xin chào, {user.username}</span>
                            <Button variant="outline-danger" onClick={handleLogout}>
                                Đăng xuất
                            </Button>
                        </div>
                    )}
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
};

export default Header;

// import { useEffect, useState } from "react";
// import { Button, Container, Nav, Navbar, NavDropdown } from "react-bootstrap";
// import { Link, useNavigate } from "react-router-dom";
// import cookie from "react-cookies"; // Sử dụng react-cookies
// import Apis, { endpoints } from "../../configs/Apis";

// const Header = () => {
//     const [categories, setCategories] = useState([]);
//     const [isLoggedIn, setIsLoggedIn] = useState(false);
//     const navigate = useNavigate();

//     const loadCates = async () => {
//         try {
//             let res = await Apis.get(endpoints['categories']);
//             setCategories(res.data);
//         } catch (err) {
//             console.error("Error loading categories:", err);
//         }
//     };

//     useEffect(() => {
//         loadCates();
//         // Kiểm tra trạng thái đăng nhập dựa vào cookie
//         const token = cookie.load("token"); // Đồng nhất key "token" với MyUserReducer
//         setIsLoggedIn(!!token); // Nếu có token, đặt trạng thái là true
//     }, []);

//     const handleLogout = () => {
//         // Xóa cookie chứa token
//         cookie.remove("token", { path: "/" }); // Đảm bảo đúng đường dẫn khi xóa cookie
//         setIsLoggedIn(false); // Cập nhật trạng thái đăng nhập
//         navigate("/login"); // Chuyển hướng người dùng về trang đăng nhập
//     };

//     return (
//         <Navbar expand="lg" className="bg-body-tertiary">
//             <Container>
//                 <Navbar.Brand href="#home">DT's SocialNetwork</Navbar.Brand>
//                 <Navbar.Toggle aria-controls="basic-navbar-nav" />
//                 <Navbar.Collapse id="basic-navbar-nav">
//                     <Nav className="me-auto">
//                         <Link to="/" className="nav-link">Trang chủ</Link>
//                         <NavDropdown title="Danh mục" id="basic-nav-dropdown">
//                             {categories.map(c => (
//                                 <Link key={c.id} to={`/?cateId=${c.id}`} className="dropdown-item">
//                                     {c.name}
//                                 </Link>
//                             ))}
//                         </NavDropdown>
//                     </Nav>
//                     {isLoggedIn && (
//                         <Button variant="outline-danger" onClick={handleLogout}>
//                             Đăng xuất
//                         </Button>
//                     )}
//                 </Navbar.Collapse>
//             </Container>
//         </Navbar>
//     );
// };

// export default Header;
// import { useContext } from "react";
// import { Button, Container, Nav, Navbar, NavDropdown } from "react-bootstrap";
// import { Link, useNavigate } from "react-router-dom";
// import { MyUserContext, MyDispatchContext } from "../../configs/Contexts";

// const Header = () => {
//     const user = useContext(MyUserContext); // Lấy thông tin người dùng
//     const dispatch = useContext(MyDispatchContext); // Lấy hàm dispatch từ Context
//     const navigate = useNavigate();

//     const handleLogout = () => {
//         dispatch({ type: "logout" }); // Gửi hành động logout
//         navigate("/"); // Chuyển hướng về trang đăng nhập
//     };

//     return (
//         <Navbar expand="lg" className="bg-body-tertiary">
//             <Container>
//                 <Navbar.Brand href="/home">DT's SocialNetwork</Navbar.Brand>
//                 <Navbar.Toggle aria-controls="basic-navbar-nav" />
//                 <Navbar.Collapse id="basic-navbar-nav">
//                      <NavDropdown title="Danh mục" id="basic-nav-dropdown">
//                             {categories.map(c => (
//                                 <Link key={c.id} to={`/?cateId=${c.id}`} className="dropdown-item">
//                                     {c.name}
//                                 </Link>
//                             ))}
//                         </NavDropdown>
//                     {user ? (
//                         <Button variant="outline-danger" onClick={handleLogout}>
//                             Đăng xuất
//                         </Button>
//                     ) : (
//                         <Link to="/" className="btn btn-outline-primary">
//                             Đăng nhập
//                         </Link>
//                     )}
//                 </Navbar.Collapse>
//             </Container>
//         </Navbar>
//     );
// };

// export default Header;
