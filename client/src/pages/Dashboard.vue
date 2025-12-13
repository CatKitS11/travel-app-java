<script setup lang="ts">
import { ref, onMounted, onUnmounted, watchEffect } from 'vue'
import { useRouter } from 'vue-router'
import { tripsApi } from '../services/api'
import { useAuth, useUser } from '@clerk/vue'
import { Plus, Edit, Trash2, MapPin, LayoutDashboard } from 'lucide-vue-next'
import TripFormModal from '../components/TripFormModal.vue'
import TripSkeleton from '../components/TripSkeleton.vue'

const router = useRouter()
const { isSignedIn, isLoaded } = useUser() // ดึงสถานะ user
const { getToken } = useAuth()

watchEffect(() => {
  if (isLoaded.value && !isSignedIn.value) {
    router.push('/') // ถ้าโหลดเสร็จแล้ว และไม่ได้ Login ให้ดีดกลับหน้าแรก
  }
})

interface Trip {
  id: number
  title: string
  province: string
  shortDescription: string
  coverImage: string | null
  updatedAt: string
}

const trips = ref<Trip[]>([])
const loading = ref(true)
const isModalOpen = ref(false)
const editTripId = ref<number | null>(null) // เก็บ ID ที่จะ Edit

const fetchMyTrips = async () => {
  loading.value = true
  try {
    const token = await getToken.value()
    if (!token) return
    const response = await tripsApi.getMyTrips(token)
    trips.value = response
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}

// Open Modal for Create
const openCreateModal = () => {
  editTripId.value = null
  isModalOpen.value = true
}

// Open Modal for Edit
const openEditModal = (id: number) => {
  editTripId.value = id
  isModalOpen.value = true
}

const handleModalSuccess = () => {
  fetchMyTrips() // Refresh list when success
}

const handleDelete = async (id: number) => {
  if (!confirm('Are you sure you want to delete this trip?')) return
  try {
    const token = await getToken.value()
    if (!token) return
    await tripsApi.delete(id, token)
    fetchMyTrips()
  } catch (err) {
    alert('Failed to delete trip')
  }
}

onMounted(() => {
  const appContainer = document.querySelector('.overflow-y-auto')
  if (appContainer) {
    (appContainer as HTMLElement).style.scrollBehavior = 'auto'
    appContainer.classList.remove('snap-y', 'snap-mandatory')
    appContainer.scrollTop = 0
  }
  fetchMyTrips()
})

onUnmounted(() => {
  const appContainer = document.querySelector('.h-screen.overflow-y-auto')
  if (appContainer) {
    appContainer.classList.add('snap-y', 'snap-mandatory')
  }
})
</script>

<template>
  <div class="w-full max-w-7xl mx-auto px-4 py-8 pb-20">
    <!-- Header -->
    <div class="flex  items-center justify-between mb-8 gap-4">
      <div>
        <h1 class="text-xl sm:text-3xl font-bold flex items-center gap-2 whitespace-nowrap">
          <LayoutDashboard class="w-6 sm:w-8 h-6 sm:h-8 text-primary" />
          My Dashboard
        </h1>
        <p class="sm:text-muted-foreground text-muted-foreground whitespace-nowrap text-xs sm:text-base sm:block mt-1">Manage your shared destinations</p>
      </div>
      <button @click="openCreateModal"
        class="bg-primary text-primary-foreground sm:px-6 px-2 sm:py-2.5 py-2 rounded-full font-medium hover:bg-primary/90 transition-all flex items-center gap-2 shadow-lg hover:shadow-primary/20">
        <Plus class="w-5 h-5" />
        <span class="hidden sm:block">Add Destination</span>
      </button>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <TripSkeleton v-for="i in 6" :key="i" />
    </div>

    <!-- Empty State -->
    <div v-else-if="trips.length === 0"
      class="text-center py-20 bg-muted/30 rounded-3xl border border-dashed border-border">
      <div class="w-16 h-16 bg-muted rounded-full flex items-center justify-center mx-auto mb-4">
        <Plus class="w-8 h-8 text-muted-foreground" />
      </div>
      <h3 class="text-xl font-semibold">No trips yet</h3>
      <p class="text-muted-foreground mb-6">Start sharing your favorite places with the world!</p>
      <button @click="openCreateModal" class="text-primary font-medium hover:underline">Create your first trip</button>
    </div>

    <!-- Grid Layout -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-for="trip in trips" :key="trip.id"
        class="group bg-card border border-border/50 rounded-2xl overflow-hidden hover:shadow-lg transition-all duration-300">
        <!-- Image -->
        <div class="h-48 bg-muted relative overflow-hidden">
          <img v-if="trip.coverImage" :src="trip.coverImage"
            class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500" />
          <div v-else class="w-full h-full flex items-center justify-center text-muted-foreground">No Image</div>

          <!-- Actions Overlay -->
          <div
            class="absolute inset-0 xl:bg-black/40 xl:opacity-0 opacity-100 group-hover:opacity-100 transition-opacity flex xl:items-center xl:justify-center justify-end items-start xl:gap-3 gap-2">
            <button @click="openEditModal(trip.id)"
              class="p-2 bg-white rounded-full text-black hover:bg-gray-100 transition-colors mt-2 xl:mt-0" title="Edit">
              <Edit class="w-5 h-5" />
            </button>
            <button @click="handleDelete(trip.id)"
              class="p-2 bg-red-500 rounded-full text-white hover:bg-red-600 transition-colors mt-2 mr-2 xl:mr-0 xl:mt-0" title="Delete">
              <Trash2 class="w-5 h-5" />
            </button>
          </div>
        </div>

        <!-- Content -->
        <div class="p-4 bg-gradient-to-r from-accent via-sky-50 to-accent bg-300-percent animate-gradient">
          <div class="flex items-start justify-between mb-2">
            <h3 class="font-bold text-lg line-clamp-1">{{ trip.title }}</h3>
          </div>
          <div class="flex items-center text-muted-foreground text-sm mb-3">
            <MapPin class="w-3 h-3 mr-1" />
            {{ trip.province || 'No location' }}
          </div>
          <p class="text-sm text-muted-foreground/80 line-clamp-2 mb-4 h-10">{{ trip.shortDescription }}</p>

          <div class="flex items-center justify-between text-xs text-muted-foreground border-t border-border/50 pt-3">
            <span>Updated: {{ new Date(trip.updatedAt).toLocaleDateString() }}</span>
            <router-link :to="`/trips/${trip.id}?from=dashboard`"
              class="inline-flex items-center shrink-0 mb-1 px-4 py-1 rounded-full bg-primary text-primary-foreground text-sm font-medium hover:bg-primary/90 transition-colors whitespace-nowrap ml-2 shadow-sm">
              <span class="text-base p-0.5">View Detail</span>
              <!-- <span class="text-2xl pr-1 pb-1">&rarr;</span> -->
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Component -->
    <TripFormModal :is-open="isModalOpen" :edit-id="editTripId" @close="isModalOpen = false"
      @success="handleModalSuccess" />
  </div>
</template>

<style scoped>
.bg-300-percent {
  background-size: 300% auto !important;
}

.animate-gradient {
  animation: gradient 10s linear infinite;
}

@keyframes gradient {
  0% {
    background-position: 0% 50%;
  }

  50% {
    background-position: 100% 50%;
  }

  100% {
    background-position: 0% 50%;
  }
}
</style>