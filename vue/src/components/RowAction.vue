<script setup lang="ts">
import type { Component } from "vue";

const { icon, danger } = defineProps<{
  icon: Component;
  danger?: boolean;
}>();

const emit = defineEmits<{ click: [event: MouseEvent] }>();
</script>

<template>
  <a-button
    type="text"
    size="small"
    class="table-action"
    :danger="danger"
    @click="emit('click', $event)"
  >
    <component :is="icon" :size="14" aria-hidden="true" />
    <slot />
  </a-button>
</template>

<style scoped>
.table-action {
  min-height: 24px;
  gap: 4px;
  padding-inline: 4px;
  color: var(--ui-accent);
  font-size: 13px;
  font-weight: 500;
  white-space: nowrap;
}

.table-action.ant-btn-dangerous {
  color: var(--ui-danger);
}
</style>
