<template>
  <div class="wishlist-container">
    <h1 class="page-title">찜 목록</h1>

    <!-- 로딩 상태 -->
    <div v-if="loading" class="loading">
      로딩 중...
    </div>

    <!-- 에러 상태 -->
    <div v-else-if="error" class="error">
      {{ error }}
    </div>

    <!-- 빈 찜 목록 -->
    <div v-else-if="wishlist.length === 0" class="empty-state">
      <div class="empty-icon">♡</div>
      <p>찜한 상품이 없습니다.</p>
      <button @click="goToProducts" class="btn-primary">상품 둘러보기</button>
    </div>

    <!-- 찜 목록 -->
    <div v-else class="wishlist-grid">
      <div v-for="item in wishlist" :key="item.wishlistId" class="wishlist-card">
        <div class="product-image" @click="goToProduct(item.productId)">
          <img :src="item.imageUrl || 'https://via.placeholder.com/300'" :alt="item.productName">
          <div v-if="item.stock === 0" class="out-of-stock-badge">품절</div>
        </div>
        
        <div class="product-info">
          <p class="category">{{ item.category }}</p>
          <h3 @click="goToProduct(item.productId)" class="product-name">{{ item.productName }}</h3>
          <p class="price">{{ formatPrice(item.price) }}원</p>
          <p class="stock">재고: {{ item.stock }}개</p>
          <p class="added-date">{{ formatDate(item.addedAt) }}</p>
        </div>

        <div class="card-actions">
          <button @click="removeFromWishlist(item.productId)" class="btn-remove">
            삭제
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getWishlist, removeFromWishlist as removeWishlistItem } from '../services/wishlistService';

const router = useRouter();

const wishlist = ref([]);
const loading = ref(false);
const error = ref(null);

onMounted(() => {
  loadWishlist();
});

// 찜 목록 로드
const loadWishlist = async () => {
  loading.value = true;
  error.value = null;
  
  try {
    wishlist.value = await getWishlist();
  } catch (err) {
    if (err.response?.status === 401) {
      error.value = '로그인이 필요합니다.';
      setTimeout(() => router.push('/login'), 2000);
    } else {
      error.value = '찜 목록을 불러오는데 실패했습니다.';
    }
    console.error(err);
  } finally {
    loading.value = false;
  }
};

// 찜 목록에서 삭제
const removeFromWishlist = async (productId) => {
  if (!confirm('찜 목록에서 삭제하시겠습니까?')) {
    return;
  }
  
  try {
    await removeWishlistItem(productId);
    wishlist.value = wishlist.value.filter(item => item.productId !== productId);
  } catch (err) {
    alert('삭제에 실패했습니다.');
    console.error(err);
  }
};

// 가격 포맷팅
const formatPrice = (price) => {
  return price.toLocaleString('ko-KR');
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

// 상품 목록으로 이동
const goToProducts = () => {
  router.push('/');
};

// 상품 상세로 이동
const goToProduct = (productId) => {
  router.push(`/products/${productId}`);
};
</script>

<style scoped>
.wishlist-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 32px;
  margin-bottom: 30px;
  color: #333;
  text-align: center;
}

.loading,
.error {
  text-align: center;
  padding: 60px 20px;
  font-size: 18px;
}

.error {
  color: #f44336;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  font-size: 80px;
  color: #ddd;
  margin-bottom: 20px;
}

.empty-state p {
  font-size: 18px;
  color: #666;
  margin-bottom: 30px;
}

.btn-primary {
  background: #2196F3;
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-primary:hover {
  background: #1976D2;
}

.wishlist-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 30px;
}

.wishlist-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
}

.wishlist-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.product-image {
  position: relative;
  width: 100%;
  height: 280px;
  background: #f5f5f5;
  cursor: pointer;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.out-of-stock-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  background: rgba(244, 67, 54, 0.9);
  color: white;
  padding: 6px 12px;
  border-radius: 4px;
  font-weight: 600;
}

.product-info {
  padding: 20px;
}

.category {
  font-size: 13px;
  color: #2196F3;
  margin-bottom: 8px;
  font-weight: 600;
}

.product-name {
  font-size: 18px;
  margin: 0 0 12px 0;
  color: #333;
  cursor: pointer;
  transition: color 0.2s;
}

.product-name:hover {
  color: #2196F3;
}

.price {
  font-size: 20px;
  font-weight: 700;
  color: #2196F3;
  margin-bottom: 8px;
}

.stock {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.added-date {
  font-size: 13px;
  color: #999;
  margin-top: 8px;
}

.card-actions {
  padding: 0 20px 20px;
}

.btn-remove {
  width: 100%;
  background: #f44336;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-remove:hover {
  background: #da190b;
}

@media (max-width: 768px) {
  .wishlist-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 20px;
  }
  
  .page-title {
    font-size: 24px;
  }
}
</style>
