<!--
  AdminProducts.vue - 商品管理页面
  用途：管理茶馆所有茶品/商品的增删改查，以及商品图片的管理。
  功能：商品列表（分页展示）、新增/编辑商品（弹窗表单）、
        删除商品、管理商品图片（添加/删除图片URL）。
  说明：商品包含名称、价格、库存、分类、标签（热门/新品/推荐）等字段。
-->
<template>
  <div class="admin-page">
    <!-- ==================== 页面头部：标题 + 新增按钮 ==================== -->
    <div class="page-header">
      <div class="page-header__meta">
        <span class="page-header__eyebrow">Tea Product Library</span>
        <h3>商品管理</h3>
      </div>
      <div class="page-actions">
        <el-button type="primary" @click="openDialog()">新增商品</el-button>
      </div>
    </div>

    <!-- ==================== 商品数据表格 ==================== -->
    <div class="admin-card-table">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="商品名称" width="180" />
        <el-table-column prop="price" label="价格" width="100" />
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="sales" label="销量" width="80" />
        <!-- 状态列：上架(绿色) / 下架(灰色) -->
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 操作列：编辑 / 图片管理 / 删除 -->
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" @click="openImages(row)">图片</el-button>
            <el-button size="small" type="danger" @click="doDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- ==================== 分页组件 ==================== -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="page"
        :total="total"
        :page-size="size"
        layout="total, prev, pager, next"
        @current-change="loadData"
      />
    </div>

    <!-- ==================== 新增/编辑商品弹窗 ==================== -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑商品' : '新增商品'" width="720px">
      <el-form :model="form" label-width="92px" class="product-form">
        <!-- 双列网格布局的基础字段 -->
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

        <!-- 描述字段：多行文本 -->
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="4" /></el-form-item>
        <!-- 标签字段：复选框组（热门/新品/推荐） -->
        <el-form-item label="标签">
          <div class="tag-row">
            <el-checkbox v-model="form.isHot" :true-value="1" :false-value="0">热门</el-checkbox>
            <el-checkbox v-model="form.isNew" :true-value="1" :false-value="0">新品</el-checkbox>
            <el-checkbox v-model="form.isRecommend" :true-value="1" :false-value="0">推荐</el-checkbox>
          </div>
        </el-form-item>
      </el-form>
      <!-- 弹窗底部按钮 -->
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- ==================== 商品图片管理弹窗 ==================== -->
    <el-dialog v-model="imageVisible" title="商品图片管理" width="560px">
      <p class="admin-section-note">商品图片会直接影响前台首页、商品列表和详情页的呈现，建议保持统一的色调、比例和清晰度。</p>
      <!-- 已有图片列表 -->
      <div class="image-list">
        <div v-for="img in images" :key="img.id" class="image-item">
          <span>{{ img.imageUrl }}</span>
          <el-button size="small" type="danger" @click="doDeleteImage(img.id)">删除</el-button>
        </div>
      </div>
      <!-- 添加新图片 -->
      <div class="add-image">
        <el-input v-model="newImageUrl" placeholder="图片 URL" />
        <el-button type="primary" @click="doAddImage">添加</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
// ==================== 依赖导入 ====================
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminCategoryApi, adminProductApi } from '@/api/admin'

// ==================== 响应式数据 ====================
const tableData = ref([])            // 商品列表表格数据
const categories = ref([])           // 分类下拉选项数据
const loading = ref(false)           // 表格加载状态
const page = ref(1)                  // 当前页码
const size = ref(10)                 // 每页条数
const total = ref(0)                 // 总记录数

// 商品编辑弹窗相关状态
const dialogVisible = ref(false)     // 弹窗是否可见
const isEdit = ref(false)            // 是否为编辑模式（false=新增模式）
const form = ref({})                 // 表单数据对象
const editId = ref(null)             // 编辑中的商品ID

// 图片管理弹窗相关状态
const imageVisible = ref(false)      // 图片弹窗是否可见
const images = ref([])               // 当前商品的图片列表
const currentProductId = ref(null)   // 当前操作的商品ID
const newImageUrl = ref('')          // 新图片URL输入值

// ==================== 方法 ====================

// 加载商品列表（分页数据）
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

// 打开新增/编辑弹窗
// row 参数：传入时为编辑模式，不传时为新增模式
const openDialog = async (row) => {
  // 确保分类数据已加载（用于下拉选项）
  if (!categories.value.length) categories.value = await adminCategoryApi.list()
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
      subtitle: '',
      categoryId: null,
      price: 0,
      originalPrice: 0,
      stock: 0,
      status: 1,          // 默认上架
      mainImage: '',
      description: '',
      isHot: 0,
      isNew: 1,           // 默认标记为新品
      isRecommend: 0
    }
  }
  dialogVisible.value = true
}

// 保存商品（新增或更新）
const doSave = async () => {
  if (isEdit.value) {
    await adminProductApi.update(editId.value, form.value)
  } else {
    await adminProductApi.create(form.value)
  }
  ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
  dialogVisible.value = false
  loadData()  // 刷新列表
}

// 删除商品（需确认）
const doDelete = async (id) => {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await adminProductApi.delete(id)
  ElMessage.success('删除成功')
  loadData()  // 刷新列表
}

// 打开图片管理弹窗
const openImages = async (row) => {
  currentProductId.value = row.id
  images.value = await adminProductApi.getImages(row.id)
  newImageUrl.value = ''
  imageVisible.value = true
}

// 添加商品图片
const doAddImage = async () => {
  if (!newImageUrl.value) return      // 空白URL不做处理
  await adminProductApi.addImage(currentProductId.value, { imageUrl: newImageUrl.value, sort: 0 })
  ElMessage.success('添加成功')
  images.value = await adminProductApi.getImages(currentProductId.value)  // 刷新图片列表
  newImageUrl.value = ''
}

// 删除商品图片
const doDeleteImage = async (imageId) => {
  await adminProductApi.deleteImage(imageId)
  ElMessage.success('删除成功')
  images.value = await adminProductApi.getImages(currentProductId.value)  // 刷新图片列表
}

// ==================== 生命周期：页面加载时获取数据 ====================
onMounted(loadData)
</script>

<style scoped>
/* ==================== 商品表单样式 ==================== */
.product-form {
  padding-top: 4px;
}

/* 表单字段双列网格布局 */
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 16px;
}

/* 标签复选框行：水平排列 */
.tag-row {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

/* ==================== 图片管理样式 ==================== */
/* 图片列表：纵向排列 */
.image-list {
  display: grid;
  gap: 10px;
}

/* 单张图片条目 */
.image-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 12px;
  background: rgba(245, 241, 232, 0.72);
}

/* 图片URL文字溢出省略 */
.image-item span {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--color-ink-soft);
  font-size: 13px;
}

/* 添加图片区域：输入框 + 按钮 */
.add-image {
  display: grid;
  grid-template-columns: 1fr 96px;
  gap: 10px;
  margin-top: 16px;
}

/* ==================== 响应式：小屏幕单列布局 ==================== */
@media (max-width: 768px) {
  .form-grid,
  .add-image {
    grid-template-columns: 1fr;
  }
}
</style>
