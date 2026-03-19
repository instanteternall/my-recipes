import request from '@/utils/request'
import type { Ingredient, IngredientCategory, CreateIngredientRequest, IngredientListQuery, ApiResponse } from '@/types/ingredients'

export const getIngredientList = (params?: IngredientListQuery) => {
  return request.get<ApiResponse<{ list: Ingredient[]; total: number }>>('/ingredients', { params })
}

export const getIngredientById = (id: number) => {
  return request.get<ApiResponse<Ingredient>>(`/ingredients/${id}`)
}

export const createIngredient = (data: CreateIngredientRequest) => {
  return request.post<ApiResponse<Ingredient>>('/ingredients', data)
}

export const updateIngredient = (id: number, data: CreateIngredientRequest) => {
  return request.put<ApiResponse<Ingredient>>(`/ingredients/${id}`, data)
}

export const deleteIngredient = (id: number) => {
  return request.delete<ApiResponse<null>>(`/ingredients/${id}`)
}

export const getIngredientCategories = () => {
  return request.get<ApiResponse<IngredientCategory[]>>('/ingredients/categories')
}

export const searchIngredients = (keyword: string) => {
  return request.get<ApiResponse<Ingredient[]>>('/search/ingredients', { params: { keyword } })
}
