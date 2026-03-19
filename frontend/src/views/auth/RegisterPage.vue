<template>
  <div class="auth-page">
    <div class="auth-left">
      <div class="auth-brand">
        <span class="brand-icon">🍜</span>
        <span class="brand-name">我的菜谱</span>
      </div>
      <h2 class="auth-slogan">开始记录，<br>属于你的味道</h2>
      <div class="auth-deco-items">
        <span>🥕</span><span>🧄</span><span>🍋</span><span>🫑</span><span>🧅</span>
      </div>
    </div>
    <div class="auth-right">
      <div class="auth-card">
        <h1 class="auth-title">创建账号 🎉</h1>
        <p class="auth-subtitle">加入我们，开始你的烹饪之旅</p>

        <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" size="large">
              <template #prefix><span style="font-size:16px">📱</span></template>
            </el-input>
          </el-form-item>
          <el-form-item label="验证码" prop="code">
            <div class="code-row">
              <el-input v-model="form.code" placeholder="请输入验证码" size="large" />
              <button class="code-btn" :disabled="codeSent" @click.prevent="sendCode">
                {{ codeSent ? `${countdown}s后重发` : '获取验证码' }}
              </button>
            </div>
          </el-form-item>
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="form.nickname" placeholder="给自己起个名字" size="large">
              <template #prefix><span style="font-size:16px">😊</span></template>
            </el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="设置密码（至少6位）" size="large" show-password>
              <template #prefix><span style="font-size:16px">🔒</span></template>
            </el-input>
          </el-form-item>
          <button class="submit-btn" :disabled="loading" @click.prevent="handleRegister">
            {{ loading ? '注册中...' : '注 册' }}
          </button>
        </el-form>

        <p class="auth-footer-link">
          已有账号？
          <span @click="$router.push('/login')">立即登录</span>
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
import { sendSms, register } from '@/api/auth'
import { useAuthStore } from '@/store/auth'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref<FormInstance>()
const loading = ref(false)
const codeSent = ref(false)
const countdown = ref(60)

const form = reactive({ phone: '', code: '', nickname: '', password: '' })

const rules = {
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [{ required: true, min: 6, message: '密码至少6位', trigger: 'blur' }]
}

const sendCode = async () => {
  if (!form.phone) { ElMessage.warning('请先输入手机号'); return }
  try {
    await sendSms({ phone: form.phone })
    ElMessage.success('验证码已发送（开发模式：123456）')
    codeSent.value = true
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) { clearInterval(timer); codeSent.value = false; countdown.value = 60 }
    }, 1000)
  } catch (e) { console.error(e) }
}

const handleRegister = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await register(form)
        authStore.setAuth(res.token, res.user)
        ElMessage.success('注册成功，欢迎加入！')
        router.push('/')
      } catch (e) { console.error(e) }
      finally { loading.value = false }
    }
  })
}
</script>

<style scoped>
.auth-page { min-height: 100vh; display: flex; }

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

.auth-brand { display: flex; align-items: center; gap: 10px; }
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

.auth-deco-items { display: flex; gap: 16px; font-size: 32px; opacity: 0.8; }

.auth-right {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg);
  padding: 48px;
}

.auth-card { width: 100%; max-width: 360px; }

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

.code-row { display: flex; gap: 10px; }

.code-btn {
  flex-shrink: 0;
  background: var(--color-primary-lighter);
  color: var(--color-primary);
  border: 1.5px solid var(--color-border);
  border-radius: 10px;
  padding: 0 16px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
  font-family: var(--font-body);
}

.code-btn:hover:not(:disabled) {
  background: var(--color-primary);
  color: #fff;
  border-color: var(--color-primary);
}

.code-btn:disabled { opacity: 0.5; cursor: not-allowed; }

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
