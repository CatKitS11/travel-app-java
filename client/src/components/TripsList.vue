<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useClerk, useSession, SignInButton, SignUpButton, UserButton } from '@clerk/vue'
import { tripsApi } from '../services/api'

const { isSignedIn, user, signOut } = useClerk()
const { session } = useSession()
const trips = ref<any[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

// ดึง token จาก session
const getToken = async () => {
  if (session.value) {
    return await session.value.getToken()
  }
  return null
}

// Load trips
const loadTrips = async () => {
  loading.value = true
  error.value = null
  try {
    const token = await getToken()
    const data = await tripsApi.getAll(0, 12, token)
    trips.value = data.content || []
  } catch (err: any) {
    console.error('Failed to load trips:', err)
    error.value = err.message || 'Failed to load trips'
  } finally {
    loading.value = false
  }
}

// Handle logout
const handleSignOut = async () => {
  await signOut()
  trips.value = []
}

onMounted(() => {
  loadTrips()
})
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navigation Bar -->
    <nav class="bg-white shadow-sm border-b">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <h1 class="text-2xl font-bold text-gray-900">Travel Explorer</h1>
          
          <div class="flex items-center gap-4">
            <template v-if="!isSignedIn">
              <SignInButton mode="modal">
                <button class="px-4 py-2 text-sm font-medium text-gray-700 hover:text-gray-900">
                  Login
                </button>
              </SignInButton>
              <SignUpButton mode="modal">
                <button class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700">
                  Register
                </button>
              </SignUpButton>
            </template>
            <template v-else>
              <span class="text-sm text-gray-700">
                Hello, {{ user?.primaryEmailAddress?.emailAddress }}
              </span>
              <button 
                @click="handleSignOut"
                class="px-4 py-2 text-sm font-medium text-gray-700 hover:text-gray-900"
              >
                Logout
              </button>
              <UserButton />
            </template>
          </div>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Error Message -->
      <div v-if="error" class="mb-4 p-4 bg-red-50 border border-red-200 rounded-md">
        <p class="text-sm text-red-800">{{ error }}</p>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="text-center py-12">
        <p class="text-gray-500">Loading trips...</p>
      </div>

      <!-- Empty State -->
      <div v-else-if="trips.length === 0" class="text-center py-12">
        <p class="text-gray-500 text-lg">
          No trips have been added yet. Be the first to share your favorite spot!
        </p>
      </div>

      <!-- Trips Grid -->
      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div 
          v-for="trip in trips" 
          :key="trip.id" 
          class="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow"
        >
          <!-- Cover Image -->
          <div class="h-48 bg-gray-200 overflow-hidden">
            <img 
              v-if="trip.coverImage" 
              :src="trip.coverImage" 
              :alt="trip.title"
              class="w-full h-full object-cover"
            />
            <div v-else class="w-full h-full flex items-center justify-center text-gray-400">
              No Image
            </div>
          </div>
          
          <!-- Content -->
          <div class="p-4">
            <h3 class="text-lg font-semibold text-gray-900 mb-2 line-clamp-2">
              {{ trip.title }}
            </h3>
            <p v-if="trip.province" class="text-sm text-blue-600 mb-2">
              {{ trip.province }}
            </p>
            <p class="text-sm text-gray-600 mb-4 line-clamp-3">
              {{ trip.shortDescription }}
            </p>
            <button class="w-full px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 transition-colors">
              View Detail
            </button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>