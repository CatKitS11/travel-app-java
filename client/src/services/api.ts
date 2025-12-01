const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

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
    console.log('Adding Authorization token to request')
    headers['Authorization'] = `Bearer ${token}`
  } else {
    console.log('No token provided for request')
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
  // แก้ให้รับ page และ limit (size) เพิ่ม
  getAll: async (keyword: string = '', token?: string | null, page: number = 0, limit: number = 4) => { 
    // keyword ต้องถูก encode และส่งไป
    const encodedKeyword = encodeURIComponent(keyword);
    const response = await apiRequest(`/api/trips?page=${page}&size=${limit}&keyword=${encodedKeyword}`, {}, token)
    
    // คืนค่า response ทั้งก้อน (เพื่อให้ได้ totalPages) ไม่ใช่แค่ content
    // ถ้า Backend return { content: [...], totalPages: 5 }
    return response 
  },
  
  getById: async (id: string, token?: string | null) => 
    apiRequest(`/api/trips/${id}`, {}, token),

  // เพิ่ม CRUD functions
  getMyTrips: async (token: string) => 
    apiRequest('/api/trips/mine', {}, token),

  create: async (data: any, token: string) => 
    apiRequest('/api/trips', {
      method: 'POST',
      body: JSON.stringify(data)
    }, token),

  update: async (id: number | string, data: any, token: string) => 
    apiRequest(`/api/trips/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data)
    }, token),

  delete: async (id: number | string, token: string) => 
    apiRequest(`/api/trips/${id}`, {
      method: 'DELETE'
    }, token),
}