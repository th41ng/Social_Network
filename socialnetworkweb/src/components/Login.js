import { Button, Col, Form } from "react-bootstrap";
import MySpinner from "./layouts/MySpinner";
import { useContext, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import Apis, { authApis, endpoints } from "../configs/Apis";
import cookie from 'react-cookies';
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
    const [user, setUser] = useState({});
    const [loading, setLoading] = useState(false);
    const nav = useNavigate();
    const dispatch = useContext(MyDispatchContext);
    const [q] = useSearchParams();

    const setState = (value, field) => {
        setUser({ ...user, [field]: value });
    }

    const login = async (e) => {
        e.preventDefault();

        try {
            setLoading(true);

            let res = await Apis.post(endpoints['login'], { ...user });
            cookie.save('token', res.data.token);
            console.log(res.data.token);
            // Comment đoạn code lấy profile để tắt tạm
            /*
            let u = await authApis().get(endpoints['profile']);
    
            dispatch({
                type: "login",
                payload: u.data
            });
            */

            // Thay thế bằng dispatch tạm thời nếu cần
            // dispatch({
            //     type: "login",
            //     payload: { username: user.username } // Hoặc dữ liệu tạm
            // });

            nav("/");
        } catch (ex) {
            console.error(ex);
        } finally {
            setLoading(false);
        }
    };


    return (
        <>
            <h1 className="text-center text-info mt-1">ĐĂNG NHẬP</h1>
            <Form onSubmit={login}>
                {info.map(i => <Form.Group className="mb-3" controlId={i.field}>
                    <Form.Label column sm="2">{i.title}</Form.Label>
                    <Col sm="10">
                        <Form.Control required type={i.type} placeholder={i.title} value={user[i.field]} onChange={e => setState(e.target.value, i.field)} />
                    </Col>
                </Form.Group>)}

                {loading === true ? <MySpinner /> : <Button type="submit" variant="danger" className="mb-1">Đăng nhập</Button>}

            </Form>
        </>
    );
}

export default Login;