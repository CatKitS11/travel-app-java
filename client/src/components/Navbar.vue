<script setup lang="ts">
import { Plane, User, Menu } from 'lucide-vue-next';
</script>

<template>
  <nav class="sticky top-0 z-50 w-full border-b border-white/10 bg-white/5 backdrop-blur-md transition-all duration-300">
    <div class="container mx-auto px-6 h-20 flex items-center justify-between">
      <!-- Logo -->
      <div class="flex items-center gap-3 group cursor-pointer">
        <div class="p-2 bg-primary/10 rounded-xl group-hover:bg-primary/20 transition-colors">
          <Plane class="w-6 h-6 text-primary transform group-hover:-rotate-45 transition-transform duration-500" />
        </div>
        <span class="text-xl font-bold bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
          Wanderlust
        </span>
      </div>

      <!-- Desktop Menu -->
      <div class="hidden md:flex items-center gap-8">
        <a href="#" class="text-sm font-medium text-muted-foreground hover:text-primary transition-colors">Destinations</a>
        <a href="#" class="text-sm font-medium text-muted-foreground hover:text-primary transition-colors">Hotels</a>
        <a href="#" class="text-sm font-medium text-muted-foreground hover:text-primary transition-colors">Flights</a>
        <a href="#" class="text-sm font-medium text-muted-foreground hover:text-primary transition-colors">Packages</a>
      </div>

      <!-- Actions -->
      <div class="flex items-center gap-4">
        <button class="hidden md:flex items-center gap-2 px-4 py-2 rounded-full bg-primary/10 hover:bg-primary/20 text-primary font-medium transition-all hover:scale-105 active:scale-95">
          <User class="w-4 h-4" />
          <span>Sign In</span>
        </button>
        
        <!-- Mobile Menu Button -->
        <button class="md:hidden p-2 text-muted-foreground hover:text-foreground">
          <Menu class="w-6 h-6" />
        </button>
      </div>
    </div>
  </nav>
</template>

<style scoped>
/* Additional specific styles if needed */
</style>