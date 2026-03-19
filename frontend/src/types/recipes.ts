export interface Recipe {
  id: number
  userId: number
  name: string
  description?: string
  coverImage?: string
  cookingTime?: number
  difficulty?: string
  categoryId?: number
  categoryName?: string
  tags?: Tag[]
  ingredients?: RecipeIngredient[]
  steps?: CookingStep[]
  createdAt: string
  updatedAt: string
}

export interface RecipeCategory {
  id: number
  name: string
  description?: string
  createdAt: string
  updatedAt: string
}

export interface Tag {
  id: number
  name: string
  createdAt: string
  updatedAt: string
}

export interface RecipeIngredient {
  id?: number
  ingredientName: string
  quantity: number
  unit: string
}

export interface CookingStep {
  id?: number
  recipeId?: number
  stepOrder: number
  content: string
  image?: string
  createdAt?: string
  updatedAt?: string
}

export interface CreateRecipeRequest {
  name: string
  description?: string
  coverImage?: string
  cookingTime?: number
  difficulty?: string
  categoryId?: number
  tagIds?: number[]
  ingredients: RecipeIngredient[]
  steps: CookingStep[]
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface RecipeListQuery {
  page?: number
  pageSize?: number
  categoryId?: number
  tagId?: number
  keyword?: string
}
