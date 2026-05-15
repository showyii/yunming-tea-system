import test from 'node:test'
import assert from 'node:assert/strict'

import {
  categoryQuickFilters,
  buildCategoryLink,
  decorateCategories,
  getCategoryGuide,
  getQuickFilterMatches
} from './teaCategories.js'

test('getCategoryGuide returns configured guide data for known categories', () => {
  const guide = getCategoryGuide('绿茶')

  assert.equal(guide.mark, '绿')
  assert.equal(guide.accent, '#6a8b1d')
  assert.deepEqual(guide.tags, ['鲜爽', '清香', '轻盈'])
  assert.equal(guide.recommendation, '想喝得鲜爽清润、口中利落些时，不妨先从绿茶看起。')
})

test('getCategoryGuide falls back to generic guide data for unknown categories', () => {
  const guide = getCategoryGuide('黄茶')

  assert.equal(guide.mark, '茗')
  assert.equal(guide.accent, '#6f553a')
  assert.deepEqual(guide.tags, ['茶香', '慢品'])
  assert.equal(guide.heroTitle, '顺着茶性与香气去挑，更容易遇见当下想喝的茶')
})

test('buildCategoryLink builds product list link with category id', () => {
  assert.equal(buildCategoryLink({ id: 6 }), '/products?categoryId=6')
})

test('decorateCategories merges backend data with guide metadata', () => {
  const result = decorateCategories([
    { id: 2, name: '红茶', description: '' }
  ])

  assert.equal(result.length, 1)
  assert.equal(result[0].mark, '红')
  assert.equal(result[0].linkText, '查看该类茶品')
  assert.equal(result[0].description, '想喝得暖一些、柔一些，入口顺顺地落下去时，可以先看红茶。')
  assert.equal(result[0].href, '/products?categoryId=2')
})

test('category quick filters expose guide options for the page', () => {
  assert.equal(categoryQuickFilters[0].id, 'all')
  assert.equal(categoryQuickFilters[0].label, '全部方向')
  assert.equal(categoryQuickFilters[1].categoryNames.includes('绿茶'), true)
})

test('getQuickFilterMatches returns only categories included in the chosen profile', () => {
  const result = getQuickFilterMatches([
    { id: 1, name: '绿茶' },
    { id: 2, name: '红茶' },
    { id: 3, name: '乌龙茶' }
  ], 'fresh')

  assert.deepEqual(result.map((item) => item.name), ['绿茶'])
})

test('getQuickFilterMatches returns all categories for the all profile', () => {
  const source = [
    { id: 1, name: '绿茶' },
    { id: 2, name: '红茶' }
  ]

  const result = getQuickFilterMatches(source, 'all')

  assert.equal(result.length, 2)
  assert.deepEqual(result, source)
})
