export interface Ingredient {
  id: number
  userId: number
  name: string
  quantity: number
  unit: string
  expiryDate?: string
  categoryId?: number
  categoryName?: string
  createdAt: string
  updatedAt: string
}

export interface IngredientCategory {
  id: number
  name: string
  description?: string
  createdAt: string
  updatedAt: string
}

export interface CreateIngredientRequest {
  name: string
  quantity: number
  unit: string
  expiryDate?: string
  categoryId?: number
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface IngredientListQuery {
  page?: number
  pageSize?: number
  categoryId?: number
  keyword?: string
}
