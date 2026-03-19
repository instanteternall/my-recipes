<template>
  <div class="recipe-detail-page" v-loading="loading">
    <div v-if="recipe">
      <!-- 全宽封面 -->
      <div class="cover-hero">
        <img v-if="recipe.coverImage" :src="recipe.coverImage" alt="cover" class="cover-img" />
        <div v-else class="cover-placeholder">🍳</div>
        <div class="cover-overlay">
          <div class="cover-content">
            <div class="breadcrumb" @click="$router.push('/recipes')">← 返回菜谱列表</div>
            <h1 class="cover-title">{{ recipe.name }}</h1>
            <p class="cover-desc">{{ recipe.description }}</p>
            <div class="cover-badges">
              <span v-if="recipe.cookingTime" class="badge">⏱ {{ recipe.cookingTime }} 分钟</span>
              <span v-if="recipe.difficulty" class="badge" :class="diffClass(recipe.difficulty)">{{ recipe.difficulty }}</span>
              <span v-if="recipe.categoryName" class="badge">📁 {{ recipe.categoryName }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 主体 -->
      <div class="detail-body">
        <div class="detail-main">
          <div class="tags-row" v-if="recipe.tags?.length">
            <span v-for="tag in recipe.tags" :key="tag.id" class="tag-chip">{{ tag.name }}</span>
          </div>
          <div class="section">
            <div class="section-title"><span>👨‍🍳</span>烹饪步骤</div>
            <div class="steps-list">
              <div v-for="(step, index) in recipe.steps" :key="index" class="step-card">
                <div class="step-num">{{ index + 1 }}</div>
                <div class="step-body">
                  <p>{{ step.content }}</p>
                  <img v-if="step.image" :src="step.image" alt="step" class="step-img" />
                </div>
              </div>
              <div v-if="!recipe.steps?.length" class="empty-tip">暂无步骤信息</div>
            </div>
          </div>
        </div>

        <div class="detail-aside">
          <div class="aside-card actions-card" v-if="isOwner">
            <button class="action-btn edit-btn" @click="$router.push(`/recipes/${recipe.id}/edit`)">✏️ 编辑菜谱</button>
            <button class="action-btn delete-btn" @click="handleDelete">🗑️ 删除菜谱</button>
          </div>
          <div class="aside-card">
            <div class="aside-title">
              <span>🛒</span>食材清单
              <span class="count">{{ recipe.ingredients?.length || 0 }} 种</span>
            </div>
            <div class="ingredients-list">
              <div v-for="(ing, index) in recipe.ingredients" :key="index" class="ingredient-row">
                <span class="ing-dot"></span>
                <span class="ing-name">{{ ing.ingredientName }}</span>
                <span class="ing-amount">{{ ing.quantity }} {{ ing.unit }}</span>
              </div>
              <div v-if="!recipe.ingredients?.length" class="empty-tip">暂无食材信息</div>
            </div>
          </div>
          <div class="aside-card">
            <div class="aside-title"><span>ℹ️</span>菜谱信息</div>
            <div class="info-list">
              <div class="info-row" v-if="recipe.cookingTime">
                <span class="info-label">烹饪时间</span>
                <span class="info-value">{{ recipe.cookingTime }} 分钟</span>
              </div>
              <div class="info-row" v-if="recipe.difficulty">
                <span class="info-label">难度</span>
                <span class="info-value" :class="diffClass(recipe.difficulty)">{{ recipe.difficulty }}</span>
              </div>
              <div class="info-row" v-if="recipe.categoryName">
                <span class="info-label">分类</span>
                <span class="info-value">{{ recipe.categoryName }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="!loading" class="not-found">
      <span style="font-size:56px">🍽️</span>
      <p>菜谱不存在或已被删除</p>
      <button class="back-btn" @click="$router.push('/recipes')">返回菜谱列表</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRecipeById, deleteRecipe } from '@/api/recipes'
import { useAuthStore } from '@/store/auth'
import type { Recipe } from '@/types/recipes'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const recipe = ref<Recipe>()

const isOwner = computed(() =>
  recipe.value && authStore.user && recipe.value.userId === authStore.user.id
)

const diffClass = (d: string) => {
  if (d === '简单') return 'easy'
  if (d === '中等') return 'medium'
  return 'hard'
}

const loadRecipe = async () => {
  loading.value = true
  try { recipe.value = await getRecipeById(Number(route.params.id)) }
  catch (e) { console.error(e) }
  finally { loading.value = false }
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这道菜谱吗？删除后无法恢复。', '删除确认', {
      confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning'
    })
    await deleteRecipe(recipe.value!.id)
    ElMessage.success('菜谱已删除')
    router.push('/recipes')
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

onMounted(() => loadRecipe())
</script>

<style scoped>
.recipe-detail-page { min-height: 60vh; }

.cover-hero {
  position: relative;
  width: 100%;
  height: 420px;
  overflow: hidden;
}

.cover-img { width: 100%; height: 100%; object-fit: cover; display: block; }

.cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #ffecd9, #fff5ee);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 96px;
}

.cover-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(30,15,5,0.78) 0%, rgba(30,15,5,0.15) 60%, transparent 100%);
  display: flex;
  align-items: flex-end;
}

.cover-content {
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 0 24px 32px;
}

.breadcrumb {
  font-size: 13px;
  color: rgba(255,255,255,0.7);
  margin-bottom: 10px;
  cursor: pointer;
  transition: color 0.2s;
  display: inline-block;
}
.breadcrumb:hover { color: #fff; }

.cover-title {
  font-family: var(--font-main);
  font-size: 38px;
  color: #fff;
  margin: 0 0 8px;
  text-shadow: 0 2px 8px rgba(0,0,0,0.3);
}

.cover-desc {
  font-size: 15px;
  color: rgba(255,255,255,0.82);
  margin-bottom: 16px;
  max-width: 600px;
  line-height: 1.6;
}

.cover-badges { display: flex; gap: 8px; flex-wrap: wrap; }

.badge {
  background: rgba(255,255,255,0.18);
  backdrop-filter: blur(4px);
  color: #fff;
  font-size: 13px;
  padding: 4px 12px;
  border-radius: 20px;
  border: 1px solid rgba(255,255,255,0.28);
}
.badge.easy { background: rgba(61,145,83,0.5); }
.badge.medium { background: rgba(154,111,0,0.5); }
.badge.hard { background: rgba(192,57,43,0.5); }

.detail-body {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px;
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 28px;
  align-items: start;
}

.tags-row { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 28px; }

.tag-chip {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 20px;
  background: var(--color-primary-lighter);
  color: var(--color-primary-dark);
  border: 1px solid var(--color-border);
}

.section { margin-bottom: 32px; }

.section-title {
  font-family: var(--font-main);
  font-size: 20px;
  color: var(--color-text);
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 12px;
  border-bottom: 2px solid var(--color-border-light);
}

.steps-list { display: flex; flex-direction: column; gap: 16px; }

.step-card {
  display: flex;
  gap: 16px;
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  padding: 20px;
  transition: box-shadow 0.2s;
}
.step-card:hover { box-shadow: var(--shadow-sm); }

.step-num {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary), var(--color-accent));
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-top: 2px;
}

.step-body { flex: 1; }
.step-body p { margin: 0 0 12px; color: var(--color-text); line-height: 1.8; font-size: 15px; }
.step-img { max-width: 100%; border-radius: var(--radius-sm); border: 1px solid var(--color-border); }

.aside-card {
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  padding: 20px;
  margin-bottom: 16px;
}

.aside-title {
  font-family: var(--font-main);
  font-size: 16px;
  color: var(--color-text);
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.count { margin-left: auto; font-size: 12px; color: var(--color-text-muted); font-family: var(--font-body); }

.action-btn {
  width: 100%;
  padding: 11px;
  border-radius: var(--radius-sm);
  border: none;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  font-family: var(--font-body);
  display: block;
  margin-bottom: 10px;
}
.action-btn:last-child { margin-bottom: 0; }
.edit-btn { background: var(--color-primary); color: #fff; }
.edit-btn:hover { background: var(--color-primary-dark); transform: translateY(-1px); }
.delete-btn { background: #fff0f0; color: #c0392b; border: 1.5px solid #ffbdbd; }
.delete-btn:hover { background: #ffe0e0; }

.ingredients-list { display: flex; flex-direction: column; gap: 10px; }

.ingredient-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 10px;
  border-radius: var(--radius-sm);
  background: var(--color-bg);
  border: 1px solid var(--color-border-light);
}

.ing-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--color-primary);
  flex-shrink: 0;
}
.ing-name { flex: 1; font-size: 14px; color: var(--color-text); }
.ing-amount { font-size: 13px; color: var(--color-text-secondary); white-space: nowrap; }

.info-list { display: flex; flex-direction: column; gap: 12px; }

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--color-border-light);
}
.info-row:last-child { border-bottom: none; padding-bottom: 0; }
.info-label { font-size: 13px; color: var(--color-text-secondary); }
.info-value { font-size: 14px; color: var(--color-text); font-weight: 500; }
.info-value.easy { color: #3d9153; }
.info-value.medium { color: #9a6f00; }
.info-value.hard { color: #c0392b; }

.empty-tip { color: var(--color-text-muted); font-size: 14px; text-align: center; padding: 20px 0; }

.not-found { text-align: center; padding: 80px 0; color: var(--color-text-secondary); }
.not-found p { font-size: 16px; margin: 16px 0 24px; }

.back-btn {
  background: var(--color-primary);
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 10px 24px;
  font-size: 14px;
  cursor: pointer;
  font-family: var(--font-body);
}

@media (max-width: 900px) {
  .cover-hero { height: 280px; }
  .cover-title { font-size: 26px; }
  .detail-body { grid-template-columns: 1fr; }
  .detail-aside { order: -1; }
}
</style>
