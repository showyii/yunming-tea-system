<!--
  AdminDashboard.vue - 管理后台数据概览仪表盘
  用途：以图表形式展示茶馆经营的核心数据指标。
  包含：每日订单趋势（柱状+折线）、商品销量排行（横向柱状）、
        分类销量占比（环形饼图）、包间预订统计（折线面积图）、
        订单状态分布（饼图）。
  技术：使用 ECharts 5 渲染所有图表，系统启动时一次性加载全部数据。
  说明：页面挂载时自动调用 loadData()，销毁前清理所有图表实例防止内存泄漏。
-->
<template>
  <div class="dashboard" v-loading="loading">
    <!-- ==================== 页面头部：仪表盘标题 ==================== -->
    <section class="dashboard-header">
      <div>
        <span class="dashboard-kicker">ADMIN OVERVIEW</span>
        <h2>经营数据总览</h2>
      </div>
    </section>

    <!-- ==================== 图表网格布局 ==================== -->
    <div class="chart-grid">
      <!-- 每日订单趋势图：柱状图(订单数) + 折线图(金额) -->
      <div class="chart-card paper-panel">
        <h3>每日订单趋势</h3>
        <div ref="dailyChart" class="chart-box"></div>
      </div>
      <!-- 商品销量排行：横向柱状图 -->
      <div class="chart-card paper-panel">
        <h3>商品销量排行</h3>
        <div ref="salesChart" class="chart-box"></div>
      </div>
      <!-- 分类销量占比：环形饼图 -->
      <div class="chart-card paper-panel">
        <h3>分类销量占比</h3>
        <div ref="categoryChart" class="chart-box"></div>
      </div>
      <!-- 包间预订统计：折线面积图 -->
      <div class="chart-card paper-panel">
        <h3>包间预订统计</h3>
        <div ref="bookingChart" class="chart-box"></div>
      </div>
      <!-- 订单状态分布：饼图（跨两列宽卡片） -->
      <div class="chart-card paper-panel chart-card-wide">
        <h3>订单状态分布</h3>
        <div ref="statusChart" class="chart-box"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
// ==================== 依赖导入 ====================
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
// ECharts 核心与组件按需引入，减小打包体积
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

// ==================== ECharts 组件注册 ====================
// 按需注册图表类型、组件、渲染器
use([BarChart, LineChart, PieChart, GridComponent, LegacyGridContainLabel, LegendComponent, TooltipComponent, CanvasRenderer])

// ==================== 响应式数据 ====================
const loading = ref(false)        // 全局加载状态
const dailyChart = ref(null)      // 每日订单趋势图DOM引用
const salesChart = ref(null)      // 销量排行图DOM引用
const categoryChart = ref(null)   // 分类占比图DOM引用
const bookingChart = ref(null)    // 包间预订图DOM引用
const statusChart = ref(null)     // 订单状态图DOM引用

// 存放所有已创建的图表实例，用于组件销毁时统一释放资源
let charts = []

// ==================== 图表主题配置 ====================
// 统一的图表文字样式，与整体茶馆风格协调
const chartTheme = {
  textStyle: {
    color: '#5b5b5b',
    fontFamily: 'Microsoft YaHei'
  }
}

// ==================== 工具方法 ====================
// 初始化单个图表：创建 ECharts 实例、设置配置项、加入管理数组
const initChart = (el, option) => {
  const chart = init(el, chartTheme)
  chart.setOption(option)
  charts.push(chart)              // 添加到数组便于后续统一销毁
  return chart
}

// ==================== 数据加载与图表渲染 ====================
// 并行请求所有统计数据，响应返回后渲染各图表
const loadData = async () => {
  loading.value = true
  try {
    // 使用 Promise.all 同时请求5个统计接口，提升加载速度
    const [daily, sales, category, bookings, orderStatus] = await Promise.all([
      statisticsApi.dailyOrders(7),  // 近7日订单趋势
      statisticsApi.salesRanking(),  // 商品销量排行
      statisticsApi.categorySales(), // 分类销量占比
      statisticsApi.roomBookings(),  // 包间预订统计
      statisticsApi.orderStatus()    // 订单状态分布
    ])

    // 等待 Vue 完成 DOM 更新后再初始化图表
    await nextTick()

    // ---- 图表1：每日订单趋势（柱状图 + 折线图双轴） ----
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

    // ---- 图表2：商品销量排行（横向柱状图） ----
    initChart(salesChart.value, {
      tooltip: { trigger: 'axis' },
      grid: { left: '4%', right: '8%', bottom: '5%', top: '6%', containLabel: true },
      xAxis: {
        type: 'value',
        splitLine: { lineStyle: { color: 'rgba(216, 210, 196, 0.8)' } }
      },
      yAxis: {
        type: 'category',
        data: sales.map((s) => s.name).reverse(),  // 反转使销量高的排在上面
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

    // ---- 图表3：分类销量占比（环形饼图） ----
    initChart(categoryChart.value, {
      tooltip: { trigger: 'item' },
      color: ['#2f4f3e', '#6b8e6e', '#8c6b3f', '#9f2d20', '#5c4033', '#b9ad98'],
      series: [{
        type: 'pie',
        radius: ['42%', '70%'],    // 内外半径形成环形效果
        itemStyle: { borderColor: '#fffaf2', borderWidth: 2 },
        label: { formatter: '{b}\n{d}%', color: '#5b5b5b' },
        data: category.map((c) => ({ name: c.name, value: c.value }))
      }]
    })

    // ---- 图表4：包间预订统计（折线面积图） ----
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
        // 渐变填充区域，使折线下方有半透明色块
        areaStyle: {
          color: new graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(72, 105, 87, 0.28)' },
            { offset: 1, color: 'rgba(72, 105, 87, 0.02)' }
          ])
        },
        lineStyle: { width: 3, color: '#486957' }
      }]
    })

    // ---- 图表5：订单状态分布（饼图） ----
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

// ==================== 生命周期钩子 ====================
// 页面挂载完成后立即加载数据并渲染图表
onMounted(loadData)

// 组件卸载前销毁所有图表实例，释放内存，防止内存泄漏
onBeforeUnmount(() => {
  charts.forEach((c) => c.dispose())
})
</script>

<style scoped>
/* ==================== 仪表盘头部样式 ==================== */
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

/* ==================== 图表网格布局：默认双列 ==================== */
.chart-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

/* 单个图表卡片 */
.chart-card {
  padding: 22px;
}

/* 宽卡片：跨两列显示 */
.chart-card-wide {
  grid-column: span 2;
}

.chart-card h3 {
  margin: 0 0 14px;
  color: var(--color-wood);
  font-size: 15px;
  letter-spacing: 0.12em;
}

/* 图表容器高度 */
.chart-box {
  width: 100%;
  height: 320px;
}

/* ==================== 响应式：小屏幕时单列布局 ==================== */
@media (max-width: 960px) {
  .chart-grid {
    grid-template-columns: 1fr;
  }

  .chart-card-wide {
    grid-column: span 1;
  }
}
</style>
