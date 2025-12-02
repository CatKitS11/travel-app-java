<script setup lang="ts">
import { useUser } from '@clerk/vue'
import { ref, computed, onMounted, onUnmounted } from 'vue'

const { user } = useUser()

// Map ข้อมูลจาก Clerk User Object
const profile = computed(() => {
  if (!user.value) return null
  return {
    firstName: user.value.firstName,
    lastName: user.value.lastName,
    email: user.value.primaryEmailAddress?.emailAddress,
    imageUrl: user.value.imageUrl,
    username: user.value.username || 'No username set',
    lastActiveAt: user.value.lastActiveAt
  }
})

// State สำหรับ Edit Mode (Coming Soon)
const isEditing = ref(false)

onMounted(() => {
  const appContainer = document.querySelector('.overflow-y-auto')
  if (appContainer) {
    (appContainer as HTMLElement).style.scrollBehavior = 'auto'
    appContainer.classList.remove('snap-y', 'snap-mandatory')
    appContainer.scrollTop = 0
  }
})

onUnmounted(() => {
  const appContainer = document.querySelector('.h-screen.overflow-y-auto')
  if (appContainer) {
    appContainer.classList.add('snap-y', 'snap-mandatory')
  }
})

</script>

<template>
  <div class="max-w-4xl mx-auto px-4 py-8">
    <div v-if="profile" class="bg-card rounded-3xl shadow-sm border border-border overflow-hidden">
      
      <!-- Cover / Header Background -->
      <div class="h-32 bg-gradient-to-r from-primary/20 to-secondary/20 relative"></div>

      <!-- Profile Content -->
      <div class="px-8 pb-8">
        
        <!-- Avatar Section -->
        <div class="relative -mt-16 mb-6 flex justify-between items-end">
          <div class="flex items-end gap-6">
            <img 
              :src="profile.imageUrl" 
              alt="Profile" 
              class="w-32 h-32 rounded-full border-4 border-card shadow-md object-cover bg-muted"
            />
            <div class="mb-2">
              <h1 class="text-3xl font-bold">{{ profile.firstName }} {{ profile.lastName }}</h1>
              <p class="text-muted-foreground">{{ profile.email }}</p>
            </div>
          </div>
          
          <!-- Edit Button -->
          <button 
            @click="isEditing = !isEditing"
            class="px-4 py-2 bg-primary text-primary-foreground rounded-xl font-medium hover:bg-primary/90 transition-colors mb-2"
          >
            {{ isEditing ? 'Cancel Edit' : 'Edit Profile' }}
          </button>
        </div>

        <!-- Details Section -->
        <div class="space-y-8">
          
          <!-- Basic Info -->
          <div>
            <h2 class="text-xl font-semibold mb-4 flex items-center gap-2">
              <span>Personal Information</span>
            </h2>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="bg-muted/30 p-4 rounded-xl border border-border/50">
                <label class="text-sm text-muted-foreground block mb-1">First Name</label>
                <div class="font-medium text-lg">{{ profile.firstName }}</div>
              </div>
              <div class="bg-muted/30 p-4 rounded-xl border border-border/50">
                <label class="text-sm text-muted-foreground block mb-1">Last Name</label>
                <div class="font-medium text-lg">{{ profile.lastName }}</div>
              </div>
              <div class="bg-muted/30 p-4 rounded-xl border border-border/50">
                <label class="text-sm text-muted-foreground block mb-1">Email Address</label>
                <div class="font-medium text-lg">{{ profile.email }}</div>
              </div>
              <div class="bg-muted/30 p-4 rounded-xl border border-border/50">
                <label class="text-sm text-muted-foreground block mb-1">Username</label>
                <div class="font-medium text-lg">{{ profile.username }}</div>
              </div>
            </div>
          </div>

          <!-- Coming Soon Overlay for Edit Mode -->
          <div v-if="isEditing" class="mt-8 p-8 border-2 border-dashed border-primary/30 rounded-2xl bg-primary/5 text-center animate-in fade-in slide-in-from-bottom-4">
            <div class="text-4xl mb-4">🚧</div>
            <h3 class="text-2xl font-bold mb-2">Edit Profile Coming Soon!</h3>
            <p class="text-muted-foreground max-w-md mx-auto">
              We are working hard to allow you to update your profile information directly from here. Stay tuned!
            </p>
          </div>

        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-else class="text-center py-20 text-muted-foreground">
      Loading profile...
    </div>
  </div>
</template>
