const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'https://travel.catkits.work'

export async function apiRequest(
  endpoint: string, 
  options: RequestInit = {},
  token?: string | null
) {
  // สร้าง headers object แบบ Record<string, string>
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string> || {}),
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
  getAll: async (_keyword: string = '') => {
    // Backend ยังไม่รองรับ keywords parameter ตอนนี้
    // ใช้ pagination params แทน
    const response = await apiRequest(`/api/trips?page=0&size=12`)
    // Backend ส่งกลับ PageResponse ที่มี content array
    return response.content || response
  },
  
  getById: async (id: string) => 
    apiRequest(`/api/trips/${id}`),
}