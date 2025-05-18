import React, { useEffect, useState } from 'react';
import { authApis, endpoints } from '../configs/Apis';
import cookie from 'react-cookies';

const Notifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchNotifications = async () => {
      try {
        const token = cookie.load('token');
        if (!token) {
          setError("Bạn chưa đăng nhập!");
          setLoading(false);
          return;
        }

        const api = authApis();
        const res = await api.get(endpoints['notifications']); // Gọi đúng endpoint

        setNotifications(res.data);
      } catch (ex) {
        setError("Lỗi khi lấy thông báo: " + ex.message);
      } finally {
        setLoading(false);
      }
    };

    fetchNotifications();
  }, []);

  if (loading) return <div>Đang tải...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div>
      <h2>Thông báo của bạn</h2>
      {notifications.length === 0 ? (
        <p>Không có thông báo nào.</p>
      ) : (
        <ul>
          {notifications.map(notif => (
            <li key={notif.notificationId}>
              <strong>{notif.title}</strong>: {notif.content} {/* Hiển thị title và content */}
              <br />
              <small>Gửi lúc: {new Date(notif.sentAt).toLocaleString()}</small> {/* Hiển thị thời gian gửi */}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default Notifications;
