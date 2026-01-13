<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-header">
        <h2 class="auth-title">
          <span class="auth-icon">✨</span>
          회원가입
        </h2>
        <p class="auth-subtitle">Modern Shop의 회원이 되어보세요</p>
      </div>

      <form @submit.prevent="handleSignup" class="auth-form">
        <div class="form-group">
          <label for="username" class="form-label">
            <span class="label-icon">👤</span>
            사용자명
          </label>
          <input
            id="username"
            v-model="username"
            type="text"
            class="form-input"
            placeholder="3~50자 사이로 입력하세요"
            required
            minlength="3"
            maxlength="50"
            :disabled="isLoading"
          />
        </div>

        <div class="form-group">
          <label for="email" class="form-label">
            <span class="label-icon">📧</span>
            이메일
          </label>
          <input
            id="email"
            v-model="email"
            type="email"
            class="form-input"
            placeholder="example@email.com"
            required
            :disabled="isLoading"
          />
        </div>

        <div class="form-group">
          <label for="password" class="form-label">
            <span class="label-icon">🔑</span>
            비밀번호
          </label>
          <input
            id="password"
            v-model="password"
            type="password"
            class="form-input"
            placeholder="6자 이상으로 입력하세요"
            required
            minlength="6"
            :disabled="isLoading"
          />
        </div>

        <div class="form-group">
          <label for="confirmPassword" class="form-label">
            <span class="label-icon">🔐</span>
            비밀번호 확인
          </label>
          <input
            id="confirmPassword"
            v-model="confirmPassword"
            type="password"
            class="form-input"
            placeholder="비밀번호를 다시 입력하세요"
            required
            :disabled="isLoading"
          />
        </div>

        <div v-if="errorMessage" class="error-message">
          <span class="error-icon">⚠️</span>
          {{ errorMessage }}
        </div>

        <div v-if="successMessage" class="success-message">
          <span class="success-icon">✅</span>
          {{ successMessage }}
        </div>

        <button type="submit" class="auth-button" :disabled="isLoading">
          <span v-if="isLoading" class="loading-spinner"></span>
          <span v-else>회원가입</span>
        </button>
      </form>

      <div class="auth-footer">
        <p>이미 계정이 있으신가요?</p>
        <router-link to="/login" class="auth-link">로그인</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import authService from '../services/authService';

const router = useRouter();

const username = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');
const errorMessage = ref('');
const successMessage = ref('');
const isLoading = ref(false);

const handleSignup = async () => {
  errorMessage.value = '';
  successMessage.value = '';

  // 비밀번호 확인
  if (password.value !== confirmPassword.value) {
    errorMessage.value = '비밀번호가 일치하지 않습니다.';
    return;
  }

  // 비밀번호 길이 확인
  if (password.value.length < 6) {
    errorMessage.value = '비밀번호는 6자 이상이어야 합니다.';
    return;
  }

  isLoading.value = true;

  try {
    const result = await authService.signup(
      username.value,
      email.value,
      password.value
    );

    if (result.success) {
      successMessage.value = '회원가입이 완료되었습니다! 로그인 페이지로 이동합니다.';
      setTimeout(() => {
        router.push('/login');
      }, 2000);
    } else {
      errorMessage.value = result.message;
    }
  } catch (error) {
    errorMessage.value = '회원가입 중 오류가 발생했습니다.';
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
  padding: 20px;
}

.auth-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  padding: 48px;
  width: 100%;
  max-width: 440px;
  transform: translateY(0);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.auth-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.18);
}

.auth-header {
  text-align: center;
  margin-bottom: 36px;
}

.auth-title {
  font-size: 32px;
  font-weight: 800;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.auth-icon {
  font-size: 36px;
}

.auth-subtitle {
  color: #718096;
  font-size: 15px;
  margin: 0;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #4a5568;
  font-size: 14px;
}

.label-icon {
  font-size: 16px;
}

.form-input {
  padding: 16px 20px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 16px;
  transition: all 0.3s ease;
  outline: none;
  background: #f8fafc;
  color: #1a202c;
}

.form-input:focus {
  border-color: #f093fb;
  background: white;
  box-shadow: 0 0 0 4px rgba(240, 147, 251, 0.15);
}

.form-input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.form-input::placeholder {
  color: #a0aec0;
}

.error-message {
  display: flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, #fff5f5 0%, #fed7d7 100%);
  border: 1px solid #fc8181;
  border-radius: 12px;
  padding: 14px 18px;
  color: #c53030;
  font-size: 14px;
  font-weight: 500;
}

.error-icon {
  font-size: 18px;
}

.success-message {
  display: flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, #f0fff4 0%, #c6f6d5 100%);
  border: 1px solid #68d391;
  border-radius: 12px;
  padding: 14px 18px;
  color: #276749;
  font-size: 14px;
  font-weight: 500;
}

.success-icon {
  font-size: 18px;
}

.auth-button {
  padding: 18px 24px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 17px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-top: 8px;
}

.auth-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(245, 87, 108, 0.4);
}

.auth-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-spinner {
  width: 22px;
  height: 22px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.auth-footer {
  text-align: center;
  margin-top: 28px;
  padding-top: 24px;
  border-top: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.auth-footer p {
  color: #718096;
  margin: 0;
  font-size: 14px;
}

.auth-link {
  color: #f5576c;
  font-weight: 700;
  text-decoration: none;
  transition: color 0.3s ease;
}

.auth-link:hover {
  color: #f093fb;
}
</style>
