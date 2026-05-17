<!--
  AdminCategories.vue - 分类管理页面
  用途：管理茶馆商品的分类体系（如绿茶、红茶、乌龙茶等）。
  功能：分类列表展示、新增/编辑分类（弹窗表单）、删除分类。
  说明：分类支持层级结构（通过 parentId 字段关联上级分类），
        可设置名称、图标、描述、排序等属性。
-->
<template>
  <div class="admin-page">
    <!-- ==================== 页面头部：标题 + 新增按钮 ==================== -->
    <div class="page-header">
      <div class="page-header__meta">
        <span class="page-header__eyebrow">Tea Taxonomy</span>
        <h3>分类管理</h3>
      </div>
      <div class="page-actions">
        <el-button type="primary" @click="openDialog()">新增分类</el-button>
      </div>
    </div>

    <!-- ==================== 分类数据表格 ==================== -->
    <div class="admin-card-table">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" width="180" />
        <el-table-column prop="icon" label="图标" width="100" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="sort" label="排序" width="80" />
        <!-- 操作列：编辑 / 删除 -->
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="doDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- ==================== 新增/编辑分类弹窗 ==================== -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="560px">
      <el-form :model="form" label-width="84px">
        <!-- 分类名称 -->
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <!-- 图标：支持 emoji 或图标文字 -->
        <el-form-item label="图标"><el-input v-model="form.icon" placeholder="emoji 或图标文字" /></el-form-item>
        <!-- 分类描述 -->
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <!-- 排序值：数字越小越靠前 -->
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" :max="999" /></el-form-item>
        <!-- 上级分类：支持选择父级分类形成层级结构 -->
        <el-form-item label="上级分类">
          <el-select v-model="form.parentId" placeholder="无（一级分类）" clearable>
            <el-option v-for="c in allCategories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
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
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminCategoryApi } from '@/api/admin'

// ==================== 响应式数据 ====================
const tableData = ref([])            // 分类列表数据
const allCategories = ref([])        // 所有分类（用于上级分类下拉选项）
const loading = ref(false)           // 表格加载状态
const dialogVisible = ref(false)     // 弹窗是否可见
const isEdit = ref(false)            // 是否为编辑模式
const form = ref({})                 // 表单数据对象
const editId = ref(null)             // 编辑中的分类ID

// ==================== 方法 ====================

// 加载分类列表
const loadData = async () => {
  loading.value = true
  try {
    tableData.value = await adminCategoryApi.list()
    allCategories.value = tableData.value   // 同步用于上级分类选择
  } finally {
    loading.value = false
  }
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
    form.value = { name: '', icon: '', description: '', sort: 0, parentId: null }
  }
  dialogVisible.value = true
}

// 保存分类（新增或更新）
const doSave = async () => {
  if (isEdit.value) {
    await adminCategoryApi.update(editId.value, form.value)
  } else {
    await adminCategoryApi.create(form.value)
  }
  ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
  dialogVisible.value = false
  loadData()  // 刷新列表
}

// 删除分类（需确认）
const doDelete = async (id) => {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await adminCategoryApi.delete(id)
  ElMessage.success('删除成功')
  loadData()  // 刷新列表
}

// ==================== 生命周期：页面加载时获取数据 ====================
onMounted(loadData)
</script>
