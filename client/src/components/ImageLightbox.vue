<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { X, ChevronLeft, ChevronRight } from 'lucide-vue-next'

const props = defineProps<{
  isOpen: boolean
  images: string[]
  initialIndex: number
}>()

const emit = defineEmits(['close'])

const currentIndex = ref(props.initialIndex)

// อัปเดต index เมื่อเปิด Modal ใหม่
watch(() => props.isOpen, (newVal) => {
  if (newVal) {
    currentIndex.value = props.initialIndex
    document.body.style.overflow = 'hidden' // ป้องกัน Scroll หน้าหลัง
  } else {
    document.body.style.overflow = ''
  }
})

// Navigation
const next = () => {
  currentIndex.value = (currentIndex.value + 1) % props.images.length
}

const prev = () => {
  currentIndex.value = (currentIndex.value - 1 + props.images.length) % props.images.length
}

const close = () => {
  emit('close')
}

// Keyboard Support
const handleKeydown = (e: KeyboardEvent) => {
  if (!props.isOpen) return
  if (e.key === 'Escape') close()
  if (e.key === 'ArrowRight') next()
  if (e.key === 'ArrowLeft') prev()
}

onMounted(() => window.addEventListener('keydown', handleKeydown))
onUnmounted(() => window.removeEventListener('keydown', handleKeydown))
</script>

<template>
  <Transition name="fade">
    <div v-if="isOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black/90 backdrop-blur-sm" @click.self="close">
      
      <!-- Close Button -->
      <button @click="close" class="absolute top-4 right-4 p-2 text-white/70 hover:text-white transition-colors bg-black/20 rounded-full">
        <X class="w-8 h-8" />
      </button>

      <!-- Prev Button -->
      <button v-if="images.length > 1" @click.stop="prev" class="absolute left-4 p-2 text-white/70 hover:text-white transition-colors hover:bg-white/10 rounded-full">
        <ChevronLeft class="w-10 h-10" />
      </button>

      <!-- Main Image -->
      <div class="relative max-w-5xl max-h-[85vh] w-full px-4 flex justify-center">
        <img 
          :src="images[currentIndex]" 
          class="max-w-full max-h-[85vh] object-contain rounded-lg shadow-2xl" 
          @click.stop 
        />
        
        <!-- Counter -->
        <div class="absolute bottom-[-40px] left-1/2 -translate-x-1/2 text-white/80 font-medium bg-black/40 px-3 py-1 rounded-full text-sm">
          {{ currentIndex + 1 }} / {{ images.length }}
        </div>
      </div>

      <!-- Next Button -->
      <button v-if="images.length > 1" @click.stop="next" class="absolute right-4 p-2 text-white/70 hover:text-white transition-colors hover:bg-white/10 rounded-full">
        <ChevronRight class="w-10 h-10" />
      </button>

    </div>
  </Transition>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
