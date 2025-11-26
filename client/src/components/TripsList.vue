<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { MapPin, Clock } from 'lucide-vue-next'
import { tripsApi } from '../services/api'
import { useAuth } from '@clerk/vue'

const { getToken, isSignedIn } = useAuth()

interface Trip {
  id: string
  title: string
  description: string
  duration: string
  location: string
  image: string
  photos: string[]
  tags: string[]
  url: string
}

const trips = ref<Trip[]>([])
const loading = ref(true)
const error = ref('')

const fetchTrips = async (keyword: string = '') => {
  try {
    loading.value = true
    error.value = ''
    
    const token = await getToken.value()
    const response = await tripsApi.getAll(keyword, token)
    const data = Array.isArray(response) ? response : (response.content || [])
    
    console.log('Mapped data:', data) // Debug log
    
    trips.value = data.map((item: any) => ({
      id: String(item.id || item.eid || ''), 
      title: item.title || '',
      description: item.shortDescription || item.description || '', 
      duration: `${Math.floor(Math.random() * 5) + 3} Days`, 
      location: item.province || item.tags?.[0] || 'Thailand', 
      image: item.coverImage || item.photos?.[0] || '', 
      photos: item.photos || [item.coverImage || ''], // EDIT: ใช้ array รูปภาพจริงที่มี ถ้าไม่มีก็ใช้ coverImage เป็นรูปเดียว
      tags: item.tags || [], 
      url: item.url || '#' 
    }))
    
    console.log('Trips after mapping:', trips.value) // Debug log
  } catch (err) {
    console.error('Error fetching trips:', err)
    error.value = 'Failed to load trips'
  } finally {
    loading.value = false
  }
}

watch(isSignedIn, () => {
  fetchTrips()
})

onMounted(() => {
  fetchTrips()
})

defineExpose({ fetchTrips })
</script>

<template>
  <div class="w-full max-w-7xl mx-auto px-4 pb-20">
    <div class="flex items-center justify-between mb-8">
      <h2 class="text-3xl font-bold text-foreground">Popular Destinations</h2>
      <button class="text-primary font-medium hover:underline">View All</button>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex flex-col gap-8">
      <div v-for="i in 3" :key="i" class="h-64 rounded-3xl bg-muted/50 animate-pulse"></div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="text-center py-12 text-destructive">
      {{ error }}
    </div>

    <!-- Data State -->
    <div v-else class="grid grid-cols-1 lg:grid-cols-2 gap-6"> <!-- EDIT: ปรับเป็น Grid 2 คอลัมน์ -->
      <div v-for="trip in trips" :key="trip.id" 
           class="group relative bg-card rounded-3xl shadow-sm hover:shadow-md transition-all duration-300 border border-border/50 flex flex-col sm:flex-row h-full">
        
        <!-- Image Section -->
        <div class="sm:w-[200px] shrink-0 p-3">
          <div class="relative h-[200px] sm:h-full rounded-2xl overflow-hidden group-hover:shadow-sm transition-all">
            <img :src="trip.photos[0]" :alt="trip.title" class="w-full h-full object-cover transition-transform duration-700 group-hover:scale-105" />
          </div>
        </div>

        <!-- Content -->
        <div class="flex-1 p-4 flex flex-col justify-between min-w-0">
          <div>
            <a :href="trip.url" target="_blank" class="block mb-2">
              <h3 class="text-xl font-bold text-card-foreground group-hover:text-primary transition-colors line-clamp-2">{{ trip.title }}</h3>
            </a>
            
            <div class="flex flex-wrap items-center gap-x-4 gap-y-2 text-muted-foreground text-xs mb-3">
              <div class="flex items-center gap-1">
                <MapPin class="w-3 h-3" />
                <span>{{ trip.location }}</span>
              </div>
              <div class="flex items-center gap-1">
                <Clock class="w-3 h-3" />
                <span>{{ trip.duration }}</span>
              </div>
            </div>

            <p class="text-muted-foreground text-sm line-clamp-2 mb-3">{{ trip.description }}</p>
          </div>

          <!-- Footer with Small Photos -->
          <div class="flex items-center justify-between pt-3 mt-auto border-t border-border/50">
            <div class="flex gap-2">
               <!-- แสดงรูปเล็ก 3 รูป (รูปที่ 2-4) -->
               <div v-for="(photo, idx) in trip.photos.slice(1, 4)" :key="idx" 
                    class="w-10 h-10 rounded-lg overflow-hidden border border-border/50 shrink-0">
                 <img :src="photo" :alt="trip.title" class="w-full h-full object-cover hover:scale-110 transition-transform duration-500" />
               </div>
            </div>
            
            <a :href="trip.url" target="_blank" class="px-4 py-2 rounded-full bg-primary text-primary-foreground text-sm font-medium hover:bg-primary/90 transition-colors whitespace-nowrap ml-2 shadow-sm">
              View more
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>