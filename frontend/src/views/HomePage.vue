<template>
  <div class="home-page">

    <!-- Hero -->
    <section class="hero">
      <div class="hero-deco hero-deco-1">🥕</div>
      <div class="hero-deco hero-deco-2">🧅</div>
      <div class="hero-deco hero-deco-3">🍅</div>
      <div class="hero-deco hero-deco-4">🫚</div>
      <div class="hero-content">
        <h1 class="hero-title">记录每一道<span class="highlight">温暖</span>的味道</h1>
        <p class="hero-subtitle">把妈妈的秘方、自己的创意、朋友的分享，都好好收藏起来</p>
        <div class="hero-btns">
          <button class="hero-btn-primary" @click="$router.push('/recipes')">探索菜谱</button>
          <button class="hero-btn-secondary" @click="$router.push('/recipes/create')">✏️ 写下我的菜谱</button>
        </div>
      </div>
    </section>

    <!-- 特性 -->
    <section class="features">
      <div class="feature-card" v-for="f in features" :key="f.title">
        <div class="feature-emoji">{{ f.icon }}</div>
        <h3>{{ f.title }}</h3>
        <p>{{ f.desc }}</p>
      </div>
    </section>

    <!-- 热门菜谱 -->
    <section class="hot-section">
      <div class="section-hd">
        <h2>🔥 大家都在做</h2>
        <span class="more-link" @click="$router.push('/recipes')">查看更多 →</span>
      </div>
      <div v-loading="hotLoading" class="hot-grid">
        <div
          v-for="recipe in hotRecipes"
          :key="recipe.id"
          class="hot-card"
          @click="$router.push(`/recipes/${recipe.id}`)">
          <div class="hot-cover">
            <img v-if="recipe.coverImage" :src="recipe.coverImage" alt="cover" />
            <div v-else class="hot-cover-placeholder">
              <span>🍳</span>
            </div>
            <div v-if="recipe.difficulty" class="difficulty-badge">{{ recipe.difficulty }}</div>
          </div>
          <div class="hot-body">
            <h4>{{ recipe.name }}</h4>
            <p>{{ recipe.description || '暂无描述' }}</p>
            <div class="hot-meta">
              <span v-if="recipe.cookingTime">⏱ {{ recipe.cookingTime }}分钟</span>
              <span v-if="recipe.categoryName">📁 {{ recipe.categoryName }}</span>
            </div>
          </div>
        </div>
      </div>
      <div v-if="!hotLoading && hotRecipes.length === 0" class="empty-tip">
        <span>厨房还是空的，快来添加第一道菜谱吧 🍳</span>
      </div>
    </section>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getHotRecipes } from '@/api/recipes'
import type { Recipe } from '@/types/recipes'

const hotLoading = ref(false)
const hotRecipes = ref<Recipe[]>([])

const features = [
  { icon: '📒', title: '菜谱收藏', desc: '把家传秘方、创意料理一一记录，随时翻阅' },
  { icon: '🛒', title: '食材管理', desc: '掌握冰箱库存，减少浪费，合理安排采购' },
  { icon: '🔍', title: '快速搜索', desc: '按名称、分类、难度，秒找心仪菜谱' },
]

const loadHotRecipes = async () => {
  hotLoading.value = true
  try { hotRecipes.value = await getHotRecipes() }
  catch (e) { console.error(e) }
  finally { hotLoading.value = false }
}

onMounted(() => loadHotRecipes())
</script>

<style scoped>
.home-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

