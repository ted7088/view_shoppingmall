<template>
  <div class="review-section">
    <h2 class="section-title">상품 리뷰</h2>
    
    <!-- 평균 별점 -->
    <div class="rating-summary">
      <StarRating :modelValue="averageRating" :readonly="true" />
      <span class="average-rating">{{ averageRating.toFixed(1) }}</span>
      <span class="review-count">리뷰 {{ reviewCount }}개</span>
    </div>

    <!-- 리뷰 작성 폼 (로그인 시) -->
    <div v-if="isLoggedIn" class="review-form">
      <h3>리뷰 작성</h3>
      <div class="form-group">
        <label>별점</label>
        <StarRating v-model="newReview.rating" />
      </div>
      <div class="form-group">
        <label>내용</label>
        <textarea 
          v-model="newReview.content" 
          placeholder="상품에 대한 솔직한 리뷰를 작성해주세요."
          rows="4"
        ></textarea>
      </div>
      <button @click="submitReview" :disabled="!canSubmit" class="btn-submit">
        리뷰 등록
      </button>
    </div>
    <div v-else class="login-prompt">
      <p>리뷰를 작성하려면 <router-link to="/login">로그인</router-link>이 필요합니다.</p>
    </div>

    <!-- 리뷰 목록 -->
    <div class="review-list">
      <div v-if="loading" class="loading">리뷰를 불러오는 중...</div>
      <div v-else-if="reviews.length === 0" class="no-reviews">
        아직 작성된 리뷰가 없습니다. 첫 번째 리뷰를 작성해보세요!
      </div>
      <div v-else>
        <div v-for="review in reviews" :key="review.id" class="review-item">
          <div class="review-header">
            <div class="review-user">
              <span class="username">{{ review.username }}</span>
              <StarRating :modelValue="review.rating" :readonly="true" />
            </div>
            <div class="review-meta">
              <span class="date">{{ formatDate(review.createdAt) }}</span>
              <button 
                v-if="canDelete(review)" 
                @click="handleDelete(review.id)" 
                class="btn-delete"
              >
                삭제
              </button>
            </div>
          </div>
          <p class="review-content">{{ review.content }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, defineProps } from 'vue';
import StarRating from './StarRating.vue';
import { getReviewsByProductId, getProductRating, createReview, deleteReview } from '../services/reviewService';
import authService from '../services/authService';

const props = defineProps({
  productId: {
    type: [Number, String],
    required: true
  }
});

const reviews = ref([]);
const averageRating = ref(0);
const reviewCount = ref(0);
const loading = ref(false);
const newReview = ref({
  rating: 0,
  content: ''
});

const isLoggedIn = computed(() => authService.isLoggedIn());
const currentUser = computed(() => authService.getCurrentUser());

const canSubmit = computed(() => {
  return newReview.value.rating > 0 && newReview.value.content.trim().length > 0;
});

// 리뷰 삭제 가능 여부 (본인 리뷰만)
const canDelete = (review) => {
  if (!currentUser.value) return false;
  return review.userId === currentUser.value.id;
};

// 날짜 포맷팅
const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
};

// 리뷰 목록 로드
const loadReviews = async () => {
  loading.value = true;
  try {
    reviews.value = await getReviewsByProductId(props.productId);
  } catch (error) {
    console.error('리뷰 목록 로드 실패:', error);
  } finally {
    loading.value = false;
  }
};

// 평균 별점 로드
const loadRating = async () => {
  try {
    const data = await getProductRating(props.productId);
    averageRating.value = data.averageRating || 0;
    reviewCount.value = data.reviewCount || 0;
  } catch (error) {
    console.error('평균 별점 로드 실패:', error);
  }
};

// 리뷰 작성
const submitReview = async () => {
  if (!canSubmit.value) return;
  
  try {
    await createReview({
      productId: Number(props.productId),
      rating: newReview.value.rating,
      content: newReview.value.content
    });
    
    // 폼 초기화 및 새로고침
    newReview.value = { rating: 0, content: '' };
    await loadReviews();
    await loadRating();
    alert('리뷰가 등록되었습니다.');
  } catch (error) {
    const message = error.response?.data?.error || '리뷰 등록에 실패했습니다.';
    alert(message);
  }
};

// 리뷰 삭제
const handleDelete = async (reviewId) => {
  if (!confirm('정말 이 리뷰를 삭제하시겠습니까?')) return;
  
  try {
    await deleteReview(reviewId);
    await loadReviews();
    await loadRating();
    alert('리뷰가 삭제되었습니다.');
  } catch (error) {
    alert('리뷰 삭제에 실패했습니다.');
  }
};

onMounted(() => {
  loadReviews();
  loadRating();
});
</script>

<style scoped>
.review-section {
  margin-top: 40px;
  padding-top: 30px;
  border-top: 2px solid #eee;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 20px;
}

.rating-summary {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px;
  background: linear-gradient(135deg, #fff8e1 0%, #ffecb3 100%);
  border-radius: 12px;
  margin-bottom: 30px;
}

.average-rating {
  font-size: 32px;
  font-weight: 700;
  color: #f57c00;
}

.review-count {
  color: #666;
  font-size: 16px;
}

.review-form {
  background: #f8f9fa;
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 30px;
}

.review-form h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  color: #333;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #555;
}

.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
  font-family: inherit;
}

.form-group textarea:focus {
  outline: none;
  border-color: #2196F3;
}

.btn-submit {
  background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.4);
}

.btn-submit:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.login-prompt {
  background: #e3f2fd;
  padding: 20px;
  border-radius: 12px;
  text-align: center;
  margin-bottom: 30px;
}

.login-prompt a {
  color: #2196F3;
  font-weight: 600;
  text-decoration: none;
}

.login-prompt a:hover {
  text-decoration: underline;
}

.review-list {
  margin-top: 20px;
}

.loading,
.no-reviews {
  text-align: center;
  padding: 40px;
  color: #666;
  font-size: 16px;
}

.review-item {
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 12px;
  margin-bottom: 16px;
  background: white;
  transition: box-shadow 0.3s;
}

.review-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.review-user {
  display: flex;
  align-items: center;
  gap: 12px;
}

.username {
  font-weight: 600;
  color: #333;
  font-size: 16px;
}

.review-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.date {
  color: #999;
  font-size: 14px;
}

.btn-delete {
  background: none;
  border: 1px solid #f44336;
  color: #f44336;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-delete:hover {
  background: #f44336;
  color: white;
}

.review-content {
  color: #555;
  line-height: 1.6;
  margin: 0;
}
</style>
