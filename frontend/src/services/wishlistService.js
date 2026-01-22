import api from './api';

/**
 * 찜하기 추가
 */
export const addToWishlist = async (productId) => {
    try {
        const response = await api.post(`/wishlist/${productId}`);
        return response.data;
    } catch (error) {
        console.error('찜하기 추가 실패:', error);
        throw error;
    }
};

/**
 * 찜하기 삭제
 */
export const removeFromWishlist = async (productId) => {
    try {
        const response = await api.delete(`/wishlist/${productId}`);
        return response.data;
    } catch (error) {
        console.error('찜하기 삭제 실패:', error);
        throw error;
    }
};

/**
 * 찜 목록 조회
 */
export const getWishlist = async () => {
    try {
        const response = await api.get('/wishlist');
        return response.data;
    } catch (error) {
        console.error('찜 목록 조회 실패:', error);
        throw error;
    }
};

/**
 * 찜 상태 확인
 */
export const checkWishlistStatus = async (productId) => {
    try {
        const response = await api.get(`/wishlist/check/${productId}`);
        return response.data.isWishlisted;
    } catch (error) {
        console.error('찜 상태 확인 실패:', error);
        return false;
    }
};
