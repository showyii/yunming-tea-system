<!--
  AdminActivities.vue - 活动管理页面
  用途：管理茶馆举办的各类茶事活动（茶艺课、品鉴会等）。
  功能：活动列表展示、新增/编辑活动（弹窗表单）、删除活动。
  说明：活动包含标题、地点、开始/结束时间、最大参与人数、
        费用、封面图、描述等字段，状态分为"进行中"和"已结束"。
-->
<template>
  <div class="admin-page">
    <!-- ==================== 页面头部：标题 + 新增按钮 ==================== -->
    <div class="page-header">
      <div class="page-header__meta">
        <span class="page-header__eyebrow">Tea Event Board</span>
        <h3>活动管理</h3>
      </div>
      <div class="page-actions">
        <el-button type="primary" @click="openDialog()">新增活动</el-button>
      </div>
    </div>

    <!-- ==================== 活动数据表格 ==================== -->
    <div class="admin-card-table">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" width="180" />
        <el-table-column prop="location" label="地点" width="140" />
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="maxParticipants" label="人数" width="80" />
        <!-- 状态列：进行中(绿色) / 已结束(灰色) -->
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '进行中' : '已结束' }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 操作列：编辑 / 删除 -->
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="doDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- ==================== 新增/编辑活动弹窗 ==================== -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑活动' : '新增活动'" width="600px">
      <el-form :model="form" label-width="80px">
        <!-- 活动标题 -->
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <!-- 活动地点 -->
        <el-form-item label="地点"><el-input v-model="form.location" /></el-form-item>
        <!-- 开始时间：日期时间选择器 -->
        <el-form-item label="开始时间">
          <el-date-picker v-model="form.startTime" type="datetime" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DDTHH:mm:ss" />
        </el-form-item>
        <!-- 结束时间：日期时间选择器 -->
        <el-form-item label="结束时间">
          <el-date-picker v-model="form.endTime" type="datetime" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DDTHH:mm:ss" />
        </el-form-item>
        <!-- 最大参与人数 -->
        <el-form-item label="最大人数"><el-input-number v-model="form.maxParticipants" :min="1" /></el-form-item>
        <!-- 活动费用（元） -->
        <el-form-item label="费用"><el-input-number v-model="form.fee" :min="0" :precision="2" /></el-form-item>
        <!-- 活动状态 -->
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">进行中</el-radio>
            <el-radio :value="0">已结束</el-radio>
          </el-radio-group>
        </el-form-item>
        <!-- 封面图片URL -->
        <el-form-item label="封面URL"><el-input v-model="form.coverImage" /></el-form-item>
        <!-- 活动描述：多行文本 -->
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <!-- 弹窗底部按钮 -->
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// ==================== 依赖导入 ====================
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminActivityApi } from '@/api/admin'

// ==================== 响应式数据 ====================
const tableData = ref([])            // 活动列表数据
const loading = ref(false)           // 表格加载状态
const dialogVisible = ref(false)     // 弹窗是否可见
const isEdit = ref(false)            // 是否为编辑模式
const form = ref({})                 // 表单数据对象
const editId = ref(null)             // 编辑中的活动ID

// ==================== 方法 ====================

// 加载活动列表
const loadData = async () => {
  loading.value = true
  try { tableData.value = await adminActivityApi.list() } finally { loading.value = false }
}

// 打开新增/编辑弹窗
const openDialog = (row) => {
  if (row) {
    // 编辑模式：填充已有数据
    isEdit.value = true
    editId.value = row.id
    form.value = { ...row }
  } else {
    // 新增模式：初始化默认值
    isEdit.value = false
    editId.value = null
    form.value = {
      title: '',
      location: '',
      startTime: '',
      endTime: '',
      maxParticipants: 20,   // 默认最大参与人数20
      fee: 0,                // 默认免费
      status: 1,             // 默认进行中
      coverImage: '',
      description: ''
    }
  }
  dialogVisible.value = true
}

// 保存活动（新增或更新）
const doSave = async () => {
  if (isEdit.value) {
    await adminActivityApi.update(editId.value, form.value)
  } else {
    await adminActivityApi.create(form.value)
  }
  ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
  dialogVisible.value = false
  loadData()  // 刷新列表
}

// 删除活动（需确认）
const doDelete = async (id) => {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await adminActivityApi.delete(id)
  ElMessage.success('删除成功')
  loadData()  // 刷新列表
}

// ==================== 生命周期：页面加载时获取数据 ====================
onMounted(loadData)
</script>
