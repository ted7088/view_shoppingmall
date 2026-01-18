<template>
  <div class="qna-detail-container">
    <!-- 로딩 상태 -->
    <div v-if="loading" class="loading">로딩 중...</div>

    <!-- 에러 상태 -->
    <div v-else-if="error" class="error">
      {{ error }}
      <button @click="goBack" class="btn-secondary">목록으로 돌아가기</button>
    </div>

    <!-- 질문 상세 -->
    <div v-else-if="question" class="qna-detail">
      <div class="detail-header">
        <button @click="goBack" class="btn-back">← 목록으로</button>
        <button 
          v-if="canDelete" 
          @click="handleDelete" 
          class="btn-delete"
        >
          삭제
        </button>
      </div>

      <!-- 질문 영역 -->
      <div class="question-section">
        <div class="question-header">
          <span :class="['status-badge', question.isAnswered ? 'answered' : 'pending']">
            {{ question.isAnswered ? '답변완료' : '답변대기' }}
          </span>
          <h1>{{ question.title }}</h1>
        </div>
        <div class="question-meta">
          <span class="author">작성자: {{ question.username }}</span>
          <span class="date">{{ formatDate(question.createdAt) }}</span>
        </div>
        <div class="question-content">
          {{ question.content }}
        </div>
      </div>

      <!-- 답변 영역 -->
      <div class="answer-section">
        <h2>답변</h2>
        
        <!-- 기존 답변 표시 -->
        <div v-if="question.answer" class="answer-box">
          <div class="answer-meta">
            <span class="author">관리자: {{ question.answer.username }}</span>
            <span class="date">{{ formatDate(question.answer.createdAt) }}</span>
          </div>
          <div class="answer-content">
            {{ question.answer.content }}
          </div>
        </div>

        <!-- 답변 작성 폼 (관리자용) -->
        <div v-else-if="isAdmin" class="answer-form">
          <h3>답변 작성</h3>
          <textarea
            v-model="answerContent"
            placeholder="답변 내용을 입력하세요"
            rows="5"
            class="answer-textarea"
          ></textarea>
          <button 
            @click="submitAnswer" 
            :disabled="!answerContent.trim() || submitting"
            class="btn-submit"
          >
            {{ submitting ? '등록 중...' : '답변 등록' }}
          </button>
        </div>

        <!-- 답변 대기 메시지 -->
        <div v-else class="waiting-message">
          아직 답변이 등록되지 않았습니다.
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { getQuestionById, deleteQuestion, createAnswer } from '../services/qnaService';
import authService from '../services/authService';

const router = useRouter();
const route = useRoute();

const question = ref(null);
const loading = ref(false);
const error = ref(null);
const answerContent = ref('');
const submitting = ref(false);

const currentUser = computed(() => authService.getUser());
const isLoggedIn = computed(() => authService.isLoggedIn());
const isAdmin = computed(() => currentUser.value?.role === 'ADMIN');
const canDelete = computed(() => {
  if (!isLoggedIn.value || !question.value) return false;
  return question.value.userId === currentUser.value?.id;
});

onMounted(() => {
  loadQuestion();
});

// 질문 로드
const loadQuestion = async () => {
  loading.value = true;
  error.value = null;

  try {
    const id = route.params.id;
    question.value = await getQuestionById(id);
  } catch (err) {
    error.value = '질문을 불러오는데 실패했습니다.';
    console.error(err);
  } finally {
    loading.value = false;
  }
};

// 목록으로 돌아가기
const goBack = () => {
  router.push('/qna');
};

// 질문 삭제
const handleDelete = async () => {
  if (!confirm('정말 이 질문을 삭제하시겠습니까?')) {
    return;
  }

  try {
    await deleteQuestion(question.value.id);
    alert('질문이 삭제되었습니다.');
    router.push('/qna');
  } catch (err) {
    alert('질문 삭제에 실패했습니다.');
    console.error(err);
  }
};

// 답변 등록
const submitAnswer = async () => {
  if (!answerContent.value.trim()) return;

  submitting.value = true;

  try {
    await createAnswer(question.value.id, { content: answerContent.value });
    alert('답변이 등록되었습니다.');
    loadQuestion(); // 새로고침
    answerContent.value = '';
  } catch (err) {
    alert('답변 등록에 실패했습니다.');
    console.error(err);
  } finally {
    submitting.value = false;
  }
};

// 날짜 포맷팅
const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};
</script>

<style scoped>
.qna-detail-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.loading,
.error {
  text-align: center;
  padding: 40px;
  font-size: 18px;
}

.error {
  color: #f44336;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
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

.btn-delete {
  background: #f44336;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-delete:hover {
  background: #d32f2f;
}

.btn-secondary {
  margin-top: 20px;
  background: #757575;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
}

.question-section {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.question-header {
  margin-bottom: 20px;
}

.status-badge {
  display: inline-block;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 15px;
}

.status-badge.answered {
  background: #e8f5e9;
  color: #2e7d32;
}

.status-badge.pending {
  background: #fff3e0;
  color: #e65100;
}

.question-header h1 {
  margin: 0;
  font-size: 28px;
  color: #333;
  line-height: 1.4;
}

.question-meta {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 14px;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.question-content {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
}

.answer-section {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.answer-section h2 {
  margin: 0 0 20px 0;
  font-size: 22px;
  color: #333;
}

.answer-box {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  border-left: 4px solid #667eea;
}

.answer-meta {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 14px;
  margin-bottom: 15px;
}

.answer-content {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
}

.answer-form h3 {
  margin: 0 0 15px 0;
  font-size: 18px;
  color: #333;
}

.answer-textarea {
  width: 100%;
  padding: 15px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  resize: vertical;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.answer-textarea:focus {
  outline: none;
  border-color: #667eea;
}

.btn-submit {
  margin-top: 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s;
}

.btn-submit:hover:not(:disabled) {
  opacity: 0.9;
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.waiting-message {
  text-align: center;
  padding: 30px;
  color: #666;
  background: #f8f9fa;
  border-radius: 8px;
}
</style>
