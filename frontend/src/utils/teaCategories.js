/**
 * 茶叶分类指南数据模块
 * 提供六大茶类的详细展示数据（图标、描述、标签、推荐语等）
 * 用于首页分类导航、分类选择引导等场景
 */

// ========== 默认分类展示数据 ==========
// 当某个分类没有配置数据时，使用此默认值
const defaultGuide = {
  mark: '茗', // 分类标记文字
  accent: '#6f553a', // 主题强调色
  tags: ['茶香', '慢品'], // 标签列表
  recommendation: '先按大致茶性缩小范围，再进茶品页慢慢细看，会更容易找到合口的那一款。',
  description: '从茶类看起，往往比直接翻整页茶单更容易找到此刻想喝的味道。',
  guideTitle: '适合先从熟悉的茶性看起',
  heroTitle: '顺着茶性与香气去挑，更容易遇见当下想喝的茶',
  scenarios: ['日常自饮', '待客闲聊'], // 适合场景
  examples: ['馆内常备茶', '时令推荐茶'], // 代表茶品
  linkText: '查看该类茶品' // 跳转链接文字
}

// ========== 六大茶类的详细展示数据 ==========
export const teaCategoryGuides = {
  // 绿茶：鲜爽清香的路线
  绿茶: {
    mark: '绿',
    accent: '#6a8b1d', // 绿色系
    tags: ['鲜爽', '清香', '轻盈'],
    recommendation: '想喝得鲜爽清润、口中利落些时，不妨先从绿茶看起。',
    description: '清鲜明快，入口爽利，适合喜欢轻盈茶感的时候细细去喝。',
    guideTitle: '先从鲜爽清润的路子看起',
    scenarios: ['晨起清口', '午前提神', '清淡配餐'],
    examples: ['龙井', '碧螺春', '毛尖']
  },
  // 红茶：温和甜润的路线
  红茶: {
    mark: '红',
    accent: '#c75d43', // 红棕色系
    tags: ['甜润', '醇和', '暖感'],
    recommendation: '想喝得暖一些、柔一些，入口顺顺地落下去时，可以先看红茶。',
    description: '茶汤温润，甜香安稳，适合日常慢饮，也适合待客时先端上一盏。',
    guideTitle: '想喝得柔和甘润，可以先看红茶',
    scenarios: ['午后配点心', '待客稳妥', '办公室常备'],
    examples: ['正山小种', '祁门红茶', '滇红']
  },
  // 乌龙茶：层次丰富的路线
  乌龙茶: {
    mark: '乌',
    accent: '#9d5b1f', // 棕褐色系
    tags: ['焙火', '花香', '层次'],
    recommendation: '若你想慢慢闻香、细细比一比回甘与层次，乌龙茶会很值得多停一会儿。',
    description: '香气有起伏，滋味有层层展开的意思，适合坐下来认真泡、认真喝。',
    guideTitle: '适合细闻香气，慢慢比较层次',
    scenarios: ['茶席待客', '与茶友同饮', '慢慢品香'],
    examples: ['大红袍', '铁观音', '单丛']
  },
  // 白茶：清柔平和的路线
  白茶: {
    mark: '白',
    accent: '#baa98d', // 浅驼色系
    tags: ['清甜', '柔和', '松弛'],
    recommendation: '想喝得柔和些、松快些，不想茶味太急太重时，可以先从白茶看起。',
    description: '气息清柔，落口安静，适合想让整个人慢下来时细细去品。',
    guideTitle: '想喝得清柔平和，可以从白茶开始',
    scenarios: ['晚间慢饮', '独处放松', '轻礼相赠'],
    examples: ['白毫银针', '白牡丹', '月光白']
  },
  // 黑茶：醇厚陈香的路线
  黑茶: {
    mark: '黑',
    accent: '#5a473d', // 深棕色系
    tags: ['陈香', '厚实', '耐泡'],
    recommendation: '若你偏爱厚一点、稳一点、越泡越出味的茶感，不妨先看看黑茶。',
    description: '茶汤醇厚，耐泡耐坐，适合饭后慢饮，也适合老茶客细细回味。',
    guideTitle: '偏爱醇厚与陈香，可以先看黑茶',
    scenarios: ['饭后慢饮', '秋冬常备', '老茶客复购'],
    examples: ['普洱', '茯砖', '六堡']
  },
  // 花茶：花香亲切的路线
  花茶: {
    mark: '花',
    accent: '#b68084', // 玫瑰色系
    tags: ['花香', '柔甜', '轻松'],
    recommendation: '想先从香气亲切、入口轻松的茶看起，花茶往往会更容易让人一下子喜欢上。',
    description: '花香先到，茶味随后，喝起来轻盈亲切，适合新手也适合闲坐时慢饮。',
    guideTitle: '想从香气亲切的茶入门，可以先看花茶',
    scenarios: ['新手入门', '午后闲聊', '轻松送礼'],
    examples: ['茉莉花茶', '桂花龙井', '玫瑰花茶']
  }
}

