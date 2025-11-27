<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { tripsApi } from '../services/api'
import { useAuth } from '@clerk/vue'
import { Plus, Edit, Trash2, MapPin, X, Loader2, LayoutDashboard } from 'lucide-vue-next'

const { getToken } = useAuth()

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
const error = ref('')
const isModalOpen = ref(false)
const isSubmitting = ref(false)
const isEditing = ref(false)

// Form Data
const formData = ref({
  id: null as number | null,
  title: '',
  description: '',
  photos: [] as string[],
  tags: [] as string[],
  latitude: 13.7563,
  longitude: 100.5018,
  tempPhoto: '',
  tempTag: ''
})

const fetchMyTrips = async () => {
  loading.value = true
  try {
    const token = await getToken.value()
    if (!token) return
    const response = await tripsApi.getMyTrips(token)
    trips.value = response
  } catch (err) {
    console.error(err)
    error.value = 'Failed to load your trips'
  } finally {
    loading.value = false
  }
}

const openCreateModal = () => {
  isEditing.value = false
  formData.value = {
    id: null,
    title: '',
    description: '',
    photos: [],
    tags: [],
    latitude: 13.7563,
    longitude: 100.5018,
    tempPhoto: '',
    tempTag: ''
  }
  isModalOpen.value = true
}

const openEditModal = async (tripId: number) => {
  isEditing.value = true
  isSubmitting.value = true // Show loading while fetching details
  try {
    const token = await getToken.value()
    if(!token) return
    
    // Fetch full details
    const trip = await tripsApi.getById(String(tripId), token)
    
    formData.value = {
      id: trip.id,
      title: trip.title,
      description: trip.description,
      photos: trip.photos || [],
      tags: trip.tags || [],
      latitude: trip.latitude || 13.7563,
      longitude: trip.longitude || 100.5018,
      tempPhoto: '',
      tempTag: ''
    }
    isModalOpen.value = true
  } catch (err) {
    alert('Failed to fetch trip details')
  } finally {
    isSubmitting.value = false
  }
}

