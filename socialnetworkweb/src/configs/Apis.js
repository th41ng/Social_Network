import axios from "axios";
import cookie from 'react-cookies'

const BASE_URL = 'http://localhost:8080/SpringSocialApp/api/';

export const endpoints = {
    'categories': '/categories',
    'posts': '/posts',
    'register':'/user',
    'login':'/login',
    'notifications': '/notifications',
    'profile': '/secure/profile', // Endpoint lấy thông tin người dùng
   
}
export const authApis = () => {
    return axios.create({
        baseURL: BASE_URL,
        headers: {
            "Authorization": `Bearer ${cookie.load('token')}`
        }
    })
}

export default axios.create({
    baseURL: BASE_URL
});