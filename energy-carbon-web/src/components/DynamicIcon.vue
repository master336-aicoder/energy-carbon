<template>
  <el-icon v-if="isElementIcon"><component :is="name" /></el-icon>
  <img v-else :src="iconSrc" class="custom-icon" />
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{ name?: string }>()

const isElementIcon = computed(() => {
  if (!props.name) return true
  return !props.name.startsWith('custom-')
})

const iconSrc = computed(() => `${import.meta.env.BASE_URL}icons/${props.name}.svg`)
</script>

<style scoped>
.custom-icon {
  width: 1em;
  height: 1em;
  vertical-align: middle;
}
</style>
