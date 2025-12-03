<script setup lang="ts">
// ไม่ต้องมี Logic อะไรมาก เป็นแค่ Template สำหรับแสดงผล
</script>

<template>
  <!-- 
    ปรับแต่ง Class ตรงนี้ได้เลย 
    เช่นเปลี่ยนจาก bg-muted/50 เป็น bg-muted เฉยๆ ถ้าอยากให้เข้มขึ้น
  -->
  <div class="h-64 w-full rounded-3xl bg-gray-300 animate-pulse border border-border/50">
    <div class="h-full flex flex-col sm:flex-row p-4 gap-4">
      <!-- จำลองส่วนรูปภาพ -->
      <div class="sm:w-[350px] h-[200px] sm:h-full bg-background/50 rounded-2xl"></div>
      
      <!-- จำลองส่วนเนื้อหา -->
      <div class="flex-1 flex flex-col gap-3 py-2">
        <div class="h-8 bg-background/50 rounded-lg w-3/4"></div>
        <div class="h-4 bg-background/50 rounded w-1/2"></div>
        <div class="h-20 bg-background/50 rounded w-full mt-2"></div>
      </div>
    </div>
  </div>
</template>
