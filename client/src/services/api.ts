const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'

export async function apiRequest(
  endpoint: string, 
  options: RequestInit = {},
  token?: string | null
) {
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...options.headers,
  }
  
  // ถ้ามี token ให้เพิ่ม Authorization header
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }
  
  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers,
  })
  
  if (!response.ok) {
    const errorText = await response.text()
    throw new Error(`API Error: ${response.status} - ${errorText}`)
  }
  
  return response.json()
}

// Helper functions
export const tripsApi = {
  getAll: async (page = 0, size = 12, token?: string | null) => 
    apiRequest(`/api/trips?page=${page}&size=${size}`, {}, token),
  
  getById: async (id: number, token?: string | null) => 
    apiRequest(`/api/trips/${id}`, {}, token),
  
  create: async (data: any, token?: string | null) => 
    apiRequest('/api/trips/create', {
      method: 'POST',
      body: JSON.stringify(data),
    }, token),
}