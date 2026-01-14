<template>
  <div class="product-list-container">
    <div class="header">
      <div class="header-text">
        <h1>✨ 상품 목록</h1>
        <p class="subtitle">{{ products.length }}개의 상품이 있습니다</p>
      </div>
    </div>

    <!-- 검색 및 필터 -->
    <div class="filters-card">
      <div class="search-wrapper">
        <span class="search-icon">🔍</span>
        <input 
          v-model="searchKeyword" 
          @input="handleSearch"
          type="text" 
          placeholder="상품명을 검색하세요..." 
          class="search-input"
        >
      </div>
      
      <div class="category-filters">
        <button 
          :class="['filter-btn', { active: selectedCategory === null }]"
          @click="filterByCategory(null)"
        >
          <span class="filter-icon">🌐</span>
          전체
        </button>
        <button 
          :class="['filter-btn', { active: selectedCategory === '전자제품' }]"
          @click="filterByCategory('전자제품')"
        >
          <span class="filter-icon">💻</span>
          전자제품
        </button>
        <button 
          :class="['filter-btn', { active: selectedCategory === '가구' }]"
          @click="filterByCategory('가구')"
        >
          <span class="filter-icon">🪑</span>
          가구
        </button>
      </div>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>로딩 중...</p>
    </div>

    <!-- 에러 상태 -->
    <div v-else-if="error" class="error">
      <span class="error-icon">⚠️</span>
      {{ error }}
    </div>

    <!-- 상품 목록 -->
    <div v-else-if="products.length > 0" class="product-grid">
      <ProductCard 
        v-for="product in products" 
        :key="product.id" 
        :product="product"
      />
    </div>

    <!-- 상품 없음 -->
    <div v-else class="no-products">
      <span class="empty-icon">📦</span>
      <p>상품이 없습니다.</p>
      <button @click="goToNewProduct" class="btn-add">첫 상품 등록하기</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import ProductCard from '../components/ProductCard.vue';
import { getAllProducts, getProductsByCategory, searchProducts } from '../services/productService';

const router = useRouter();

// 반응형 데이터
const products = ref([]);
const loading = ref(false);
const error = ref(null);
const searchKeyword = ref('');
const selectedCategory = ref(null);

// 컴포넌트 마운트 시 상품 목록 로드
onMounted(() => {
  loadProducts();
});

// 상품 목록 로드
const loadProducts = async () => {
  loading.value = true;
  error.value = null;
  
  try {
    products.value = await getAllProducts();
  } catch (err) {
    error.value = '상품 목록을 불러오는데 실패했습니다.';
    console.error(err);
  } finally {
    loading.value = false;
  }
};

// 카테고리 필터링
const filterByCategory = async (category) => {
  selectedCategory.value = category;
  searchKeyword.value = ''; // 검색어 초기화
  loading.value = true;
  error.value = null;
  
  try {
    if (category === null) {
      products.value = await getAllProducts();
    } else {
      products.value = await getProductsByCategory(category);
    }
  } catch (err) {
    error.value = '상품을 불러오는데 실패했습니다.';
    console.error(err);
  } finally {
    loading.value = false;
  }
};

// 검색 처리 (디바운싱 적용)
let searchTimeout;
const handleSearch = () => {
  clearTimeout(searchTimeout);
  
  if (searchKeyword.value.trim() === '') {
    loadProducts();
    return;
  }
  
  searchTimeout = setTimeout(async () => {
    selectedCategory.value = null; // 카테고리 필터 초기화
    loading.value = true;
    error.value = null;
    
    try {
      products.value = await searchProducts(searchKeyword.value);
    } catch (err) {
      error.value = '검색에 실패했습니다.';
      console.error(err);
    } finally {
      loading.value = false;
    }
  }, 300);
};

// 상품 등록 페이지로 이동
const goToNewProduct = () => {
  router.push('/products/new');
};
</script>

<style scoped>
.product-list-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 40px;
}

.header {
  margin-bottom: 30px;
}

.header-text h1 {
  margin: 0 0 8px 0;
  font-size: 36px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  margin: 0;
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  font-weight: 500;
}

.filters-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 40px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.search-wrapper {
  position: relative;
  margin-bottom: 20px;
}

.search-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 20px;
}

.search-input {
  width: 100%;
  padding: 16px 16px 16px 50px;
  font-size: 16px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  transition: all 0.3s ease;
  background: white;
  color: #1a202c;
}

.search-input::placeholder {
  color: #a0aec0;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.category-filters {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: 2px solid #e2e8f0;
  background: white;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
  color: #4a5568;
}

.filter-btn:hover {
  background: rgba(102, 126, 234, 0.1);
  border-color: #667eea;
  transform: translateY(-2px);
}

.filter-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.filter-icon {
  font-size: 18px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 30px;
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.loading {
  text-align: center;
  padding: 80px 40px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.spinner {
  width: 50px;
  height: 50px;
  margin: 0 auto 20px;
  border: 4px solid rgba(102, 126, 234, 0.2);
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading p {
  margin: 0;
  font-size: 18px;
  color: #4a5568;
  font-weight: 600;
}

.error {
  text-align: center;
  padding: 60px 40px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  color: #f56565;
  font-size: 18px;
  font-weight: 600;
}

.error-icon {
  font-size: 48px;
  display: block;
  margin-bottom: 16px;
}

.no-products {
  text-align: center;
  padding: 80px 40px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.empty-icon {
  font-size: 64px;
  display: block;
  margin-bottom: 20px;
  opacity: 0.5;
}

.no-products p {
  margin: 0 0 24px 0;
  font-size: 18px;
  color: #718096;
  font-weight: 600;
}

.btn-add {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 14px 32px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-add:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}
</style>
