// import React, { useEffect, useState } from 'react';
// import { authApis, endpoints } from '../configs/Apis';
// import cookie from 'react-cookies';

// const Notifications = () => {
//   const [notifications, setNotifications] = useState([]);
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);

//   useEffect(() => {
//     const fetchNotifications = async () => {
//       try {
//         const token = cookie.load('token');
//         if (!token) {
//           setError("Bạn chưa đăng nhập!");
//           setLoading(false);
//           return;
//         }

//         const api = authApis();
//         const res = await api.get(endpoints['notifications']); // Gọi đúng endpoint

//         setNotifications(res.data);
//       } catch (ex) {
//         setError("Lỗi khi lấy thông báo: " + ex.message);
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchNotifications();
//   }, []);

//   if (loading) return <div>Đang tải...</div>;
//   if (error) return <div>{error}</div>;

//   return (
//     <div>
//       <h2>Thông báo của bạn</h2>
//       {notifications.length === 0 ? (
//         <p>Không có thông báo nào.</p>
//       ) : (
//         <ul>
//           {notifications.map(notif => (
//             <li key={notif.notificationId}>
//               <strong>{notif.title}</strong>cho sự kiện: {notif.eventId}
//               <br />
//               <>Mô tả: {notif.content}</> 
//                <br />
//               <small>Gửi lúc: {new Date(notif.sentAt).toLocaleString()}</small> 
//             </li>
//           ))}
//         </ul>
//       )}
//     </div>
//   );
// };

// export default Notifications;
// import React, { useEffect, useState } from 'react';
// import { authApis, endpoints } from '../configs/Apis';
// import cookie from 'react-cookies';

// const Notifications = () => {
//   const [notifications, setNotifications] = useState([]);
//   const [eventDetails, setEventDetails] = useState({});
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);

//   useEffect(() => {
//     const fetchNotifications = async () => {
//       try {
//         const token = cookie.load('token');
//         if (!token) {
//           setError("Bạn chưa đăng nhập!");
//           setLoading(false);
//           return;
//         }

//         const api = authApis();
//         const res = await api.get(endpoints['notifications']); // Gọi đúng endpoint
//         setNotifications(res.data);

//         // Gọi API lấy thông tin sự kiện chi tiết cho từng eventId
//         const eventPromises = res.data.map(async (notif) => {
//           try {
//             const eventRes = await api.get(endpoints.eventDetails(notif.eventId));

//             return { eventId: notif.eventId, details: eventRes.data };
//           } catch {
//             return { eventId: notif.eventId, details: null }; // Trả về null nếu lỗi
//           }
//         });

//         const events = await Promise.all(eventPromises);
//         const eventMap = events.reduce((acc, event) => {
//           acc[event.eventId] = event.details;
//           return acc;
//         }, {});

//         setEventDetails(eventMap);
//       } catch (ex) {
//         setError("Lỗi khi lấy thông báo: " + ex.message);
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchNotifications();
//   }, []);

//   if (loading) return <div>Đang tải...</div>;
//   if (error) return <div>{error}</div>;

//   return (
//     <div>
//       <h2>Thông báo của bạn</h2>
//       {notifications.length === 0 ? (
//         <p>Không có thông báo nào.</p>
//       ) : (
//         <ul>
//           {notifications.map((notif) => (
//             <li key={notif.notificationId}>
//               <strong>{notif.title}</strong> cho sự kiện: {notif.eventId}
//               <br />
//               <>Mô tả: {notif.content}</>
//               <br />
//               <small>Gửi lúc: {new Date(notif.sentAt).toLocaleString()}</small>
//               <br />
//               <div>
//                 <h4>Thông tin sự kiện:</h4>
//                 {eventDetails[notif.eventId] ? (
//                   <div>
//                     <p><strong>Tên sự kiện:</strong> {eventDetails[notif.eventId].name}</p>
//                     <p><strong>Mô tả:</strong> {eventDetails[notif.eventId].description}</p>
//                     <p><strong>Thời gian:</strong> {new Date(eventDetails[notif.eventId].time).toLocaleString()}</p>
//                   </div>
//                 ) : (
//                   <p>Không thể tải thông tin sự kiện.</p>
//                 )}
//               </div>
//             </li>
//           ))}
//         </ul>
//       )}
//     </div>
//   );
// };

// export default Notifications;
import React, { useEffect, useState } from 'react';
import { authApis, endpoints } from '../configs/Apis';
import cookie from 'react-cookies';

const Notifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [eventDetails, setEventDetails] = useState({});
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
        const res = await api.get(endpoints['notifications']);
        setNotifications(res.data);

        const eventPromises = res.data.map(async (notif) => {
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

        setEventDetails(eventMap);
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
     
    <div className="notifications-container">
     <h2>Thông báo của bạn</h2>
      {notifications.length === 0 ? (
        <p>Không có thông báo nào.</p>
      ) : (
        <div className="grid">
          {notifications.map((notif) => (
            <div className="notification-card" key={notif.notificationId}>
              <h3>{notif.title}</h3>
              <p><strong>Mô tả:</strong> {notif.content}</p>
              <p><small>Gửi lúc: {new Date(notif.sentAt).toLocaleString()}</small></p>
              <div className="event-details">
                <h4>Thông tin sự kiện:</h4>
                {eventDetails[notif.eventId] ? (
                  <div>
                    <p><strong>Tên sự kiện:</strong> {eventDetails[notif.eventId].name}</p>
                    <p><strong>Mô tả:</strong> {eventDetails[notif.eventId].description}</p>
                    <p><strong>Thời gian</strong> {new Date(eventDetails[notif.eventId].start_date).toLocaleString()} đến {new Date(eventDetails[notif.eventId].end_date).toLocaleString()}</p>
                  </div>
                ) : (
                  <p>Không thể tải thông tin sự kiện.</p>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Notifications;
