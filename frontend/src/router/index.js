import { createRouter, createWebHistory } from 'vue-router';
import ProductList from '../views/ProductList.vue';
import ProductDetail from '../views/ProductDetail.vue';
import ProductForm from '../views/ProductForm.vue';
import Login from '../views/Login.vue';
import Signup from '../views/Signup.vue';
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
];

const router = createRouter({
    history: createWebHistory(),
    routes,
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

