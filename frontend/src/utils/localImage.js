/**
 * 本地图片路径解析工具
 *
 * 由于项目使用本地图片资源（而非远程 CDN），后端数据库中的图片路径可能不完整。
 * 此模块通过名称匹配规则，将商品/包间/活动名称映射到正确的本地图片路径。
 * 同时提供兜底默认图片，确保任何情况下都能显示图片。
 */

// ========== 商品图片匹配规则 ==========
// match：商品名称关键词（匹配任一关键词即命中）
// main：商品主图路径
// gallery：商品详情图库列表
const productImageRules = [
  { match: ['西湖龙井'], main: '/images/products/main/绿茶–西湖龙井.png', gallery: ['/images/products/gallery/绿茶–西湖龙井-01.png', '/images/products/gallery/绿茶–西湖龙井-02.png'] },
  { match: ['碧螺春'], main: '/images/products/main/绿茶–碧螺春.png', gallery: ['/images/products/gallery/绿茶–碧螺春-01.png', '/images/products/gallery/绿茶–碧螺春-02.png'] },
  { match: ['信阳毛尖'], main: '/images/products/main/绿茶–信阳毛尖.png', gallery: ['/images/products/gallery/绿茶–信阳毛尖-01.png', '/images/products/gallery/绿茶–信阳毛尖-02.png'] },
  { match: ['正山小种'], main: '/images/products/main/红茶-正山小种.jpg', gallery: ['/images/products/gallery/红茶-正山小种-01.jpg', '/images/products/gallery/红茶-正山小种-02.jpg'] },
  { match: ['祁门红茶'], main: '/images/products/main/红茶–祁门红茶.png', gallery: ['/images/products/gallery/红茶–祁门红茶-01.png', '/images/products/gallery/红茶–祁门红茶-02.png'] },
  { match: ['滇红'], main: '/images/products/main/红茶–滇红.png', gallery: ['/images/products/gallery/红茶–滇红-01.png', '/images/products/gallery/红茶–滇红-02.png'] },
  { match: ['大红袍'], main: '/images/products/main/乌龙茶–大红袍.png', gallery: ['/images/products/gallery/乌龙茶–大红袍-01.png', '/images/products/gallery/乌龙茶–大红袍-02.png'] },
  { match: ['铁观音'], main: '/images/products/main/乌龙茶–铁观音.png', gallery: ['/images/products/gallery/乌龙茶–铁观音-01.png', '/images/products/gallery/乌龙茶–铁观音-02.png'] },
  { match: ['鸭屎香', '单丛'], main: '/images/products/main/乌龙茶–黄金桂.png', gallery: ['/images/products/gallery/乌龙茶–黄金桂-01.png', '/images/products/gallery/乌龙茶–黄金桂-02.png'] },
  { match: ['白毫银针'], main: '/images/products/main/白茶–白毫银针.png', gallery: ['/images/products/gallery/白茶–白毫银针-01.png', '/images/products/gallery/白茶–白毫银针-02.png'] },
  { match: ['白牡丹'], main: '/images/products/main/白茶–白牡丹.jpg', gallery: ['/images/products/gallery/白茶–白牡丹-01.jpg', '/images/products/gallery/白茶–白牡丹-02.jpg'] },
  { match: ['月光白'], main: '/images/products/main/白茶–荒野白茶.png', gallery: ['/images/products/gallery/白茶–荒野白茶-01.png', '/images/products/gallery/白茶–荒野白茶-02.png'] },
  { match: ['普洱'], main: '/images/products/main/黑茶–湖南安化黑茶.jpg', gallery: ['/images/products/gallery/黑茶–湖南安化黑茶-01.jpg', '/images/products/gallery/黑茶–湖南安化黑茶-02.jpg'] },
  { match: ['茯砖'], main: '/images/products/main/黑茶–安化黑砖茶.jpg', gallery: ['/images/products/gallery/黑茶–安化黑砖茶-01.jpg', '/images/products/gallery/黑茶–安化黑砖茶-02.jpg'] },
  { match: ['六堡茶'], main: '/images/products/main/黑茶–六堡茶.jpg', gallery: ['/images/products/gallery/黑茶–六堡茶-01.jpg', '/images/products/gallery/黑茶–六堡茶-02.jpg'] },
  { match: ['茉莉花茶'], main: '/images/products/main/花茶–茉莉花茶.png', gallery: ['/images/products/gallery/花茶–茉莉花茶-01.png', '/images/products/gallery/花茶–茉莉花茶-02.png'] },
  { match: ['桂花龙井'], main: '/images/products/main/花茶–桂花龙井.png', gallery: ['/images/products/gallery/花茶–桂花龙井-01.png', '/images/products/gallery/花茶–桂花龙井-02.png'] },
  { match: ['玫瑰花茶'], main: '/images/products/main/花茶–玫瑰红茶.png', gallery: ['/images/products/gallery/花茶–玫瑰红茶-01.png', '/images/products/gallery/花茶–玫瑰红茶-02.png'] }
]

