<script setup lang="ts">
import { ref } from 'vue'
import { Menu, X } from 'lucide-vue-next'
import { SignedIn, SignedOut, SignInButton, SignUpButton, useUser } from '@clerk/vue'
import UserDropdown from './UserDropdown.vue'

const { isLoaded } = useUser()
const isOpen = ref(false)
</script>

<template>
  <section class="relative w-full flex items-center bg-white py-4 md:py-6 border-b border-gray-200 sticky top-0 z-50 shadow-sm">
    <div class="container mx-auto px-4 md:px-6 lg:px-8">
      <nav class="flex items-center justify-between">
        <!-- Logo/Title -->
        <div class="flex items-center">
          <h1 class="text-2xl font-bold text-blue-600">
            เที่ยวไหนดี
          </h1>
        </div>

        <!-- Desktop Navigation -->
        <div class="hidden md:flex items-center space-x-8">

          <!-- Auth Buttons -->
          <template v-if="isLoaded">
            <!-- เมื่อยังไม่ login: แสดงปุ่ม Login และ Register -->
            <SignedOut>
              <SignInButton mode="modal">
                <button class="px-4 py-2 text-sm font-medium text-white hover:text-gray-500 transition-colors">
                  Login
                </button>
              </SignInButton>
              <SignUpButton mode="modal">
                <button class="px-4 py-2 text-sm font-medium text-white  hover:text-gray-500 transition-colors">
                  Register
                </button>
              </SignUpButton>
            </SignedOut>
            
            <!-- เมื่อ login แล้ว: แสดง UserDropdown -->
            <SignedIn>
              <UserDropdown />
            </SignedIn>
          </template>
          
          <!-- Loading state -->
          <div v-else class="px-4 py-2 text-sm text-gray-500">
            Loading...
          </div>
        </div>

        <!-- Mobile Navigation Button -->
        <div class="md:hidden">
          <button
            @click="isOpen = !isOpen"
            class="p-2 text-gray-600 hover:text-gray-900"
          >
            <Menu v-if="!isOpen" class="h-6 w-6" />
            <X v-else class="h-6 w-6" />
          </button>
        </div>
      </nav>

      <!-- Mobile Navigation Menu -->
      <div
        v-if="isOpen"
        class="md:hidden absolute top-full left-0 w-full bg-white border-b border-gray-200 py-4 shadow-lg"
      >
        <div class="container mx-auto px-4">
          <div class="flex flex-col space-y-4">
            <!-- Mobile Search Bar -->
            <div class="relative">
              <input
                type="text"
                placeholder="ค้นหาที่เกี่ยว"
                class="w-full px-4 py-2 pl-10 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-gray-900"
              />
              <svg
                class="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400"
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
            <p class="text-xs text-gray-500 text-center">
              หาที่เกี่ยวแล้วไปกัน...
            </p>

            <!-- Mobile Auth Buttons -->
            <div class="flex flex-col space-y-2">
              <template v-if="isLoaded">
                <!-- เมื่อยังไม่ login: แสดงปุ่ม Login และ Register -->
                <SignedOut>
                  <SignInButton mode="modal">
                    <button class="w-full px-4 py-2 text-sm font-medium text-gray-700 hover:text-gray-900 border border-gray-300 rounded-md">
                      Login
                    </button>
                  </SignInButton>
                  <SignUpButton mode="modal">
                    <button class="w-full px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700">
                      Register
                    </button>
                  </SignUpButton>
                </SignedOut>
                
                <!-- เมื่อ login แล้ว: แสดง UserDropdown -->
                <SignedIn>
                  <UserDropdown />
                </SignedIn>
              </template>
              
              <div v-else class="text-center text-sm text-gray-500">
                Loading...
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>