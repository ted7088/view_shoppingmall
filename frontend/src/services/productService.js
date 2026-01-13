import apiClient from './api';

/**
 * 상품 관련 API 호출 함수들
 */

// 모든 상품 조회
export const getAllProducts = async () => {
    try {
        const response = await apiClient.get('/products');
        return response.data;
    } catch (error) {
        console.error('상품 목록 조회 실패:', error);
        throw error;
    }
};

// 특정 상품 조회
export const getProductById = async (id) => {
    try {
        const response = await apiClient.get(`/products/${id}`);
        return response.data;
    } catch (error) {
        console.error('상품 조회 실패:', error);
        throw error;
    }
};

// 카테고리별 상품 조회
export const getProductsByCategory = async (category) => {
    try {
        const response = await apiClient.get(`/products/category/${category}`);
        return response.data;
    } catch (error) {
        console.error('카테고리별 상품 조회 실패:', error);
        throw error;
    }
};

// 상품 검색
export const searchProducts = async (keyword) => {
    try {
        const response = await apiClient.get('/products/search', {
            params: { keyword }
        });
        return response.data;
    } catch (error) {
        console.error('상품 검색 실패:', error);
        throw error;
    }
};

// 상품 등록
export const createProduct = async (productData) => {
    try {
        const response = await apiClient.post('/products', productData);
        return response.data;
    } catch (error) {
        console.error('상품 등록 실패:', error);
        throw error;
    }
};

// 상품 수정
export const updateProduct = async (id, productData) => {
    try {
        const response = await apiClient.put(`/products/${id}`, productData);
        return response.data;
    } catch (error) {
        console.error('상품 수정 실패:', error);
        throw error;
    }
};

// 상품 삭제
export const deleteProduct = async (id) => {
    try {
        await apiClient.delete(`/products/${id}`);
    } catch (error) {
        console.error('상품 삭제 실패:', error);
        throw error;
    }
};
