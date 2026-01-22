import { createRouter, createWebHistory } from 'vue-router';
import ProductList from '../views/ProductList.vue';
import ProductDetail from '../views/ProductDetail.vue';
import ProductForm from '../views/ProductForm.vue';
import Login from '../views/Login.vue';
import Signup from '../views/Signup.vue';
import QnaList from '../views/QnaList.vue';
import QnaDetail from '../views/QnaDetail.vue';
import QnaForm from '../views/QnaForm.vue';
import WishlistPage from '../views/WishlistPage.vue';
import authService from '../services/authService';

const routes = [
    {
        path: '/',
        name: 'ProductList',
        component: ProductList,
    },
    {
        path: '/login',
        name: 'Login',
        component: Login,
        meta: { guest: true },
    },
    {
        path: '/signup',
        name: 'Signup',
        component: Signup,
        meta: { guest: true },
    },
    {
        path: '/products/new',
        name: 'ProductNew',
        component: ProductForm,
        meta: { requiresAuth: true },
    },
    {
        path: '/products/:id',
        name: 'ProductDetail',
        component: ProductDetail,
    },
    {
        path: '/products/:id/edit',
        name: 'ProductEdit',
        component: ProductForm,
        meta: { requiresAuth: true },
    },
    {
        path: '/wishlist',
        name: 'Wishlist',
        component: WishlistPage,
        meta: { requiresAuth: true },
    },
    {
        path: '/qna',
        name: 'QnaList',
        component: QnaList,
    },
    {
        path: '/qna/new',
        name: 'QnaNew',
        component: QnaForm,
        meta: { requiresAuth: true },
    },
    {
        path: '/qna/:id',
        name: 'QnaDetail',
        component: QnaDetail,
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
    // 페이지 전환 시 스크롤 위치 제어
    scrollBehavior(to, from, savedPosition) {
        // 뒤로가기/앞으로가기 시 이전 위치로 복원
        if (savedPosition) {
            return savedPosition;
        }
        // 해시가 있으면 해당 위치로 스크롤
        if (to.hash) {
            return { el: to.hash, behavior: 'smooth' };
        }
        // 기본: 페이지 맨 위로 스크롤
        return { top: 0, behavior: 'smooth' };
    },
});

// 네비게이션 가드
router.beforeEach((to, from, next) => {
    const isLoggedIn = authService.isLoggedIn();

    // 인증이 필요한 페이지
    if (to.meta.requiresAuth && !isLoggedIn) {
        next({ name: 'Login', query: { redirect: to.fullPath } });
        return;
    }

    // 게스트 전용 페이지 (로그인/회원가입)
    if (to.meta.guest && isLoggedIn) {
        next({ name: 'ProductList' });
        return;
    }

    next();
});

export default router;

