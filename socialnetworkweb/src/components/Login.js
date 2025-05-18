// src/components/Login.js
// THAY ĐỔI: Thêm Alert và Row vào import
import { Button, Col, Form, Alert, Row } from "react-bootstrap";
import MySpinner from "./layouts/MySpinner";
import { useContext, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import Apis, { authApis, endpoints } from "../configs/Apis";
import cookie from 'react-cookies';
// THAY ĐỔI: Bỏ MyUserContext nếu không dùng, hoặc giữ lại nếu bạn có kế hoạch sử dụng
import { MyDispatchContext } from "../configs/Contexts"; 

const Login = () => {
    const info = [{
        "type": "text",
        "title": "Tên đăng nhập",
        "field": "username"
    }, {
        "type": "password",
        "title": "Mật khẩu",
        "field": "password"
    }];

    const [user, setUser] = useState({
        username: '',
        password: ''
    });
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState(null);
    const nav = useNavigate();
    const dispatch = useContext(MyDispatchContext);
    const [q] = useSearchParams();

    const setState = (value, field) => {
        setUser(current => ({ ...current, [field]: value }));
    }

    const login = async (e) => {
        e.preventDefault();
        setErr(null);

        try {
            setLoading(true);
            let res = await Apis.post(endpoints['login'], user);
            cookie.save('token', res.data.token);
            console.log("Token đã lưu:", res.data.token);

            // Giả sử bạn có endpoint 'current-user' để lấy thông tin người dùng sau khi đăng nhập
            // Endpoint này cần được bảo vệ và trả về thông tin User (UserDTO hoặc User Pojo)
            // Ví dụ: let userRes = await authApis().get(endpoints['current-user']);
            // Tạm thời, nếu chưa có API current-user, bạn có thể dispatch thông tin cơ bản
            // Hoặc bạn cần đảm bảo API /login trả về đủ thông tin user để dispatch
            
            // Nếu API /login đã trả về thông tin user trong res.data.user (ví dụ)
            // const userData = res.data.user; 
            // if (dispatch && userData) {
            //     dispatch({
            //         type: "login",
            //         payload: userData 
            //     });
            // } else 
            // Giả sử bạn cần gọi API khác để lấy thông tin user
            if (dispatch) {
                 // Bạn cần một API call ở đây để lấy thông tin người dùng đầy đủ
                 // Ví dụ:
                 const currentUserRes = await authApis().get(endpoints['current-user']); // Đảm bảo endpoints['current-user'] tồn tại
                 if (currentUserRes.data) {
                     dispatch({
                         type: "login",
                         payload: currentUserRes.data
                     });
                 } else {
                    // Fallback nếu không lấy được thông tin user chi tiết
                    dispatch({
                        type: "login",
                        payload: { username: user.username } // Thông tin tối thiểu
                    });
                 }
            }
            
            const nextUrl = q.get("next") || "/";
            nav(nextUrl);

        } catch (ex) {
            console.error("Lỗi đăng nhập:", ex);
            if (ex.response && ex.response.data && ex.response.data.error) { // Kiểm tra cấu trúc lỗi trả về từ backend
                setErr(ex.response.data.error);
            } else if (ex.response && ex.response.data && typeof ex.response.data === 'string') {
                 setErr(ex.response.data);
            } else if (ex.message) {
                setErr(ex.message);
            }
            else {
                setErr("Có lỗi xảy ra trong quá trình đăng nhập. Vui lòng thử lại!");
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <h1 className="text-center text-info mt-4 mb-3">ĐĂNG NHẬP</h1>
            {/* SỬ DỤNG Row đã import */}
            <Row className="justify-content-center"> 
                <Col md={5}>
                    {/* SỬ DỤNG Alert đã import */}
                    {err && <Alert variant="danger">{typeof err === 'object' ? JSON.stringify(err) : err}</Alert>} 
                    <Form onSubmit={login}>
                        {info.map(i => (
                            <Form.Group key={i.field} className="mb-3" controlId={`login-${i.field}`}>
                                <Form.Label>{i.title}</Form.Label>
                                <Form.Control
                                    required
                                    type={i.type}
                                    placeholder={i.title}
                                    value={user[i.field]} 
                                    onChange={e => setState(e.target.value, i.field)}
                                />
                            </Form.Group>
                        ))}
                        
                        {loading ? <div className="text-center"><MySpinner /></div> : <Button type="submit" variant="primary" className="w-100">Đăng nhập</Button>}
                    </Form>
                    <div className="mt-3 text-center">
                        Chưa có tài khoản? <a href="/register">Đăng ký ngay</a>
                    </div>
                </Col>
            </Row>
        </>
    );
}

export default Login;