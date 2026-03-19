<template>
  <div class="ingredient-list-page">
    <div class="page-header">
      <h1>我的食材</h1>
      <el-button type="primary" @click="$router.push('/ingredients/create')">添加食材</el-button>
    </div>
    <el-card>
      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索食材..." clearable @clear="loadIngredients">
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="categoryId" placeholder="选择分类" clearable @change="loadIngredients">
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
      </div>
      <el-table :data="ingredientList" v-loading="loading" stripe>
        <el-table-column prop="name" label="食材名称" />
        <el-table-column prop="quantity" label="数量" width="120">
          <template #default="{ row }">
            {{ row.quantity }} {{ row.unit }}
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" />
        <el-table-column prop="expiryDate" label="保质期" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button type="primary" link @click="$router.push(`/ingredients/${row.id}/edit`)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && ingredientList.length === 0" description="暂无食材" />
      <div class="pagination" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadIngredients"
          @current-change="loadIngredients"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getIngredientList, getIngredientCategories, deleteIngredient } from '@/api/ingredients'
import type { Ingredient, IngredientCategory } from '@/types/ingredients'

const loading = ref(false)
const ingredientList = ref<Ingredient[]>([])
const categories = ref<IngredientCategory[]>([])
const searchKeyword = ref('')
const categoryId = ref<number>()
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadIngredients = async () => {
  loading.value = true
  try {
    const res = await getIngredientList({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value || undefined,
      categoryId: categoryId.value
    })
    ingredientList.value = res.list
    total.value = res.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const res = await getIngredientCategories()
    categories.value = res
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个食材吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteIngredient(id)
    ElMessage.success('删除成功')
    loadIngredients()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  loadIngredients()
  loadCategories()
})
</script>

<style scoped>
.ingredient-list-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  color: #303133;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.search-bar .el-input {
  max-width: 300px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
