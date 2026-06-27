<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="700px" destroy-on-close>
    <div v-loading="loading" class="notice-detail">
      <div v-if="detailData" class="notice-meta">
        <el-tag size="small" :type="detailData.noticeType === 1 ? 'primary' : 'warning'">
          {{ detailData.noticeType === 1 ? '通知' : '公告' }}
        </el-tag>
        <el-tag v-if="mode === 'message'" size="small" :type="detailData.readStatus === 1 ? 'success' : 'danger'">
          {{ detailData.readStatus === 1 ? '已读' : '未读' }}
        </el-tag>
        <span class="notice-time">{{ detailData.createTime }}</span>
      </div>
      <div v-if="detailData" class="notice-content" v-html="detailData.noticeContent || detailData.content"></div>
      <el-empty v-if="!loading && !detailData" description="暂无内容" />
    </div>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
      <el-button v-if="mode === 'message'" type="primary" @click="handlePrev" :disabled="!hasPrev">上一条</el-button>
      <el-button v-if="mode === 'message'" type="primary" @click="handleNext" :disabled="!hasNext">下一条</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { getNoticeDetail } from '@/api/notice'
import { getMessageDetail, markMessageRead } from '@/api/message'

const props = defineProps<{
  modelValue: boolean
  itemId: number | undefined
  mode?: 'notice' | 'message'
  noticeList?: any[]
  messageList?: any[]
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', val: boolean): void
  (e: 'read', id: number): void
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
})

const loading = ref(false)
const detailData = ref<any>(null)
const currentList = computed(() => props.mode === 'message' ? (props.messageList || []) : (props.noticeList || []))

const currentIndex = computed(() => {
  if (!props.itemId || !currentList.value.length) return -1
  return currentList.value.findIndex((n: any) => n.id === props.itemId)
})

const hasPrev = computed(() => currentIndex.value > 0)
const hasNext = computed(() => currentIndex.value >= 0 && currentIndex.value < currentList.value.length - 1)

const dialogTitle = computed(() => {
  if (detailData.value) return detailData.value.noticeTitle || detailData.value.title || '通知详情'
  return props.mode === 'message' ? '消息详情' : '通知详情'
})

async function loadDetail(id: number) {
  if (!id) return
  loading.value = true
  try {
    if (props.mode === 'message') {
      const res = await getMessageDetail(id)
      detailData.value = res.data
      // 标记已读
      try {
        await markMessageRead(id)
        emit('read', id)
      } catch (e) { /* 静默 */ }
    } else {
      const res = await getNoticeDetail(id)
      detailData.value = res.data
    }
  } catch (e) {
    console.error('Failed to load detail:', e)
  } finally {
    loading.value = false
  }
}

function handlePrev() {
  if (hasPrev.value && currentList.value[currentIndex.value - 1]) {
    const prev = currentList.value[currentIndex.value - 1]
    loadDetail(prev.id)
  }
}

function handleNext() {
  if (hasNext.value && currentList.value[currentIndex.value + 1]) {
    const next = currentList.value[currentIndex.value + 1]
    loadDetail(next.id)
  }
}

watch(
  () => props.modelValue,
  (val) => {
    if (val && props.itemId) {
      loadDetail(props.itemId)
    } else {
      detailData.value = null
    }
  },
  { immediate: true }
)
</script>

<style scoped>
.notice-detail { min-height: 100px; }
.notice-meta {
  display: flex; align-items: center; gap: 12px;
  margin-bottom: 16px; padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}
.notice-time { font-size: 13px; color: #909399; }
.notice-content {
  line-height: 1.8; color: #303133; font-size: 14px; word-break: break-word;
}
.notice-content :deep(img) { max-width: 100%; height: auto; }
.notice-content :deep(table) { border-collapse: collapse; width: 100%; }
.notice-content :deep(table td),
.notice-content :deep(table th) { border: 1px solid #ddd; padding: 8px; }
</style>
