<template>
  <div class="search-page">
    <div class="search-header">
      <h1>搜索菜谱</h1>
      <el-input
        v-model="keyword"
        placeholder="输入菜谱名称或描述..."
        size="large"
        clearable
        @input="debouncedSearch"
        @clear="clearSearch"
        style="max-width: 600px">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <div class="search-filters">
        <el-select v-model="categoryId" placeholder="全部分类" clearable @change="doSearch" style="width:140px">
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
        <el-select v-model="difficulty" placeholder="全部难度" clearable @change="doSearch" style="width:120px">
          <el-option label="简单" value="简单" />
          <el-option label="中等" value="中等" />
          <el-option label="困难" value="困难" />
        </el-select>
      </div>
    </div>

    <div v-loading="loading">
      <el-row :gutter="20" v-if="results.length > 0">
        <el-col :xs="24" :sm="12" :lg="8" v-for="recipe in results" :key="recipe.id">
          <el-card class="result-card" shadow="hover" @click="$router.push(`/recipes/${recipe.id}`)">
            <div class="result-cover">
              <img v-if="recipe.coverImage" :src="recipe.coverImage" alt="cover" />
              <div v-else class="cover-placeholder">🍳</div>
            </div>
            <div class="result-info">
              <h3>{{ recipe.name }}</h3>
              <p>{{ recipe.description || '暂无描述' }}</p>
              <div class="result-meta">
                <span v-if="recipe.cookingTime">⏱ {{ recipe.cookingTime }}分钟</span>
                <el-tag v-if="recipe.difficulty" size="small">{{ recipe.difficulty }}</el-tag>
                <span v-if="recipe.categoryName">{{ recipe.categoryName }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty
        v-else-if="!loading && searched"
        description="没有找到相关菜谱"
      />
      <div v-else-if="!loading && !searched" class="search-tips">
        <p>输入关键词开始搜索，或选择分类和难度进行筛选</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { searchRecipes } from '@/api/recipes'
import { getRecipeCategories } from '@/api/recipes'
import type { Recipe, RecipeCategory } from '@/types/recipes'

const route = useRoute()
const loading = ref(false)
const searched = ref(false)
const keyword = ref('')
const categoryId = ref<number | undefined>()
const difficulty = ref('')
const results = ref<Recipe[]>([])
const categories = ref<RecipeCategory[]>([])

let timer: ReturnType<typeof setTimeout> | null = null

const debouncedSearch = () => {
  if (timer) clearTimeout(timer)
  timer = setTimeout(doSearch, 300)
}

const doSearch = async () => {
  loading.value = true
  searched.value = true
  try {
    results.value = await searchRecipes(keyword.value, categoryId.value, difficulty.value || undefined)
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const clearSearch = () => {
  results.value = []
  searched.value = false
}

onMounted(async () => {
  try { categories.value = await getRecipeCategories() } catch {}
  // 支持从其他页面携带关键词跳转
  const q = route.query.q as string
  if (q) {
    keyword.value = q
    doSearch()
  }
})
</script>

<style scoped>
.search-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.search-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
  text-align: center;
}

.search-header h1 {
  font-size: 28px;
  color: #303133;
  margin: 0;
}

.search-filters {
  display: flex;
  gap: 12px;
}

.result-card {
  cursor: pointer;
  margin-bottom: 20px;
  transition: transform 0.2s;
}

.result-card:hover { transform: translateY(-3px); }

.result-cover {
  width: 100%;
  height: 150px;
  overflow: hidden;
  border-radius: 6px;
  margin-bottom: 10px;
}

.result-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
}

.result-info h3 {
  margin: 0 0 6px;
  font-size: 15px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.result-info p {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.result-meta {
  display: flex;
  gap: 10px;
  align-items: center;
  font-size: 12px;
  color: #c0c4cc;
}

.search-tips {
  text-align: center;
  color: #c0c4cc;
  padding: 60px 0;
  font-size: 15px;
}
</style>
