<template>
  <div class="image-uploader">
    <el-upload
      class="uploader-area"
      action="/api/upload"
      :headers="uploadHeaders"
      :show-file-list="false"
      :before-upload="beforeUpload"
      :on-success="onSuccess"
      :on-error="onError"
      accept="image/jpeg,image/png,image/gif,image/webp"
      drag
    >
      <template v-if="modelValue">
        <img :src="modelValue" class="preview-img" alt="preview" />
        <div class="preview-mask">
          <span>点击或拖拽替换图片</span>
        </div>
      </template>
      <template v-else>
        <div class="upload-placeholder">
          <el-icon class="upload-icon"><UploadFilled /></el-icon>
          <p class="upload-text">点击或拖拽上传图片</p>
          <p class="upload-hint">支持 JPG / PNG / GIF / WebP，最大 5MB</p>
        </div>
      </template>
    </el-upload>
    <div v-if="uploading" class="uploading-tip">
      <el-icon class="is-loading"><Loading /></el-icon> 上传中...
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled, Loading } from '@element-plus/icons-vue'
import type { UploadRawFile } from 'element-plus'

const props = defineProps<{
  modelValue?: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', url: string): void
}>()

const uploading = ref(false)

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
})

const beforeUpload = (file: UploadRawFile) => {
  const allowed = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!allowed.includes(file.type)) {
    ElMessage.error('仅支持 JPG / PNG / GIF / WebP 格式')
    return false
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过 5MB')
    return false
  }
  uploading.value = true
  return true
}

const onSuccess = (response: any) => {
  uploading.value = false
  if (response.code === 200) {
    emit('update:modelValue', response.data)
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const onError = () => {
  uploading.value = false
  ElMessage.error('上传失败，请稍后重试')
}
</script>

<style scoped>
.image-uploader {
  width: 100%;
}

.uploader-area {
  width: 100%;
}

.uploader-area :deep(.el-upload) {
  width: 100%;
}

.uploader-area :deep(.el-upload-dragger) {
  width: 100%;
  height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: 0;
}

.preview-img {
  width: 100%;
  height: 160px;
  object-fit: cover;
  display: block;
}

.preview-mask {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
  color: #fff;
  font-size: 14px;
}

.uploader-area :deep(.el-upload-dragger:hover) .preview-mask {
  opacity: 1;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #909399;
}

.upload-icon {
  font-size: 40px;
  color: #c0c4cc;
}

.upload-text {
  font-size: 14px;
  color: #606266;
}

.upload-hint {
  font-size: 12px;
  color: #c0c4cc;
}

.uploading-tip {
  margin-top: 8px;
  font-size: 13px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
