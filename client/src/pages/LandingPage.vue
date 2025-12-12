<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import SearchBar from '../components/SearchBar.vue'
import TripsList from '../components/TripsList.vue'

const tripsListRef = ref<InstanceType<typeof TripsList> | null>(null)
const isSticky = ref(false)
// const scrollContainer = ref<HTMLElement | null>(null)

const handleSearch = (query: string) => {
  tripsListRef.value?.fetchTrips(query)
}

const handleFocus = () => {
  isSticky.value = true
  // เลื่อนหน้าจอไปที่ Section 2
  const section2 = document.getElementById('trips-section')
  section2?.scrollIntoView({ behavior: 'smooth' })
}

const handleScroll = (e: Event) => {
  const target = e.target as HTMLElement
  // ถ้า scroll ลงมาเกิน 30% ของความสูงจอ ให้ SearchBar ลอย
  if (target.scrollTop > window.innerHeight * 0.3) {
    isSticky.value = true
  } else {
    isSticky.value = false
  }
}

onMounted(() => {
  // หาตัว container ที่มี scroll (ในที่นี้คือ div ใน App.vue ที่มี class overflow-y-auto)
  const scroller = document.querySelector('.overflow-y-auto')
  if (scroller) {
    scroller.addEventListener('scroll', handleScroll)
  }
})

onUnmounted(() => {
  const scroller = document.querySelector('.overflow-y-auto')
  if (scroller) {
    scroller.removeEventListener('scroll', handleScroll)
  }
})
</script>

<template>
  <div class="w-full">

    <!-- Section 1: Hero (หน้าแรก) -->
    <section class="h-screen w-full snap-start flex flex-col items-center justify-center relative">
      <!-- เนื้อหา Hero เดิมของคุณ -->
      <div class="py-14 text-center max-w-4xl mx-auto space-y-6 animate-fade-in-up">
        <div
          class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-accent/50 text-accent-foreground text-sm font-medium mb-4 border border-accent/20">
          <span>✨ ออกเดินทางสู่โลกกว้างไปกับเรา</span>
        </div>

        <h1 class="py-4 text-7xl font-bold tracking-tight text-foreground leading-tight">
          ค้นหาจุดหมาย... <br />
          <span
            class="bg-gradient-to-r from-primary via-yellow-300 to-primary bg-clip-text text-transparent bg-300-percent animate-gradient">
            ที่ใช่สำหรับคุณ
          </span>
        </h1>

        <p class="text-lg py-4 md:text-xl text-muted-foreground max-w-2xl mx-auto">
          รวบรวม TripAdvisor ที่พักและเที่ยวบิน เพื่อการพักผ่อนที่สมบูรณ์แบบของคุณ
        </p>
      </div>
      <div class="w-full max-w-5xl h-20 relative z-40 px-4">
        <div :class="[
          'transition-all duration-900 ease-in-out',
          isSticky
            ? 'fixed top-24 left-0 right-0 mx-auto w-full max-w-5xl z-50 px-4'
            : 'w-full relative'
        ]">
          <SearchBar @search="handleSearch" @focus="handleFocus" />
        </div>
      </div>
    </section>

    <!-- Section 2: Trips List -->
    <section id="trips-section" class="min-h-screen w-full snap-start pt-48 px-4 bg-background">
      <TripsList ref="tripsListRef" />
    </section>

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

.animate-fade-in-up {
  animation: fadeInUp 0.8s ease-out forwards;
  opacity: 0;
  transform: translateY(20px);
}

.delay-300 {
  animation-delay: 0.3s;
}

.delay-600 {
  animation-delay: 0.6s;
}

@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>

