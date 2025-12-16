<script setup lang="ts">
import { Loader2, AlertTriangle, Info } from 'lucide-vue-next'

withDefaults(defineProps<{
  isOpen: boolean
  title?: string
  description?: string
  confirmText?: string
  cancelText?: string
  isLoading?: boolean
  variant?: 'destructive' | 'default'
}>(), {
  title: 'Are you sure?',
  description: 'This action cannot be undone.',
  confirmText: 'Confirm',
  cancelText: 'Cancel',
  isLoading: false,
  variant: 'destructive'
})

const emit = defineEmits(['confirm', 'cancel'])
</script>

<template>
  <Teleport to="body">
    <div v-if="isOpen" class="fixed inset-0 z-[100] flex items-center justify-center px-4">
      <!-- Backdrop -->
      <div 
        class="absolute inset-0 bg-black/60 backdrop-blur-sm transition-opacity" 
        @click="!isLoading && emit('cancel')"
      ></div>
      
      <!-- Dialog Content -->
      <div class="bg-card w-full max-w-sm rounded-2xl shadow-2xl relative z-10 overflow-hidden animate-in fade-in zoom-in-95 duration-200 border border-border">
        <div class="p-6 text-center">
          
          <!-- Icon -->
          <div v-if="variant === 'destructive'" class="w-12 h-12 rounded-full bg-red-100 dark:bg-red-900/20 text-destructive flex items-center justify-center mx-auto mb-4">
            <AlertTriangle class="w-6 h-6" />
          </div>
          <div v-else class="w-12 h-12 rounded-full bg-primary/10 text-primary flex items-center justify-center mx-auto mb-4">
            <Info class="w-6 h-6" />
          </div>
          
          <h3 class="text-xl font-bold mb-2">{{ title }}</h3>
          <p class="text-muted-foreground mb-6 text-sm leading-relaxed">{{ description }}</p>
          
          <div class="flex gap-3 justify-center">
            <button 
              @click="emit('cancel')" 
              :disabled="isLoading"
              class="flex-1 px-4 py-2.5 rounded-xl font-medium border border-border hover:bg-muted transition-colors disabled:opacity-50 text-sm"
            >
              {{ cancelText }}
            </button>
            
            <button 
              @click="emit('confirm')" 
              :disabled="isLoading"
              class="flex-1 px-4 py-2.5 rounded-xl font-medium transition-colors flex items-center justify-center gap-2 disabled:opacity-50 text-sm shadow-sm"
              :class="variant === 'destructive' ? 'bg-destructive text-destructive-foreground hover:bg-destructive/90' : 'bg-primary text-primary-foreground hover:bg-primary/90'"
            >
              <Loader2 v-if="isLoading" class="w-4 h-4 animate-spin" />
              {{ confirmText }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

