<template>
  <div class="star-rating" :class="{ 'readonly': readonly }">
    <span
      v-for="star in 5"
      :key="star"
      class="star"
      :class="{ 'filled': star <= displayRating, 'hovering': !readonly && star <= hoverRating }"
      @click="!readonly && selectRating(star)"
      @mouseenter="!readonly && (hoverRating = star)"
      @mouseleave="!readonly && (hoverRating = 0)"
    >
      ★
    </span>
    <span v-if="showCount && count !== undefined" class="rating-count">
      ({{ count }}개)
    </span>
  </div>
</template>

<script setup>
import { ref, computed, defineProps, defineEmits } from 'vue';

const props = defineProps({
  modelValue: {
    type: Number,
    default: 0
  },
  readonly: {
    type: Boolean,
    default: false
  },
  showCount: {
    type: Boolean,
    default: false
  },
  count: {
    type: Number,
    default: undefined
  }
});

const emit = defineEmits(['update:modelValue']);

const hoverRating = ref(0);

const displayRating = computed(() => {
  if (!props.readonly && hoverRating.value > 0) {
    return hoverRating.value;
  }
  return props.modelValue;
});

const selectRating = (rating) => {
  emit('update:modelValue', rating);
};
</script>

<style scoped>
.star-rating {
  display: inline-flex;
  align-items: center;
  gap: 2px;
}

.star {
  font-size: 24px;
  color: #ddd;
  cursor: pointer;
  transition: color 0.2s, transform 0.2s;
}

.star.filled,
.star.hovering {
  color: #ffc107;
}

.star-rating:not(.readonly) .star:hover {
  transform: scale(1.2);
}

.star-rating.readonly .star {
  cursor: default;
}

.rating-count {
  margin-left: 8px;
  font-size: 14px;
  color: #666;
}
</style>
