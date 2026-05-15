<template>
  <div class="admin-page">
    <div class="page-header">
      <div class="page-header__meta">
        <span class="page-header__eyebrow">Tea Product Library</span>
        <h3>商品管理</h3>
      </div>
      <div class="page-actions">
        <el-button type="primary" @click="openDialog()">新增商品</el-button>
      </div>
    </div>

    <div class="admin-card-table">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="商品名称" width="180" />
        <el-table-column prop="price" label="价格" width="100" />
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" @click="openImages(row)">图片</el-button>
            <el-button size="small" type="danger" @click="doDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination">
      <el-pagination
        v-model:current-page="page"
        :total="total"
        :page-size="size"
        layout="total, prev, pager, next"
        @current-change="loadData"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑商品' : '新增商品'" width="720px">
      <el-form :model="form" label-width="92px" class="product-form">
        <div class="form-grid">
          <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
          <el-form-item label="副标题"><el-input v-model="form.subtitle" /></el-form-item>
          <el-form-item label="分类">
            <el-select v-model="form.categoryId">
              <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-radio-group v-model="form.status">
              <el-radio :value="1">上架</el-radio>
              <el-radio :value="0">下架</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
          <el-form-item label="原价"><el-input-number v-model="form.originalPrice" :min="0" :precision="2" /></el-form-item>
          <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" /></el-form-item>
          <el-form-item label="主图URL"><el-input v-model="form.mainImage" /></el-form-item>
        </div>

        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="标签">
          <div class="tag-row">
            <el-checkbox v-model="form.isHot" :true-value="1" :false-value="0">热门</el-checkbox>
            <el-checkbox v-model="form.isNew" :true-value="1" :false-value="0">新品</el-checkbox>
            <el-checkbox v-model="form.isRecommend" :true-value="1" :false-value="0">推荐</el-checkbox>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doSave">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="imageVisible" title="商品图片管理" width="560px">
      <p class="admin-section-note">商品图片会直接影响前台首页、商品列表和详情页的呈现，建议保持统一的色调、比例和清晰度。</p>
      <div class="image-list">
        <div v-for="img in images" :key="img.id" class="image-item">
          <span>{{ img.imageUrl }}</span>
          <el-button size="small" type="danger" @click="doDeleteImage(img.id)">删除</el-button>
        </div>
      </div>
      <div class="add-image">
        <el-input v-model="newImageUrl" placeholder="图片 URL" />
        <el-button type="primary" @click="doAddImage">添加</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminCategoryApi, adminProductApi } from '@/api/admin'

const tableData = ref([])
const categories = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({})
const editId = ref(null)

const imageVisible = ref(false)
const images = ref([])
const currentProductId = ref(null)
const newImageUrl = ref('')

const loadData = async () => {
  loading.value = true
  try {
    const res = await adminProductApi.list({ page: page.value, size: size.value })
    tableData.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

const openDialog = async (row) => {
  if (!categories.value.length) categories.value = await adminCategoryApi.list()
  if (row) {
    isEdit.value = true
    editId.value = row.id
    form.value = { ...row }
  } else {
    isEdit.value = false
    editId.value = null
    form.value = {
      name: '',
      subtitle: '',
      categoryId: null,
      price: 0,
      originalPrice: 0,
      stock: 0,
      status: 1,
      mainImage: '',
      description: '',
      isHot: 0,
      isNew: 1,
      isRecommend: 0
    }
  }
  dialogVisible.value = true
}

const doSave = async () => {
  if (isEdit.value) {
    await adminProductApi.update(editId.value, form.value)
  } else {
    await adminProductApi.create(form.value)
  }
  ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
  dialogVisible.value = false
  loadData()
}

const doDelete = async (id) => {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await adminProductApi.delete(id)
  ElMessage.success('删除成功')
  loadData()
}

const openImages = async (row) => {
  currentProductId.value = row.id
  images.value = await adminProductApi.getImages(row.id)
  newImageUrl.value = ''
  imageVisible.value = true
}

const doAddImage = async () => {
  if (!newImageUrl.value) return
  await adminProductApi.addImage(currentProductId.value, { imageUrl: newImageUrl.value, sort: 0 })
  ElMessage.success('添加成功')
  images.value = await adminProductApi.getImages(currentProductId.value)
  newImageUrl.value = ''
}

const doDeleteImage = async (imageId) => {
  await adminProductApi.deleteImage(imageId)
  ElMessage.success('删除成功')
  images.value = await adminProductApi.getImages(currentProductId.value)
}

onMounted(loadData)
</script>

<style scoped>
.product-form {
  padding-top: 4px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 16px;
}

.tag-row {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.image-list {
  display: grid;
  gap: 10px;
}

.image-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 12px;
  background: rgba(245, 241, 232, 0.72);
}

.image-item span {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--color-ink-soft);
  font-size: 13px;
}

.add-image {
  display: grid;
  grid-template-columns: 1fr 96px;
  gap: 10px;
  margin-top: 16px;
}

@media (max-width: 768px) {
  .form-grid,
  .add-image {
    grid-template-columns: 1fr;
  }
}
</style>
