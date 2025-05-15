import { useRef, useState } from "react";
import { Alert, Button, Col, Form } from "react-bootstrap";
import MySpinner from "./layouts/MySpinner";
import Apis, { endpoints } from "../configs/Apis";
import { useNavigate } from "react-router-dom";

const Register = () => {
    const info = [{
        "type": "text",
        "title": "ROLE",
        "field": "role"
    }, {
        "type": "text",
        "title": "Họ và tên",
        "field": "fullname"
    }, {
        "type": "number",
        "title": "mã sinh viên",
        "field": "studentId"
    }, {
        "type": "email",
        "title": "Email",
        "field": "email"
    }, {
        "type": "text",
        "title": "Tên đăng nhập",
        "field": "username"
    }, {
        "type": "password",
        "title": "Mật khẩu",
        "field": "password"
    }, {
        "type": "password",
        "title": "Xác nhận mật khẩu",
        "field": "confirm"
    }]

    const [user, setUser] = useState({});
    const avatar = useRef();
    const [msg, setMsg] = useState();
    const [loading, setLoading] = useState(false);
    const nav = useNavigate();

    const setState = (value, field) => {
        setUser({...user, [field]: value});
    }

    const register = async (e) => {
        e.preventDefault();

        if (user.password !== user.confirm) {
            setMsg("Mật khẩu KHÔNG khớp!");
        } else {
            try {
                let form = new FormData();

                for (let key in user)
                    if (key !== 'confirm')
                        form.append(key, user[key]);

                form.append("avatar", avatar.current.files[0]);

                setLoading(true);
                await Apis.post(endpoints['register'], form, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                });

                nav("/login");
            } catch (ex) {
                console.error(ex);
            } finally {
                setLoading(false);
            }
        }
    }

    return (
        <>
            {msg && <Alert variant="danger" className="mt-1">{msg}</Alert>}
            <Form onSubmit={register}>
                {info.map(i => <Form.Group className="mb-3" controlId={i.field}>
                                    <Form.Label column sm="2">{i.title}</Form.Label>
                                    <Col sm="10">
                                        <Form.Control required type={i.type} placeholder={i.title} value={user[i.field]} onChange={e => setState(e.target.value, i.field)} />
                                    </Col>
                                </Form.Group>)}

                <Form.Group className="mb-3" controlId="avatar">
                    <Form.Label column sm="2">Ảnh đại diện</Form.Label>
                    <Col sm="10">
                        <Form.Control type="file" ref={avatar}  />
                    </Col>
                </Form.Group>

                {loading === true ?<MySpinner />:<Button type="submit" variant="danger" className="mb-1">Đăng ký</Button>}
                
            </Form>
        </>
    );
}

export default Register;