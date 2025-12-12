import { createRouter, createWebHistory } from 'vue-router'
import LandingPage from './pages/LandingPage.vue' // หน้า Home เดิม
import TripDetail from './pages/TripDetail.vue' // หน้าใหม่ที่เราเพิ่งสร้าง
import Dashboard from './pages/Dashboard.vue' // EDIT: Import Dashboard
import Profile from './pages/Profile.vue' // Import หน้า Profile

const routes = [
  { path: '/', component: LandingPage },
  { path: '/trips/:id', component: TripDetail },
  { 
    path: '/dashboard', 
    component: Dashboard,
    meta: { requiresAuth: true } // EDIT: เพิ่ม meta field
  },
  { 
    path: '/profile', 
    component: Profile,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
