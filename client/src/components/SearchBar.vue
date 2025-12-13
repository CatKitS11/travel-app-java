<script setup lang="ts">
import { ref } from 'vue'
import { Search } from 'lucide-vue-next'

const query = ref('')
const emit = defineEmits<{
  (e: 'search', query: string): void
  (e: 'focus'): void
}>()

const handleSearch = () => {
  emit('search', query.value)
}
</script>

<template>
  <div class="relative sm:w-[90%] flex items-center w-[100%] mx-auto">
    <div class="absolute left-4 text-muted-foreground">
      <Search class="sm:w-6 sm:h-6 w-5 h-5" />
    </div>
    <input 
      v-model="query"
      @input="handleSearch" 
      @keyup.enter="handleSearch"
      @focus="emit('focus')"
      type="text" 
      placeholder="Where do you want to go?" 
      class="w-full sm:h-16 h-12 pl-14 pr-32 rounded-2xl bg-white/50 hover:bg-white/80 focus:bg-white transition-all border-none outline-none sm:text-xl text-sm placeholder:text-muted-foreground/70 text-foreground shadow-inner"
    />
    <button 
      @click="handleSearch"
      class="absolute right-2 h-12 sm:px-8 px-4 text-sm sm:text-base rounded-xl bg-primary text-primary-foreground font-semibold hover:bg-primary/90 transition-all shadow-lg shadow-primary/20">
      Search
    </button>
  </div>
</template>

<style scoped>
/* Custom focus ring if needed */
input:focus {
  box-shadow: inset 0 0 0 2px var(--primary);
}
</style>