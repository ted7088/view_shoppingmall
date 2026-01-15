import apiClient from './api';
import authService from './authService';

/**
 * 리뷰 관련 API 호출 함수들
 */

// 상품별 리뷰 목록 조회
export const getReviewsByProductId = async (productId) => {
    try {
        const response = await apiClient.get(`/reviews/product/${productId}`);
        return response.data;
    } catch (error) {
        console.error('리뷰 목록 조회 실패:', error);
        throw error;
    }
};

// 상품별 평균 별점 및 리뷰 개수 조회
export const getProductRating = async (productId) => {
    try {
        const response = await apiClient.get(`/reviews/product/${productId}/rating`);
        return response.data;
    } catch (error) {
        console.error('평균 별점 조회 실패:', error);
        throw error;
    }
};

// 리뷰 생성
export const createReview = async (reviewData) => {
    try {
        const token = authService.getToken();
        const response = await apiClient.post('/reviews', reviewData, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        return response.data;
    } catch (error) {
        console.error('리뷰 생성 실패:', error);
        throw error;
    }
};

// 리뷰 삭제
export const deleteReview = async (reviewId) => {
    try {
        const token = authService.getToken();
        await apiClient.delete(`/reviews/${reviewId}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
    } catch (error) {
        console.error('리뷰 삭제 실패:', error);
        throw error;
    }
};
