import axios from 'axios';

// API 기본 URL 설정
const API_BASE_URL = 'http://localhost:8080/api';

// Axios 인스턴스 생성
const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

export default apiClient;
