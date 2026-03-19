<template>
  <div class="recipe-detail-page" v-loading="loading">
    <div v-if="recipe" class="recipe-content">
      <div class="recipe-header">
        <div class="cover-image">
          <img v-if="recipe.coverImage" :src="recipe.coverImage" alt="cover" />
          <div v-else class="default-cover">🍳</div>
        </div>
        <div class="recipe-info">
          <h1>{{ recipe.name }}</h1>
          <p class="description">{{ recipe.description || '暂无描述' }}</p>
          <div class="recipe-meta">
            <span v-if="recipe.cookingTime">⏱️ 烹饪时间：{{ recipe.cookingTime }}分钟</span>
            <span v-if="recipe.difficulty">📊 难度：{{ recipe.difficulty }}</span>
            <span v-if="recipe.categoryName">📁 分类：{{ recipe.categoryName }}</span>
          </div>
          <div class="recipe-tags" v-if="recipe.tags?.length">
            <el-tag v-for="tag in recipe.tags" :key="tag.id">{{ tag.name }}</el-tag>
          </div>
          <div class="recipe-actions" v-if="isOwner">
            <el-button type="primary" @click="$router.push(`/recipes/${recipe.id}/edit`)">编辑</el-button>
            <el-button type="danger" @click="handleDelete">删除</el-button>
          </div>
        </div>
      </div>
      <el-card class="ingredients-card">
        <template #header>
          <h2>食材清单</h2>
        </template>
        <el-table :data="recipe.ingredients" stripe>
          <el-table-column prop="ingredientName" label="食材" />
          <el-table-column prop="quantity" label="数量" width="120">
            <template #default="{ row }">
              {{ row.quantity }} {{ row.unit }}
            </template>
          </el-table-column>
        </el-table>
      </el-card>
      <el-card class="steps-card">
        <template #header>
          <h2>烹饪步骤</h2>
        </template>
        <div class="steps-list">
          <div v-for="(step, index) in recipe.steps" :key="index" class="step-item">
            <div class="step-number">{{ index + 1 }}</div>
            <div class="step-content">
              <p>{{ step.content }}</p>
              <img v-if="step.image" :src="step.image" alt="step" class="step-image" />
            </div>
          </div>
        </div>
      </el-card>
    </div>
    <el-empty v-else-if="!loading" description="菜谱不存在" />
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

const isOwner = computed(() => {
  return recipe.value && authStore.user && recipe.value.userId === authStore.user.id
})

const loadRecipe = async () => {
  const id = Number(route.params.id)
  loading.value = true
  try {
    const res = await getRecipeById(id)
    recipe.value = res
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这个菜谱吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteRecipe(recipe.value!.id)
    ElMessage.success('删除成功')
    router.push('/recipes')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  loadRecipe()
})
</script>

<style scoped>
.recipe-detail-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.recipe-header {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
}

.cover-image {
  width: 300px;
  height: 225px;
  flex-shrink: 0;
  border-radius: 12px;
  overflow: hidden;
}

.cover-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-cover {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 64px;
  background: #f5f7fa;
}

.recipe-info {
  flex: 1;
}

.recipe-info h1 {
  margin: 0 0 12px;
  color: #303133;
}

.description {
  color: #606266;
  margin-bottom: 16px;
}

.recipe-meta {
  display: flex;
  gap: 16px;
  color: #909399;
  margin-bottom: 12px;
}

.recipe-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.recipe-actions {
  display: flex;
  gap: 12px;
}

.ingredients-card,
.steps-card {
  margin-bottom: 24px;
}

.ingredients-card h2,
.steps-card h2 {
  margin: 0;
  font-size: 18px;
}

.steps-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.step-item {
  display: flex;
  gap: 16px;
}

.step-number {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: #409eff;
  color: #fff;
  border-radius: 50%;
  font-size: 16px;
  font-weight: bold;
  flex-shrink: 0;
}

.step-content {
  flex: 1;
}

.step-content p {
  margin: 0 0 12px;
  color: #303133;
  line-height: 1.6;
}

.step-image {
  max-width: 300px;
  border-radius: 8px;
}
</style>
