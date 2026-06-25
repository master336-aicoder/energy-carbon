<template>
  <view class="chart-container">
    <view class="chart-header">
      <text class="chart-title">{{ title }}</text>
    </view>
    <qiun-data-charts
      type="line"
      :opts="opts"
      :chartData="chartData"
    />
  </view>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: '趋势图'
  },
  categories: {
    type: Array,
    default: () => []
  },
  series: {
    type: Array,
    default: () => []
  }
})

const chartData = ref({})

const opts = ref({
  color: ['#1a5f3f', '#2d8b5a', '#faad14', '#f5222d'],
  padding: [15, 10, 0, 15],
  enableScroll: false,
  legend: {
    show: true,
    position: 'top',
    float: 'right'
  },
  xAxis: {
    disableGrid: true,
    fontColor: '#666666',
    fontSize: 11
  },
  yAxis: {
    gridType: 'dash',
    dashLength: 4,
    fontColor: '#666666',
    fontSize: 11
  },
  extra: {
    line: {
      type: 'straight',
      width: 2,
      activeType: 'hollow'
    }
  }
})

watch(
  () => [props.categories, props.series],
  () => {
    chartData.value = {
      categories: props.categories,
      series: props.series
    }
  },
  { immediate: true, deep: true }
)
</script>

<style scoped>
.chart-container {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}
.chart-header {
  margin-bottom: 16rpx;
}
.chart-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
}
</style>
