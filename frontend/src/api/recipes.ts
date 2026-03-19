import request from '@/utils/request'
import type { Recipe, RecipeCategory, Tag, CreateRecipeRequest, RecipeListQuery, ApiResponse } from '@/types/recipes'

export const getRecipeList = (params?: RecipeListQuery) => {
  return request.get<ApiResponse<{ list: Recipe[]; total: number }>>('/recipes', { params })
}

export const getRecipeById = (id: number) => {
  return request.get<ApiResponse<Recipe>>(`/recipes/${id}`)
}

export const createRecipe = (data: CreateRecipeRequest) => {
  return request.post<ApiResponse<Recipe>>('/recipes', data)
}

export const updateRecipe = (id: number, data: CreateRecipeRequest) => {
  return request.put<ApiResponse<Recipe>>(`/recipes/${id}`, data)
}

export const deleteRecipe = (id: number) => {
  return request.delete<ApiResponse<null>>(`/recipes/${id}`)
}

export const getRecipeCategories = () => {
  return request.get<ApiResponse<RecipeCategory[]>>('/recipes/categories')
}

export const getRecipeTags = () => {
  return request.get<ApiResponse<Tag[]>>('/recipes/tags')
}

export const searchRecipes = (keyword?: string, categoryId?: number, difficulty?: string) => {
  return request.get<ApiResponse<Recipe[]>>('/search/recipes', { params: { keyword, categoryId, difficulty } })
}

export const getHotRecipes = () => {
  return request.get<ApiResponse<Recipe[]>>('/recipes/hot')
}

export const getRecommendRecipes = () => {
  return request.get<ApiResponse<Recipe[]>>('/recommend/recipes')
}
