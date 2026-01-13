import apiClient from './api';

const TOKEN_KEY = 'access_token';
const REFRESH_TOKEN_KEY = 'refresh_token';
const USER_KEY = 'user';

// 토큰 저장
const saveTokens = (accessToken, refreshToken) => {
    localStorage.setItem(TOKEN_KEY, accessToken);
    if (refreshToken) {
        localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
    }
};

// 토큰 가져오기
const getAccessToken = () => {
    return localStorage.getItem(TOKEN_KEY);
};

const getRefreshToken = () => {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
};

// 토큰 삭제
const removeTokens = () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
};

// 사용자 정보 저장/가져오기
const saveUser = (user) => {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
};

const getUser = () => {
    const user = localStorage.getItem(USER_KEY);
    return user ? JSON.parse(user) : null;
};

// 로그인 여부 확인
const isLoggedIn = () => {
    return !!getAccessToken();
};

// 로그인 API
const login = async (username, password) => {
    try {
        const response = await apiClient.post('/auth/login', {
            username,
            password,
        });

        const { token, refreshToken, username: name, email, role } = response.data;

        saveTokens(token, refreshToken);
        saveUser({ username: name, email, role });

        return { success: true, user: { username: name, email, role } };
    } catch (error) {
        const message = error.response?.data?.message || '로그인에 실패했습니다.';
        return { success: false, message };
    }
};

// 회원가입 API
const signup = async (username, email, password) => {
    try {
        const response = await apiClient.post('/auth/signup', {
            username,
            email,
            password,
        });

        return { success: true, message: response.data.message };
    } catch (error) {
        const message = error.response?.data?.message || '회원가입에 실패했습니다.';
        return { success: false, message };
    }
};

// 토큰 갱신 API
const refreshAccessToken = async () => {
    try {
        const refreshToken = getRefreshToken();
        if (!refreshToken) {
            return { success: false };
        }

        const response = await apiClient.post('/auth/refresh', {
            refreshToken,
        });

        const { accessToken, refreshToken: newRefreshToken } = response.data;
        saveTokens(accessToken, newRefreshToken);

        return { success: true };
    } catch (error) {
        removeTokens();
        return { success: false };
    }
};

// 로그아웃
const logout = () => {
    removeTokens();
};

// 현재 사용자 정보 가져오기 (API)
const getCurrentUser = async () => {
    try {
        const response = await apiClient.get('/auth/me');
        return { success: true, user: response.data };
    } catch (error) {
        return { success: false };
    }
};

// Axios 인터셉터 설정 - 모든 요청에 토큰 추가
apiClient.interceptors.request.use(
    (config) => {
        const token = getAccessToken();
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Axios 인터셉터 설정 - 401 응답 시 토큰 갱신 시도
apiClient.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            const result = await refreshAccessToken();
            if (result.success) {
                originalRequest.headers.Authorization = `Bearer ${getAccessToken()}`;
                return apiClient(originalRequest);
            }
        }

        return Promise.reject(error);
    }
);

export default {
    login,
    signup,
    logout,
    refreshAccessToken,
    getCurrentUser,
    getAccessToken,
    getUser,
    isLoggedIn,
    saveUser,
};
