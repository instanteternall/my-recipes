<template>
  <div class="recipe-list-page">
    <div class="page-header">
      <div>
        <h1>📖 我的菜谱</h1>
        <p class="page-desc">收藏每一道喜爱的味道</p>
      </div>
      <button class="create-btn" @click="$router.push('/recipes/create')">✏️ 写菜谱</button>
    </div>

    <div class="filter-bar">
      <div class="search-wrap">
        <span class="search-icon">🔍</span>
        <input
          v-model="searchKeyword"
          class="search-input"
          placeholder="搜索菜谱名称..."
          @input="debouncedSearch"
        />
      </div>
      <el-select v-model="categoryId" placeholder="全部分类" clearable @change="loadRecipes" style="width:130px">
        <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
      </el-select>
      <el-select v-model="tagId" placeholder="全部标签" clearable @change="loadRecipes" style="width:130px">
        <el-option v-for="tag in tags" :key="tag.id" :label="tag.name" :value="tag.id" />
      </el-select>
    </div>

    <div v-loading="loading" class="recipe-grid">
      <div
        v-for="recipe in recipeList"
        :key="recipe.id"
        class="recipe-card"
        @click="$router.push(`/recipes/${recipe.id}`)">
        <div class="card-cover">
          <img v-if="recipe.coverImage" :src="recipe.coverImage" alt="cover" />
          <div v-else class="cover-placeholder"><span>🍳</span></div>
          <div v-if="recipe.difficulty" class="diff-badge" :class="diffClass(recipe.difficulty)">{{ recipe.difficulty }}</div>
        </div>
        <div class="card-body">
          <h3>{{ recipe.name }}</h3>
          <p>{{ recipe.description || '暂无描述' }}</p>
          <div class="card-meta">
            <span v-if="recipe.cookingTime">⏱ {{ recipe.cookingTime }}分钟</span>
            <span v-if="recipe.categoryName">📁 {{ recipe.categoryName }}</span>
          </div>
          <div class="card-tags" v-if="recipe.tags?.length">
            <span v-for="tag in recipe.tags.slice(0,3)" :key="tag.id" class="tag-chip">{{ tag.name }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="!loading && recipeList.length === 0" class="empty-state">
      <span class="empty-emoji">🍳</span>
      <p>还没有菜谱，快去创建第一道吧！</p>
      <button class="create-btn" @click="$router.push('/recipes/create')">✏️ 写菜谱</button>
    </div>

    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[9, 12, 18]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadRecipes"
        @current-change="loadRecipes"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getRecipeList, getRecipeCategories, getRecipeTags } from '@/api/recipes'
import type { Recipe, RecipeCategory, Tag } from '@/types/recipes'

const loading = ref(false)
const recipeList = ref<Recipe[]>([])
const categories = ref<RecipeCategory[]>([])
const tags = ref<Tag[]>([])
const searchKeyword = ref('')
const categoryId = ref<number | undefined>()
const tagId = ref<number | undefined>()
const currentPage = ref(1)
const pageSize = ref(9)
const total = ref(0)

let debounceTimer: ReturnType<typeof setTimeout> | null = null

const debouncedSearch = () => {
  if (debounceTimer) clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => { currentPage.value = 1; loadRecipes() }, 300)
}

const diffClass = (d: string) => {
  if (d === '简单') return 'easy'
  if (d === '中等') return 'medium'
  return 'hard'
}

const loadRecipes = async () => {
  loading.value = true
  try {
    const res = await getRecipeList({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value || undefined,
      categoryId: categoryId.value,
      tagId: tagId.value
    })
    recipeList.value = res.list
    total.value = res.total
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const loadMeta = async () => {
  try { categories.value = await getRecipeCategories() } catch {}
  try { tags.value = await getRecipeTags() } catch {}
}

onMounted(() => { loadRecipes(); loadMeta() })
</script>

<style scoped>
.recipe-list-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 24px;
}

.page-header h1 {
  font-family: var(--font-main);
  font-size: 26px;
  color: var(--color-text);
  margin: 0 0 4px;
}

.page-desc {
  font-size: 13px;
  color: var(--color-text-muted);
  margin: 0;
}

.create-btn {
  background: var(--color-primary);
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 10px 22px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  font-family: var(--font-body);
}

.create-btn:hover {
  background: var(--color-primary-dark);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(224, 123, 84, 0.3);
}

/* Filter bar */
.filter-bar {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 24px;
  padding: 16px 20px;
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
}

.search-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
  background: var(--color-bg);
  border: 1.5px solid var(--color-border);
  border-radius: 20px;
  padding: 8px 16px;
  flex: 1;
  min-width: 200px;
  transition: border-color 0.2s;
}

.search-wrap:focus-within { border-color: var(--color-primary); }

.search-icon { font-size: 15px; }

.search-input {
  border: none;
  outline: none;
  background: transparent;
  font-size: 14px;
  color: var(--color-text);
  width: 100%;
  font-family: var(--font-body);
}

.search-input::placeholder { color: var(--color-text-muted); }

/* Recipe Grid */
.recipe-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.recipe-card {
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.25s;
}

.recipe-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
  border-color: var(--color-primary-light);
}

.card-cover {
  position: relative;
  width: 100%;
  height: 170px;
  overflow: hidden;
}

.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.35s;
}

.recipe-card:hover .card-cover img { transform: scale(1.06); }

.cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #ffecd9, #fff5ee);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 52px;
}

.diff-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 11px;
  font-weight: 700;
  padding: 3px 10px;
  border-radius: 12px;
  backdrop-filter: blur(4px);
}

.diff-badge.easy { background: rgba(238,248,240,0.92); color: #3d9153; }
.diff-badge.medium { background: rgba(255,248,230,0.92); color: #9a6f00; }
.diff-badge.hard { background: rgba(255,240,240,0.92); color: #c0392b; }

.card-body { padding: 14px 16px; }

.card-body h3 {
  font-family: var(--font-main);
  font-size: 16px;
  color: var(--color-text);
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-body p {
  font-size: 12px;
  color: var(--color-text-secondary);
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-meta {
  display: flex;
  gap: 12px;
  font-size: 11px;
  color: var(--color-text-muted);
  margin-bottom: 8px;
}

.card-tags { display: flex; gap: 5px; flex-wrap: wrap; }

.tag-chip {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
  background: var(--color-primary-lighter);
  color: var(--color-primary-dark);
  border: 1px solid var(--color-border);
}

/* Empty */
.empty-state {
  text-align: center;
  padding: 64px 0;
  color: var(--color-text-secondary);
}

.empty-emoji { font-size: 56px; display: block; margin-bottom: 12px; }

.empty-state p { margin-bottom: 20px; font-size: 15px; }

/* Pagination */
.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 16px 0 8px;
}

@media (max-width: 900px) {
  .recipe-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 600px) {
  .recipe-grid { grid-template-columns: 1fr; }
}
</style>
