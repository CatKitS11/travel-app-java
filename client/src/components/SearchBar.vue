<!-- client/src/components/SearchBar.vue -->
<script setup lang="ts">
import { ref } from 'vue'

const searchQuery = ref('')

// Emit event เมื่อ search
const emit = defineEmits<{
  search: [query: string]
}>()

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    emit('search', searchQuery.value.trim())
  }
}

const handleKeyPress = (event: KeyboardEvent) => {
  if (event.key === 'Enter') {
    handleSearch()
  }
}
</script>

<template>
  <div class="relative">
    <input
      v-model="searchQuery"
      @keyup="handleKeyPress"
      type="text"
      placeholder="ค้นหาที่เกี่ยว"
      class="w-3/4 mt-6 px-4 py-2 pl-10 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-gray-900"
    />
    <svg
      class="relative left-41 top-[-45px] transform -translate-y-1/2 w-5 h-5 text-gray-400 mt-6"
      fill="none"
      stroke="currentColor"
      viewBox="0 0 24 24"
    >
      <path
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="2"
        d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
      />
    </svg>
  </div>
</template>