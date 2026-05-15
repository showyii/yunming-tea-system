<template>
  <div class="dashboard" v-loading="loading">
    <section class="dashboard-header">
      <div>
        <span class="dashboard-kicker">ADMIN OVERVIEW</span>
        <h2>经营数据总览</h2>
      </div>
    </section>

    <div class="chart-grid">
      <div class="chart-card paper-panel">
        <h3>每日订单趋势</h3>
        <div ref="dailyChart" class="chart-box"></div>
      </div>
      <div class="chart-card paper-panel">
        <h3>商品销量排行</h3>
        <div ref="salesChart" class="chart-box"></div>
      </div>
      <div class="chart-card paper-panel">
        <h3>分类销量占比</h3>
        <div ref="categoryChart" class="chart-box"></div>
      </div>
      <div class="chart-card paper-panel">
        <h3>包间预订统计</h3>
        <div ref="bookingChart" class="chart-box"></div>
      </div>
      <div class="chart-card paper-panel chart-card-wide">
        <h3>订单状态分布</h3>
        <div ref="statusChart" class="chart-box"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { use } from 'echarts/core'
import { BarChart, LineChart, PieChart } from 'echarts/charts'
import {
  GridComponent,
  LegendComponent,
  TooltipComponent
} from 'echarts/components'
import { LegacyGridContainLabel } from 'echarts/features'
import { CanvasRenderer } from 'echarts/renderers'
import { graphic, init } from 'echarts/core'
import { statisticsApi } from '@/api/admin'

use([BarChart, LineChart, PieChart, GridComponent, LegacyGridContainLabel, LegendComponent, TooltipComponent, CanvasRenderer])

const loading = ref(false)
const dailyChart = ref(null)
const salesChart = ref(null)
const categoryChart = ref(null)
const bookingChart = ref(null)
const statusChart = ref(null)

let charts = []

const chartTheme = {
  textStyle: {
    color: '#5b5b5b',
    fontFamily: 'Microsoft YaHei'
  }
}

const initChart = (el, option) => {
  const chart = init(el, chartTheme)
  chart.setOption(option)
  charts.push(chart)
  return chart
}

