<script setup lang="ts">
import { shallowRef, useId, useTemplateRef, watch } from "vue";
import { ResizeObserver as ResizeObserverPolyfill } from "@juggle/resize-observer";
import type { FormProps } from "antdv-next";
import BkDialog from "@/components/BkDialog.vue";

const {
  model = {},
  loading,
  confirmDisabled,
} = defineProps<{
  title: string;
  model?: FormProps["model"];
  rules?: FormProps["rules"];
  loading?: boolean;
  confirmDisabled?: boolean;
}>();

const open = defineModel<boolean>("open", { required: true });
const emit = defineEmits<{ confirm: [] }>();
const formId = useId();
const fields = useTemplateRef<HTMLDivElement>("fields");
const columns = shallowRef(1);

watch(
  fields,
  (element, _, onCleanup) => {
    if (!element) return;

    const Observer = window.ResizeObserver ?? ResizeObserverPolyfill;
    const observer = new Observer(([entry]) => {
      if (entry)
        columns.value = Math.min(4, Math.max(1, Math.floor(entry.contentRect.width / 300)));
    });
    observer.observe(element);
    onCleanup(() => observer.disconnect());
  },
  { flush: "post" },
);

function handleConfirm() {
  if (open.value && !loading && !confirmDisabled) emit("confirm");
}
</script>

<template>
  <BkDialog v-model:open="open" :title="title" :locked="loading">
    <a-form
      :name="formId"
      :model="model"
      :rules="rules"
      :disabled="loading"
      layout="vertical"
      @finish="handleConfirm"
    >
      <fieldset class="bk-form-dialog-fieldset" :disabled="loading">
        <div ref="fields" class="bk-form-dialog-fields" :style="{ '--bk-form-columns': columns }">
          <slot />
        </div>
      </fieldset>
    </a-form>

    <template #footer>
      <a-button :disabled="loading" @click="open = false">取消</a-button>
      <a-button
        type="primary"
        html-type="submit"
        :form="formId"
        :loading="loading"
        :disabled="loading || confirmDisabled"
      >
        确定
      </a-button>
    </template>
  </BkDialog>
</template>

<style scoped>
.bk-form-dialog-fieldset {
  min-inline-size: 0;
  margin: 0;
  padding: 0;
  border: 0;
}

.bk-form-dialog-fields {
  display: grid;
  grid-template-columns: repeat(var(--bk-form-columns), minmax(0, 1fr));
  align-items: start;
  gap: 16px 24px;
}

.bk-form-dialog-fields > :deep(.ant-form-item) {
  min-width: 0;
  margin-bottom: 0;
}

.bk-form-dialog-fields :deep(.ant-input-number) {
  width: 100%;
}

.bk-form-dialog-fields > :deep(.bk-form-dialog-wide) {
  grid-column: 1 / -1;
  min-width: 0;
}
</style>
