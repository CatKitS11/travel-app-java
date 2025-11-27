import { createRouter, createWebHistory } from 'vue-router'
import LandingPage from './components/LandingPage.vue' // หน้า Home เดิม
import TripDetail from './components/TripDetail.vue' // หน้าใหม่ที่เราเพิ่งสร้าง
import Dashboard from './components/Dashboard.vue' // EDIT: Import Dashboard

const routes = [
  { path: '/', component: LandingPage },
  { path: '/trips/:id', component: TripDetail },
  { path: '/dashboard', component: Dashboard } // EDIT: เพิ่ม route สำหรับ Dashboard
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
