export interface User {
  id: number
  phone: string
  nickname: string
  avatar?: string
  createdAt: string
  updatedAt: string
}

export interface LoginRequest {
  phone: string
  password: string
  smsCode: string
}

export interface RegisterRequest {
  phone: string
  password: string
  nickname: string
  smsCode: string
}

export interface LoginResponse {
  token: string
  user: User
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface SmsRequest {
  phone: string
}

export interface SmsVerifyRequest {
  phone: string
  code: string
}
