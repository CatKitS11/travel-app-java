<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { tripsApi, filesApi } from '../services/api'
import { useAuth } from '@clerk/vue'
import { X, Loader2, MapPin, ExternalLink } from 'lucide-vue-next'
import Alert from './Alert.vue'

const props = defineProps<{
  isOpen: boolean
  editId: number | null // ถ้าเป็น null คือ Create, ถ้ามีค่าคือ Edit
}>()

const emit = defineEmits(['close', 'success'])
const { getToken } = useAuth()

const isSubmitting = ref(false)
const isLoading = ref(false)

// Alert State
const alertState = ref({
  visible: false,
  variant: 'default' as 'default' | 'destructive' | 'success' | 'warning' | 'info',
  message: ''
})

const showAlert = (variant: 'destructive' | 'warning', message: string) => {
  alertState.value = { visible: true, variant, message }
}

// ----------------------------------------------------
// [เพิ่มส่วนนี้] Logic สำหรับ Upload รูปภาพ
// ----------------------------------------------------
const isUploading = ref(false)
const fileInput = ref<HTMLInputElement | null>(null)

const handleFileUpload = async (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files && target.files[0]) {
    const file = target.files[0]
    isUploading.value = true
    alertState.value.visible = false // Hide previous alerts
    
    try {
      const token = await getToken.value()
      if (!token) throw new Error('No token')
      
      // เรียกใช้ filesApi ที่เพิ่มใน services/api.ts
      const result = await filesApi.upload(file, token)
      
      if (result.url) {
        formData.value.photos.push(result.url)
      }
    } catch (err) {
      console.error('Upload failed:', err)
      showAlert('destructive', 'Failed to upload image. Please try again.')
    } finally {
      isUploading.value = false
      target.value = '' // Reset input file ให้เลือกไฟล์เดิมซ้ำได้ถ้าต้องการ
    }
  }
}
// ----------------------------------------------------

// Form Data
const formData = ref({
  title: '',
  description: '',
  photos: [] as string[],
  tags: [] as string[],
  latitude: 13.7563,
  longitude: 100.5018,
  tempPhoto: '',
  tempTag: '',
  // เพิ่ม field สำหรับ input แบบรวม
  coordinateInput: ''
})

const isEditing = computed(() => !!props.editId)

// Reset Form
const resetForm = () => {
  formData.value = {
    title: '',
    description: '',
    photos: [],
    tags: [],
    latitude: 13.7563,
    longitude: 100.5018,
    tempPhoto: '',
    tempTag: '',
    coordinateInput: ''
  }
  alertState.value.visible = false
}

// Watch เมื่อเปิด Modal หรือเปลี่ยน editId
watch(() => props.isOpen, async (newVal) => {
  if (newVal) {
    if (props.editId) {
      // Edit Mode: Fetch Data
      await fetchTripDetails(props.editId)
    } else {
      // Create Mode: Reset Form
      resetForm()
    }
  }
})

const fetchTripDetails = async (id: number) => {
  isLoading.value = true
  alertState.value.visible = false
  try {
    const token = await getToken.value()
    if (!token) return
    const trip = await tripsApi.getById(String(id), token)
    
    formData.value = {
      title: trip.title,
      description: trip.description,
      photos: trip.photos || [],
      tags: trip.tags || [],
      latitude: trip.latitude || 13.7563,
      longitude: trip.longitude || 100.5018,
      tempPhoto: '',
      tempTag: '',
      coordinateInput: `${trip.latitude}, ${trip.longitude}` // Set input string
    }
  } catch (err) {
    showAlert('destructive', 'Failed to fetch trip details.')
    // emit('close') // Maybe don't close immediately so user sees the error?
  } finally {
    isLoading.value = false
  }
}

// *** ฟังก์ชันจัดการพิกัดใหม่ ***
const handleCoordinateInput = () => {
  const input = formData.value.coordinateInput.trim()
  
  // ลองแยกด้วย comma (รองรับ "lat, long" หรือ "lat,long")
  const parts = input.split(',')
  
  if (parts.length === 2) {
    const lat = parseFloat(parts[0]!.trim())
    const lng = parseFloat(parts[1]!.trim())
    
    if (!isNaN(lat) && !isNaN(lng)) {
      formData.value.latitude = lat
      formData.value.longitude = lng
    }
  }
}

const openGoogleMapsFinder = () => {
  // เปิด Google Maps เพื่อให้ user ไปหาพิกัด
  window.open('https://www.google.com/maps', '_blank')
}

