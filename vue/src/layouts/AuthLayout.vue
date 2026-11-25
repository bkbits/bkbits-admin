<script setup lang="ts">
defineProps<{ title: string }>();
</script>

<template>
  <main class="auth-page">
    <div class="auth-shell">
      <div class="auth-brand">
        <div class="auth-identity">
          <span class="auth-logo"><img src="/favicon.svg" alt="" /></span>
          <div>
            <div class="auth-name">bkbits-admin</div>
            <div class="auth-description">后台管理系统</div>
          </div>
        </div>
        <div class="auth-art" aria-hidden="true">
          <span class="auth-tiles"></span>
        </div>
      </div>

      <div class="auth-panel">
        <div class="auth-content">
          <h1>{{ title }}</h1>
          <slot />
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
.auth-page {
  box-sizing: border-box;
  display: grid;
  place-items: center;
  min-height: 100vh;
  min-height: 100dvh;
  padding: clamp(16px, 4vw, 48px);
  background: var(--ui-canvas, #f3f5f9);
  color: var(--ui-text, #20283c);
}

.auth-shell {
  box-sizing: border-box;
  display: grid;
  grid-template-columns: minmax(0, 2fr) minmax(0, 3fr);
  width: min(100%, 1060px);
  min-height: 548px;
  overflow: hidden;
  border: 1px solid var(--ui-border, #e0e5ee);
  border-radius: 24px;
  background: var(--ui-surface, #fff);
  box-shadow: var(--ui-shadow, 0 24px 64px rgb(26 36 64 / 10%));
}

.auth-brand {
  position: relative;
  isolation: isolate;
  display: flex;
  align-items: flex-start;
  padding: clamp(24px, 4vw, 44px);
  overflow: hidden;
  background:
    radial-gradient(ellipse at 58% 89%, rgb(75 69 212 / 55%), transparent 65%),
    linear-gradient(145deg, #20283c, #272849);
  color: #fff;
}

.auth-identity {
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 14px;
}

.auth-logo {
  display: grid;
  flex: none;
  place-items: center;
  width: 48px;
  height: 48px;
  border: 1px solid rgb(206 210 255 / 25%);
  border-radius: 13px;
  background: rgb(205 210 255 / 12%);
}

.auth-logo img {
  width: 27px;
  height: auto;
}

.auth-name {
  font-size: 20px;
  font-weight: 700;
  line-height: 1.2;
}

.auth-description {
  margin-top: 5px;
  color: #c8cce5;
  font-size: 13px;
}

.auth-art {
  position: absolute;
  inset: 116px 20px 24px;
  display: grid;
  place-items: center;
  mask-image: radial-gradient(ellipse at center, #000 35%, transparent 74%);
  background-image: radial-gradient(rgb(188 192 255 / 28%) 1px, transparent 1px);
  background-size: 18px 18px;
}

.auth-tiles {
  position: relative;
  width: clamp(106px, 14vw, 174px);
  aspect-ratio: 1;
  margin-bottom: 42px;
  transform: rotate(-36deg) skew(13deg, 13deg);
  border: 1px solid #a49fff;
  border-radius: 18px;
  background: linear-gradient(145deg, #7b74f3, #4942c8 85%);
  box-shadow: -12px 16px 44px rgb(25 17 98 / 36%);
}

.auth-tiles::before,
.auth-tiles::after {
  position: absolute;
  inset: -1px;
  border: 1px solid rgb(156 151 255 / 60%);
  border-radius: inherit;
  content: "";
}

.auth-tiles::before {
  inset: 22%;
  border-color: rgb(231 230 255 / 42%);
  background: rgb(227 224 255 / 10%);
}

.auth-tiles::after {
  transform: translate(-48px, 48px);
  box-shadow: 24px -24px 0 -1px rgb(122 113 255 / 25%);
}

.auth-panel {
  display: grid;
  align-items: center;
  padding: clamp(28px, 5vw, 64px);
}

.auth-content {
  width: min(100%, 448px);
  margin-inline: auto;
}

.auth-content h1 {
  margin: 0 0 26px;
  font-size: clamp(27px, 3vw, 36px);
  font-weight: 700;
  line-height: 1.2;
}

.auth-content :deep(.ant-form-item) {
  margin-bottom: 16px;
}

.auth-content :deep(.auth-link) {
  color: var(--ui-accent, #4b45d4);
  font-weight: 600;
}

.auth-content :deep(.auth-link:hover) {
  text-decoration: underline;
}

.auth-content :deep(.auth-link:focus-visible) {
  outline: 2px solid currentColor;
  outline-offset: 3px;
}

@media (max-width: 720px) {
  .auth-shell {
    grid-template-columns: minmax(0, 1fr);
    width: min(100%, 540px);
    min-height: 0;
    border-radius: 18px;
  }

  .auth-brand {
    align-items: center;
    padding: 20px 24px;
  }

  .auth-art {
    inset: -60px -36px -60px auto;
    width: 190px;
    opacity: 0.55;
  }

  .auth-tiles {
    width: 80px;
    margin: 0;
  }

  .auth-panel {
    padding: 28px 24px;
  }
}
</style>
