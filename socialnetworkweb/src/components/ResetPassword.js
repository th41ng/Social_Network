// import React, { useState } from "react";
// import { Button, Form, Spinner } from "react-bootstrap";
// import Apis, { endpoints } from "../configs/Apis";

// const ResetPassword = () => {
//     const [email, setEmail] = useState("");
//     const [verificationCode, setVerificationCode] = useState("");
//     const [newPassword, setNewPassword] = useState("");
//     const [confirmNewPassword, setConfirmNewPassword] = useState("");
//     const [error, setError] = useState("");
//     const [success, setSuccess] = useState("");
//     const [isCodeSent, setIsCodeSent] = useState(false);
//     const [isLoading, setIsLoading] = useState(false);

//     const handleSendCode = async (e) => {
//         e.preventDefault();
//         setError("");
//         setSuccess("");
//         setIsLoading(true);

//         if (!email) {
//             setError("Vui lòng nhập email.");
//             setIsLoading(false);
//             return;
//         }

//         try {
//             await Apis.post(endpoints['end-verification-code'], { email });
//             setSuccess("Mã xác thực đã được gửi tới email của bạn.");
//             setIsCodeSent(true);
//         } catch (ex) {
//             setError("Không gửi được mã xác thực. Vui lòng thử lại.");
//         } finally {
//             setIsLoading(false);
//         }
//     };

//     const handleResetPassword = async (e) => {
//         e.preventDefault();
//         setError("");
//         setSuccess("");
//         setIsLoading(true);

//         if (!verificationCode) {
//             setError("Vui lòng nhập mã xác thực.");
//             setIsLoading(false);
//             return;
//         }

//         if (newPassword !== confirmNewPassword) {
//             setError("Mật khẩu mới và xác nhận mật khẩu không khớp.");
//             setIsLoading(false);
//             return;
//         }

//         try {
//             const response = await Apis.post(endpoints['reset-password'], {
//                 email,
//                 verificationCode,
//                 newPassword,
//             });
//             setSuccess(response.data);
//             setError("");
//             setEmail("");
//             setVerificationCode("");
//             setNewPassword("");
//             setConfirmNewPassword("");
//             setIsCodeSent(false);
//         } catch (ex) {
//             setError(ex.response?.data || "Đã xảy ra lỗi khi đặt lại mật khẩu.");
//             setSuccess("");
//         } finally {
//             setIsLoading(false);
//         }
//     };

//     return (
//         <div className="container mt-5" style={{ maxWidth: "500px" }}>
//             <h2>Đặt lại mật khẩu</h2>
//             {error && <div className="alert alert-danger">{error}</div>}
//             {success && <div className="alert alert-success">{success}</div>}

//             {!isCodeSent && (
//                 <Form onSubmit={handleSendCode}>
//                     <Form.Group className="mb-3">
//                         <Form.Label>Email</Form.Label>
//                         <Form.Control
//                             type="email"
//                             placeholder="Nhập email của bạn"
//                             value={email}
//                             onChange={(e) => setEmail(e.target.value)}
//                             required
//                         />
//                     </Form.Group>
//                     <Button type="submit" variant="primary" disabled={isLoading}>
//                         {isLoading ? <Spinner as="span" animation="border" size="sm" /> : "Gửi mã xác thực"}
//                     </Button>
//                 </Form>
//             )}

//             {isCodeSent && (
//                 <Form onSubmit={handleResetPassword}>
//                     <Form.Group className="mb-3">
//                         <Form.Label>Email</Form.Label>
//                         <Form.Control type="email" value={email} disabled />
//                     </Form.Group>
//                     <Form.Group className="mb-3">
//                         <Form.Label>Mã xác thực</Form.Label>
//                         <Form.Control
//                             type="text"
//                             placeholder="Nhập mã xác thực"
//                             value={verificationCode}
//                             onChange={(e) => setVerificationCode(e.target.value)}
//                             required
//                         />
//                     </Form.Group>
//                     <Form.Group className="mb-3">
//                         <Form.Label>Mật khẩu mới</Form.Label>
//                         <Form.Control
//                             type="password"
//                             placeholder="Nhập mật khẩu mới"
//                             value={newPassword}
//                             onChange={(e) => setNewPassword(e.target.value)}
//                             required
//                         />
//                     </Form.Group>
//                     <Form.Group className="mb-3">
//                         <Form.Label>Xác nhận mật khẩu mới</Form.Label>
//                         <Form.Control
//                             type="password"
//                             placeholder="Nhập lại mật khẩu"
//                             value={confirmNewPassword}
//                             onChange={(e) => setConfirmNewPassword(e.target.value)}
//                             required
//                         />
//                     </Form.Group>
//                     <Button type="submit" variant="primary" disabled={isLoading}>
//                         {isLoading ? <Spinner as="span" animation="border" size="sm" /> : "Đặt lại mật khẩu"}
//                     </Button>
//                 </Form>
//             )}
//         </div>
//     );
// };

