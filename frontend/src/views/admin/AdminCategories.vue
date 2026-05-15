<template>
  <div class="admin-page">
    <div class="page-header">
      <div class="page-header__meta">
        <span class="page-header__eyebrow">Tea Taxonomy</span>
        <h3>分类管理</h3>
      </div>
      <div class="page-actions">
        <el-button type="primary" @click="openDialog()">新增分类</el-button>
      </div>
    </div>

    <div class="admin-card-table">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" width="180" />
        <el-table-column prop="icon" label="图标" width="100" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="doDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="560px">
      <el-form :model="form" label-width="84px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="图标"><el-input v-model="form.icon" placeholder="emoji 或图标文字" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" :max="999" /></el-form-item>
        <el-form-item label="上级分类">
          <el-select v-model="form.parentId" placeholder="无（一级分类）" clearable>
            <el-option v-for="c in allCategories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminCategoryApi } from '@/api/admin'

const tableData = ref([])
const allCategories = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({})
const editId = ref(null)

const loadData = async () => {
  loading.value = true
  try {
    tableData.value = await adminCategoryApi.list()
    allCategories.value = tableData.value
  } finally {
    loading.value = false
  }
}

const openDialog = (row) => {
  if (row) {
    isEdit.value = true
    editId.value = row.id
    form.value = { ...row }
  } else {
    isEdit.value = false
    editId.value = null
    form.value = { name: '', icon: '', description: '', sort: 0, parentId: null }
  }
  dialogVisible.value = true
}

const doSave = async () => {
  if (isEdit.value) {
    await adminCategoryApi.update(editId.value, form.value)
  } else {
    await adminCategoryApi.create(form.value)
  }
  ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
  dialogVisible.value = false
  loadData()
}

const doDelete = async (id) => {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await adminCategoryApi.delete(id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>
