<!-- client/src/components/UserDropdown.vue -->
<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useClerk, useUser } from '@clerk/vue'
import { LogOut, User, LayoutDashboard } from 'lucide-vue-next' // EDIT: Import Icon

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

const handleLogout = async () => {
  isOpen.value = false
  try {
    await clerk.value?.signOut()
    window.location.href = '/'
  } catch (error) {
    console.error('Logout error:', error)
  }
}
</script>

<template>
  <div class="relative" ref="dropdownRef">
    <!-- Trigger Button -->
    <button @click="toggleDropdown" class="flex items-center gap-2 hover:bg-white/10 p-1 pr-3 rounded-full transition-all border border-transparent hover:border-white/10">
        <img :src="user?.imageUrl" class="w-8 h-8 rounded-full border-2 border-primary/20" />
        <span class="text-sm font-medium hidden sm:block max-w-[100px] truncate">{{ user?.fullName }}</span>
    </button>

    <!-- Dropdown Menu -->
    <div v-if="isOpen" class="absolute right-0 mt-2 w-48 bg-card rounded-xl shadow-xl border border-border py-1 z-50 animate-in fade-in zoom-in-95 duration-200 origin-top-right">
        
        <!-- User Info Header -->
        <div class="px-4 py-2 border-b border-border mb-1">
            <p class="text-sm font-bold truncate">{{ user?.fullName }}</p>
            <p class="text-xs text-muted-foreground truncate">{{ user?.primaryEmailAddress?.emailAddress }}</p>
        </div>

        <!-- Menu Items -->
        <router-link to="/dashboard" 
            class="w-full text-left px-4 py-2 text-sm hover:bg-muted transition-colors flex items-center gap-2 text-foreground"
            @click="isOpen = false">
             <LayoutDashboard class="w-4 h-4" />
             Dashboard
        </router-link>

        <button class="w-full text-left px-4 py-2 text-sm hover:bg-muted transition-colors flex items-center gap-2 text-foreground opacity-50 cursor-not-allowed">
             <User class="w-4 h-4" />
             Profile (Coming Soon)
        </button>
        
        <div class="h-px bg-border my-1"></div>

        <button @click="handleLogout" class="w-full text-left px-4 py-2 text-sm text-red-500 hover:bg-red-500/10 transition-colors flex items-center gap-2">
            <LogOut class="w-4 h-4" />
            Sign Out
        </button>
    </div>
  </div>
</template>