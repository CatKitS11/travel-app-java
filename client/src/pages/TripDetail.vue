<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { tripsApi } from '../services/api'
import { useAuth } from '@clerk/vue'
// EDIT: เพิ่ม import icons
import { MapPin, ExternalLink, MapPinOff } from 'lucide-vue-next'
// 1. เพิ่ม Import Component ใหม่
import ImageLightbox from '../components/ImageLightbox.vue'
import Alert from '../components/Alert.vue'

const route = useRoute()
const { getToken } = useAuth()
const tripId = ref(route.params.id as string)

// สร้าง Computed Property สำหรับลิงก์ Back
const backRoute = computed(() => {
  if (route.query.from === 'dashboard') {
    return '/dashboard'
  }
  return '/' // ค่า Default กลับไปหน้า Home
})

// สร้าง Computed Property สำหรับข้อความปุ่ม (Optional)
const backText = computed(() => {
  if (route.query.from === 'dashboard') {
    return 'Back to Dashboard'
  }
  return 'Back to Home'
})

// Interface ให้ตรงกับ TripsResponse จาก Backend
interface TripDetail {
    id: number
    title: string
    description: string
    photos: string[]
    tags: string[]
    latitude?: number
    longitude?: number
    author?: {
        displayName: string
    }
}

const trip = ref<TripDetail | null>(null)
const loading = ref(true)
const error = ref('')

// 2. เพิ่ม State สำหรับควบคุม Lightbox
const lightboxOpen = ref(false)
const lightboxIndex = ref(0)

const openLightbox = (index: number) => {
    lightboxIndex.value = index
    lightboxOpen.value = true
}

const fetchTripDetail = async () => {
    loading.value = true
    error.value = ''
    try {
        const token = await getToken.value()
        // เรียก API getById
        const response = await tripsApi.getById(tripId.value, token)
        trip.value = response
    } catch (err) {
        console.error('Error fetching trip:', err)
        error.value = 'Failed to load trip details. It might have been deleted or you do not have permission to view it.'
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    // 1. Logic เดิม: เลื่อน Scroll ไปบนสุด
    const appContainer = document.querySelector('.overflow-y-auto')
    if (appContainer) {
        (appContainer as HTMLElement).style.scrollBehavior = 'auto'
        appContainer.classList.remove('snap-y', 'snap-mandatory')
        appContainer.scrollTop = 0
    }

    // 2. เรียกข้อมูลจริง
    fetchTripDetail()
})

onUnmounted(() => {
    const appContainer = document.querySelector('.h-screen.overflow-y-auto')
    if (appContainer) {
        appContainer.classList.add('snap-y', 'snap-mandatory')
    }
})
</script>

