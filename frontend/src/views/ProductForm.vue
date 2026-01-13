<template>
  <div class="product-form-container">
    <div class="form-header">
      <h1>{{ isEditMode ? '상품 수정' : '상품 등록' }}</h1>
      <button @click="goBack" class="btn-back">← 취소</button>
    </div>

    <form @submit.prevent="handleSubmit" class="product-form">
      <div class="form-group">
        <label for="name">상품명 *</label>
        <input 
          id="name"
          v-model="formData.name" 
          type="text" 
          required
          placeholder="상품명을 입력하세요"
        >
      </div>

      <div class="form-group">
        <label for="category">카테고리 *</label>
        <select id="category" v-model="formData.category" required>
          <option value="">카테고리를 선택하세요</option>
          <option value="전자제품">전자제품</option>
          <option value="가구">가구</option>
          <option value="의류">의류</option>
          <option value="식품">식품</option>
          <option value="도서">도서</option>
          <option value="기타">기타</option>
        </select>
      </div>

      <div class="form-group">
        <label for="price">가격 *</label>
        <input 
          id="price"
          v-model.number="formData.price" 
          type="number" 
          required
          min="0"
          placeholder="가격을 입력하세요"
        >
      </div>

      <div class="form-group">
        <label for="stock">재고 *</label>
        <input 
          id="stock"
          v-model.number="formData.stock" 
          type="number" 
          required
          min="0"
          placeholder="재고 수량을 입력하세요"
        >
      </div>

      <div class="form-group">
        <label for="imageUrl">이미지 URL</label>
        <input 
          id="imageUrl"
          v-model="formData.imageUrl" 
          type="url" 
          placeholder="이미지 URL을 입력하세요"
        >
        <small>이미지 URL을 입력하지 않으면 기본 이미지가 표시됩니다.</small>
      </div>

      <div class="form-group">
        <label for="description">상품 설명</label>
        <textarea 
          id="description"
          v-model="formData.description" 
          rows="5"
          placeholder="상품 설명을 입력하세요"
        ></textarea>
      </div>

      <div class="form-actions">
        <button type="submit" class="btn-submit" :disabled="submitting">
          {{ submitting ? '처리 중...' : (isEditMode ? '수정하기' : '등록하기') }}
        </button>
        <button type="button" @click="goBack" class="btn-cancel">
          취소
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { getProductById, createProduct, updateProduct } from '../services/productService';

const router = useRouter();
const route = useRoute();

// 수정 모드 여부 확인
const isEditMode = computed(() => route.name === 'ProductEdit');

// 폼 데이터
const formData = ref({
  name: '',
  category: '',
  price: 0,
  stock: 0,
  imageUrl: '',
  description: '',
});

const submitting = ref(false);

onMounted(() => {
  if (isEditMode.value) {
    loadProduct();
  }
});

// 수정 모드일 때 기존 상품 정보 로드
const loadProduct = async () => {
  try {
    const productId = route.params.id;
    const product = await getProductById(productId);
    
    formData.value = {
      name: product.name,
      category: product.category,
      price: product.price,
      stock: product.stock,
      imageUrl: product.imageUrl || '',
      description: product.description || '',
    };
  } catch (err) {
    alert('상품 정보를 불러오는데 실패했습니다.');
    console.error(err);
    goBack();
  }
};

// 폼 제출 처리
const handleSubmit = async () => {
  submitting.value = true;
  
  try {
    if (isEditMode.value) {
      // 수정
      const productId = route.params.id;
      await updateProduct(productId, formData.value);
      alert('상품이 수정되었습니다.');
      router.push(`/products/${productId}`);
    } else {
      // 등록
      const newProduct = await createProduct(formData.value);
      alert('상품이 등록되었습니다.');
      router.push(`/products/${newProduct.id}`);
    }
  } catch (err) {
    alert(isEditMode.value ? '상품 수정에 실패했습니다.' : '상품 등록에 실패했습니다.');
    console.error(err);
  } finally {
    submitting.value = false;
  }
};

// 취소 및 뒤로가기
const goBack = () => {
  router.back();
};
</script>

<style scoped>
.product-form-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.form-header h1 {
  margin: 0;
  color: #333;
}

.btn-back {
  background: white;
  border: 1px solid #ddd;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-back:hover {
  background: #f5f5f5;
}

.product-form {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #333;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #2196F3;
}

.form-group small {
  display: block;
  margin-top: 6px;
  color: #666;
  font-size: 14px;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 30px;
}

.btn-submit {
  flex: 1;
  background: #2196F3;
  color: white;
  border: none;
  padding: 14px;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-submit:hover:not(:disabled) {
  background: #1976D2;
}

.btn-submit:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.btn-cancel {
  flex: 1;
  background: white;
  color: #666;
  border: 1px solid #ddd;
  padding: 14px;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-cancel:hover {
  background: #f5f5f5;
}
</style>
