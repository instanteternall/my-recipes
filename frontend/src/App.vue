<template>
  <el-config-provider :locale="zhCn">
    <div id="app-root">
      <template v-if="isAuthPage">
        <router-view />
      </template>
      <template v-else>
        <header class="app-header">
          <div class="header-inner">
            <div class="header-left">
              <router-link to="/" class="logo">
                <span class="logo-icon">🍜</span>
                <span class="logo-text">我的菜谱</span>
              </router-link>
              <nav class="main-nav">
                <router-link to="/" class="nav-link">首页</router-link>
                <router-link to="/recipes" class="nav-link">菜谱</router-link>
                <router-link to="/ingredients" class="nav-link">食材</router-link>
                <router-link to="/search" class="nav-link">搜索</router-link>
              </nav>
            </div>
            <div class="header-right">
              <template v-if="!authStore.isAuthenticated()">
                <button class="btn-outline" @click="router.push('/login')">登录</button>
                <button class="btn-primary" @click="router.push('/register')">注册</button>
              </template>
              <template v-else>
                <button class="btn-primary" @click="router.push('/recipes/create')">✏️ 写菜谱</button>
                <el-dropdown @command="handleCommand" trigger="click">
                  <div class="user-chip">
                    <div class="user-avatar">{{ authStore.user?.nickname?.charAt(0) }}</div>
                    <span class="user-name">{{ authStore.user?.nickname }}</span>
                    <span class="caret">▾</span>
                  </div>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="profile">👤 个人资料</el-dropdown-item>
                      <el-dropdown-item command="recipes">📖 我的菜谱</el-dropdown-item>
                      <el-dropdown-item command="ingredients">🥬 我的食材</el-dropdown-item>
                      <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </template>
            </div>
          </div>
        </header>

        <main class="app-main">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </main>

        <footer class="app-footer">
          <p>🍜 我的菜谱 · 记录每一道温暖的味道</p>
        </footer>
      </template>
    </div>
  </el-config-provider>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { useAuthStore } from '@/store/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const isAuthPage = computed(() => ['/login', '/register'].includes(route.path))

const handleCommand = (command: string) => {
  switch (command) {
    case 'profile': router.push('/profile'); break
    case 'recipes': router.push('/recipes'); break
    case 'ingredients': router.push('/ingredients'); break
    case 'logout':
      authStore.logout()
      router.push('/')
      break
  }
}
</script>

<style>
#app-root {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--color-bg);
}

.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(253, 248, 244, 0.96);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--color-border);
  height: 64px;
  display: flex;
  align-items: center;
}

.header-inner {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 36px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
}

.logo-icon { font-size: 24px; line-height: 1; }

.logo-text {
  font-family: var(--font-main);
  font-size: 20px;
  font-weight: 600;
  color: var(--color-primary);
  letter-spacing: 1px;
}

.main-nav { display: flex; gap: 4px; }

.nav-link {
  padding: 6px 14px;
  border-radius: 20px;
  color: var(--color-text-secondary);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}

.nav-link:hover,
.nav-link.router-link-active {
  color: var(--color-primary);
  background: var(--color-primary-lighter);
  font-weight: 600;
}

.header-right { display: flex; align-items: center; gap: 12px; }

.btn-primary {
  background: var(--color-primary);
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 8px 18px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  font-family: var(--font-body);
}

.btn-primary:hover {
  background: var(--color-primary-dark);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(224, 123, 84, 0.3);
}

.btn-outline {
  background: transparent;
  color: var(--color-primary);
  border: 1.5px solid var(--color-primary);
  border-radius: 20px;
  padding: 7px 18px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  font-family: var(--font-body);
}

.btn-outline:hover { background: var(--color-primary-lighter); }

.user-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 5px 12px 5px 6px;
  border-radius: 20px;
  border: 1.5px solid var(--color-border);
  background: var(--color-bg-card);
  transition: all 0.2s;
}

.user-chip:hover {
  border-color: var(--color-primary-light);
  box-shadow: var(--shadow-sm);
}

.user-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary), var(--color-accent));
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-name {
  font-size: 13px;
  color: var(--color-text);
  max-width: 72px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.caret { font-size: 11px; color: var(--color-text-muted); }

.app-main { flex: 1; padding: 28px 0; }

.app-footer {
  text-align: center;
  padding: 24px;
  color: var(--color-text-muted);
  font-size: 13px;
  border-top: 1px solid var(--color-border-light);
}
</style>
