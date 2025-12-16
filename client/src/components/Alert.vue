<script setup lang="ts">
import { computed } from 'vue'
import { cva, type VariantProps } from 'class-variance-authority'
import { AlertCircle, CheckCircle, AlertTriangle, Info, X } from 'lucide-vue-next'
import { cn } from '../lib/utils'

const alertVariants = cva(
  "relative w-full rounded-lg border p-4 [&>svg~*]:pl-7 [&>svg+div]:translate-y-[-3px] [&>svg]:absolute [&>svg]:left-4 [&>svg]:top-4",
  {
    variants: {
      variant: {
        default: "bg-background text-foreground",
        destructive:
          "border-destructive/50 text-destructive dark:border-destructive [&>svg]:text-destructive bg-destructive/5",
        success:
          "border-green-500/50 text-green-700 dark:border-green-500 dark:text-green-400 [&>svg]:text-green-600 dark:[&>svg]:text-green-400 bg-green-50/50 dark:bg-green-900/10",
        warning:
          "border-yellow-500/50 text-yellow-700 dark:border-yellow-500 dark:text-yellow-400 [&>svg]:text-yellow-600 dark:[&>svg]:text-yellow-400 bg-yellow-50/50 dark:bg-yellow-900/10",
        info: 
          "border-blue-500/50 text-blue-700 dark:border-blue-500 dark:text-blue-400 [&>svg]:text-blue-600 dark:[&>svg]:text-blue-400 bg-blue-50/50 dark:bg-blue-900/10"
      },
    },
    defaultVariants: {
      variant: "default",
    },
  }
)

type AlertVariant = NonNullable<VariantProps<typeof alertVariants>['variant']>

interface Props {
  variant?: AlertVariant
  title?: string
  message?: string
  dismissible?: boolean
  class?: string
  overlay?: boolean // New prop for overlay mode
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'default',
  dismissible: false,
  overlay: false
})

const emit = defineEmits(['dismiss'])

const iconMap: Record<string, any> = {
  default: Info,
  info: Info,
  destructive: AlertCircle,
  success: CheckCircle,
  warning: AlertTriangle
}

const IconComponent = computed(() => iconMap[props.variant] || Info)

function dismiss() {
  emit('dismiss')
}
</script>

<template>
  <component :is="overlay ? 'Teleport' : 'div'" to="body">
    <Transition
      enter-active-class="transition ease-out duration-300"
      enter-from-class="opacity-0 translate-y-[-1rem]"
      enter-to-class="opacity-100 translate-y-0"
      leave-active-class="transition ease-in duration-200"
      leave-from-class="opacity-100 translate-y-0"
      leave-to-class="opacity-0 translate-y-[-1rem]"
      appear
    >
      <div 
        :class="cn(
          alertVariants({ variant }), 
          props.class,
          // Overlay Styles
          overlay && 'fixed top-4 left-1/2 -translate-x-1/2 z-50 w-[90%] max-w-md shadow-lg backdrop-blur-sm'
        )" 
        role="alert"
        v-bind="$attrs"
      >
        <component :is="IconComponent" class="h-4 w-4" />
        <h5 v-if="title" class="mb-1 font-medium leading-none tracking-tight">
          {{ title }}
        </h5>
        <div v-if="message || $slots.default" class="text-sm [&_p]:leading-relaxed opacity-90">
          <slot>{{ message }}</slot>
        </div>
        <button 
          v-if="dismissible" 
          @click="dismiss"
          class="absolute right-4 top-4 rounded-sm opacity-70 ring-offset-background transition-opacity hover:opacity-100 focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2"
        >
          <X class="h-4 w-4" />
          <span class="sr-only">Close</span>
        </button>
      </div>
    </Transition>
  </component>
</template>
