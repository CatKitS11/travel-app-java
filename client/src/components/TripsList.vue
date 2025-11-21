<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { MapPin, Star, Clock } from 'lucide-vue-next'
import { tripsApi } from '../services/api'

interface Trip {
  id: string
  title: string
  description: string
  price: number
  rating: number
  duration: string
  location: string
  image: string
  tags: string[]
  url: string
}

const trips = ref<Trip[]>([])
const loading = ref(true)
const error = ref('')

const fetchTrips = async (keyword: string = '') => {
  try {
    loading.value = true
    const response = await tripsApi.getAll(keyword)
    // Map API response to UI model
    trips.value = response.map((item: any) => ({
      id: item.eid,
      title: item.title,
      description: item.description,
      price: Math.floor(Math.random() * 1000) + 500, // Mock price
      rating: (Math.random() * 1.5 + 3.5).toFixed(1), // Mock rating 3.5-5.0
      duration: `${Math.floor(Math.random() * 5) + 3} Days`, // Mock duration
      location: item.tags[0] || 'Thailand', // Use first tag as location or default
      image: item.photos?.[0] || '',
      tags: item.tags || [],
      url: item.url
    }))
  } catch (err) {
    console.error(err)
    error.value = 'Failed to load trips'
  } finally {
    loading.value = false
  }
}

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
    <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
      <div v-for="i in 6" :key="i" class="h-96 rounded-3xl bg-muted/50 animate-pulse"></div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="text-center py-12 text-destructive">
      {{ error }}
    </div>

    <!-- Data State -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
      <div v-for="trip in trips" :key="trip.id" 
           class="group relative bg-card rounded-3xl overflow-hidden shadow-lg hover:shadow-2xl hover:shadow-primary/10 transition-all duration-500 hover:-translate-y-2 border border-border/50">
        
        <!-- Image -->
        <div class="relative h-64 overflow-hidden">
          <img :src="trip.image" :alt="trip.title" class="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110" />
          <div class="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent opacity-60"></div>
          
          <div class="absolute top-4 right-4 bg-white/20 backdrop-blur-md px-3 py-1 rounded-full text-white text-sm font-medium border border-white/20">
            ${{ trip.price }}
          </div>
        </div>

        <!-- Content -->
        <div class="p-6 space-y-4">
          <div class="flex justify-between items-start">
            <div>
              <a :href="trip.url" target="_blank" class="block">
                <h3 class="text-xl font-bold text-card-foreground group-hover:text-primary transition-colors line-clamp-1">{{ trip.title }}</h3>
              </a>
              <div class="flex items-center gap-1 text-muted-foreground text-sm mt-1">
                <MapPin class="w-4 h-4" />
                <span>{{ trip.location }}</span>
              </div>
            </div>
            <div class="flex items-center gap-1 bg-yellow-400/10 px-2 py-1 rounded-lg text-yellow-600 font-bold text-sm">
              <Star class="w-4 h-4 fill-current" />
              <span>{{ trip.rating }}</span>
            </div>
          </div>

          <p class="text-muted-foreground text-sm line-clamp-2">{{ trip.description }}</p>

          <div class="pt-4 border-t border-border flex items-center justify-between">
            <div class="flex items-center gap-2 text-sm text-muted-foreground">
              <Clock class="w-4 h-4" />
              <span>{{ trip.duration }}</span>
            </div>
            <a :href="trip.url" target="_blank" class="px-4 py-2 rounded-xl bg-primary text-primary-foreground text-sm font-medium hover:bg-primary/90 transition-colors">
              Book Now
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>