const handleSubmit = async () => {
  isSubmitting.value = true
  try {
    const token = await getToken.value()
    if (!token) return

    const payload = {
      title: formData.value.title,
      description: formData.value.description,
      photos: formData.value.photos,
      tags: formData.value.tags,
      latitude: Number(formData.value.latitude),
      longitude: Number(formData.value.longitude)
    }

    if (isEditing.value && formData.value.id) {
      await tripsApi.update(formData.value.id, payload, token)
    } else {
      await tripsApi.create(payload, token)
    }

    isModalOpen.value = false
    fetchMyTrips() // Refresh list
  } catch (err) {
    alert('Failed to save trip. Please check your inputs.')
    console.error(err)
  } finally {
    isSubmitting.value = false
  }
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

// Helpers for array inputs
const addPhoto = () => {
  if (formData.value.tempPhoto) {
    formData.value.photos.push(formData.value.tempPhoto)
    formData.value.tempPhoto = ''
  }
}
const removePhoto = (idx: number) => formData.value.photos.splice(idx, 1)

const addTag = () => {
  if (formData.value.tempTag) {
    formData.value.tags.push(formData.value.tempTag)
    formData.value.tempTag = ''
  }
}
const removeTag = (idx: number) => formData.value.tags.splice(idx, 1)

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
    <div class="flex flex-col sm:flex-row items-center justify-between mb-8 gap-4">
      <div>
        <h1 class="text-3xl font-bold flex items-center gap-2">
           <LayoutDashboard class="w-8 h-8 text-primary" />
           My Dashboard
        </h1>
        <p class="text-muted-foreground mt-1">Manage your shared destinations</p>
      </div>
      <button @click="openCreateModal" class="bg-primary text-primary-foreground px-6 py-2.5 rounded-full font-medium hover:bg-primary/90 transition-all flex items-center gap-2 shadow-lg hover:shadow-primary/20">
        <Plus class="w-5 h-5" />
        Add Destination
      </button>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-center py-20">
      <Loader2 class="w-10 h-10 animate-spin mx-auto text-primary" />
      <p class="mt-4 text-muted-foreground">Loading your trips...</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="trips.length === 0" class="text-center py-20 bg-muted/30 rounded-3xl border border-dashed border-border">
      <div class="w-16 h-16 bg-muted rounded-full flex items-center justify-center mx-auto mb-4">
        <Plus class="w-8 h-8 text-muted-foreground" />
      </div>
      <h3 class="text-xl font-semibold">No trips yet</h3>
      <p class="text-muted-foreground mb-6">Start sharing your favorite places with the world!</p>
      <button @click="openCreateModal" class="text-primary font-medium hover:underline">Create your first trip</button>
    </div>

    <!-- Grid Layout -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-for="trip in trips" :key="trip.id" class="group bg-card border border-border/50 rounded-2xl overflow-hidden hover:shadow-lg transition-all duration-300">
        <!-- Image -->
        <div class="h-48 bg-muted relative overflow-hidden">
          <img v-if="trip.coverImage" :src="trip.coverImage" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500" />
          <div v-else class="w-full h-full flex items-center justify-center text-muted-foreground">No Image</div>
          
          <!-- Actions Overlay -->
          <div class="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center gap-3">
            <button @click="openEditModal(trip.id)" class="p-2 bg-white rounded-full text-black hover:bg-gray-100 transition-colors" title="Edit">
              <Edit class="w-5 h-5" />
            </button>
            <button @click="handleDelete(trip.id)" class="p-2 bg-red-500 rounded-full text-white hover:bg-red-600 transition-colors" title="Delete">
              <Trash2 class="w-5 h-5" />
            </button>
          </div>
        </div>

        <!-- Content -->
        <div class="p-4">
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
            <router-link :to="`/trips/${trip.id}`" class="text-primary hover:underline">View Detail &rarr;</router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Form -->
    <div v-if="isModalOpen" class="fixed inset-0 z-50 flex items-center justify-center px-4">
      <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="isModalOpen = false"></div>
      <div class="bg-card w-full max-w-2xl max-h-[90vh] overflow-y-auto rounded-3xl shadow-2xl relative animate-in fade-in zoom-in-95 duration-200">
        
        <div class="sticky top-0 bg-card z-10 px-6 py-4 border-b border-border flex items-center justify-between">
          <h2 class="text-xl font-bold">{{ isEditing ? 'Edit Destination' : 'Add New Destination' }}</h2>
          <button @click="isModalOpen = false" class="p-2 hover:bg-muted rounded-full transition-colors"><X class="w-5 h-5" /></button>
        </div>

        <div class="p-6 space-y-4">
          <!-- Title -->
          <div>
            <label class="block text-sm font-medium mb-1">Title</label>
            <input v-model="formData.title" type="text" class="w-full px-4 py-2 rounded-xl bg-muted/50 border border-border focus:ring-2 focus:ring-primary/20 outline-none" placeholder="Trip name..." />
          </div>

          <!-- Description -->
          <div>
            <label class="block text-sm font-medium mb-1">Description</label>
            <textarea v-model="formData.description" rows="4" class="w-full px-4 py-2 rounded-xl bg-muted/50 border border-border focus:ring-2 focus:ring-primary/20 outline-none" placeholder="Tell us about this place..."></textarea>
          </div>

          <!-- Photos -->
          <div>
            <label class="block text-sm font-medium mb-1">Photos (URLs)</label>
            <div class="flex gap-2 mb-2">
              <input v-model="formData.tempPhoto" type="text" class="flex-1 px-4 py-2 rounded-xl bg-muted/50 border border-border outline-none" placeholder="https://example.com/image.jpg" @keydown.enter.prevent="addPhoto" />
              <button @click.prevent="addPhoto" class="px-4 py-2 bg-secondary rounded-xl font-medium">Add</button>
            </div>
            <div class="space-y-2">
              <div v-for="(photo, idx) in formData.photos" :key="idx" class="flex items-center gap-2 bg-muted/30 p-2 rounded-lg group">
                <img :src="photo" class="w-10 h-10 rounded object-cover bg-muted" />
                <span class="text-xs truncate flex-1">{{ photo }}</span>
                <button @click="removePhoto(idx)" class="text-destructive hover:bg-destructive/10 p-1 rounded"><X class="w-4 h-4" /></button>
              </div>
            </div>
          </div>

          <!-- Tags -->
          <div>
            <label class="block text-sm font-medium mb-1">Tags (Include province here)</label>
            <div class="flex gap-2 mb-2">
              <input v-model="formData.tempTag" type="text" class="flex-1 px-4 py-2 rounded-xl bg-muted/50 border border-border outline-none" placeholder="e.g. Bangkok, Mountain" @keydown.enter.prevent="addTag" />
              <button @click.prevent="addTag" class="px-4 py-2 bg-secondary rounded-xl font-medium">Add</button>
            </div>
            <div class="flex flex-wrap gap-2">
              <span v-for="(tag, idx) in formData.tags" :key="idx" class="bg-primary/10 text-primary px-3 py-1 rounded-full text-sm flex items-center gap-1">
                #{{ tag }}
                <button @click="removeTag(idx)" class="hover:text-destructive"><X class="w-3 h-3" /></button>
              </span>
            </div>
          </div>

          <!-- Coordinates -->
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium mb-1">Latitude</label>
              <input v-model="formData.latitude" type="number" step="any" class="w-full px-4 py-2 rounded-xl bg-muted/50 border border-border outline-none" />
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">Longitude</label>
              <input v-model="formData.longitude" type="number" step="any" class="w-full px-4 py-2 rounded-xl bg-muted/50 border border-border outline-none" />
            </div>
          </div>

        </div>

        <div class="sticky bottom-0 bg-card px-6 py-4 border-t border-border flex justify-end gap-3">
          <button @click="isModalOpen = false" class="px-6 py-2 rounded-xl hover:bg-muted transition-colors font-medium">Cancel</button>
          <button @click="handleSubmit" :disabled="isSubmitting" class="px-6 py-2 bg-primary text-primary-foreground rounded-xl font-medium hover:bg-primary/90 transition-all flex items-center gap-2">
            <Loader2 v-if="isSubmitting" class="w-4 h-4 animate-spin" />
            {{ isEditing ? 'Save Changes' : 'Create Trip' }}
          </button>
        </div>

      </div>
    </div>
  </div>
</template>
