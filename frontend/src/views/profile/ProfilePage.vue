<template>
  <div class="profile-page">

    <!-- 顶部 Banner -->
    <div class="profile-banner">
      <div class="banner-inner">
        <div class="avatar-wrap">
          <div class="avatar-ring">
            <img v-if="form.avatar" :src="form.avatar" class="avatar-img" alt="avatar" />
            <span v-else class="avatar-letter">{{ form.nickname?.charAt(0) }}</span>
          </div>
          <label class="avatar-upload-btn" title="更换头像">
            📷
            <input type="file" accept="image/*" style="display:none" @change="handleAvatarChange" />
          </label>
        </div>
        <div class="banner-info">
          <h2>{{ form.nickname || '未设置昵称' }}</h2>
          <p>📱 {{ form.phone }}</p>
        </div>
      </div>
    </div>

    <div class="profile-body">

      <!-- 基本信息 -->
      <div class="profile-card">
        <div class="card-title"><span>👤</span>基本信息</div>
        <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="form.nickname" placeholder="给自己起个好听的名字" size="large" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="form.phone" disabled size="large">
              <template #prefix><span>📱</span></template>
            </el-input>
          </el-form-item>
          <button class="save-btn" :disabled="saving" @click.prevent="handleSaveProfile">
            {{ saving ? '保存中...' : '💾 保存信息' }}
          </button>
        </el-form>
      </div>

      <!-- 修改密码 -->
      <div class="profile-card">
        <div class="card-title"><span>🔐</span>修改密码</div>
        <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-position="top">
          <el-form-item label="当前密码" prop="oldPassword">
            <el-input v-model="pwdForm.oldPassword" type="password" placeholder="请输入当前密码" size="large" show-password />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="pwdForm.newPassword" type="password" placeholder="新密码（至少6位）" size="large" show-password />
          </el-form-item>
          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="再次输入新密码" size="large" show-password />
          </el-form-item>
          <button class="save-btn" :disabled="changingPwd" @click.prevent="handleChangePassword">
            {{ changingPwd ? '修改中...' : '🔒 修改密码' }}
          </button>
        </el-form>
      </div>

      <!-- 账号统计 -->
      <div class="profile-card stats-card">
        <div class="card-title"><span>📊</span>我的数据</div>
        <div class="stats-grid">
          <div class="stat-item" @click="$router.push('/recipes')">
            <span class="stat-num">{{ stats.recipeCount }}</span>
            <span class="stat-label">发布菜谱</span>
          </div>
          <div class="stat-item" @click="$router.push('/ingredients')">
            <span class="stat-num">{{ stats.ingredientCount }}</span>
            <span class="stat-label">记录食材</span>
          </div>
          <div class="stat-item">
            <span class="stat-num">{{ joinDays }}</span>
            <span class="stat-label">加入天数</span>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { useAuthStore } from '@/store/auth'
import { updateProfile, changePassword } from '@/api/auth'
import { uploadImage } from '@/api/upload'
import { getRecipeList } from '@/api/recipes'
import { getIngredientList } from '@/api/ingredients'

const authStore = useAuthStore()
const formRef = ref<FormInstance>()
const pwdFormRef = ref<FormInstance>()
const saving = ref(false)
const changingPwd = ref(false)
const stats = reactive({ recipeCount: 0, ingredientCount: 0 })

const form = reactive({
  nickname: '',
  phone: '',
  avatar: ''
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度2-20个字符', trigger: 'blur' }
  ]
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [{ required: true, min: 6, message: '新密码至少6位', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (_: any, value: string, callback: Function) => {
        if (value !== pwdForm.newPassword) callback(new Error('两次密码不一致'))
        else callback()
      },
      trigger: 'blur'
    }
  ]
}

const joinDays = computed(() => {
  if (!authStore.user?.createdAt) return 0
  const diff = Date.now() - new Date(authStore.user.createdAt).getTime()
  return Math.floor(diff / (1000 * 60 * 60 * 24))
})

