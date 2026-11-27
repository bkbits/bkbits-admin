<script setup lang="ts">
const { title, loading } = defineProps<{
  title?: string;
  loading?: boolean;
}>();
</script>

<template>
  <a-card variant="borderless" class="bk-card" :loading="loading">
    <template v-if="title || $slots.title" #title>
      <slot name="title">{{ title }}</slot>
    </template>

    <template v-if="$slots.actions" #extra>
      <div class="bk-card-actions">
        <slot name="actions" />
      </div>
    </template>

    <slot />
  </a-card>
</template>

<style scoped>
.bk-card :deep(.ant-card-head) {
  min-height: 56px;
  padding-inline: 18px;
  border-bottom-color: var(--ui-border);
}

.bk-card :deep(.ant-card-head-title) {
  color: var(--ui-text);
  font-size: 16px;
  font-weight: 650;
}

.bk-card-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}
</style>
