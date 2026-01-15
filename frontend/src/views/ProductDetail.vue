<template>
  <div class="product-detail-container">
    <!-- 로딩 상태 -->
    <div v-if="loading" class="loading">
      로딩 중...
    </div>

    <!-- 에러 상태 -->
    <div v-else-if="error" class="error">
      {{ error }}
      <button @click="goBack" class="btn-secondary">목록으로 돌아가기</button>
    </div>

    <!-- 상품 상세 정보 -->
    <div v-else-if="product" class="product-detail">
      <div class="detail-header">
        <button @click="goBack" class="btn-back">← 목록으로</button>
        <div class="action-buttons">
          <button @click="goToEdit" class="btn-edit">수정</button>
          <button @click="handleDelete" class="btn-delete">삭제</button>
        </div>
      </div>

      <div class="detail-content">
        <div class="product-image-large">
          <img :src="product.imageUrl || 'https://via.placeholder.com/400'" :alt="product.name">
        </div>

        <div class="product-info-detail">
          <h1>{{ product.name }}</h1>
          <p class="category-badge">{{ product.category }}</p>
          
          <div class="price-section">
            <span class="price">{{ formatPrice(product.price) }}원</span>
          </div>

          <div class="info-row">
            <span class="label">재고:</span>
            <span :class="['stock', { 'out-of-stock': product.stock === 0 }]">
              {{ product.stock }}개
            </span>
          </div>

          <div class="description-section">
            <h3>상품 설명</h3>
            <p>{{ product.description || '상품 설명이 없습니다.' }}</p>
          </div>
        </div>
      </div>

      <!-- 리뷰 섹션 -->
      <ReviewSection :productId="product.id" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { getProductById, deleteProduct } from '../services/productService';
import ReviewSection from '../components/ReviewSection.vue';

const router = useRouter();
const route = useRoute();

const product = ref(null);
const loading = ref(false);
const error = ref(null);

onMounted(() => {
  loadProduct();
});

// 상품 정보 로드
const loadProduct = async () => {
  loading.value = true;
  error.value = null;
  
  try {
    const productId = route.params.id;
    product.value = await getProductById(productId);
  } catch (err) {
    error.value = '상품 정보를 불러오는데 실패했습니다.';
    console.error(err);
  } finally {
    loading.value = false;
  }
};

// 가격 포맷팅
const formatPrice = (price) => {
  return price.toLocaleString('ko-KR');
};

// 목록으로 돌아가기
const goBack = () => {
  router.push('/');
};

// 수정 페이지로 이동
const goToEdit = () => {
  router.push(`/products/${product.value.id}/edit`);
};

// 상품 삭제
const handleDelete = async () => {
  if (!confirm('정말 이 상품을 삭제하시겠습니까?')) {
    return;
  }
  
  try {
    await deleteProduct(product.value.id);
    alert('상품이 삭제되었습니다.');
    router.push('/');
  } catch (err) {
    alert('상품 삭제에 실패했습니다.');
    console.error(err);
  }
};
</script>

<style scoped>
.product-detail-container {
  max-width: 1200px;
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
  border: 1px solid #ddd;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background 0.2s;
}

.btn-back:hover {
  background: #f5f5f5;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.btn-edit {
  background: #4CAF50;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-edit:hover {
  background: #45a049;
}

.btn-delete {
  background: #f44336;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-delete:hover {
  background: #da190b;
}

.btn-secondary {
  margin-top: 20px;
  background: #757575;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
}

.detail-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.product-image-large {
  width: 100%;
  height: 400px;
  background: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
}

.product-image-large img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info-detail h1 {
  margin: 0 0 10px 0;
  font-size: 32px;
  color: #333;
}

.category-badge {
  display: inline-block;
  background: #e3f2fd;
  color: #2196F3;
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 14px;
  margin-bottom: 20px;
}

.price-section {
  margin: 20px 0;
  padding: 20px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
}

.price {
  font-size: 36px;
  font-weight: 700;
  color: #2196F3;
}

.info-row {
  display: flex;
  align-items: center;
  margin: 15px 0;
  font-size: 18px;
}

.label {
  font-weight: 600;
  margin-right: 10px;
  color: #666;
}

.stock {
  color: #4CAF50;
  font-weight: 600;
}

.stock.out-of-stock {
  color: #f44336;
}

.description-section {
  margin-top: 30px;
}

.description-section h3 {
  margin-bottom: 10px;
  color: #333;
}

.description-section p {
  line-height: 1.6;
  color: #666;
}

@media (max-width: 768px) {
  .detail-content {
    grid-template-columns: 1fr;
  }
}
</style>
