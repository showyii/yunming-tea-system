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

const roomImageRules = [
  { match: ['听雨轩', '松涛居'], image: '/images/rooms/包间1.jpg' },
  { match: ['清风阁', '龙井厅'], image: '/images/rooms/包间2.jpg' },
  { match: ['揽月台', '云茗堂'], image: '/images/rooms/包间3.jpg' }
]

const activityImageRules = [
  { match: ['龙井', '点茶', '少儿茶礼'], image: '/images/activities/活动1.png' },
  { match: ['紫砂', '养生'], image: '/images/activities/活动 2.png' }
]

const defaultProductImage = '/images/products/main/绿茶–西湖龙井.png'
const defaultRoomImage = '/images/rooms/包间1.jpg'
const defaultActivityImage = '/images/activities/活动1.png'

const findRule = (name, rules) => {
  if (!name) return null
  return rules.find((rule) => rule.match.some((keyword) => name.includes(keyword))) || null
}

export const resolveProductImage = (productOrName, currentImage) => {
  if (currentImage) return currentImage
  const name = typeof productOrName === 'string' ? productOrName : productOrName?.name || productOrName?.productName
  return findRule(name, productImageRules)?.main || defaultProductImage
}

export const resolveProductGallery = (product) => {
  const gallery = (product?.images || []).filter(Boolean)
  if (gallery.length > 0) return gallery
  const name = product?.name || product?.productName
  const rule = findRule(name, productImageRules)
  if (rule?.gallery?.length) return rule.gallery
  return [resolveProductImage(product, product?.mainImage || product?.productImage)]
}

export const resolveRoomImage = (roomOrName, currentImage) => {
  if (currentImage) return currentImage
  const name = typeof roomOrName === 'string' ? roomOrName : roomOrName?.name || roomOrName?.roomName
  return findRule(name, roomImageRules)?.image || defaultRoomImage
}

export const resolveActivityImage = (activityOrTitle, currentImage) => {
  if (currentImage) return currentImage
  const title = typeof activityOrTitle === 'string' ? activityOrTitle : activityOrTitle?.title || activityOrTitle?.activityTitle
  return findRule(title, activityImageRules)?.image || defaultActivityImage
}