// ========== 快捷筛选按钮数据 ==========
// 首页分类引导区的快捷入口，帮助用户按口味偏好筛选茶类
export const categoryQuickFilters = [
  {
    id: 'all',
    label: '全部方向',
    description: '先把六类茶都慢慢看一遍',
    categoryNames: [] // 空数组表示不过滤
  },
  {
    id: 'fresh',
    label: '清鲜爽口',
    description: '更偏鲜爽、利落、轻盈',
    categoryNames: ['绿茶']
  },
  {
    id: 'gentle',
    label: '甘润柔和',
    description: '更偏甜润、柔和、好入口',
    categoryNames: ['红茶', '白茶']
  },
  {
    id: 'layered',
    label: '闻香看韵',
    description: '更适合慢慢闻香、细看层次',
    categoryNames: ['乌龙茶']
  },
  {
    id: 'rich',
    label: '醇厚耐坐',
    description: '更适合饭后慢饮与偏厚茶感',
    categoryNames: ['黑茶']
  },
  {
    id: 'friendly',
    label: '花香轻柔',
    description: '更适合初喝时慢慢亲近',
    categoryNames: ['花茶']
  },
  {
    id: 'gift',
    label: '送礼得体',
    description: '先看大多数人都容易接受的茶类',
    categoryNames: ['红茶', '乌龙茶', '花茶'] // 送礼推荐：红茶+乌龙茶+花茶
  }
]

/**
 * 根据分类对象生成跳转链接
 * @param {Object} category - 分类对象（需包含 id）
 * @returns {string} 商品列表页链接（带分类筛选参数）
 */
export const buildCategoryLink = (category) => `/products?categoryId=${category.id}`

/**
 * 获取分类的展示指南数据，合并默认值
 * @param {string} categoryName - 分类名称
 * @returns {Object} 完整的展示数据对象
 */
export const getCategoryGuide = (categoryName) => ({
  ...defaultGuide, // 先展开默认值
  ...(teaCategoryGuides[categoryName] || {}) // 用匹配的分类数据覆盖默认值
})

/**
 * 装饰分类列表：为每个分类附加展示数据（图标、颜色、标签等）和跳转链接
 * @param {Array} categories - 后端返回的分类数组
 * @returns {Array} 装饰后的分类数组
 */
export const decorateCategories = (categories = []) => categories.map((category) => {
  const guide = getCategoryGuide(category.name) // 获取该类茶的展示指南

  return {
    ...category, // 保留后端原始字段
    ...guide, // 合并展示数据
    description: category.description || guide.recommendation, // 优先使用后端描述
    href: buildCategoryLink(category) // 生成跳转链接
  }
})

/**
 * 根据筛选器 ID 过滤分类列表
 * @param {Array} categories - 装饰后的分类数组
 * @param {string} filterId - 筛选器 ID
 * @returns {Array} 过滤后的分类数组
 */
export const getQuickFilterMatches = (categories = [], filterId = 'all') => {
  if (filterId === 'all') return categories // "全部"不过滤

  const filter = categoryQuickFilters.find((item) => item.id === filterId)
  if (!filter) return categories // 未找到筛选器，返回全部

  // 保留分类名称在筛选器 categoryNames 列表中的分类
  return categories.filter((category) => filter.categoryNames.includes(category.name))
}
