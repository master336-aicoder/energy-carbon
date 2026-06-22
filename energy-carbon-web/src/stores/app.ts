import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const sidebarCollapsed = ref(false)
  const visitedViews = ref<any[]>([])

  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  function addVisitedView(view: any) {
    if (visitedViews.value.some(v => v.path === view.path)) return
    visitedViews.value.push(view)
  }

  function removeVisitedView(view: any) {
    const index = visitedViews.value.findIndex(v => v.path === view.path)
    if (index > -1) visitedViews.value.splice(index, 1)
  }

  return {
    sidebarCollapsed,
    visitedViews,
    toggleSidebar,
    addVisitedView,
    removeVisitedView
  }
})
