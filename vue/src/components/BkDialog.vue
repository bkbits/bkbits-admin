<script setup lang="ts">
const { locked, maxWidth = 1250 } = defineProps<{
  title: string;
  locked?: boolean;
  maxWidth?: number;
}>();

const open = defineModel<boolean>("open", { required: true });

const classes = {
  wrapper: "bk-dialog-wrapper",
  container: "bk-dialog-panel",
  header: "bk-dialog-header",
  title: "bk-dialog-title",
  body: "bk-dialog-body",
  footer: "bk-dialog-footer",
  close: "bk-dialog-close",
};

function updateOpen(next: boolean) {
  if (!locked) open.value = next;
}
</script>

<template>
  <a-modal
    :open="open"
    :title="title"
    width="min(max(84vw, 600px), calc(100vw - 32px))"
    :style="{ maxWidth: `${maxWidth}px` }"
    class="bk-dialog"
    :classes="classes"
    :closable="{ disabled: locked }"
    :keyboard="!locked"
    :mask="{ closable: !locked }"
    :footer="$slots.footer ? undefined : null"
    scroll-lock
    destroy-on-hidden
    @update:open="updateOpen"
  >
    <slot />
    <template v-if="$slots.footer" #footer>
      <slot name="footer" />
    </template>
  </a-modal>
</template>

<style>
.bk-dialog-wrapper.ant-modal-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.bk-dialog-wrapper .bk-dialog.ant-modal {
  top: 0;
  margin: 0;
  padding-bottom: 0;
}

.bk-dialog-wrapper .bk-dialog-panel {
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  min-height: min(400px, calc(100dvh - 16px));
  max-height: min(max(90vh, 400px), calc(100dvh - 16px));
  overflow: hidden;
  padding: 0;
  border: 1px solid var(--ui-border);
  border-radius: 14px;
  background: var(--ui-surface);
}

.bk-dialog-wrapper .bk-dialog-header,
.bk-dialog-wrapper .bk-dialog-footer {
  flex: none;
  margin: 0;
  background: var(--ui-surface);
}

.bk-dialog-wrapper .bk-dialog-header {
  padding-block: 18px;
  padding-inline: 16px 56px;
  border-bottom: 1px solid var(--ui-border);
}

.bk-dialog-wrapper .bk-dialog-title {
  color: var(--ui-text);
  font-size: 18px;
  line-height: 24px;
}

.bk-dialog-wrapper .bk-dialog-close {
  top: 14px;
  inset-inline-end: 14px;
}

.bk-dialog-wrapper .bk-dialog-close:disabled {
  opacity: 0.45;
}

.bk-dialog-wrapper .bk-dialog-body {
  flex: 1;
  min-height: 0;
  overflow: auto;
  overscroll-behavior: contain;
  padding: 24px 16px;
}

.bk-dialog-wrapper .bk-dialog-footer {
  padding: 12px 16px;
  border-top: 1px solid var(--ui-border);
  background: var(--ui-canvas);
}
</style>
