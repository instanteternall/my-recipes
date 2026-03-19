<template>
  <div class="ingredient-form-page">
    <div class="page-header">
      <h1>添加食材</h1>
    </div>
    <el-card v-loading="loading">
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="食材名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入食材名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="数量" prop="quantity">
              <el-input-number v-model="form.quantity" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="form.unit" placeholder="如：个、斤、克" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="保质期" prop="expiryDate">
          <el-date-picker v-model="form.expiryDate" type="date" placeholder="选择保质期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { createIngredient, getIngredientCategories } from '@/api/ingredients'
import type { IngredientCategory } from '@/types/ingredients'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const submitting = ref(false)
const categories = ref<IngredientCategory[]>([])

const form = reactive({
  name: '',
  quantity: 1,
  unit: '',
  expiryDate: '',
  categoryId: undefined as number | undefined
})

const rules = {
  name: [{ required: true, message: '请输入食材名称', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        await createIngredient(form)
        ElMessage.success('添加成功')
        router.push('/ingredients')
      } catch (error) {
        console.error(error)
      } finally {
        submitting.value = false
      }
    }
  })
}

const loadCategories = async () => {
  try {
    const res = await getIngredientCategories()
    categories.value = res
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.ingredient-form-page {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  color: #303133;
}
</style>
