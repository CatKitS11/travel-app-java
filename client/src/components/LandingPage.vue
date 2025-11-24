<script setup lang="ts">
import { ref } from 'vue'
import SearchBar from './SearchBar.vue'
import TripsList from './TripsList.vue'

const tripsListRef = ref<InstanceType<typeof TripsList> | null>(null)

const handleSearch = (query: string) => {
  tripsListRef.value?.fetchTrips(query)
}
</script>

<template>
  <div class="w-full">
    
    <!-- Section 1: Hero (หน้าแรก) -->
    <section class="h-screen w-full snap-start flex flex-col items-center justify-center relative">
       <!-- เนื้อหา Hero เดิมของคุณ -->
       <div class="py-14 text-center max-w-4xl mx-auto space-y-6 animate-fade-in-up">
          <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-accent/50 text-accent-foreground text-sm font-medium mb-4 border border-accent/20">
            <span>✨ ออกเดินทางสู่โลกกว้างไปกับเรา</span>
          </div>
          
          <h1 class="py-4 text-7xl font-bold tracking-tight text-foreground leading-tight">
            ค้นหาจุดหมาย... <br />
            <span class="bg-gradient-to-r from-primary via-yellow-600 to-primary bg-clip-text text-transparent bg-300% animate-gradient">
              ที่ใช่สำหรับคุณ
            </span>
          </h1>
          
          <p class="text-lg py-4 md:text-xl text-muted-foreground max-w-2xl mx-auto">
            รวบรวม TripAdvisor ที่พักและเที่ยวบิน เพื่อการพักผ่อนที่สมบูรณ์แบบของคุณ
          </p>
        </div>

        <!-- เพิ่มปุ่ม scroll down บอก user -->
        <div class="absolute bottom-10 animate-bounce">
          👇 Scroll Down
        </div>
    </section>

    <!-- Section 2: Search Bar & Intro -->
    <section class="h-screen w-full snap-start flex flex-col items-center justify-center bg-accent/5">
       <div class="w-full max-w-5xl">
          <SearchBar @search="handleSearch" />
       </div>
    </section>

    <!-- Section 3: Trips List -->
    <section class="min-h-screen w-full snap-start pt-24 px-4 bg-background">
       <TripsList ref="tripsListRef" />
    </section>

  </div>
</template>

<style scoped>
.bg-300% {
  background-size: 300% auto;
}
.animate-gradient {
  animation: gradient 8s linear infinite;
}
@keyframes gradient {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}
.animate-fade-in-up {
  animation: fadeInUp 0.8s ease-out forwards;
  opacity: 0;
  transform: translateY(20px);
}
.delay-200 { animation-delay: 0.2s; }
.delay-300 { animation-delay: 0.3s; }

@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>