<template>
  <div class="auth-page">
    <div class="auth-left">
      <div class="auth-brand">
        <span class="brand-icon">🍜</span>
        <span class="brand-name">我的菜谱</span>
      </div>
      <h2 class="auth-slogan">每一道菜，<br>都是一个故事</h2>
      <div class="auth-deco-items">
        <span>🍝</span><span>🥘</span><span>🍲</span><span>🥗</span><span>🍱</span>
      </div>
    </div>
    <div class="auth-right">
      <div class="auth-card">
        <h1 class="auth-title">欢迎回来 👋</h1>
        <p class="auth-subtitle">登录你的菜谱账号</p>

        <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" size="large">
              <template #prefix><span style="font-size:16px">📱</span></template>
            </el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" show-password>
              <template #prefix><span style="font-size:16px">🔒</span></template>
            </el-input>
          </el-form-item>
          <button class="submit-btn" :disabled="loading" @click.prevent="handleLogin">
            {{ loading ? '登录中...' : '登 录' }}
          </button>
        </el-form>

        <p class="auth-footer-link">
          还没有账号？
          <span @click="$router.push('/register')">立即注册</span>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { login } from '@/api/auth'
import { useAuthStore } from '@/store/auth'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({ phone: '', password: '' })

const rules = {
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login(form)
        authStore.setAuth(res.token, res.user)
        ElMessage.success('登录成功，欢迎回来！')
        router.push('/')
      } catch (e) {
        console.error(e)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
}

.auth-left {
  flex: 1;
  background: linear-gradient(160deg, #e07b54 0%, #f5a67a 50%, #f5c842 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 48px;
  gap: 24px;
}

.auth-brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-icon { font-size: 36px; }

.brand-name {
  font-family: var(--font-main);
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 2px;
}

.auth-slogan {
  font-family: var(--font-main);
  font-size: 32px;
  color: #fff;
  text-align: center;
  line-height: 1.5;
  opacity: 0.95;
}

.auth-deco-items {
  display: flex;
  gap: 16px;
  font-size: 32px;
  opacity: 0.8;
}

.auth-right {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg);
  padding: 48px;
}

.auth-card {
  width: 100%;
  max-width: 360px;
}

.auth-title {
  font-family: var(--font-main);
  font-size: 28px;
  color: var(--color-text);
  margin-bottom: 6px;
}

.auth-subtitle {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-bottom: 32px;
}

.submit-btn {
  width: 100%;
  background: var(--color-primary);
  color: #fff;
  border: none;
  border-radius: 12px;
  padding: 14px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 8px;
  transition: all 0.2s;
  font-family: var(--font-body);
  letter-spacing: 4px;
}

.submit-btn:hover:not(:disabled) {
  background: var(--color-primary-dark);
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(224, 123, 84, 0.35);
}

.submit-btn:disabled { opacity: 0.6; cursor: not-allowed; }

.auth-footer-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.auth-footer-link span {
  color: var(--color-primary);
  cursor: pointer;
  font-weight: 600;
}

.auth-footer-link span:hover { text-decoration: underline; }

@media (max-width: 768px) {
  .auth-left { display: none; }
  .auth-right { width: 100%; }
}
</style>
