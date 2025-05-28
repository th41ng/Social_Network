// import { useRef, useState } from "react";
// import { Alert, Button, Col, Form } from "react-bootstrap";
// import MySpinner from "./layouts/MySpinner";
// import Apis, { endpoints } from "../configs/Apis";
// import { useNavigate } from "react-router-dom";

// const Register = () => {
//     const info = [{
//         "type": "text",
//         "title": "ROLE",
//         "field": "role"
//     }, {
//         "type": "text",
//         "title": "Họ và tên",
//         "field": "fullName"
//     }, {
//         "type": "number",
//         "title": "mã sinh viên",
//         "field": "studentId"
//     }, {
//         "type": "email",
//         "title": "Email",
//         "field": "email"
//     }, {
//         "type": "text",
//         "title": "Tên đăng nhập",
//         "field": "username"
//     }, {
//         "type": "password",
//         "title": "Mật khẩu",
//         "field": "password"
//     }, {
//         "type": "password",
//         "title": "Xác nhận mật khẩu",
//         "field": "confirm"
//     }]

//     const [user, setUser] = useState({});
//     const avatar = useRef();
//     const [msg, setMsg] = useState();
//     const [loading, setLoading] = useState(false);
//     const nav = useNavigate();

//     const setState = (value, field) => {
//         setUser({...user, [field]: value});
//     }

//     const register = async (e) => {
//         e.preventDefault();

//         if (user.password !== user.confirm) {
//             setMsg("Mật khẩu KHÔNG khớp!");
//         } else {
//             try {
//                 let form = new FormData();

//                 for (let key in user)
//                     if (key !== 'confirm')
//                         form.append(key, user[key]);

//                 form.append("avatar", avatar.current.files[0]);

//                 setLoading(true);
//                 await Apis.post(endpoints['register'], form, {
//                     headers: {
//                         'Content-Type': 'multipart/form-data'
//                     }
//                 });

//                 nav("/login");
//             } catch (ex) {
//                 console.error(ex);
//             } finally {
//                 setLoading(false);
//             }
//         }
//     }

//     return (
//         <>
//             {msg && <Alert variant="danger" className="mt-1">{msg}</Alert>}
//             <Form onSubmit={register}>
//                 {info.map(i => <Form.Group className="mb-3" controlId={i.field}>
//                                     <Form.Label column sm="2">{i.title}</Form.Label>
//                                     <Col sm="10">
//                                         <Form.Control required type={i.type} placeholder={i.title} value={user[i.field]} onChange={e => setState(e.target.value, i.field)} />
//                                     </Col>
//                                 </Form.Group>)}

//                 <Form.Group className="mb-3" controlId="avatar">
//                     <Form.Label column sm="2">Ảnh đại diện</Form.Label>
//                     <Col sm="10">
//                         <Form.Control type="file" ref={avatar}  />
//                     </Col>
//                 </Form.Group>

//                 {loading === true ?<MySpinner />:<Button type="submit" variant="danger" className="mb-1">Đăng ký</Button>}

//             </Form>
//         </>
//     );
// }

// export default Register;
// import { useRef, useState } from "react";
// import { Alert, Button, Col, Form } from "react-bootstrap";
// import MySpinner from "./layouts/MySpinner";
// import Apis, { endpoints } from "../configs/Apis";
// import { useNavigate } from "react-router-dom";

// const Register = () => {
//     const info = [{
//         "type": "text",
//         "title": "Họ và tên",
//         "field": "fullName"
//     }, {
//         "type": "number",
//         "title": "Mã sinh viên",
//         "field": "studentId"
//     }, {
//         "type": "email",
//         "title": "Email",
//         "field": "email"
//     }, {
//         "type": "text",
//         "title": "Tên đăng nhập",
//         "field": "username"
//     }];

//     const roles = [
//         { value: "ROLE_ALUMNI", label: "Cựu sinh viên (Alumni)" },
//         { value: "ROLE_LECTURER", label: "Giảng viên (Lecturer)" },
//     ];

//     const [user, setUser] = useState({});
//     const avatar = useRef();
//     const coverImage = useRef();
//     const [msg, setMsg] = useState();
//     const [loading, setLoading] = useState(false);
//     const nav = useNavigate();

//     const setState = (value, field) => {
//         let updatedUser = { ...user, [field]: value };

//         // Nếu role là ROLE_LECTURER, tự động đặt mật khẩu là ou@123
//         if (field === "role" && value === "ROLE_LECTURER") {
//             updatedUser.password = "ou@123";
//             updatedUser.confirm = "ou@123";
//         }