const handleAvatarChange = async (e: Event) => {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  try {
    const url = await uploadImage(file)
    form.avatar = url
    ElMessage.success('头像上传成功')
  } catch (err) {
    ElMessage.error('头像上传失败')
  }
}

const handleSaveProfile = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        await updateProfile({ nickname: form.nickname, avatar: form.avatar })
        authStore.getCurrentUser()
        ElMessage.success('资料保存成功 🎉')
      } catch (e) { console.error(e) }
      finally { saving.value = false }
    }
  })
}

const handleChangePassword = async () => {
  if (!pwdFormRef.value) return
  await pwdFormRef.value.validate(async (valid) => {
    if (valid) {
      changingPwd.value = true
      try {
        await changePassword({ oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
        ElMessage.success('密码修改成功，请重新登录')
        pwdForm.oldPassword = ''
        pwdForm.newPassword = ''
        pwdForm.confirmPassword = ''
      } catch (e) { console.error(e) }
      finally { changingPwd.value = false }
    }
  })
}

const loadStats = async () => {
  try {
    const r = await getRecipeList({ page: 1, pageSize: 1 })
    stats.recipeCount = r.total
  } catch {}
  try {
    const i = await getIngredientList({ page: 1, pageSize: 1 })
    stats.ingredientCount = i.total
  } catch {}
}

onMounted(() => {
  if (authStore.user) {
    form.nickname = authStore.user.nickname
    form.phone = authStore.user.phone
    form.avatar = authStore.user.avatar || ''
  }
  loadStats()
})
</script>

<style scoped>
.profile-page {
  max-width: 720px;
  margin: 0 auto;
  padding: 0 24px 40px;
}

/* Banner */
.profile-banner {
  background: linear-gradient(135deg, #e07b54 0%, #f5a67a 60%, #f5c842 100%);
  border-radius: var(--radius-lg);
  padding: 36px 32px;
  margin-bottom: 28px;
}

.banner-inner {
  display: flex;
  align-items: center;
  gap: 24px;
}

.avatar-wrap {
  position: relative;
  flex-shrink: 0;
}

.avatar-ring {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  border: 4px solid rgba(255,255,255,0.8);
  overflow: hidden;
  background: rgba(255,255,255,0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-img { width: 100%; height: 100%; object-fit: cover; }

.avatar-letter {
  font-size: 36px;
  font-weight: 700;
  color: #fff;
  font-family: var(--font-main);
}

.avatar-upload-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  transition: transform 0.2s;
}

.avatar-upload-btn:hover { transform: scale(1.1); }

.banner-info h2 {
  font-family: var(--font-main);
  font-size: 24px;
  color: #fff;
  margin: 0 0 6px;
}

.banner-info p {
  font-size: 14px;
  color: rgba(255,255,255,0.85);
  margin: 0;
}

/* Body */
.profile-body {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.profile-card {
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  padding: 24px;
}

.card-title {
  font-family: var(--font-main);
  font-size: 17px;
  color: var(--color-text);
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--color-border-light);
}

.save-btn {
  width: 100%;
  background: var(--color-primary);
  color: #fff;
  border: none;
  border-radius: var(--radius-sm);
  padding: 12px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 8px;
  transition: all 0.2s;
  font-family: var(--font-body);
}

.save-btn:hover:not(:disabled) {
  background: var(--color-primary-dark);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(224,123,84,0.3);
}

.save-btn:disabled { opacity: 0.6; cursor: not-allowed; }

/* Stats */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.stat-item {
  background: var(--color-bg);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-sm);
  padding: 20px 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}

.stat-item:hover {
  border-color: var(--color-primary-light);
  box-shadow: var(--shadow-sm);
  transform: translateY(-2px);
}

.stat-num {
  display: block;
  font-family: var(--font-main);
  font-size: 32px;
  color: var(--color-primary);
  font-weight: 700;
  line-height: 1;
  margin-bottom: 6px;
}

.stat-label {
  font-size: 12px;
  color: var(--color-text-secondary);
}

@media (max-width: 600px) {
  .banner-inner { flex-direction: column; text-align: center; }
  .stats-grid { grid-template-columns: repeat(3, 1fr); }
}
</style>
