<template>
  <div class="qna-list-container">
    <div class="qna-header">
      <h1>Q&A 게시판</h1>
      <router-link v-if="isLoggedIn" to="/qna/new" class="btn-write">
        질문하기
      </router-link>
    </div>

    <!-- 검색 영역 -->
    <div class="search-section">
      <input
        v-model="searchKeyword"
        type="text"
        placeholder="검색어를 입력하세요"
        @keyup.enter="handleSearch"
        class="search-input"
      />
      <button @click="handleSearch" class="btn-search">검색</button>
      <button v-if="isSearchMode" @click="clearSearch" class="btn-clear">초기화</button>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="loading" class="loading">로딩 중...</div>

    <!-- 에러 상태 -->
    <div v-else-if="error" class="error">{{ error }}</div>

    <!-- 질문 목록 -->
    <div v-else class="qna-table">
      <div class="table-header">
        <span class="col-status">상태</span>
        <span class="col-title">제목</span>
        <span class="col-author">작성자</span>
        <span class="col-date">작성일</span>
      </div>

      <div v-if="questions.length === 0" class="empty-message">
        등록된 질문이 없습니다.
      </div>

      <div
        v-for="question in questions"
        :key="question.id"
        class="table-row"
        @click="goToDetail(question.id)"
      >
        <span :class="['col-status', 'status-badge', question.isAnswered ? 'answered' : 'pending']">
          {{ question.isAnswered ? '답변완료' : '답변대기' }}
        </span>
        <span class="col-title">{{ question.title }}</span>
        <span class="col-author">{{ question.username }}</span>
        <span class="col-date">{{ formatDate(question.createdAt) }}</span>
      </div>
    </div>

    <!-- 페이지네이션 -->
    <div v-if="totalPages > 1" class="pagination">
      <button 
        @click="changePage(currentPage - 1)" 
        :disabled="currentPage === 0"
        class="page-btn"
      >
        이전
      </button>
      <span class="page-info">{{ currentPage + 1 }} / {{ totalPages }}</span>
      <button 
        @click="changePage(currentPage + 1)" 
        :disabled="currentPage >= totalPages - 1"
        class="page-btn"
      >
        다음
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { getQuestions, searchQuestions } from '../services/qnaService';
import authService from '../services/authService';

const router = useRouter();

const questions = ref([]);
const loading = ref(false);
const error = ref(null);
const currentPage = ref(0);
const totalPages = ref(0);
const pageSize = 10;
const searchKeyword = ref('');
const isSearchMode = ref(false);

const isLoggedIn = computed(() => authService.isLoggedIn());

onMounted(() => {
  loadQuestions();
});

// 질문 목록 로드
const loadQuestions = async () => {
  loading.value = true;
  error.value = null;
  
  try {
    let response;
    if (isSearchMode.value && searchKeyword.value) {
      response = await searchQuestions(searchKeyword.value, currentPage.value, pageSize);
    } else {
      response = await getQuestions(currentPage.value, pageSize);
    }
    questions.value = response.content;
    totalPages.value = response.totalPages;
  } catch (err) {
    error.value = '질문 목록을 불러오는데 실패했습니다.';
    console.error(err);
  } finally {
    loading.value = false;
  }
};

// 검색 처리
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    isSearchMode.value = true;
    currentPage.value = 0;
    loadQuestions();
  }
};

// 검색 초기화
const clearSearch = () => {
  searchKeyword.value = '';
  isSearchMode.value = false;
  currentPage.value = 0;
  loadQuestions();
};

// 페이지 변경
const changePage = (page) => {
  currentPage.value = page;
  loadQuestions();
};

// 상세 페이지로 이동
const goToDetail = (id) => {
  router.push(`/qna/${id}`);
};

// 날짜 포맷팅
const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR');
};
</script>

<style scoped>
.qna-list-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.qna-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.qna-header h1 {
  margin: 0;
  font-size: 28px;
  color: #333;
}

.btn-write {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  transition: transform 0.2s, box-shadow 0.2s;
}

.btn-write:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.search-section {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-input {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.2s;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
}

.btn-search {
  background: #667eea;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-search:hover {
  background: #5a6fd6;
}

.btn-clear {
  background: #757575;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-clear:hover {
  background: #616161;
}

.loading,
.error,
.empty-message {
  text-align: center;
  padding: 40px;
  font-size: 18px;
  color: #666;
}

.error {
  color: #f44336;
}

.qna-table {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.table-header {
  display: grid;
  grid-template-columns: 100px 1fr 120px 120px;
  padding: 16px 20px;
  background: #f8f9fa;
  font-weight: 600;
  color: #333;
  border-bottom: 2px solid #e0e0e0;
}

.table-row {
  display: grid;
  grid-template-columns: 100px 1fr 120px 120px;
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.2s;
}

.table-row:hover {
  background: #f8f9fa;
}

.table-row:last-child {
  border-bottom: none;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-align: center;
}

.status-badge.answered {
  background: #e8f5e9;
  color: #2e7d32;
}

.status-badge.pending {
  background: #fff3e0;
  color: #e65100;
}

.col-title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.col-author,
.col-date {
  color: #666;
  font-size: 14px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
}

.page-btn {
  background: white;
  border: 2px solid #667eea;
  color: #667eea;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
  background: #667eea;
  color: white;
}

.page-btn:disabled {
  border-color: #ccc;
  color: #ccc;
  cursor: not-allowed;
}

.page-info {
  font-size: 16px;
  color: #333;
  font-weight: 600;
}
</style>
