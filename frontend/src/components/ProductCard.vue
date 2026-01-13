<template>
  <div class="product-card" @click="goToDetail">
    <div class="product-image">
      <img :src="product.imageUrl || 'https://via.placeholder.com/300x300/667eea/ffffff?text=Product'" :alt="product.name">
      <div class="image-overlay">
        <span class="view-details">자세히 보기 →</span>
      </div>
    </div>
    <div class="product-info">
      <span class="product-category">{{ product.category }}</span>
      <h3 class="product-name">{{ product.name }}</h3>
      <p class="product-price">
        <span class="price-label">가격</span>
        <span class="price-value">{{ formatPrice(product.price) }}원</span>
      </p>
      <div class="product-footer">
        <p class="product-stock" :class="{ 'out-of-stock': product.stock === 0 }">
          <span class="stock-icon">📦</span>
          재고 {{ product.stock }}개
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';

// Props 정의 - 부모 컴포넌트로부터 상품 데이터를 받음
const props = defineProps({
  product: {
    type: Object,
    required: true,
  },
});

const router = useRouter();

// 상세 페이지로 이동
const goToDetail = () => {
  router.push(`/products/${props.product.id}`);
};

// 가격 포맷팅 (천 단위 콤마)
const formatPrice = (price) => {
  return price.toLocaleString('ko-KR');
};
</script>

<style scoped>
.product-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.product-card:hover {
  transform: translateY(-12px) scale(1.02);
  box-shadow: 0 16px 48px rgba(102, 126, 234, 0.3);
}

.product-image {
  width: 100%;
  height: 240px;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.1);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, transparent 0%, rgba(0, 0, 0, 0.7) 100%);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding: 20px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.product-card:hover .image-overlay {
  opacity: 1;
}

.view-details {
  color: white;
  font-weight: 600;
  font-size: 16px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.product-info {
  padding: 20px;
}

.product-category {
  display: inline-block;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.product-name {
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 16px 0;
  color: #2d3748;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin: 0 0 16px 0;
}

.price-label {
  font-size: 13px;
  color: #718096;
  font-weight: 500;
}

.price-value {
  font-size: 24px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.product-footer {
  border-top: 1px solid #e2e8f0;
  padding-top: 12px;
}

.product-stock {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #48bb78;
  margin: 0;
}

.stock-icon {
  font-size: 16px;
}

.product-stock.out-of-stock {
  color: #f56565;
}
</style>
