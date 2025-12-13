<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { MapPin, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import { tripsApi } from '../services/api'
import { useAuth } from '@clerk/vue'
import TripSkeleton from './TripSkeleton.vue'

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
// เพิ่ม state สำหรับ Pagination
const currentPage = ref(0) // เริ่มหน้า 0 (Spring Boot ใช้ 0-based index)
const totalPages = ref(0)
const currentKeyword = ref('') // เพิ่ม state เก็บ keyword ปัจจุบัน

// แก้ไข fetchTrips ให้รับ pageNumber
const fetchTrips = async (keyword: string = '', page: number = 0) => {
  try {
    loading.value = true
    error.value = ''

    // Logic การจัดการ keyword:
    // ถ้ามีการส่ง keyword มาใหม่ (เช่น กดค้นหา) -> อัปเดต currentKeyword และ reset หน้าไปที่ 0
    // ถ้าเป็นการเปลี่ยนหน้า (keyword อาจจะว่างจากการเรียก changePage) -> ใช้ currentKeyword เดิม

    // กรณีเรียกจาก SearchBar (keyword มีค่า) หรือ Reset (keyword='')
    // เราจะรู้ได้ไงว่านี่คือการ "เปลี่ยนหน้า" หรือ "ค้นหาใหม่"?
    // ปกติ changePage เราจะเรียก fetchTrips โดยไม่ส่ง keyword (หรือส่ง currentKeyword)

    // เพื่อความชัวร์:
    // 1. ถ้า keyword !== currentKeyword.value แปลว่ามีการ Search ใหม่ -> Reset page = 0
    // 2. ถ้า keyword === currentKeyword.value แปลว่าอาจจะเปลี่ยนหน้า หรือ refresh -> ใช้ page ที่ส่งมา

    // ถ้า keyword ไม่ส่งมา ให้ใช้ keyword ล่าสุด
    // แต่ถ้า keyword ส่งมาเป็น '' (จาก search bar ที่ว่างเปล่า) เราก็ต้องรับค่า '' นั้น

    // Logic ที่อธิบายไปก่อนหน้านี้:
    if (keyword !== currentKeyword.value) {
      currentKeyword.value = keyword
      page = 0
    }
    currentPage.value = page

    const token = await getToken.value()

    // ใน fetchTrips
    // เช็คว่า keyword ถูกส่งมาจริงๆ (ลอง console.log ดู)
    console.log('Searching for:', keyword);
    const response = await tripsApi.getAll(keyword, token, page, 4)

    console.log('API Response:', response)

    const content = Array.isArray(response) ? response : (response.content || [])

    if (!Array.isArray(response) && response.totalPages !== undefined) {
      totalPages.value = response.totalPages
    } else {
      // Fallback ถ้าไม่มี pagination info
      totalPages.value = content.length > 0 ? 1 : 0
    }

    trips.value = content.map((item: any) => ({
      id: String(item.id || item.eid || ''),
      title: item.title || '',
      description: item.shortDescription || item.description || '',
      duration: `${Math.floor(Math.random() * 5) + 3} Days`,
      location: item.province || item.tags?.[0] || 'Thailand',
      image: item.coverImage || item.photos?.[0] || '',
      photos: item.photos || [item.coverImage || ''],
      tags: item.tags || [],
      url: item.url || '#'
    }))

  } catch (err) {
    console.error('Error fetching trips:', err)
    error.value = 'Failed to load trips'
  } finally {
    loading.value = false
  }
}

// ฟังก์ชันเปลี่ยนหน้า
const changePage = (newPage: number) => {
  if (newPage >= 0 && newPage < totalPages.value) {
    // ส่ง keyword เดิมไป เพื่อให้ผลลัพธ์การค้นหายังอยู่
    fetchTrips(currentKeyword.value, newPage)
    document.getElementById('trips-header')?.scrollIntoView({ behavior: 'smooth' })
  }
}

watch(isSignedIn, () => {
  fetchTrips()
})

onMounted(() => {
  fetchTrips()
})

defineExpose({ fetchTrips })

// ฟังก์ชันจัดการเมื่อโหลดรูปไม่สำเร็จ
const handleImageError = (e: Event) => {
  const target = e.target as HTMLImageElement
  // ซ่อนรูปที่เสีย
  target.style.display = 'none'
  // แสดง parent div ที่มี background หรือ icon แทน (จัดการใน template)
  target.parentElement?.classList.add('bg-muted', 'flex', 'items-center', 'justify-center')

  // สร้าง icon element ใส่เข้าไปแทน (หรือจะใช้ v-if ใน template ก็ได้ แต่วิธีนี้ง่ายกับ v-for)
  const icon = document.createElement('div')
  icon.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-muted-foreground w-6 h-6"><line x1="2" y1="2" x2="22" y2="22"></line><path d="M10.41 10.41a2 2 0 1 1-2.83-2.83"></path><line x1="13.5" y1="13.5" x2="6" y2="21"></line><line x1="18" y1="12" x2="21" y2="15"></line><path d="M21 15l-2.71-2.71a1 1 0 0 0-1.42 0l-5.06 5.06"></path><path d="M15.12 3.82l-1.25-1.25a2 2 0 0 0-2.83 0l-1.25 1.25"></path></svg>'
  target.parentElement?.appendChild(icon)
}
</script>

<template>
  <div class="w-full max-w-[1500px] mx-auto px-4 pb-20">
    <div id="trips-header" class="flex items-center justify-between mb-8">
      <h2 class="text-xl sm:text-3xl font-bold text-foreground">ค้นหาที่เที่ยวที่สนใจ</h2>

      <!-- Pagination Controls (แทน View All) -->
      <div class="flex items-center gap-2 self-end" v-if="totalPages > 1">
        <button @click="changePage(currentPage - 1)" :disabled="currentPage === 0 || loading"
          class="flex items-center justify-center p-2 rounded-full hover:bg-muted transition-colors disabled:opacity-50 disabled:cursor-not-allowed "
          aria-label="Previous Page">
          <ChevronLeft class="w-5 h-5" />
        </button>

        <span class="text-sm font-medium text-muted-foreground whitespace-nowrap">
          {{ currentPage + 1 }} / {{ totalPages }}
        </span>

        <button @click="changePage(currentPage + 1)" :disabled="currentPage >= totalPages - 1 || loading"
          class="p-2 rounded-full hover:bg-muted transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          aria-label="Next Page">
          <ChevronRight class="w-5 h-5" />
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="grid grid-cols-1 xl:grid-cols-2 gap-6">
      <TripSkeleton v-for="i in 4" :key="i" />
    </div>

    <!-- No trips found -->
    <div v-else-if="!loading && trips.length === 0"
      class="flex flex-col items-center justify-center py-20 text-center space-y-4">
      <div class="w-16 h-16 bg-muted rounded-full flex items-center justify-center mb-2">
        <MapPin class="w-8 h-8 text-muted-foreground opacity-50" />
      </div>
      <h3 class="text-xl font-semibold text-foreground">No trips have been added yet</h3>
      <p class="text-muted-foreground max-w-sm">
        Be the first to share your favorite spot!
      </p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="text-center py-12 text-destructive">
      {{ error }}
    </div>

    <!-- Data State -->
    <div v-else class="grid grid-cols-1 xl:grid-cols-2 gap-6"> <!-- EDIT: ปรับเป็น Grid 2 คอลัมน์ -->
      <div v-for="trip in trips" :key="trip.id"
        class="h-full group relative bg-gradient-to-r from-accent via-sky-50 to-accent bg-300-percent animate-gradient rounded-2xl hover:shadow-md transition-all duration-300 border border-border/50 flex flex-col sm:flex-row">

        <!-- Image Section -->
        <div class="sm:w-[350px] shrink-0 p-3">
          <router-link :to="`/trips/${trip.id}`"
            class="relative h-[200px] sm:h-full rounded-2xl overflow-hidden group-hover:shadow-sm transition-all bg-muted block">
            <!-- เพิ่ม bg-muted รองรับตอนไม่มีรูป -->
            <img :src="trip.photos[0]" :alt="trip.title"
              class="w-full h-full object-cover transition-transform duration-700 group-hover:scale-105"
              @error="handleImageError" /> <!-- เพิ่ม @error -->
          </router-link>
        </div>

        <!-- Content -->
        <div class="flex-1 p-4 flex flex-col justify-between min-w-0">
          <div>
            <a :href="`/trips/${trip.id}`" class="block mb-2">
              <h3
                class="text-xl font-bold text-card-foreground group-hover:text-primary transition-colors line-clamp-2">
                {{ trip.title }}</h3>
            </a>

            <div class="flex flex-wrap items-center gap-x-4 gap-y-2 text-muted-foreground text-xs mb-3">
              <div class="flex items-center gap-1">
                <MapPin class="w-3 h-3" />
                <span>{{ trip.location }}</span>
              </div>
            </div>

            <p class="text-muted-foreground text-sm line-clamp-2 mb-3">{{ trip.description }}</p>

            <!-- เพิ่ม Tag ตรงนี้แทน -->
            <div class="flex flex-wrap gap-2 mb-4">
              <span v-for="(tag, idx) in trip.tags.slice(0, 3)" :key="idx"
                class="text-xs font-medium px-2.5 py-0.5 rounded-full bg-secondary text-secondary-foreground">
                {{ tag }}
              </span>
              <span v-if="trip.tags.length > 3" class="text-xs text-muted-foreground flex items-center">
                +{{ trip.tags.length - 3 }}
              </span>
            </div>

            <!-- Footer with Small Photos -->
            <div class="flex items-center justify-between pt-3 mt-auto border-t border-border/50 overflow-hidden">
              <div class="flex gap-2 overflow-hidden"> <!-- เพิ่ม overflow-x-auto ให้เลื่อนได้ถ้าล้น หรือจะซ่อนก็ได้ -->
                <!-- แสดงรูปเล็ก -->
                <!-- ใช้ class hidden และ block เพื่อกำหนดการแสดงผลตามขนาดหน้าจอ -->
                <!-- รูปแรก (index 0 ของ slice) แสดงตลอด -->
                <!-- รูปถัดๆ ไป ซ่อนในจอเล็ก แสดงในจอใหญ่ -->
                <div v-for="(photo, idx) in trip.photos.slice(1, 4)" :key="idx" :class="[
                  'w-15 h-15 rounded-md overflow-hidden border border-border/50 shrink-0 bg-muted relative',
                  idx > 0 ? 'hidden sm:block' : '' // EDIT: ถ้าเป็นรูปที่ 2,3 ของแถวเล็ก ให้ซ่อนในมือถือ (sm ขึ้นไปถึงแสดง)
                ]">
                  <img :src="photo" :alt="trip.title"
                    class="w-full h-full object-cover hover:scale-110 transition-transform duration-500"
                    @error="handleImageError" />
                </div>
              </div>

              <router-link :to="`/trips/${trip.id}`"
                class="shrink-0 px-4 py-2 rounded-full bg-primary text-primary-foreground text-sm font-medium hover:bg-primary/90 transition-colors whitespace-nowrap ml-2 shadow-sm">
                View Detail
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.w-15 {
  width: 3.75rem;
  /* 60px */
}

.h-15 {
  height: 3.75rem;
  /* 60px */
}

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