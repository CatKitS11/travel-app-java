<!-- client/src/components/UserDropdown.vue -->
<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useClerk, useUser } from '@clerk/vue'

const { user } = useUser()
const clerk = useClerk()

const isOpen = ref(false)
const dropdownRef = ref<HTMLElement | null>(null)

// ปิด dropdown เมื่อคลิกข้างนอก
const handleClickOutside = (event: MouseEvent) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target as Node)) {
    isOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

const toggleDropdown = () => {
  isOpen.value = !isOpen.value
}

const handleProfile = () => {
  isOpen.value = false
  // TODO: Navigate to profile page (เมื่อมี router)
  console.log('Navigate to profile')
}

const handleDashboard = () => {
  isOpen.value = false
  // TODO: Navigate to dashboard (เมื่อมี router)
  console.log('Navigate to dashboard')
}

const handleLogout = async () => {
  isOpen.value = false
  try {
    await clerk.value?.signOut()
    // Redirect to home
    window.location.href = '/'
  } catch (error) {
    console.error('Logout error:', error)
  }
}
</script>

<template>
  <div class="relative" ref="dropdownRef">
    <!-- User Button -->
    <button
      @click="toggleDropdown"
      class="flex items-center gap-2 px-3 py-2 rounded-lg hover:bg-gray-100 transition-colors"
    >
      <!-- Avatar -->
      <div class="w-8 h-8 rounded-full bg-blue-600 flex items-center justify-center text-white text-sm font-semibold">
        <img
          v-if="user?.imageUrl"
          :src="user.imageUrl"
          :alt="user.fullName || 'User'"
          class="w-full h-full rounded-full object-cover"
        />
        <span v-else>
          {{ user?.firstName?.charAt(0) || user?.primaryEmailAddress?.emailAddress?.charAt(0).toUpperCase() || 'U' }}
        </span>
      </div>
      
      <!-- Name -->
      <span class="text-sm font-medium text-gray-700 hidden md:block">
        {{ user?.fullName || user?.primaryEmailAddress?.emailAddress || 'User' }}
      </span>
      
      <!-- Dropdown Icon -->
      <svg
        class="w-4 h-4 text-gray-500 transition-transform"
        :class="{ 'rotate-180': isOpen }"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M19 9l-7 7-7-7"
        />
      </svg>
    </button>

    <!-- Dropdown Menu -->
    <div
      v-if="isOpen"
      class="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 py-1 z-50"
    >
      <!-- Profile -->
      <button
        @click="handleProfile"
        class="w-full px-4 py-2 text-left text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
        </svg>
        Profile
      </button>
      
      <!-- Dashboard -->
      <button
        @click="handleDashboard"
        class="w-full px-4 py-2 text-left text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
        </svg>
        Dashboard
      </button>
      
      <!-- Divider -->
      <div class="border-t border-gray-200 my-1"></div>
      
      <!-- Logout -->
      <button
        @click="handleLogout"
        class="w-full px-4 py-2 text-left text-sm text-red-600 hover:bg-red-50 flex items-center gap-2"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
        </svg>
        Logout
      </button>
    </div>
  </div>
</template>