const handleSubmit = async () => {
  isSubmitting.value = true
  alertState.value.visible = false
  
  if (!formData.value.title || !formData.value.description) {
    showAlert('warning', 'Please fill in all required fields (Title, Description).')
    isSubmitting.value = false
    return
  }

  try {
    const token = await getToken.value()
    if (!token) return

    const payload = {
      title: formData.value.title,
      description: formData.value.description,
      photos: formData.value.photos,
      tags: formData.value.tags,
      latitude: Number(formData.value.latitude),
      longitude: Number(formData.value.longitude)
    }

    if (isEditing.value && props.editId) {
      await tripsApi.update(props.editId, payload, token)
    } else {
      await tripsApi.create(payload, token)
    }

    emit('success') // แจ้ง Parent ว่าทำรายการสำเร็จ
    emit('close')
  } catch (err) {
    showAlert('destructive', 'Failed to save trip. Please check your inputs.')
    console.error(err)
  } finally {
    isSubmitting.value = false
  }
}

// Helpers for array inputs
const addPhoto = () => {
  if (formData.value.tempPhoto) {
    formData.value.photos.push(formData.value.tempPhoto)
    formData.value.tempPhoto = ''
  }
}
const removePhoto = (idx: number) => formData.value.photos.splice(idx, 1)

const addTag = () => {
  if (formData.value.tempTag) {
    formData.value.tags.push(formData.value.tempTag)
    formData.value.tempTag = ''
  }
}
const removeTag = (idx: number) => formData.value.tags.splice(idx, 1)
</script>