//         setUser(updatedUser);
//     };

//     const register = async (e) => {
//         e.preventDefault();

//         if (user.role !== "ROLE_LECTURER" && user.password !== user.confirm) {
//             setMsg("Mật khẩu KHÔNG khớp!");
//         } else {
//             try {
//                 let form = new FormData();

//                 for (let key in user)
//                     if (key !== 'confirm')
//                         form.append(key, user[key]);

//                 form.append("avatar", avatar.current.files[0]);
//                 form.append("coverImage", coverImage.current.files[0]);
//                 setLoading(true);
//                 await Apis.post(endpoints['register'], form, {
//                     headers: {
//                         'Content-Type': 'multipart/form-data'
//                     }
//                 });

//                 nav("/login");
//             } catch (ex) {
//                 console.error(ex);
//                 setMsg("Đã có lỗi xảy ra!");
//             } finally {
//                 setLoading(false);
//             }
//         }
//     };

//     return (
//         <>
//             {msg && <Alert variant="danger" className="mt-1">{msg}</Alert>}
//             <Form onSubmit={register}>
//                 <Form.Group className="mb-3" controlId="role">
//                     <Form.Label column sm="2">Vai trò</Form.Label>
//                     <Col sm="10">
//                         <Form.Control 
//                             as="select" 
//                             required 
//                             value={user.role || ""} 
//                             onChange={e => setState(e.target.value, "role")}
//                         >
//                             <option value="">Chọn vai trò</option>
//                             {roles.map(role => (
//                                 <option key={role.value} value={role.value}>
//                                     {role.label}
//                                 </option>
//                             ))}
//                         </Form.Control>
//                     </Col>
//                 </Form.Group>

//                 {info.map(i => (
//                     <Form.Group className="mb-3" controlId={i.field} key={i.field}>
//                         <Form.Label column sm="2">{i.title}</Form.Label>
//                         <Col sm="10">
//                             <Form.Control 
//                                 required 
//                                 type={i.type} 
//                                 placeholder={i.title} 
//                                 value={user[i.field] || ""} 
//                                 onChange={e => setState(e.target.value, i.field)} 
//                             />
//                         </Col>
//                     </Form.Group>
//                 ))}

//                 {user.role !== "ROLE_LECTURER" && (
//                     <>
//                         <Form.Group className="mb-3" controlId="password">
//                             <Form.Label column sm="2">Mật khẩu</Form.Label>
//                             <Col sm="10">
//                                 <Form.Control 
//                                     required 
//                                     type="password" 
//                                     placeholder="Mật khẩu" 
//                                     value={user.password || ""} 
//                                     onChange={e => setState(e.target.value, "password")} 
//                                 />
//                             </Col>
//                         </Form.Group>
//                         <Form.Group className="mb-3" controlId="confirm">
//                             <Form.Label column sm="2">Xác nhận mật khẩu</Form.Label>
//                             <Col sm="10">
//                                 <Form.Control 
//                                     required 
//                                     type="password" 
//                                     placeholder="Xác nhận mật khẩu" 
//                                     value={user.confirm || ""} 
//                                     onChange={e => setState(e.target.value, "confirm")} 
//                                 />
//                             </Col>
//                         </Form.Group>
//                     </>
//                 )}

//                 <Form.Group className="mb-3" controlId="avatar">
//                     <Form.Label column sm="2">Ảnh đại diện</Form.Label>
//                     <Col sm="10">
//                         <Form.Control type="file" ref={avatar} />
//                     </Col>
//                 </Form.Group>
//                 <Form.Group className="mb-3" controlId="coverImage">
//                     <Form.Label column sm="2">Ảnh bìa</Form.Label>
//                     <Col sm="10">
//                         <Form.Control type="file" ref={coverImage} />
//                     </Col>
//                 </Form.Group>

//                 {loading ? <MySpinner /> : <Button type="submit" variant="danger" className="mb-1">Đăng ký</Button>}
//             </Form>
//         </>
//     );
// };

// export default Register;
import { useRef, useState } from "react";
import { Alert, Button, Col, Form } from "react-bootstrap";
import MySpinner from "./layouts/MySpinner";
import Apis, { endpoints } from "../configs/Apis";
import { useNavigate } from "react-router-dom";