<template>
    <div class="w-full max-w-7xl mx-auto px-4 py-0 sm:py-2 pb-20">
        <!-- Loading State -->
        <div v-if="loading" class="text-center py-20 flex flex-col items-center">
            <div class="animate-pulse flex flex-col items-center space-y-4 w-full">
                <div class="h-8 bg-muted rounded w-1/3"></div>
                <div class="h-[400px] bg-muted rounded-3xl w-full"></div>
                <div class="h-4 bg-muted rounded w-2/3"></div>
            </div>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="py-20 max-w-lg mx-auto">
            <Alert 
                variant="destructive" 
                title="Something went wrong" 
                :message="error" 
            />
            <div class="text-center mt-4">
                <button @click="fetchTripDetail" class="text-primary hover:underline">Try Again</button>
            </div>
        </div>

        <!-- Content State -->
        <div v-else-if="trip" class="space-y-8">
            <router-link :to="backRoute"
                class="inline-flex items-center shrink-0 mb-4 px-4 py-1 rounded-full bg-primary text-primary-foreground text-sm font-medium hover:bg-primary/90 transition-colors whitespace-nowrap ml-2 shadow-sm">
                <span class="hidden sm:block sm:text-2xl pr-2 pb-1">&larr;</span>
                <span class="text-sm sm:text-base">{{ backText }}</span>
            </router-link>

            <!-- Layout Grid: 2 Columns on Large Screens -->
            <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">

                <!-- Left Column (Images) - Span 2 cols -->
                <div class="lg:col-span-2 space-y-4">
                    <!-- Main Image -->
                    <div class="h-[300px] sm:h-[400px] lg:h-[500px] rounded-3xl overflow-hidden bg-muted relative shadow-sm border border-border/50 cursor-pointer group"
                        @click="openLightbox(0)"> <!-- เพิ่ม cursor-pointer และ click -->
                        <img v-if="trip.photos && trip.photos.length > 0" :src="trip.photos[0]" :alt="trip.title"
                            class="w-full h-full object-cover hover:scale-105 transition-transform duration-700" />
                        <!-- เพิ่ม icon แว่นขยาย เมื่อเอาเมาส์ชี้ -->
                        <div
                            class="absolute inset-0 bg-black/0 group-hover:bg-black/10 transition-colors flex items-center justify-center opacity-0 group-hover:opacity-100">
                            <span class="bg-black/50 text-white px-3 py-1 rounded-full text-sm font-medium">View
                                Fullscreen</span>
                        </div>
                        <div
                            class="w-full h-full flex items-center justify-center text-muted-foreground flex-col gap-2">
                            <div class="w-12 h-12 rounded-full bg-muted-foreground/20 flex items-center justify-center">
                                <span class="text-2xl">📷</span>
                            </div>
                            <span>No Image Available</span>
                        </div>
                    </div>

                    <!-- Image Gallery (Thumbnails) -->
                    <div v-if="trip.photos && trip.photos.length > 1" class="grid grid-cols-3 sm:grid-cols-4 gap-3">
                        <!-- แก้ไข v-for ให้รับ index เพื่อส่งไปเปิดรูปที่ถูกต้อง -->
                        <div v-for="(photo, index) in trip.photos.slice(1, 5)" :key="index"
                            class="aspect-square rounded-xl overflow-hidden bg-muted cursor-pointer border border-border/50 hover:ring-2 ring-primary/50 transition-all"
                            @click="openLightbox(index + 1)"> <!-- ส่ง index + 1 เพราะ slice เริ่มที่ 1 -->
                            <img :src="photo" :alt="trip.title + ' ' + (index + 2)"
                                class="w-full h-full object-cover hover:scale-110 transition-transform duration-500" />
                        </div>
                        <div v-if="trip.photos.length > 5"
                            class="aspect-square rounded-xl bg-muted flex items-center justify-center text-muted-foreground border border-border/50 font-medium">
                            +{{ trip.photos.length - 5 }}
                        </div>
                    </div>
                </div>

                <!-- Right Column (Info) - Span 1 col -->
                <div class="space-y-6">
                    <div>
                        <h1 class="text-3xl sm:text-4xl font-bold mb-3 leading-tight text-foreground">{{ trip.title }}
                        </h1>

                        <!-- Tags -->
                        <div class="flex flex-wrap gap-2 my-4">
                            <span v-for="tag in trip.tags" :key="tag"
                                class="bg-primary/10 text-primary px-3 py-1 rounded-full text-sm font-medium">
                                #{{ tag }}
                            </span>
                        </div>

                        <!-- Location Placeholder -->
                        <div class="flex items-center text-muted-foreground gap-2 mb-2">
                            <MapPin class="w-5 h-5 shrink-0" /> <!-- EDIT: ใช้ icon MapPin -->
                            <span class="text-lg">📍 {{ trip.tags[0] || 'Unknown Location' }}</span>
                        </div>
                    </div>

                    <div class="h-px bg-border"></div>

                    <!-- Description -->
                    <div class="prose prose-slate dark:prose-invert max-w-none">
                        <h3 class="text-xl font-semibold mb-3">About this place</h3>
                        <p class="text-base leading-relaxed text-muted-foreground whitespace-pre-line">
                            {{ trip.description }}
                        </p>
                    </div>

                    <!-- Author Info -->
                    <div v-if="trip.author"
                        class="bg-muted/30 p-4 rounded-xl flex items-center gap-3 border border-border/50">
                        <div
                            class="w-10 h-10 rounded-full bg-primary/20 flex items-center justify-center text-primary font-bold">
                            {{ trip.author.displayName?.[0]?.toUpperCase() || 'U' }}
                        </div>
                        <div>
                            <p class="text-sm text-muted-foreground">Posted by</p>
                            <p class="font-medium">{{ trip.author.displayName || 'Anonymous' }}</p>
                        </div>
                    </div>

                    <!-- CTA to Map Section -->
                    <div class="pt-4" v-if="trip.latitude && trip.longitude">
                        <a href="#map-section"
                            class="w-full block text-center bg-secondary hover:bg-secondary/80 text-secondary-foreground py-3 rounded-xl font-medium transition-colors">
                            Scroll to Map
                        </a>
                    </div>
                </div>
            </div>

            <!-- Map Section -->
            <div id="map-section" class="mt-12 border-t border-border">
                <h2 class="text-2xl font-bold my-5 flex items-center justify-start gap-2 py-[5px]">
                    Location
                </h2>

                <!-- Case 1: Have Coordinates -->
                <div v-if="trip.latitude && trip.longitude" class="space-y-4">
                    <!-- Map Embed Iframe -->
                    <div
                        class="bg-muted h-[450px] rounded-3xl overflow-hidden border border-border/50 shadow-sm relative">
                        <iframe width="100%" height="100%" frameborder="0" scrolling="no" marginheight="0"
                            marginwidth="0"
                            :src="`https://maps.google.com/maps?q=${trip.latitude},${trip.longitude}&z=15&output=embed`"
                            title="Google Map" class="w-full h-full"></iframe>
                    </div>

                    <!-- External Link Button -->
                    <div class="flex justify-end">
                        <a :href="`https://www.google.com/maps/search/?api=1&query=${trip.latitude},${trip.longitude}`"
                            target="_blank" rel="noopener noreferrer"
                            class="inline-flex items-center gap-2 px-4 py-2 rounded-lg hover:bg-muted text-primary transition-colors font-medium">
                            <span>View on Google Maps</span>
                            <ExternalLink class="w-4 h-4" />
                        </a>
                    </div>
                </div>

                <!-- Case 2: Missing Coordinates -->
                <div v-else
                    class="bg-muted/30 h-[200px] rounded-3xl flex flex-col items-center justify-center text-muted-foreground border border-border/50">
                    <MapPinOff class="w-10 h-10 mb-3 opacity-50" />
                    <p class="font-medium">Map information not available for this destination.</p>
                </div>
            </div>

        </div>

        <!-- Not Found State -->
        <div v-else class="text-center py-20 text-muted-foreground">
            Trip not found
        </div>

        <!-- วาง Lightbox ไว้ท้ายสุด ก่อนปิด div หลัก หรือ ก่อนปิด template ก็ได้ -->
        <ImageLightbox :is-open="lightboxOpen" :images="trip?.photos || []" :initial-index="lightboxIndex"
            @close="lightboxOpen = false" />
    </div> <!-- ปิด div หลัก -->
</template>
