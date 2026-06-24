<template>
  <div class="pagination-container" v-if="totalPages > 1">
    <button class="ghost-button" :disabled="page <= 0" @click="changePage(page - 1)">上一页</button>
    <span class="page-info">第 {{ page + 1 }} 页 / 共 {{ totalPages }} 页</span>
    <button class="ghost-button" :disabled="page >= totalPages - 1" @click="changePage(page + 1)">下一页</button>
  </div>
</template>

<script setup>
defineProps({
  page: { type: Number, required: true },
  totalPages: { type: Number, required: true },
});
const emit = defineEmits(['change']);
const changePage = (newPage) => {
  emit('change', newPage);
};
</script>

<style scoped>
.pagination-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  margin-top: 1.5rem;
  padding-top: 1rem;
  border-top: 1px solid var(--border);
}
.page-info {
  font-size: 0.875rem;
  color: var(--muted-fg);
}
</style>
