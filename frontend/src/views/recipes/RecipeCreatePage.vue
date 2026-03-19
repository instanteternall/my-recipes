<template>
  <div class="recipe-form-page">
    <div class="page-header">
      <h1>创建菜谱</h1>
    </div>
    <el-card v-loading="loading">
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="菜谱名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入菜谱名称" />
        </el-form-item>
        <el-form-item label="菜谱描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入菜谱描述" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="烹饪时间（分钟）" prop="cookingTime">
              <el-input-number v-model="form.cookingTime" :min="1" :max="999" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="难度" prop="difficulty">
              <el-select v-model="form.difficulty" placeholder="请选择难度" style="width: 100%">
                <el-option label="简单" value="简单" />
                <el-option label="中等" value="中等" />
                <el-option label="困难" value="困难" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
                <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="标签" prop="tagIds">
          <el-select v-model="form.tagIds" multiple placeholder="请选择标签" style="width: 100%">
            <el-option v-for="tag in tags" :key="tag.id" :label="tag.name" :value="tag.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="封面图">
          <ImageUploader v-model="form.coverImage" />
        </el-form-item>

        <el-form-item label="食材">
          <div v-for="(ing, index) in form.ingredients" :key="index" class="ingredient-row">
            <el-input v-model="ing.ingredientName" placeholder="食材名称" style="width: 35%" />
            <el-input-number v-model="ing.quantity" :min="0" :precision="2" style="width: 20%" />
            <el-input v-model="ing.unit" placeholder="单位" style="width: 20%" />
            <el-button type="danger" link @click="removeIngredient(index)">删除</el-button>
          </div>
        </el-form-item>
        <div class="add-btn-row">
          <el-button type="primary" plain @click="addIngredient">+ 添加食材</el-button>
        </div>

        <el-form-item label="烹饪步骤">
          <div v-for="(step, index) in form.steps" :key="index" class="step-block">
            <div class="step-header">
              <span class="step-number">{{ index + 1 }}</span>
              <el-button type="danger" link @click="removeStep(index)">删除步骤</el-button>
            </div>
            <el-input v-model="step.content" type="textarea" :rows="2" placeholder="请输入步骤内容" />
            <div class="step-img-row">
              <span class="step-img-label">步骤配图（可选）</span>
              <ImageUploader v-model="step.image" />
            </div>
          </div>
        </el-form-item>
        <div class="add-btn-row">
          <el-button type="primary" plain @click="addStep">+ 添加步骤</el-button>
        </div>

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
import { createRecipe, getRecipeCategories, getRecipeTags } from '@/api/recipes'
import type { RecipeCategory, Tag } from '@/types/recipes'
import ImageUploader from '@/components/ImageUploader.vue'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const submitting = ref(false)
const categories = ref<RecipeCategory[]>([])
const tags = ref<Tag[]>([])

const form = reactive({
  name: '',
  description: '',
  coverImage: '',
  cookingTime: 30,
  difficulty: '简单',
  categoryId: undefined as number | undefined,
  tagIds: [] as number[],
  ingredients: [{ ingredientName: '', quantity: 1, unit: '个' }] as any[],
  steps: [{ stepOrder: 1, content: '', image: '' }] as any[]
})

const rules = {
  name: [{ required: true, message: '请输入菜谱名称', trigger: 'blur' }]
}

const addIngredient = () => form.ingredients.push({ ingredientName: '', quantity: 1, unit: '' })
const removeIngredient = (index: number) => form.ingredients.splice(index, 1)

const addStep = () => form.steps.push({ stepOrder: form.steps.length + 1, content: '', image: '' })
const removeStep = (index: number) => {
  form.steps.splice(index, 1)
  form.steps.forEach((s, i) => { s.stepOrder = i + 1 })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const data = {
          ...form,
          ingredients: form.ingredients.filter((i: any) => i.ingredientName),
          steps: form.steps.filter((s: any) => s.content)
        }
        await createRecipe(data)
        ElMessage.success('创建成功')
        router.push('/recipes')
      } catch (error) {
        console.error(error)
      } finally {
        submitting.value = false
      }
    }
  })
}

const loadCategories = async () => {
  try { categories.value = await getRecipeCategories() } catch {}
}
const loadTags = async () => {
  try { tags.value = await getRecipeTags() } catch {}
}

onMounted(() => { loadCategories(); loadTags() })
</script>

<style scoped>
.recipe-form-page {
  max-width: 900px;
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

.ingredient-row {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  align-items: center;
}

.add-btn-row {
  margin: -8px 0 20px 0;
}

.step-block {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.step-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.step-number {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background: #409eff;
  color: #fff;
  border-radius: 50%;
  font-size: 13px;
  font-weight: bold;
}

.step-img-row {
  margin-top: 10px;
}

.step-img-label {
  font-size: 13px;
  color: #909399;
  display: block;
  margin-bottom: 6px;
}
</style>
