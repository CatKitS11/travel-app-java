<script setup lang="ts">
import { useUser } from '@clerk/vue'
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { Loader2, Upload, Save } from 'lucide-vue-next'

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

// State สำหรับ Edit Mode
const isEditing = ref(false)
const isUpdating = ref(false)
const fileInput = ref<HTMLInputElement | null>(null)

// Form Data
const editForm = ref({
  firstName: '',
  lastName: '',
  username: ''
})

// เมื่อกด Edit ให้ดึงข้อมูลปัจจุบันมาใส่ฟอร์ม
watch(isEditing, (newVal) => {
  if (newVal && user.value) {
    editForm.value = {
      firstName: user.value.firstName || '',
      lastName: user.value.lastName || '',
      username: user.value.username || ''
    }
  }
})

const updateProfile = async () => {
  if (!user.value) return
  isUpdating.value = true
  
  try {
    await user.value.update({
      firstName: editForm.value.firstName,
      lastName: editForm.value.lastName,
      // username: editForm.value.username // ปิดไว้ก่อนถ้า Clerk ยังไม่ได้เปิดให้แก้
    })
    
    alert('Profile updated successfully!')
    isEditing.value = false
  } catch (err) {
    console.error('Update failed:', err)
    alert('Failed to update profile. Please try again.')
  } finally {
    isUpdating.value = false
  }
}

const handleImageUpdate = async (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files && target.files[0] && user.value) {
    isUpdating.value = true
    try {
      await user.value.setProfileImage({
        file: target.files[0]
      })
      alert('Profile image updated!')
    } catch (err) {
      console.error('Image update failed:', err)
      alert('Failed to update image.')
    } finally {
      isUpdating.value = false
      target.value = '' // Reset input
    }
  }
}

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
            <div class="relative group">
              <img 
                :src="profile.imageUrl" 
                alt="Profile" 
                class="w-32 h-32 rounded-full border-4 border-card shadow-md object-cover bg-muted"
              />
              <!-- Upload Overlay (Only in Edit Mode) -->
              <div v-if="isEditing" 
                   class="absolute inset-0 bg-black/40 rounded-full flex items-center justify-center cursor-pointer opacity-0 group-hover:opacity-100 transition-opacity"
                   @click="fileInput?.click()">
                   <Upload class="w-8 h-8 text-white" />
              </div>
              <input type="file" ref="fileInput" class="hidden" accept="image/*" @change="handleImageUpdate" />
            </div>
            
            <div class="mb-2">
              <h1 class="text-3xl font-bold">{{ profile.firstName }} {{ profile.lastName }}</h1>
              <p class="text-muted-foreground">{{ profile.email }}</p>
            </div>
          </div>
          
          <!-- Edit Button -->
          <button 
            @click="isEditing = !isEditing"
            class="px-4 py-2 rounded-xl font-medium transition-colors mb-2 border flex items-center gap-2"
            :class="isEditing ? 'bg-muted hover:bg-muted/80' : 'bg-primary text-primary-foreground hover:bg-primary/90'"
          >
            {{ isEditing ? 'Cancel' : 'Edit Profile' }}
          </button>
        </div>

        <!-- Details Section -->
        <div class="space-y-8">
          
          <!-- View Mode -->
          <div v-if="!isEditing">
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

          <!-- Edit Mode -->
          <div v-else class="space-y-6 animate-in fade-in slide-in-from-bottom-2">
            <h2 class="text-xl font-semibold flex items-center gap-2 text-primary">
              <span>Edit Personal Information</span>
            </h2>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
               <div>
                  <label class="block text-sm font-medium mb-1">First Name</label>
                  <input v-model="editForm.firstName" type="text" class="w-full px-4 py-2 rounded-xl bg-card border border-border focus:ring-2 focus:ring-primary/20 outline-none" />
               </div>
               <div>
                  <label class="block text-sm font-medium mb-1">Last Name</label>
                  <input v-model="editForm.lastName" type="text" class="w-full px-4 py-2 rounded-xl bg-card border border-border focus:ring-2 focus:ring-primary/20 outline-none" />
               </div>
               
               <div class="opacity-50 pointer-events-none">
                  <label class="block text-sm font-medium mb-1">Email (Cannot be changed)</label>
                  <input :value="profile.email" type="text" class="w-full px-4 py-2 rounded-xl bg-muted border border-border outline-none" disabled />
               </div>
            </div>

            <div class="flex justify-end pt-4 border-t border-border">
              <button 
                @click="updateProfile" 
                :disabled="isUpdating"
                class="px-6 py-2 bg-primary text-primary-foreground rounded-xl font-medium hover:bg-primary/90 transition-all flex items-center gap-2 shadow-sm"
              >
                <Loader2 v-if="isUpdating" class="w-4 h-4 animate-spin" />
                <Save v-else class="w-4 h-4" />
                Save Changes
              </button>
            </div>
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