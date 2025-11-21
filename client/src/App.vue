<script setup lang="ts">
import Navbar from './components/Navbar.vue'
import LandingPage from './components/LandingPage.vue'
</script>

<template>
  <div class="min-h-screen flex flex-col relative overflow-hidden">
    <!-- Ambient Background Elements -->
    <div class="fixed inset-0 -z-10 bg-background">
      <div class="absolute top-0 left-1/4 w-96 h-96 bg-primary/20 rounded-full blur-3xl opacity-30 animate-pulse"></div>
      <div class="absolute bottom-0 right-1/4 w-96 h-96 bg-secondary/20 rounded-full blur-3xl opacity-30 animate-pulse delay-1000"></div>
    </div>

    <Navbar />
    
    <main class="flex-1 w-full">
      <LandingPage />
    </main>
    
    <footer class="py-8 text-center text-muted-foreground text-sm">
      <p>© 2024 Wanderlust Travels. Crafted with passion.</p>
    </footer>
  </div>
</template>

<style scoped>
/* Scoped styles if needed, but relying on utility classes */
</style>
