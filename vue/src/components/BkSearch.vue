<script setup lang="ts">
import { onMounted } from "vue";

type SearchModel = Record<string, unknown>;

const { loading } = defineProps<{
  loading?: boolean;
}>();

const model = defineModel<SearchModel>({ required: true });
const emit = defineEmits<{ search: [model: SearchModel] }>();

let initialValues: SearchModel = {};

onMounted(() => {
  initialValues = { ...model.value };
});

function handleSearch() {
  if (loading) return;
  emit("search", model.value);
}

function handleReset() {
  if (loading) return;

  const target = model.value;
  for (const key of Object.keys(target)) delete target[key];
  Object.assign(target, initialValues);

  emit("search", model.value);
}
</script>

<template>
  <a-form
    class="bk-search"
    v-bind="{ layout: 'vertical', ...$attrs }"
    :model="model"
    :disabled="loading"
    @finish="handleSearch"
  >
    <slot />

    <a-form-item class="bk-search-actions">
      <a-space>
        <a-button type="primary" html-type="submit">查询</a-button>
        <a-button @click="handleReset">重置</a-button>
      </a-space>
    </a-form-item>
  </a-form>
</template>

<style scoped>
.bk-search {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(min(100%, 170px), 1fr));
  align-items: end;
  gap: 12px;
}

.bk-search :deep(.ant-form-item) {
  margin: 0;
}

.bk-search :deep(:is(.ant-select, .ant-picker)) {
  width: 100%;
}

@media (max-width: 600px) {
  .bk-search {
    grid-template-columns: repeat(auto-fit, minmax(min(100%, 146px), 1fr));
  }

  .bk-search :deep(.ant-form-item:last-child) {
    grid-column: 1 / -1;
  }
}
</style>
