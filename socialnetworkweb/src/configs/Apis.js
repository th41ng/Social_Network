import axios from "axios";


const BASE_URL = 'http://localhost:8080/SpringSocialApp/api/';

export const endpoints = {
    'categories': '/categories',
    'posts': '/posts',
    
   
}


export default axios.create({
    baseURL: BASE_URL
});