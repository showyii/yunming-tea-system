/**
 * Vue Router 路由配置文件
 *
 * 定义了整个应用的路由表，包含：
 * - 用户端路由（首页、分类、商品、购物车、订单、收藏、包间、活动等）
 * - 管理后台路由（嵌套在 /admin 下的仪表盘、用户、商品、订单等管理页面）
 * - 路由守卫：根据 localStorage 中的 token 判断登录/管理员权限
 */

// 从 vue-router 导入路由创建方法
import { createRouter, createWebHistory } from 'vue-router'

// 路由表：每个对象定义一条路由规则
const routes = [
  // ===== 用户端公开页面 =====

  // 首页：网站入口页面
  {
    path: '/',           // URL 路径：根路径
    name: 'Home',        // 路由名称（用于编程式导航）
    component: () => import('@/views/user/Home.vue')  // 懒加载：访问时才加载组件
  },
  // 茶品分类页：展示六大茶类
  {
    path: '/categories',
    name: 'Categories',
    component: () => import('@/views/user/Categories.vue')
  },
  // 全部商品列表页：支持搜索、筛选、排序
  {
    path: '/products',
    name: 'ProductList',
    component: () => import('@/views/user/ProductList.vue')
  },
  // 商品详情页：动态路由参数 :id 表示商品 ID
  {
    path: '/products/:id',
    name: 'ProductDetail',
    component: () => import('@/views/user/ProductDetail.vue')
  },
  // 登录页
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/user/Login.vue')
  },
  // 注册页
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/user/Register.vue')
  },
  // 包间预约页：展示所有可预约的雅间
  {
    path: '/rooms',
    name: 'Rooms',
    component: () => import('@/views/user/Rooms.vue')
  },
  // 茶事活动列表页
  {
    path: '/activities',
    name: 'Activities',
    component: () => import('@/views/user/Activities.vue')
  },
  // 活动详情页：动态路由参数 :id
  {
    path: '/activities/:id',
    name: 'ActivityDetail',
    component: () => import('@/views/user/ActivityDetail.vue')
  },

  // ===== 用户端需登录页面（meta.requiresAuth = true） =====

  // 我的收藏页
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('@/views/user/Favorites.vue'),
    meta: { requiresAuth: true }  // 路由元信息：标记需要登录才能访问
  },
  // 购物车页
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/user/Cart.vue'),
    meta: { requiresAuth: true }
  },
  // 订单列表页
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('@/views/user/Orders.vue'),
    meta: { requiresAuth: true }
  },
  // 订单详情页
  {
    path: '/orders/:id',
    name: 'OrderDetail',
    component: () => import('@/views/user/OrderDetail.vue'),
    meta: { requiresAuth: true }
  },
  // 我的包间预约页
  {
    path: '/my-bookings',
    name: 'MyBookings',
    component: () => import('@/views/user/MyBookings.vue'),
    meta: { requiresAuth: true }
  },
  // 我的活动报名页
  {
    path: '/my-signups',
    name: 'MySignups',
    component: () => import('@/views/user/MySignups.vue'),
    meta: { requiresAuth: true }
  },
  // 用户中心页：个人信息、修改密码等
  {
    path: '/user-center',
    name: 'UserCenter',
    component: () => import('@/views/user/UserCenter.vue'),
    meta: { requiresAuth: true }
  },

  // ===== 管理后台路由 =====

  // 管理员登录页（独立于用户登录页）
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/AdminLogin.vue')
  },
  // 管理后台布局容器：包含侧边栏、顶栏等框架
  {
    path: '/admin',                     // 管理后台根路径
    name: 'AdminLayout',
    component: () => import('@/views/admin/AdminLayout.vue'),  // 布局组件作为父路由
    meta: { requiresAdmin: true },      // 标记：需要管理员权限
    // 嵌套路由：所有子页面在 AdminLayout 的 <router-view> 中渲染
    children: [
      // 空路径重定向到仪表盘
      {
        path: '',
        name: 'AdminRedirect',
        redirect: '/admin/dashboard'    // 访问 /admin 自动跳转仪表盘
      },
      // 管理仪表盘：显示统计数据
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/AdminDashboard.vue')
      },
      // 用户管理：查看和管理注册用户
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/AdminUsers.vue')
      },
      // 分类管理：增删改查茶叶分类
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('@/views/admin/AdminCategories.vue')
      },
      // 商品管理：增删改查茶品
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('@/views/admin/AdminProducts.vue')
      },
      // 订单管理：查看和处理订单
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/AdminOrders.vue')
      },
      // 评论管理：审核和删除用户评论
      {
        path: 'comments',
        name: 'AdminComments',
        component: () => import('@/views/admin/AdminComments.vue')
      },
      // 包间管理：增删改查雅间
      {
        path: 'rooms',
        name: 'AdminRooms',
        component: () => import('@/views/admin/AdminRooms.vue')
      },
      // 包间预约管理：查看和处理预约
      {
        path: 'room-bookings',
        name: 'AdminRoomBookings',
        component: () => import('@/views/admin/AdminRoomBookings.vue')
      },
      // 活动管理：增删改查茶事活动
      {
        path: 'activities',
        name: 'AdminActivities',
        component: () => import('@/views/admin/AdminActivities.vue')
      },
      // 活动报名管理：查看活动报名情况
      {
        path: 'activity-signups',
        name: 'AdminActivitySignups',
        component: () => import('@/views/admin/AdminActivitySignups.vue')
      }
    ]
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),  // 使用 HTML5 History 模式（无 # 号）
  routes                        // 传入路由表
})

/**
 * 全局前置路由守卫
 * 在每次路由切换前执行，用于权限校验：
 * - requiresAuth：检查用户 token，未登录则跳转登录页
 * - requiresAdmin：检查管理员 token，未登录则跳转管理登录页
 */
router.beforeEach((to, from, next) => {
  // 从 localStorage 读取用户登录 token
  const token = localStorage.getItem('token')
  // 从 localStorage 读取管理员登录 token
  const adminToken = localStorage.getItem('admin_token')

  // 如果目标路由需要用户登录权限但无 token
  if (to.meta.requiresAuth && !token) {
    next('/login')               // 重定向到登录页
  }
  // 如果目标路由需要管理员权限但无 admin_token
  else if (to.meta.requiresAdmin && !adminToken) {
    next('/admin/login')         // 重定向到管理登录页
  }
  // 权限校验通过，放行
  else {
    next()
  }
})

// 导出路由实例供 main.js 注册使用
export default router
