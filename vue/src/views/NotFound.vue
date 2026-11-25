<script setup lang="ts">
import { ArrowLeft } from "@lucide/vue";
import { useRouter } from "vue-router";

const router = useRouter();
</script>

<template>
  <main class="not-found">
    <section class="not-found-card" aria-labelledby="not-found-title">
      <div class="not-found-code">404</div>
      <h1 id="not-found-title">页面不存在</h1>
      <p>请检查网址，或返回首页继续操作。</p>
      <a-button type="primary" size="large" @click="router.push('/dashboard')">
        <template #icon><ArrowLeft :size="16" aria-hidden="true" /></template>
        返回首页
      </a-button>
    </section>
  </main>
</template>

<style scoped>
.not-found {
  box-sizing: border-box;
  display: grid;
  place-items: center;
  min-height: 100vh;
  min-height: 100dvh;
  padding: 16px;
  background: var(--ui-canvas, #f3f5f9);
  color: var(--ui-text, #20283c);
}

.not-found-card {
  box-sizing: border-box;
  width: min(100%, 520px);
  padding: clamp(24px, 7vw, 60px);
  border: 1px solid var(--ui-border, #e0e5ee);
  border-radius: 24px;
  background: var(--ui-surface, #fff);
  box-shadow: var(--ui-shadow, 0 24px 64px rgb(26 36 64 / 10%));
  text-align: center;
}

.not-found-code {
  margin-bottom: 24px;
  padding: 22px 12px;
  border-radius: 16px;
  background: var(--ui-accent-soft, #eeedff);
  color: var(--ui-accent, #4b45d4);
  font-size: clamp(64px, 16vw, 108px);
  font-weight: 750;
  font-variant-numeric: tabular-nums;
  line-height: 1;
}

.not-found h1 {
  margin: 0;
  font-size: clamp(23px, 5vw, 30px);
  line-height: 1.3;
}

.not-found p {
  margin: 10px 0 24px;
  color: var(--ui-muted, #5b6578);
}
</style>
