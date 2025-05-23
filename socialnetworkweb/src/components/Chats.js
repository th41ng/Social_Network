import React, { useState, useEffect, useContext } from "react";
import { db } from "../configs/firebase"; 
import { MyUserContext } from "../configs/Contexts";
import { ref, onValue, push, serverTimestamp } from "firebase/database";
import { Form, Button, ListGroup, InputGroup } from "react-bootstrap";

const Chats = () => {
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState("");
  const user = useContext(MyUserContext); // Lấy thông tin người dùng
  const chatRef = ref(db, "chats");

  // Lấy dữ liệu tin nhắn từ Firebase
  useEffect(() => {
    const unsubscribe = onValue(chatRef, (snapshot) => {
      const data = snapshot.val();
      const loadedMessages = [];
      for (let id in data) {
        loadedMessages.push({ id, ...data[id] });
      }
      // Sắp xếp tin nhắn theo thời gian
      setMessages(
        loadedMessages.sort((a, b) => (a.timestamp || 0) - (b.timestamp || 0))
      );
    });

    // Cleanup listener khi component bị unmount
    return () => unsubscribe();
  }, []);

  // Gửi tin nhắn
  const sendMessage = async (e) => {
    e.preventDefault();
    if (newMessage.trim() === "") return;

    const messageData = {
      content: newMessage,
      timestamp: serverTimestamp(),
      sender: user?.username || "Ẩn danh",
    };

    try {
      await push(chatRef, messageData);
      setNewMessage(""); // Xóa nội dung sau khi gửi
    } catch (error) {
      console.error("Lỗi khi gửi tin nhắn:", error);
    }
  };

  return (
    <div className="container mt-4">
      <h2 className="text-center mb-4">Hộp Tin Nhắn</h2>
      <ListGroup
        className="mb-3 chat-box"
        style={{
          maxHeight: "400px",
          overflowY: "auto",
          backgroundColor: "#f8f9fa",
          padding: "10px",
          borderRadius: "8px",
          border: "1px solid #ced4da",
        }}
      >
        {messages.map((msg) => (
          <ListGroup.Item
            key={msg.id}
            className={`d-flex ${
              msg.sender === (user?.username || "Ẩn danh")
                ? "justify-content-end"
                : "justify-content-start"
            } border-0`}
            style={{ backgroundColor: "transparent" }}
          >
            <div
              className={`p-3 rounded shadow-sm ${
                msg.sender === (user?.username || "Ẩn danh")
                  ? "bg-primary text-white"
                  : "bg-light"
              }`}
              style={{
                maxWidth: "70%",
                wordBreak: "break-word",
              }}
            >
              <div className="mb-1">
                <strong>{msg.sender}</strong>
              </div>
              <div>{msg.content}</div>
              <div
                style={{
                  fontSize: "0.75rem",
                  marginTop: "8px",
                  textAlign: "right",
                  color:
                    msg.sender === (user?.username || "Ẩn danh")
                      ? "#d1e7ff"
                      : "#6c757d",
                }}
              >
                {msg.timestamp
                  ? new Date(msg.timestamp).toLocaleTimeString("vi-VN", {
                      hour: "2-digit",
                      minute: "2-digit",
                    })
                  : "Đang gửi..."}
              </div>
            </div>
          </ListGroup.Item>
        ))}
      </ListGroup>
      <Form onSubmit={sendMessage}>
        <InputGroup>
          <Form.Control
            type="text"
            placeholder="Nhập tin nhắn..."
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            style={{
              borderTopLeftRadius: "8px",
              borderBottomLeftRadius: "8px",
            }}
          />
          <Button
            variant="primary"
            type="submit"
            style={{
              borderTopRightRadius: "8px",
              borderBottomRightRadius: "8px",
            }}
          >
            Gửi
          </Button>
        </InputGroup>
      </Form>
    </div>
  );
};

export default Chats;