const loadData = async () => {
  loading.value = true
  try {
    const [daily, sales, category, bookings, orderStatus] = await Promise.all([
      statisticsApi.dailyOrders(7),
      statisticsApi.salesRanking(),
      statisticsApi.categorySales(),
      statisticsApi.roomBookings(),
      statisticsApi.orderStatus()
    ])

    await nextTick()

    initChart(dailyChart.value, {
      tooltip: { trigger: 'axis' },
      legend: { data: ['订单数', '金额'], bottom: 4, textStyle: { color: '#6f665e' } },
      grid: { left: '4%', right: '4%', bottom: '13%', top: '10%', containLabel: true },
      xAxis: {
        type: 'category',
        data: daily.map((d) => d.date),
        axisLine: { lineStyle: { color: '#c7bda8' } },
        axisLabel: { color: '#6f665e' }
      },
      yAxis: [
        {
          type: 'value',
          name: '订单数',
          axisLine: { show: false },
          splitLine: { lineStyle: { color: 'rgba(216, 210, 196, 0.8)' } }
        },
        {
          type: 'value',
          name: '金额(元)',
          axisLine: { show: false },
          splitLine: { show: false }
        }
      ],
      series: [
        {
          name: '订单数',
          type: 'bar',
          barMaxWidth: 28,
          data: daily.map((d) => d.count),
          itemStyle: { color: '#2f4f3e', borderRadius: [8, 8, 0, 0] }
        },
        {
          name: '金额',
          type: 'line',
          yAxisIndex: 1,
          smooth: true,
          data: daily.map((d) => d.amount),
          itemStyle: { color: '#9f2d20' },
          lineStyle: { width: 3, color: '#9f2d20' }
        }
      ]
    })

    initChart(salesChart.value, {
      tooltip: { trigger: 'axis' },
      grid: { left: '4%', right: '8%', bottom: '5%', top: '6%', containLabel: true },
      xAxis: {
        type: 'value',
        splitLine: { lineStyle: { color: 'rgba(216, 210, 196, 0.8)' } }
      },
      yAxis: {
        type: 'category',
        data: sales.map((s) => s.name).reverse(),
        inverse: true,
        axisLabel: { color: '#6f665e' }
      },
      series: [{
        type: 'bar',
        data: sales.map((s) => s.sales).reverse(),
        itemStyle: { color: '#8c6b3f', borderRadius: [0, 8, 8, 0] },
        label: { show: true, position: 'right', color: '#5c4033' }
      }]
    })

    initChart(categoryChart.value, {
      tooltip: { trigger: 'item' },
      color: ['#2f4f3e', '#6b8e6e', '#8c6b3f', '#9f2d20', '#5c4033', '#b9ad98'],
      series: [{
        type: 'pie',
        radius: ['42%', '70%'],
        itemStyle: { borderColor: '#fffaf2', borderWidth: 2 },
        label: { formatter: '{b}\n{d}%', color: '#5b5b5b' },
        data: category.map((c) => ({ name: c.name, value: c.value }))
      }]
    })

    initChart(bookingChart.value, {
      tooltip: { trigger: 'axis' },
      grid: { left: '4%', right: '4%', bottom: '5%', top: '8%', containLabel: true },
      xAxis: {
        type: 'category',
        data: bookings.map((b) => b.date),
        axisLine: { lineStyle: { color: '#c7bda8' } }
      },
      yAxis: {
        type: 'value',
        name: '预订数',
        splitLine: { lineStyle: { color: 'rgba(216, 210, 196, 0.8)' } }
      },
      series: [{
        type: 'line',
        smooth: true,
        symbolSize: 8,
        data: bookings.map((b) => b.count),
        itemStyle: { color: '#486957' },
        areaStyle: {
          color: new graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(72, 105, 87, 0.28)' },
            { offset: 1, color: 'rgba(72, 105, 87, 0.02)' }
          ])
        },
        lineStyle: { width: 3, color: '#486957' }
      }]
    })

    const statusData = orderStatus.list || []
    initChart(statusChart.value, {
      tooltip: { trigger: 'item' },
      color: ['#8c6b3f', '#2f4f3e', '#6b8e6e', '#5c4033', '#9f2d20'],
      series: [{
        type: 'pie',
        radius: '66%',
        itemStyle: { borderColor: '#fffaf2', borderWidth: 2 },
        label: { formatter: '{b}: {c}单', color: '#5b5b5b' },
        data: statusData.map((s) => ({ name: s.name, value: parseInt(s.value, 10) }))
      }]
    })
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

onBeforeUnmount(() => {
  charts.forEach((c) => c.dispose())
})
</script>

<style scoped>
.dashboard-header {
  margin-bottom: 24px;
}

.dashboard-kicker {
  color: var(--color-red);
  font-size: 12px;
  letter-spacing: 0.3em;
}

.dashboard h2 {
  margin: 10px 0 8px;
  font-family: var(--font-display);
  font-size: 30px;
  font-weight: 600;
  letter-spacing: 0.12em;
  color: var(--color-ink);
}

.dashboard-header p {
  margin: 0;
  color: var(--color-ink-soft);
  font-size: 14px;
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

.chart-card {
  padding: 22px;
}

.chart-card-wide {
  grid-column: span 2;
}

.chart-card h3 {
  margin: 0 0 14px;
  color: var(--color-wood);
  font-size: 15px;
  letter-spacing: 0.12em;
}

.chart-box {
  width: 100%;
  height: 320px;
}

@media (max-width: 960px) {
  .chart-grid {
    grid-template-columns: 1fr;
  }

  .chart-card-wide {
    grid-column: span 1;
  }
}
</style>
