<template>
  <div class="profile-page">
    <div class="page-header">
      <h1>个人资料</h1>
    </div>
    <el-card v-loading="loading">
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="头像">
          <div class="avatar-wrapper">
            <el-avatar :size="80" :src="form.avatar || undefined">{{ form.nickname?.charAt(0) }}</el-avatar>
            <el-input v-model="form.avatar" placeholder="头像URL" style="margin-top: 12px" />
          </div>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" disabled />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { useAuthStore } from '@/store/auth'
import { updateProfile } from '@/api/auth'

const authStore = useAuthStore()
const formRef = ref<FormInstance>()
const loading = ref(false)
const submitting = ref(false)

const form = reactive({
  nickname: '',
  phone: '',
  avatar: ''
})

const rules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度为2-20个字符', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        await updateProfile(form)
        ElMessage.success('保存成功')
        authStore.getCurrentUser()
      } catch (error) {
        console.error(error)
      } finally {
        submitting.value = false
      }
    }
  })
}

onMounted(() => {
  if (authStore.user) {
    form.nickname = authStore.user.nickname
    form.phone = authStore.user.phone
    form.avatar = authStore.user.avatar || ''
  }
})
</script>

<style scoped>
.profile-page {
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

.avatar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>
