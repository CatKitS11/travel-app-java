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
  <div class="relative w-full flex items-center">
    <div class="absolute left-4 text-muted-foreground">
      <Search class="w-6 h-6" />
    </div>
    <input 
      v-model="query"
      @input="handleSearch" 
      @keyup.enter="handleSearch"
      @focus="emit('focus')"
      type="text" 
      placeholder="Where do you want to go?" 
      class="w-full h-16 pl-14 pr-32 rounded-2xl bg-white/50 hover:bg-white/80 focus:bg-white transition-all border-none outline-none text-lg placeholder:text-muted-foreground/70 text-foreground shadow-inner"
    />
    <button 
      @click="handleSearch"
      class="absolute right-2 h-12 px-8 rounded-xl bg-primary text-primary-foreground font-semibold hover:bg-primary/90 transition-all shadow-lg shadow-primary/20">
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