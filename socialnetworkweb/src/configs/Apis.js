// src/configs/Apis.js
import axios from "axios";
import cookie from 'react-cookies';

const BASE_URL = 'http://localhost:8080/SpringSocialApp/api/';

export const endpoints = {
    'posts': '/posts',
    'post-details': (postId) => `posts/${postId}`,
    'delete-post': (postId) => `posts/${postId}`,
    'post-reactions': (postId) => `posts/${postId}/reactions`,
    'comment-reactions': (commentId) => `comments/${commentId}/reactions`,
    'add-comment': (postId) => `posts/${postId}/comments`,
    'update-comment': (commentId) => `comments/${commentId}`,
    'delete-comment': (commentId) => `comments/${commentId}`,
    'toggle-comment-lock': (postId) => `posts/${postId}/toggle-comment-lock`,
    'categories': '/categories',
    'categories2': '/categories2',
    'register':'/user',
    'login':'/login',
    'notifications': '/notifications',
    'profile': '/secure/profile',
    'surveys_list': '/surveys',
    'survey_detail': (surveyId) => `/surveys/${surveyId}`,
    'survey_submit_responses': (surveyId) => `/surveys/${surveyId}/responses`,
    
    'reset-password': '/reset-password',
    'end-verification-code': '/send-verification-code',

    userposts: (userId) =>`/user-posts/${userId}`,
    eventDetails: (id) => `/event/${id}`,


};

export const authApis = () => {
    return axios.create({
        baseURL: BASE_URL,
        headers: {
            "Authorization": `Bearer ${cookie.load('token')}`
        }
    });
}

export default axios.create({
    baseURL: BASE_URL
});