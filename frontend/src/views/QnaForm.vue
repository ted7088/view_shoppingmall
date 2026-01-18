<template>
  <div class="qna-form-container">
    <div class="form-header">
      <button @click="goBack" class="btn-back">← 취소</button>
      <h1>질문 작성</h1>
    </div>

    <form @submit.prevent="handleSubmit" class="qna-form">
      <div class="form-group">
        <label for="title">제목</label>
        <input
          id="title"
          v-model="form.title"
          type="text"
          placeholder="질문 제목을 입력하세요"
          required
          class="form-input"
        />
      </div>

      <div class="form-group">
        <label for="content">내용</label>
        <textarea
          id="content"
          v-model="form.content"
          placeholder="질문 내용을 상세히 작성해주세요"
          rows="10"
          required
          class="form-textarea"
        ></textarea>
      </div>

      <div class="form-actions">
        <button type="button" @click="goBack" class="btn-cancel">취소</button>
        <button 
          type="submit" 
          :disabled="!isValid || submitting"
          class="btn-submit"
        >
          {{ submitting ? '등록 중...' : '질문 등록' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { createQuestion } from '../services/qnaService';
import authService from '../services/authService';

const router = useRouter();

const form = ref({
  title: '',
  content: ''
});
const submitting = ref(false);

const isValid = computed(() => {
  return form.value.title.trim() && form.value.content.trim();
});

// 로그인 체크
if (!authService.isLoggedIn()) {
  alert('로그인이 필요합니다.');
  router.push('/login');
}

// 폼 제출
const handleSubmit = async () => {
  if (!isValid.value) return;

  submitting.value = true;

  try {
    await createQuestion({
      title: form.value.title,
      content: form.value.content
    });
    alert('질문이 등록되었습니다.');
    router.push('/qna');
  } catch (err) {
    alert('질문 등록에 실패했습니다.');
    console.error(err);
  } finally {
    submitting.value = false;
  }
};

// 뒤로가기
const goBack = () => {
  router.push('/qna');
};
</script>

<style scoped>
.qna-form-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.form-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.form-header h1 {
  margin: 0;
  font-size: 28px;
  color: #333;
}

.btn-back {
  background: white;
  border: 2px solid #e0e0e0;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.2s;
}

.btn-back:hover {
  border-color: #667eea;
  color: #667eea;
}

.qna-form {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.form-group {
  margin-bottom: 25px;
}

.form-group label {
  display: block;
  margin-bottom: 10px;
  font-weight: 600;
  color: #333;
  font-size: 16px;
}

.form-input {
  width: 100%;
  padding: 15px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
}

.form-textarea {
  width: 100%;
  padding: 15px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  resize: vertical;
  transition: border-color 0.2s;
  box-sizing: border-box;
  line-height: 1.6;
}

.form-textarea:focus {
  outline: none;
  border-color: #667eea;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 30px;
}

.btn-cancel {
  background: white;
  border: 2px solid #e0e0e0;
  padding: 12px 30px;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel:hover {
  border-color: #f44336;
  color: #f44336;
}

.btn-submit {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s, transform 0.2s;
}

.btn-submit:hover:not(:disabled) {
  opacity: 0.9;
  transform: translateY(-2px);
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}
</style>
