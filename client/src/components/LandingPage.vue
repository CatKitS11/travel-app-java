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
  <div class="relative w-full flex flex-col items-center justify-center py-20 px-4 md:px-6 lg:px-8">
    
    <!-- Hero Content -->
    <div class="text-center max-w-4xl mx-auto space-y-6 animate-fade-in-up">
      <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-accent/50 text-accent-foreground text-sm font-medium mb-4 border border-accent/20">
        <span>✨ Explore the world with us</span>
      </div>
      
      <h1 class="text-5xl md:text-7xl font-bold tracking-tight text-foreground leading-tight">
        Discover Your Next <br />
        <span class="bg-gradient-to-r from-primary via-secondary to-primary bg-clip-text text-transparent bg-300% animate-gradient">
          Great Adventure
        </span>
      </h1>
      
      <p class="text-lg md:text-xl text-muted-foreground max-w-2xl mx-auto">
        Find the best deals on flights, hotels, and holiday packages. Your journey begins here.
      </p>
    </div>

    <!-- Floating Search Bar -->
    <div class="w-full max-w-5xl mt-12 relative z-10 animate-fade-in-up delay-200">
      <div class="glass-panel p-2 rounded-3xl shadow-2xl shadow-primary/10">
        <SearchBar @search="handleSearch" />
      </div>
    </div>

    <!-- Trips List Section -->
    <div class="w-full mt-20 animate-fade-in-up delay-300">
      <TripsList ref="tripsListRef" />
    </div>

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