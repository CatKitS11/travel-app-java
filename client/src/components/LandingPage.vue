<script setup lang="ts">
import Navbar from './Navbar.vue'
import TripsList from './TripsList.vue'
import SearchBar from './SearchBar.vue'
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navbar -->
    <Navbar />

    <!-- Search Bar -->
    <SearchBar />
    <!-- Trips List Content -->
    <TripsList />
  </div>
</template>