const Register = () => {
    const baseInfo = [
        { type: "text", title: "Họ và tên", field: "fullName" },
        { type: "email", title: "Email", field: "email" },
        { type: "text", title: "Tên đăng nhập", field: "username" }
    ];

    const roles = [
        { value: "ROLE_ALUMNI", label: "Cựu sinh viên (Alumni)" },
        { value: "ROLE_LECTURER", label: "Giảng viên (Lecturer)" }
    ];

    const [user, setUser] = useState({});
    const avatar = useRef();
    const coverImage = useRef();
    const [msg, setMsg] = useState();
    const [loading, setLoading] = useState(false);
    const nav = useNavigate();

    const setState = (value, field) => {
        let updatedUser = { ...user, [field]: value };

        // Nếu role là ROLE_LECTURER, tự động đặt mật khẩu và xóa studentId
        if (field === "role") {
            if (value === "ROLE_LECTURER") {
                updatedUser.password = "ou@123";
                updatedUser.confirm = "ou@123";
                delete updatedUser.studentId;
            }
        }

        setUser(updatedUser);
    };

    const register = async (e) => {
        e.preventDefault();

        if (user.role !== "ROLE_LECTURER" && user.password !== user.confirm) {
            setMsg("Mật khẩu KHÔNG khớp!");
        } else {
            try {
                let form = new FormData();

                for (let key in user) {
                    if (key !== "confirm") {
                        form.append(key, user[key]);
                    }
                }

                form.append("avatar", avatar.current.files[0]);
                form.append("coverImage", coverImage.current.files[0]);
                setLoading(true);
                await Apis.post(endpoints["register"], form, {
                    headers: {
                        "Content-Type": "multipart/form-data"
                    }
                });

                nav("/login");
            } catch (ex) {
                console.error(ex);
                setMsg("Đã có lỗi xảy ra!");
            } finally {
                setLoading(false);
            }
        }
    };

    const dynamicInfo = user.role === "ROLE_LECTURER" 
        ? baseInfo 
        : [...baseInfo, { type: "number", title: "Mã sinh viên", field: "studentId" }];

    return (
        <>
            {msg && <Alert variant="danger" className="mt-1">{msg}</Alert>}
            <Form onSubmit={register}>
                <Form.Group className="mb-3" controlId="role">
                    <Form.Label column sm="2">Vai trò</Form.Label>
                    <Col sm="10">
                        <Form.Control
                            as="select"
                            required
                            value={user.role || ""}
                            onChange={e => setState(e.target.value, "role")}
                        >
                            <option value="">Chọn vai trò</option>
                            {roles.map(role => (
                                <option key={role.value} value={role.value}>
                                    {role.label}
                                </option>
                            ))}
                        </Form.Control>
                    </Col>
                </Form.Group>

                {dynamicInfo.map(i => (
                    <Form.Group className="mb-3" controlId={i.field} key={i.field}>
                        <Form.Label column sm="2">{i.title}</Form.Label>
                        <Col sm="10">
                            <Form.Control
                                required
                                type={i.type}
                                placeholder={i.title}
                                value={user[i.field] || ""}
                                onChange={e => setState(e.target.value, i.field)}
                            />
                        </Col>
                    </Form.Group>
                ))}

                {user.role !== "ROLE_LECTURER" && (
                    <>
                        <Form.Group className="mb-3" controlId="password">
                            <Form.Label column sm="2">Mật khẩu</Form.Label>
                            <Col sm="10">
                                <Form.Control
                                    required
                                    type="password"
                                    placeholder="Mật khẩu"
                                    value={user.password || ""}
                                    onChange={e => setState(e.target.value, "password")}
                                />
                            </Col>
                        </Form.Group>
                        <Form.Group className="mb-3" controlId="confirm">
                            <Form.Label column sm="2">Xác nhận mật khẩu</Form.Label>
                            <Col sm="10">
                                <Form.Control
                                    required
                                    type="password"
                                    placeholder="Xác nhận mật khẩu"
                                    value={user.confirm || ""}
                                    onChange={e => setState(e.target.value, "confirm")}
                                />
                            </Col>
                        </Form.Group>
                    </>
                )}

                <Form.Group className="mb-3" controlId="avatar">
                    <Form.Label column sm="2">Ảnh đại diện</Form.Label>
                    <Col sm="10">
                        <Form.Control type="file" ref={avatar} />
                    </Col>
                </Form.Group>
                <Form.Group className="mb-3" controlId="coverImage">
                    <Form.Label column sm="2">Ảnh bìa</Form.Label>
                    <Col sm="10">
                        <Form.Control type="file" ref={coverImage} />
                    </Col>
                </Form.Group>

                {loading ? <MySpinner /> : <Button type="submit" variant="danger" className="mb-1">Đăng ký</Button>}
            </Form>
        </>
    );
};

export default Register;