/* Hero */
.hero {
  position: relative;
  background: linear-gradient(135deg, #fff5ee 0%, #ffecd9 60%, #fff8f0 100%);
  border-radius: var(--radius-lg);
  padding: 72px 48px;
  text-align: center;
  margin-bottom: 48px;
  overflow: hidden;
  border: 1px solid var(--color-border-light);
}

.hero-deco {
  position: absolute;
  font-size: 48px;
  opacity: 0.15;
  animation: float 6s ease-in-out infinite;
}

.hero-deco-1 { top: 16px; left: 40px; animation-delay: 0s; }
.hero-deco-2 { top: 20px; right: 60px; animation-delay: 1.5s; }
.hero-deco-3 { bottom: 20px; left: 80px; animation-delay: 3s; }
.hero-deco-4 { bottom: 16px; right: 40px; animation-delay: 4.5s; }

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.hero-title {
  font-family: var(--font-main);
  font-size: 40px;
  color: var(--color-text);
  margin-bottom: 14px;
  line-height: 1.3;
}

.highlight {
  color: var(--color-primary);
  position: relative;
}

.highlight::after {
  content: '';
  position: absolute;
  bottom: 2px;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--color-accent);
  border-radius: 2px;
  opacity: 0.7;
}

.hero-subtitle {
  font-size: 16px;
  color: var(--color-text-secondary);
  margin-bottom: 32px;
  line-height: 1.7;
}

.hero-btns {
  display: flex;
  justify-content: center;
  gap: 16px;
  flex-wrap: wrap;
}

.hero-btn-primary {
  background: var(--color-primary);
  color: #fff;
  border: none;
  border-radius: 24px;
  padding: 12px 28px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s;
  font-family: var(--font-body);
  box-shadow: 0 4px 16px rgba(224, 123, 84, 0.35);
}

.hero-btn-primary:hover {
  background: var(--color-primary-dark);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(224, 123, 84, 0.45);
}

.hero-btn-secondary {
  background: #fff;
  color: var(--color-primary);
  border: 2px solid var(--color-primary);
  border-radius: 24px;
  padding: 11px 28px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s;
  font-family: var(--font-body);
}

.hero-btn-secondary:hover {
  background: var(--color-primary-lighter);
  transform: translateY(-2px);
}

/* Features */
.features {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 48px;
}

.feature-card {
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  padding: 28px 24px;
  text-align: center;
  transition: all 0.25s;
}

.feature-card:hover {
  border-color: var(--color-primary-light);
  box-shadow: var(--shadow-md);
  transform: translateY(-3px);
}

.feature-emoji {
  font-size: 36px;
  margin-bottom: 12px;
}

.feature-card h3 {
  font-family: var(--font-main);
  font-size: 16px;
  color: var(--color-text);
  margin-bottom: 8px;
}

.feature-card p {
  font-size: 13px;
  color: var(--color-text-secondary);
  line-height: 1.6;
}

/* Hot Section */
.hot-section { margin-bottom: 48px; }

.section-hd {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.section-hd h2 {
  font-family: var(--font-main);
  font-size: 22px;
  color: var(--color-text);
  margin: 0;
}

.more-link {
  font-size: 14px;
  color: var(--color-primary);
  cursor: pointer;
  transition: opacity 0.2s;
}

.more-link:hover { opacity: 0.7; }

.hot-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.hot-card {
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.25s;
}

.hot-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
  border-color: var(--color-primary-light);
}

.hot-cover {
  position: relative;
  width: 100%;
  height: 150px;
  overflow: hidden;
}

.hot-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.hot-card:hover .hot-cover img { transform: scale(1.05); }

.hot-cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #ffecd9, #fff5ee);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
}

.difficulty-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(4px);
  color: var(--color-primary-dark);
  font-size: 11px;
  font-weight: 600;
  padding: 3px 8px;
  border-radius: 10px;
}

.hot-body { padding: 14px; }

.hot-body h4 {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hot-body p {
  font-size: 12px;
  color: var(--color-text-secondary);
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hot-meta {
  display: flex;
  gap: 10px;
  font-size: 11px;
  color: var(--color-text-muted);
}

.empty-tip {
  text-align: center;
  color: var(--color-text-muted);
  padding: 48px 0;
  font-size: 15px;
}

@media (max-width: 900px) {
  .features { grid-template-columns: 1fr; }
  .hot-grid { grid-template-columns: repeat(2, 1fr); }
  .hero { padding: 48px 24px; }
  .hero-title { font-size: 28px; }
}
</style>