// export default ResetPassword;
import React, { useState } from "react";
import { Button, Form, Spinner } from "react-bootstrap";
import { useNavigate } from "react-router-dom"; // Import useNavigate
import Apis, { endpoints } from "../configs/Apis";

const ResetPassword = () => {
    const [email, setEmail] = useState("");
    const [verificationCode, setVerificationCode] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmNewPassword, setConfirmNewPassword] = useState("");
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [isCodeSent, setIsCodeSent] = useState(false);
    const [isLoading, setIsLoading] = useState(false);

    const navigate = useNavigate(); // Initialize useNavigate

    const handleSendCode = async (e) => {
        e.preventDefault();
        setError("");
        setSuccess("");
        setIsLoading(true);

        if (!email) {
            setError("Vui lòng nhập email.");
            setIsLoading(false);
            return;
        }

        try {
            await Apis.post(endpoints["end-verification-code"], { email });
            setSuccess("Mã xác thực đã được gửi tới email của bạn.");
            setIsCodeSent(true);
        } catch (ex) {
            setError("Không gửi được mã xác thực. Vui lòng thử lại.");
        } finally {
            setIsLoading(false);
        }
    };

    const handleResetPassword = async (e) => {
        e.preventDefault();
        setError("");
        setSuccess("");
        setIsLoading(true);

        if (!verificationCode) {
            setError("Vui lòng nhập mã xác thực.");
            setIsLoading(false);
            return;
        }

        if (newPassword !== confirmNewPassword) {
            setError("Mật khẩu mới và xác nhận mật khẩu không khớp.");
            setIsLoading(false);
            return;
        }

        try {
            const response = await Apis.post(endpoints["reset-password"], {
                email,
                verificationCode,
                newPassword,
            });
            setSuccess(response.data);
            setError("");
            setEmail("");
            setVerificationCode("");
            setNewPassword("");
            setConfirmNewPassword("");
            setIsCodeSent(false);

            // Điều hướng về trang login sau 2 giây
            setTimeout(() => {
                navigate("/");
            }, 2000);
        } catch (ex) {
            setError(ex.response?.data || "Đã xảy ra lỗi khi đặt lại mật khẩu.");
            setSuccess("");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="container mt-5" style={{ maxWidth: "500px" }}>
            <h2>Đặt lại mật khẩu</h2>
            {error && <div className="alert alert-danger">{error}</div>}
            {success && <div className="alert alert-success">{success}</div>}

            {!isCodeSent && (
                <Form onSubmit={handleSendCode}>
                    <Form.Group className="mb-3">
                        <Form.Label>Email</Form.Label>
                        <Form.Control
                            type="email"
                            placeholder="Nhập email của bạn"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </Form.Group>
                    <Button type="submit" variant="primary" disabled={isLoading}>
                        {isLoading ? <Spinner as="span" animation="border" size="sm" /> : "Gửi mã xác thực"}
                    </Button>
                </Form>
            )}

            {isCodeSent && (
                <Form onSubmit={handleResetPassword}>
                    <Form.Group className="mb-3">
                        <Form.Label>Email</Form.Label>
                        <Form.Control type="email" value={email} disabled />
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Mã xác thực</Form.Label>
                        <Form.Control
                            type="text"
                            placeholder="Nhập mã xác thực"
                            value={verificationCode}
                            onChange={(e) => setVerificationCode(e.target.value)}
                            required
                        />
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Mật khẩu mới</Form.Label>
                        <Form.Control
                            type="password"
                            placeholder="Nhập mật khẩu mới"
                            value={newPassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                            required
                        />
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Xác nhận mật khẩu mới</Form.Label>
                        <Form.Control
                            type="password"
                            placeholder="Nhập lại mật khẩu"
                            value={confirmNewPassword}
                            onChange={(e) => setConfirmNewPassword(e.target.value)}
                            required
                        />
                    </Form.Group>
                    <Button type="submit" variant="primary" disabled={isLoading}>
                        {isLoading ? <Spinner as="span" animation="border" size="sm" /> : "Đặt lại mật khẩu"}
                    </Button>
                </Form>
            )}
        </div>
    );
};

export default ResetPassword;
