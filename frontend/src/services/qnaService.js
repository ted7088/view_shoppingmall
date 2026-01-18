import apiClient from './api';
import authService from './authService';

/**
 * Q&A 관련 API 호출 함수들
 */

// 질문 목록 조회 (페이지네이션)
export const getQuestions = async (page = 0, size = 10) => {
    try {
        const response = await apiClient.get(`/qna?page=${page}&size=${size}`);
        return response.data;
    } catch (error) {
        console.error('질문 목록 조회 실패:', error);
        throw error;
    }
};

// 질문 검색
export const searchQuestions = async (keyword, page = 0, size = 10) => {
    try {
        const response = await apiClient.get(`/qna/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`);
        return response.data;
    } catch (error) {
        console.error('질문 검색 실패:', error);
        throw error;
    }
};

// 질문 상세 조회
export const getQuestionById = async (id) => {
    try {
        const response = await apiClient.get(`/qna/${id}`);
        return response.data;
    } catch (error) {
        console.error('질문 상세 조회 실패:', error);
        throw error;
    }
};

// 내 질문 목록 조회
export const getMyQuestions = async () => {
    try {
        const token = authService.getAccessToken();
        const response = await apiClient.get('/qna/my', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        return response.data;
    } catch (error) {
        console.error('내 질문 목록 조회 실패:', error);
        throw error;
    }
};

// 질문 생성
export const createQuestion = async (questionData) => {
    try {
        const token = authService.getAccessToken();
        const response = await apiClient.post('/qna', questionData, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        return response.data;
    } catch (error) {
        console.error('질문 생성 실패:', error);
        throw error;
    }
};

// 질문 삭제
export const deleteQuestion = async (id) => {
    try {
        const token = authService.getAccessToken();
        await apiClient.delete(`/qna/${id}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
    } catch (error) {
        console.error('질문 삭제 실패:', error);
        throw error;
    }
};

// 답변 생성 (관리자)
export const createAnswer = async (questionId, answerData) => {
    try {
        const token = authService.getAccessToken();
        const response = await apiClient.post(`/qna/${questionId}/answer`, answerData, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        return response.data;
    } catch (error) {
        console.error('답변 생성 실패:', error);
        throw error;
    }
};
