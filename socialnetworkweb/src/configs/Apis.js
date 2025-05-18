import axios from "axios";
import cookie from 'react-cookies';

const BASE_URL = 'http://localhost:8080/SpringSocialApp/api/';

export const endpoints = {
    'categories': '/categories',
    'posts': '/posts', 
    'post-details': (postId) => `/posts/${postId}`,
    'register': '/user',
    'login': '/login',
    'current-user': '/current-user', // Bỏ / ở cuối nếu backend không có

    // Endpoint cho reactions của bài viết
    'post-reactions': (postId) => `/posts/${postId}/reactions`, // BỎ DẤU / Ở CUỐI

    // Endpoint cho reactions của bình luận
    'comment-reactions': (commentId) => `/comments/${commentId}/reactions`, // BỎ DẤU / Ở CUỐI

    // Endpoint để lấy danh sách bình luận của một bài viết
    'post-comments': (postId) => `/posts/${postId}/comments`, // BỎ DẤU / Ở CUỐI

    // Endpoint để thêm bình luận mới cho bài viết
    'add-comment': (postId) => `/posts/${postId}/comments` // POST request, BỎ DẤU / Ở CUỐI
};

export const authApis = () => {
    const token = cookie.load('token');
    return axios.create({
        baseURL: BASE_URL,
        headers: {
            "Authorization": `Bearer ${token}`
        }
    });
}

export default axios.create({
    baseURL: BASE_URL
});