<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 flex items-center justify-center px-4">
    <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="$emit('close')"></div>
    <div class="bg-card w-full max-w-2xl max-h-[90vh] overflow-y-auto rounded-3xl shadow-2xl relative animate-in fade-in zoom-in-95 duration-200">
      
      <!-- Header -->
      <div class="sticky top-0 bg-card z-10 px-6 py-4 border-b border-border flex items-center justify-between">
        <h2 class="text-xl font-bold">{{ isEditing ? 'Edit Destination' : 'Add New Destination' }}</h2>
        <button @click="$emit('close')" class="p-2 hover:bg-muted rounded-full transition-colors"><X class="w-5 h-5" /></button>
      </div>

      <!-- Loading State (Edit Mode) -->
      <div v-if="isLoading" class="p-20 text-center">
        <Loader2 class="w-10 h-10 animate-spin mx-auto text-primary mb-2" />
        <p class="text-muted-foreground">Loading details...</p>
      </div>

      <!-- Form Content -->
      <div v-else class="p-6 space-y-4">
        
        <!-- Alert Area -->
        <Alert 
          v-if="alertState.visible" 
          :variant="alertState.variant" 
          :message="alertState.message" 
          class="mb-4"
          dismissible
          @dismiss="alertState.visible = false"
        />

        <!-- Title -->
        <div>
          <label class="block text-sm font-medium mb-1">Title <span class="text-red-500">*</span></label>
          <input v-model="formData.title" type="text" class="w-full px-4 py-2 rounded-xl bg-muted/50 border border-border focus:ring-2 focus:ring-primary/20 outline-none" placeholder="Trip name..." />
        </div>

        <!-- Description -->
        <div>
          <label class="block text-sm font-medium mb-1">Description <span class="text-red-500">*</span></label>
          <textarea v-model="formData.description" rows="4" class="w-full px-4 py-2 rounded-xl bg-muted/50 border border-border focus:ring-2 focus:ring-primary/20 outline-none" placeholder="Tell us about this place..."></textarea>
        </div>

        <!-- Photos -->
        <div>
          <label class="block text-sm font-medium mb-1">Photos (URLs)</label>
          <div class="flex gap-2 mb-2">
            <input v-model="formData.tempPhoto" type="text" class="flex-1 px-4 py-2 rounded-xl bg-muted/50 border border-border outline-none" placeholder="https://example.com/image.jpg" @keydown.enter.prevent="addPhoto" />
            
            <!-- ส่วนที่เพิ่ม: Input File (ซ่อนไว้) และปุ่ม Upload -->
            <input 
              type="file" 
              ref="fileInput" 
              class="hidden" 
              accept="image/*" 
              @change="handleFileUpload" 
            />
            <button 
              @click.prevent="fileInput?.click()" 
              class="px-4 py-2 bg-muted hover:bg-muted/80 border border-border rounded-xl font-medium flex items-center gap-2 transition-colors"
              :disabled="isUploading"
            >
              <Loader2 v-if="isUploading" class="w-4 h-4 animate-spin" />
              <span v-else>Upload</span>
            </button>
            <!-- จบส่วนที่เพิ่ม -->

            <button @click.prevent="addPhoto" class="px-4 py-2 bg-secondary rounded-xl font-medium">Add Link</button>
          </div>
          <div class="space-y-2">
            <div v-for="(photo, idx) in formData.photos" :key="idx" class="flex items-center gap-2 bg-muted/30 p-2 rounded-lg group">
              <img :src="photo" class="w-10 h-10 rounded object-cover bg-muted" />
              <span class="text-xs truncate flex-1">{{ photo }}</span>
              <button @click="removePhoto(idx)" class="text-destructive hover:bg-destructive/10 p-1 rounded"><X class="w-4 h-4" /></button>
            </div>
          </div>
        </div>

        <!-- Tags -->
        <div>
          <label class="block text-sm font-medium mb-1">Tags (Include province here)</label>
          <div class="flex gap-2 mb-2">
            <input v-model="formData.tempTag" type="text" class="flex-1 px-4 py-2 rounded-xl bg-muted/50 border border-border outline-none" placeholder="e.g. Bangkok, Mountain" @keydown.enter.prevent="addTag" />
            <button @click.prevent="addTag" class="px-4 py-2 bg-secondary rounded-xl font-medium">Add</button>
          </div>
          <div class="flex flex-wrap gap-2">
            <span v-for="(tag, idx) in formData.tags" :key="idx" class="bg-primary/10 text-primary px-3 py-1 rounded-full text-sm flex items-center gap-1">
              #{{ tag }}
              <button @click="removeTag(idx)" class="hover:text-destructive"><X class="w-3 h-3" /></button>
            </span>
          </div>
        </div>

        <!-- Coordinates Section (ปรับปรุงใหม่) -->
        <div class="bg-muted/30 p-4 rounded-xl border border-border/50">
          <label class="block text-sm font-medium mb-3 flex items-center justify-between">
             <span class="flex items-center gap-2"><MapPin class="w-4 h-4" /> Location Coordinates</span>
             <button @click="openGoogleMapsFinder" class="text-xs text-primary hover:underline flex items-center gap-1">
                Open Google Maps <ExternalLink class="w-3 h-3" />
             </button>
          </label>
          
          <div class="space-y-3">
             <!-- Input แบบรวม -->
             <div>
                <input 
                    v-model="formData.coordinateInput" 
                    @input="handleCoordinateInput"
                    type="text" 
                    class="w-full px-4 py-2 rounded-xl bg-white/50 border border-border outline-none text-sm font-mono" 
                    placeholder="Paste coordinates here (e.g. 13.7563, 100.5018)" 
                />
                <p class="text-xs text-muted-foreground mt-1">
                    Tip: Right-click on Google Maps & select coordinates to copy.
                </p>
             </div>

             <!-- Preview Map -->
             <div class="h-40 rounded-lg overflow-hidden border border-border relative bg-muted">
                 <iframe 
                    v-if="formData.latitude && formData.longitude"
                    width="100%" 
                    height="100%" 
                    frameborder="0" 
                    scrolling="no" 
                    :src="`https://maps.google.com/maps?q=${formData.latitude},${formData.longitude}&z=15&output=embed`"
                    class="w-full h-full opacity-80 hover:opacity-100 transition-opacity"
                ></iframe>
                <div v-else class="flex items-center justify-center h-full text-muted-foreground text-sm">
                    Enter valid coordinates to see preview
                </div>
             </div>

             <!-- Hidden/Readonly Inputs (เผื่อ Debug) -->
             <div class="grid grid-cols-2 gap-4 text-xs text-muted-foreground">
                <div>Lat: {{ formData.latitude }}</div>
                <div>Lng: {{ formData.longitude }}</div>
             </div>
          </div>
        </div>

      </div>

      <!-- Footer -->
      <div v-if="!isLoading" class="sticky bottom-0 bg-card px-6 py-4 border-t border-border flex justify-end gap-3">
        <button @click="$emit('close')" class="px-6 py-2 rounded-xl hover:bg-muted transition-colors font-medium">Cancel</button>
        <button @click="handleSubmit" :disabled="isSubmitting" class="px-6 py-2 bg-primary text-primary-foreground rounded-xl font-medium hover:bg-primary/90 transition-all flex items-center gap-2">
          <Loader2 v-if="isSubmitting" class="w-4 h-4 animate-spin" />
          {{ isEditing ? 'Save Changes' : 'Create Trip' }}
        </button>
      </div>

    </div>
  </div>
</template>
