<template>
  <div class="test-page">
    <h1>测试发送验证码</h1>
    <el-form :model="form" :rules="rules" ref="formRef">
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="testSendSms" :loading="loading">
          发送验证码
        </el-button>
      </el-form-item>
    </el-form>
    <div class="result">
      <h3>测试结果：</h3>
      <p>{{ result }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { sendSms } from '@/api/auth'

const formRef = ref<FormInstance>()
const loading = ref(false)
const result = ref('')

const form = reactive({
  phone: '13800138000'
})

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ]
}

const testSendSms = async () => {
  console.log('testSendSms called')
  result.value = '开始验证手机号...'
  console.log('form.phone:', form.phone)
  
  // 直接验证手机号格式
  const phoneRegex = /^1[3-9]\d{9}$/
  const validateResult = phoneRegex.test(form.phone)
  console.log('validateResult:', validateResult)
  
  if (validateResult) {
    result.value = '手机号验证通过，开始发送验证码...'
    loading.value = true
    console.log('before sendSms')
    try {
      await sendSms({ phone: form.phone })
      result.value = '发送验证码成功！'
      ElMessage.success('验证码已发送')
    } catch (error) {
      console.error('sendSms error:', error)
      result.value = `发送失败: ${error}`
      ElMessage.error('发送验证码失败，请稍后重试')
    } finally {
      loading.value = false
      console.log('sendSms finally')
    }
  } else {
    result.value = '手机号格式错误'
  }
}
</script>

<style scoped>
.test-page {
  max-width: 600px;
  margin: 50px auto;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.result {
  margin-top: 20px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}
</style>