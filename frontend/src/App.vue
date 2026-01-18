<template>
  <div id="app">
    <header class="app-header">
      <div class="header-content">
        <h1 @click="goHome" class="logo">
          <span class="logo-icon">🛒</span>
          <span class="logo-text">Modern Shop</span>
        </h1>
        <nav class="nav-menu">
          <router-link to="/" class="nav-link">
            <span class="nav-icon">📦</span>
            상품 목록
          </router-link>
          <router-link to="/qna" class="nav-link">
            <span class="nav-icon">❓</span>
            Q&A
          </router-link>
          <router-link v-if="isLoggedIn" to="/products/new" class="nav-link nav-link-primary">
            <span class="nav-icon">➕</span>
            상품 등록
          </router-link>
        </nav>
        <div class="auth-section">
          <template v-if="isLoggedIn">
            <div class="user-info">
              <span class="user-icon">👤</span>
              <span class="user-name">{{ user?.username }}</span>
            </div>
            <button @click="handleLogout" class="logout-btn">
              <span class="btn-icon">🚪</span>
              로그아웃
            </button>
          </template>
          <template v-else>
            <router-link to="/login" class="auth-btn login-btn">
              <span class="btn-icon">🔐</span>
              로그인
            </router-link>
            <router-link to="/signup" class="auth-btn signup-btn">
              <span class="btn-icon">✨</span>
              회원가입
            </router-link>
          </template>
        </div>
      </div>
    </header>
    
    <main class="main-content">
      <router-view />
    </main>
    
    <footer class="app-footer">
      <div class="footer-content">
        <p>© 2026 Modern Shop - Vue + Spring Boot</p>
        <p class="footer-subtitle">Built with ❤️ for learning</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import authService from './services/authService';

const router = useRouter();
const route = useRoute();

const isLoggedIn = ref(authService.isLoggedIn());
const user = ref(authService.getUser());

const goHome = () => {
  router.push('/');
};

const handleLogout = () => {
  authService.logout();
  isLoggedIn.value = false;
  user.value = null;
  router.push('/');
};

// 라우트 변경 시 로그인 상태 업데이트
watch(route, () => {
  isLoggedIn.value = authService.isLoggedIn();
  user.value = authService.getUser();
});

onMounted(() => {
  isLoggedIn.value = authService.isLoggedIn();
  user.value = authService.getUser();
});
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', -apple-system, BlinkMacSystemFont, Roboto, 'Helvetica Neue', Arial, sans-serif;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-attachment: fixed;
  min-height: 100vh;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 3px solid transparent;
  border-image: linear-gradient(90deg, #667eea, #764ba2);
  border-image-slice: 1;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 28px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  cursor: pointer;
  margin: 0;
  transition: transform 0.3s ease;
}

.logo:hover {
  transform: scale(1.05);
}

.logo-icon {
  font-size: 32px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.logo-text {
  letter-spacing: -0.5px;
}

.nav-menu {
  display: flex;
  gap: 16px;
  flex: 1;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  color: #4a5568;
  font-weight: 600;
  padding: 12px 24px;
  border-radius: 12px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.nav-link::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.1), transparent);
  transition: left 0.5s ease;
}

.nav-link:hover::before {
  left: 100%;
}

.nav-link:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  transform: translateY(-2px);
}

.nav-link.router-link-active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.nav-link-primary {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(245, 87, 108, 0.3);
}

.nav-link-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(245, 87, 108, 0.4);
}

.nav-link-primary.router-link-active {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.nav-icon {
  font-size: 18px;
}

/* Auth Section */
.auth-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  color: white;
  font-weight: 600;
  font-size: 14px;
}

.user-icon {
  font-size: 16px;
}

.user-name {
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.auth-btn, .logout-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 18px;
  border-radius: 10px;
  font-weight: 600;
  font-size: 14px;
  text-decoration: none;
  transition: all 0.3s ease;
  cursor: pointer;
  border: none;
}

.login-btn {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.login-btn:hover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.signup-btn {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.signup-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 87, 108, 0.4);
}

.logout-btn {
  background: rgba(245, 87, 108, 0.1);
  color: #f5576c;
}

.logout-btn:hover {
  background: linear-gradient(135deg, #f5576c 0%, #ff8a65 100%);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 87, 108, 0.3);
}

.btn-icon {
  font-size: 14px;
}

.main-content {
  flex: 1;
  padding: 40px 20px;
}

.app-footer {
  background: rgba(26, 32, 44, 0.95);
  backdrop-filter: blur(10px);
  color: white;
  text-align: center;
  padding: 30px 20px;
  margin-top: 60px;
  border-top: 3px solid transparent;
  border-image: linear-gradient(90deg, #667eea, #764ba2);
  border-image-slice: 1;
}

.footer-content p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.footer-subtitle {
  margin-top: 8px !important;
  font-size: 12px;
  opacity: 0.7;
}

/* 스크롤바 스타일링 */
::-webkit-scrollbar {
  width: 10px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 5px;
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}

/* 반응형 */
@media (max-width: 768px) {
  .header-content {
    flex-wrap: wrap;
    gap: 16px;
  }

  .nav-menu {
    order: 3;
    width: 100%;
    justify-content: center;
  }

  .auth-section {
    gap: 8px;
  }

  .auth-btn, .logout-btn {
    padding: 8px 12px;
    font-size: 13px;
  }
}
</style>

