<!--
  AdminRooms.vue - 包间管理页面
  用途：管理茶馆包间（茶室）的信息，支持包间的增删改操作。
  功能：包间列表展示、新增/编辑包间（弹窗表单）、删除包间。
  说明：包间包含名称、类型、容量（人数）、时价（元/小时）、
        图片、设施描述、可用状态等字段。
-->
<template>
  <div class="admin-page">
    <!-- ==================== 页面头部：标题 + 新增按钮 ==================== -->
    <div class="page-header">
      <div class="page-header__meta">
        <span class="page-header__eyebrow">Private Room Catalog</span>
        <h3>包间管理</h3>
      </div>
      <div class="page-actions">
        <el-button type="primary" @click="openDialog()">新增包间</el-button>
      </div>
    </div>

    <!-- ==================== 包间数据表格 ==================== -->
    <div class="admin-card-table">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="名称" width="140" />
        <el-table-column prop="type" label="类型" width="120" />
        <el-table-column prop="capacity" label="容量" width="80" />
        <el-table-column prop="pricePerHour" label="时价(元)" width="100" />
        <!-- 状态列：可用(绿色) / 禁用(灰色) -->
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '可用' : '禁用' }}
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

    <!-- ==================== 新增/编辑包间弹窗 ==================== -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑包间' : '新增包间'" width="500px">
      <el-form :model="form" label-width="80px">
        <!-- 包间名称 -->
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <!-- 包间类型（如：雅间、大厅、 VIP室等） -->
        <el-form-item label="类型"><el-input v-model="form.type" /></el-form-item>
        <!-- 容量（可容纳人数） -->
        <el-form-item label="容量"><el-input-number v-model="form.capacity" :min="1" /></el-form-item>
        <!-- 时价（元/小时） -->
        <el-form-item label="时价"><el-input-number v-model="form.pricePerHour" :min="0" :precision="2" /></el-form-item>
        <!-- 包间图片URL -->
        <el-form-item label="图片URL"><el-input v-model="form.image" /></el-form-item>
        <!-- 设施描述（如：茶具、投影仪、WiFi等） -->
        <el-form-item label="设施"><el-input v-model="form.facilities" /></el-form-item>
        <!-- 使用状态 -->
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">可用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
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
import { adminRoomApi } from '@/api/admin'

// ==================== 响应式数据 ====================
const tableData = ref([])            // 包间列表数据
const loading = ref(false)           // 表格加载状态
const dialogVisible = ref(false)     // 弹窗是否可见
const isEdit = ref(false)            // 是否为编辑模式
const form = ref({})                 // 表单数据对象
const editId = ref(null)             // 编辑中的包间ID

// ==================== 方法 ====================

// 加载包间列表
const loadData = async () => {
  loading.value = true
  try { tableData.value = await adminRoomApi.list() } finally { loading.value = false }
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
      name: '',
      type: '',
      capacity: 4,           // 默认容量4人
      pricePerHour: 0,       // 默认时价0元
      image: '',
      facilities: '',
      status: 1              // 默认可用
    }
  }
  dialogVisible.value = true
}

// 保存包间（新增或更新）
const doSave = async () => {
  if (isEdit.value) {
    await adminRoomApi.update(editId.value, form.value)
  } else {
    await adminRoomApi.create(form.value)
  }
  ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
  dialogVisible.value = false
  loadData()  // 刷新列表
}

// 删除包间（需确认）
const doDelete = async (id) => {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await adminRoomApi.delete(id)
  ElMessage.success('删除成功')
  loadData()  // 刷新列表
}

// ==================== 生命周期：页面加载时获取数据 ====================
onMounted(loadData)
</script>
