import React, { useState, useEffect, useCallback } from 'react';
import { authApis, endpoints } from '../configs/Apis';
import cookie from 'react-cookies';
import { Spinner, Alert, Button } from 'react-bootstrap';

const Notifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [eventDetails, setEventDetails] = useState({});
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [canLoadMore, setCanLoadMore] = useState(true);

  const fetchEventDetailsForNotifications = useCallback(async (notifications) => {
    const api = authApis();
    const eventPromises = notifications.map(async (notif) => {
      try {
        const eventRes = await api.get(endpoints.eventDetails(notif.eventId));
        return { eventId: notif.eventId, details: eventRes.data };
      } catch {
        return { eventId: notif.eventId, details: null };
      }
    });

    const events = await Promise.all(eventPromises);
    const eventMap = events.reduce((acc, event) => {
      acc[event.eventId] = event.details;
      return acc;
    }, {});

    setEventDetails(prev => ({ ...prev, ...eventMap }));
  }, []);

  const fetchNotifications = useCallback(async (pageToLoad) => {
    if (pageToLoad === 1) {
      setLoading(true);
      setCanLoadMore(true);  // reset lại trạng thái load more
    } else {
      setLoadingMore(true);
    }
    setError(null);

    try {
      const token = cookie.load('token');
      if (!token) {
        throw new Error("Bạn chưa đăng nhập!");
      }

      const api = authApis();
      const response = await api.get(endpoints.notifications, { params: { page: pageToLoad } });

      if (response.data && Array.isArray(response.data)) {
        const fetchedNotifications = response.data;

        if (fetchedNotifications.length === 0) {
          setCanLoadMore(false);
        } else {
          setNotifications(prev =>
            pageToLoad === 1 ? fetchedNotifications : [...prev, ...fetchedNotifications]
          );
          await fetchEventDetailsForNotifications(fetchedNotifications);
        }
      } else {
        setCanLoadMore(false);
      }
    } catch (err) {
      setError(err.message || "Lỗi khi tải thông báo.");
      setCanLoadMore(false);
    } finally {
      if (pageToLoad === 1) {
        setLoading(false);
      } else {
        setLoadingMore(false);
      }
    }
  }, [fetchEventDetailsForNotifications]);

  useEffect(() => {
    fetchNotifications(currentPage);
  }, [currentPage, fetchNotifications]);

  const handleLoadMore = () => {
    if (!loadingMore && canLoadMore) {
      setCurrentPage(prevPage => prevPage + 1);
    }
  };

  if (loading && currentPage === 1) {
    return (
      <div className="text-center my-5">
        <Spinner animation="border" variant="primary" />
        <p>Đang tải thông báo...</p>
      </div>
    );
  }

  if (error && notifications.length === 0) {
    return <Alert variant="danger" className="m-3 text-center">{error}</Alert>;
  }

  return (
    <div className="mt-4">
      <h2 className="mb-4 text-center text-primary">Thông báo của bạn</h2>
      {error && <Alert variant="warning" className="text-center my-2">{error}</Alert>}

      {notifications.length === 0 && !loading && (
        <Alert variant="info" className="text-center">Hiện tại không có thông báo nào.</Alert>
      )}

      {notifications.length > 0 && (
        <div className="grid">
          {notifications.map((notif, index) => (
            <div
              className="notification-card"
              key={`${notif.notificationId}-${notif.sentAt}-${index}`}>
              <h3>{notif.title}</h3>
              <p><strong>Mô tả:</strong> {notif.content}</p>
              <p><small>Gửi lúc: {new Date(notif.sentAt).toLocaleString()}</small></p>
              <div className="event-details">
                <h4>Thông tin sự kiện:</h4>
                {eventDetails[notif.eventId] ? (
                  <div>
                    <p><strong>Tên sự kiện:</strong> {eventDetails[notif.eventId].title}</p>
                    <p><strong>Mô tả:</strong> {eventDetails[notif.eventId].description}</p>
                    <p><strong>Thời gian:</strong> {new Date(eventDetails[notif.eventId].start_date).toLocaleString()} đến {new Date(eventDetails[notif.eventId].end_date).toLocaleString()}</p>
                  </div>
                ) : (
                  <p>Không thể tải thông tin sự kiện.</p>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {canLoadMore && !loadingMore && notifications.length > 0 && (
        <div className="text-center mt-3">
          <Button onClick={handleLoadMore} variant="primary">Tải thêm</Button>
        </div>
      )}

      {loadingMore && (
        <div className="text-center my-3">
          <Spinner animation="border" variant="secondary" size="sm" />
          <span className="ms-2">Đang tải thêm...</span>
        </div>
      )}

      {!canLoadMore && notifications.length > 0 && !loading && (
        <Alert variant="light" className="text-center mt-3 mb-0">Đã hiển thị tất cả thông báo.</Alert>
      )}
    </div>
  );
};

export default Notifications;