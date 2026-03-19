import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/HomePage.vue')
  },
  {
    path: '/test',
    name: 'Test',
    component: () => import('@/views/TestPage.vue')
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/views/SearchPage.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/LoginPage.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/RegisterPage.vue')
  },
  {
    path: '/recipes',
    name: 'Recipes',
    component: () => import('@/views/recipes/RecipeListPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/recipes/create',
    name: 'RecipeCreate',
    component: () => import('@/views/recipes/RecipeCreatePage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/recipes/:id',
    name: 'RecipeDetail',
    component: () => import('@/views/recipes/RecipeDetailPage.vue')
  },
  {
    path: '/recipes/:id/edit',
    name: 'RecipeEdit',
    component: () => import('@/views/recipes/RecipeEditPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/ingredients',
    name: 'Ingredients',
    component: () => import('@/views/ingredients/IngredientListPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/ingredients/create',
    name: 'IngredientCreate',
    component: () => import('@/views/ingredients/IngredientCreatePage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/ingredients/:id/edit',
    name: 'IngredientEdit',
    component: () => import('@/views/ingredients/IngredientEditPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/profile/ProfilePage.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
