import request from '@/utils/request'
import type { LoginRequest, RegisterRequest, LoginResponse, User, ApiResponse, SmsRequest, SmsVerifyRequest } from '@/types/auth'

export const login = (data: LoginRequest) => {
  return request.post<ApiResponse<LoginResponse>>('/auth/login', data)
}

export const register = (data: RegisterRequest) => {
  return request.post<ApiResponse<User>>('/auth/register', data)
}

export const getCurrentUser = () => {
  return request.get<ApiResponse<User>>('/auth/me')
}

export const updateProfile = (data: Partial<User>) => {
  return request.put<ApiResponse<User>>('/auth/profile', data)
}

export const resetPassword = (phone: string) => {
  return request.post<ApiResponse<null>>('/auth/reset-password', { phone })
}

export const sendSms = (data: SmsRequest) => {
  return request.post<ApiResponse<null>>('/auth/send-sms', data)
}

export const verifySms = (data: SmsVerifyRequest) => {
  return request.post<ApiResponse<null>>('/auth/verify-sms', data)
}
