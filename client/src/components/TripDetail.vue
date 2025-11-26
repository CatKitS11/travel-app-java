<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router' // เราจะใช้ route เพื่อดึง id

const route = useRoute()
const tripId = ref(route.params.id)

// Mock data for now
const trip = ref<any>(null)
const loading = ref(true)

onMounted(async () => {
    // 1. หาตัว container
    const appContainer = document.querySelector('.overflow-y-auto') // หาตัวที่มี scroll bar จริงๆ (อาจจะเป็นตัวเดียวกับ snap-y)
    
    if (appContainer) {
        // 2. บังคับเลื่อนไปบนสุดทันที (ปิด smooth scroll ชั่วคราวเพื่อให้มันวาร์ปไปเลย)
        (appContainer as HTMLElement).style.scrollBehavior = 'auto' 
        
        // 3. ลบ snap classes ออก
        appContainer.classList.remove('snap-y', 'snap-mandatory')
        appContainer.scrollTop = 0 
        
        // คืนค่า scroll behavior (ถ้าต้องการ) หรือปล่อยไว้
        // appContainer.style.scrollBehavior = ''
    }

    // 4. เริ่มโหลดข้อมูล
    try {
        // Simulate API call or fetch real data
        setTimeout(() => {
            trip.value = {
                id: tripId.value,
                title: 'ตัวอย่างทริปท่องเที่ยว (Mock Data)',
                description: 'รายละเอียดทริปที่ดึงมาจาก ID ' + tripId.value,
                coverImage: 'https://img.wongnai.com/p/1600x0/2020/02/18/458b9a31b62b408d91137fbe152f7450.jpg',
                location: 'กรุงเทพมหานคร'
            }
            loading.value = false
        }, 1000)
    } catch (error) {
        console.error('Error fetching trip:', error)
        loading.value = false
    }
})

onUnmounted(() => {
    // คืนค่าเดิมเมื่อออกจากหน้า Detail
    const appContainer = document.querySelector('.h-screen.overflow-y-auto') // selector คร่าวๆ
    if (appContainer) {
        appContainer.classList.add('snap-y', 'snap-mandatory')
    }
})
</script>

<template>
    <div class="w-full max-w-5xl mx-auto px-4 py-5">
        <div v-if="loading" class="text-center py-20">Loading trip details...</div>

        <div v-else-if="trip" class="space-y-6">
            <router-link to="/" class="text-primary hover:underline mb-4 inline-block">&larr; Back to Home</router-link>

            <div class="h-[400px] rounded-3xl overflow-hidden">
                <img :src="trip.coverImage" class="w-full h-full object-cover" />
            </div>

            <h1 class="text-4xl font-bold">{{ trip.title }}</h1>
            <p class="text-xl text-muted-foreground">{{ trip.location }}</p>
            <p class="text-lg leading-relaxed">{{ trip.description }}</p>
        </div>

        <div v-else class="text-center py-20 text-destructive">
            Trip not found
        </div>
    </div>
</template>
