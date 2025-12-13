<script setup lang="ts">
import Navbar from './components/Navbar.vue'
import { watch } from 'vue'
import { useAuth } from '@clerk/vue'
import { apiRequest } from './services/api'

const { isSignedIn, getToken } = useAuth()

// เฝ้าดูสถานะการ Login
watch(isSignedIn, async (newValue) => {
  if (newValue) {
    try {
      const token = await getToken.value()
      // ยิงไปบอก Server ว่า Login แล้วนะ (Server จะ Sync User ให้เอง)
      console.log('Syncing user with backend...')
      // ต้องใส่ method POST เพราะ Controller รับ @PostMapping
      await apiRequest('/api/auth/sync', { method: 'POST' }, token)
      console.log('User synced successfully!')
    } catch (err) {
      console.error('Failed to sync user:', err)
    }
  }
}, { immediate: true })
</script>

<template>
  <!-- 
    h-screen: สูงเต็มจอ
    overflow-y-auto: ให้ scroll แนวตั้งได้
    snap-y: (tailwind) scroll-snap-type: y var(...)
    snap-mandatory: (tailwind) scroll-snap-type: y mandatory
  -->
  <div class="h-screen overflow-y-auto snap-y snap-mandatory scroll-smooth relative">
    
    <!-- Navbar (ถ้าอยากให้ติดข้างบนตลอด ต้องอยู่นอก snap container หรือใช้ sticky ใน section แรก) -->
    <!-- แต่ถ้าอยากให้ Navbar เลื่อนไปกับ section แรก ก็ใส่ไว้ใน section แรกได้ -->
    <Navbar class="fixed top-0 left-0 right-0 z-50" /> 
    
    <main class="w-full pt-2 sm:pt-5"> <!-- เพิ่ม pt-20 เพราะ Navbar fixed -->
      <!-- <LandingPage />  <-- ลบออก -->
      <router-view /> <!-- ใส่ตัวนี้แทน -->
    </main>
    
    <!-- Footer อาจจะเป็น section สุดท้าย -->
    <footer class="snap-start min-h-[50vh] flex items-center justify-center bg-muted">
       <!-- ... footer content ... -->
    </footer>

  </div>
</template>

<style scoped>
/* Scoped styles if needed, but relying on utility classes */
</style>
