/**
 * 茶叶分类工具函数单元测试
 * 使用 Node.js 内置的 test 和 assert 模块
 * 运行方式：node --test frontend/src/utils/teaCategories.test.js
 */

// 导入 Node.js 原生测试框架
import test from 'node:test'       // test 函数：定义测试用例
import assert from 'node:assert/strict'  // assert 断言库（严格模式）

// 从被测试模块中导入需要测试的工具函数和数据
import {
  categoryQuickFilters,     // 快捷筛选配置
  buildCategoryLink,       // 构建分类链接
  decorateCategories,      // 用引导数据装饰分类
  getCategoryGuide,        // 获取分类引导数据
  getQuickFilterMatches    // 获取快捷筛选匹配结果
} from './teaCategories.js'

/**
 * 测试 getCategoryGuide：已知分类应返回正确的引导数据
 * 验证绿茶分类的标记字、强调色、标签和推荐语
 */
test('getCategoryGuide returns configured guide data for known categories', () => {
  const guide = getCategoryGuide('绿茶')  // 获取绿茶的引导数据

  assert.equal(guide.mark, '绿')                              // 绿茶标记字应为"绿"
  assert.equal(guide.accent, '#6a8b1d')                       // 绿茶强调色
  assert.deepEqual(guide.tags, ['鲜爽', '清香', '轻盈'])        // 绿茶标签
  assert.equal(guide.recommendation, '想喝得鲜爽清润、口中利落些时，不妨先从绿茶看起。')  // 推荐语
})

/**
 * 测试 getCategoryGuide：未知分类应返回兜底的通用引导数据
 * 验证黄茶（未配置）时返回默认的标记字和通用推荐语
 */
test('getCategoryGuide falls back to generic guide data for unknown categories', () => {
  const guide = getCategoryGuide('黄茶')  // 黄茶不在六大茶类中，应触发兜底逻辑

  assert.equal(guide.mark, '茗')                             // 兜底标记字
  assert.equal(guide.accent, '#6f553a')                      // 兜底强调色
  assert.deepEqual(guide.tags, ['茶香', '慢品'])              // 兜底标签
  assert.equal(guide.heroTitle, '顺着茶性与香气去挑，更容易遇见当下想喝的茶')  // 兜底推荐标题
})

/**
 * 测试 buildCategoryLink：根据分类对象构建带 categoryId 参数的查询链接
 */
test('buildCategoryLink builds product list link with category id', () => {
  assert.equal(buildCategoryLink({ id: 6 }), '/products?categoryId=6')  // 验证生成的链接格式
})

/**
 * 测试 decorateCategories：将后端分类数据与前端引导元数据合并
 * 验证合并后的数组包含标记字、链接文本、引导描述和跳转链接
 */
test('decorateCategories merges backend data with guide metadata', () => {
  // 模拟后端返回的红茶分类数据（缺少前端引导信息）
  const result = decorateCategories([
    { id: 2, name: '红茶', description: '' }
  ])

  assert.equal(result.length, 1)                                              // 应返回 1 条结果
  assert.equal(result[0].mark, '红')                                          // 标记字应为"红"
  assert.equal(result[0].linkText, '查看该类茶品')                             // 默认链接文本
  assert.equal(result[0].description, '想喝得暖一些、柔一些，入口顺顺地落下去时，可以先看红茶。')  // 合并后的引导描述
  assert.equal(result[0].href, '/products?categoryId=2')                      // 自动生成的跳转链接
})

/**
 * 测试 categoryQuickFilters：快捷筛选配置应包含"全部方向"和"清新派"等筛选选项
 */
test('category quick filters expose guide options for the page', () => {
  assert.equal(categoryQuickFilters[0].id, 'all')                            // 第一个筛选器 ID 为 "all"
  assert.equal(categoryQuickFilters[0].label, '全部方向')                     // 标签为"全部方向"
  assert.equal(categoryQuickFilters[1].categoryNames.includes('绿茶'), true)  // 第二个筛选器应包含绿茶
})

/**
 * 测试 getQuickFilterMatches：按筛选配置过滤分类列表
 * "fresh" 筛选器应只返回绿茶
 */
test('getQuickFilterMatches returns only categories included in the chosen profile', () => {
  // 模拟三个分类，使用 "fresh" 筛选器
  const result = getQuickFilterMatches([
    { id: 1, name: '绿茶' },
    { id: 2, name: '红茶' },
    { id: 3, name: '乌龙茶' }
  ], 'fresh')

  assert.deepEqual(result.map((item) => item.name), ['绿茶'])  // "fresh" 只匹配绿茶
})

/**
 * 测试 getQuickFilterMatches："all" 筛选器应返回全部分类
 */
test('getQuickFilterMatches returns all categories for the all profile', () => {
  // 模拟两个分类
  const source = [
    { id: 1, name: '绿茶' },
    { id: 2, name: '红茶' }
  ]

  const result = getQuickFilterMatches(source, 'all')  // 使用 "all" 筛选器

  assert.equal(result.length, 2)             // 应返回全部 2 条
  assert.deepEqual(result, source)           // 应与原始数组相同
})