// ========== 包间图片匹配规则 ==========
const roomImageRules = [
  { match: ['听雨轩', '松涛居'], image: '/images/rooms/包间1.jpg' },
  { match: ['清风阁', '龙井厅'], image: '/images/rooms/包间2.jpg' },
  { match: ['揽月台', '云茗堂'], image: '/images/rooms/包间3.jpg' }
]

// ========== 活动图片匹配规则 ==========
const activityImageRules = [
  { match: ['龙井', '点茶', '少儿茶礼'], image: '/images/activities/活动1.png' },
  { match: ['紫砂', '养生'], image: '/images/activities/活动 2.png' }
]

// ========== 默认兜底图片 ==========
const defaultProductImage = '/images/products/main/绿茶–西湖龙井.png' // 默认商品图
const defaultRoomImage = '/images/rooms/包间1.jpg' // 默认包间图
const defaultActivityImage = '/images/activities/活动1.png' // 默认活动图

/**
 * 在规则数组中查找匹配项
 * @param {string} name - 要匹配的名称
 * @param {Array} rules - 规则数组
 * @returns {Object|null} 匹配的规则对象，未找到返回 null
 */
const findRule = (name, rules) => {
  if (!name) return null // 名称为空时直接返回 null
  // 在规则数组中查找：规则的 match 关键词中只要有任一关键词包含在 name 中即匹配
  return rules.find((rule) => rule.match.some((keyword) => name.includes(keyword))) || null
}

/**
 * 解析商品主图路径
 * 优先使用已有的 currentImage，其次按名称匹配规则，最后使用默认图
 */
export const resolveProductImage = (productOrName, currentImage) => {
  if (currentImage) return currentImage // 已经有图片路径，直接使用
  // 提取商品名称（兼容传入对象或字符串）
  const name = typeof productOrName === 'string' ? productOrName : productOrName?.name || productOrName?.productName
  return findRule(name, productImageRules)?.main || defaultProductImage // 按规则匹配，未匹配到使用默认图
}

/**
 * 解析商品详情图库
 * 优先使用已有的 images，其次按名称匹配，最后使用主图作为唯一图片
 */
export const resolveProductGallery = (product) => {
  const gallery = (product?.images || []).filter(Boolean) // 过滤掉空值
  if (gallery.length > 0) return gallery // 后端已返回图库
  const name = product?.name || product?.productName
  const rule = findRule(name, productImageRules)
  if (rule?.gallery?.length) return rule.gallery // 按规则匹配的图库
  // 图库为空时，将主图作为唯一图片返回
  return [resolveProductImage(product, product?.mainImage || product?.productImage)]
}

/**
 * 解析包间图片路径
 */
export const resolveRoomImage = (roomOrName, currentImage) => {
  if (currentImage) return currentImage
  const name = typeof roomOrName === 'string' ? roomOrName : roomOrName?.name || roomOrName?.roomName
  return findRule(name, roomImageRules)?.image || defaultRoomImage
}

/**
 * 解析活动封面图片路径
 */
export const resolveActivityImage = (activityOrTitle, currentImage) => {
  if (currentImage) return currentImage
  const title = typeof activityOrTitle === 'string' ? activityOrTitle : activityOrTitle?.title || activityOrTitle?.activityTitle
  return findRule(title, activityImageRules)?.image || defaultActivityImage
}
