<!--
  App.vue - Vue 3 根组件
  作为整个应用的容器，通过 <router-view /> 动态渲染当前路由对应的页面组件
  所有页面的公共外层结构（如导航栏、页脚）由各个页面组件自行引入
-->
<template>
  <!-- 应用根容器，class="page-shell" 用于全局样式定位 -->
  <div id="app" class="page-shell">
    <!-- router-view 是 Vue Router 提供的占位组件 -->
    <!-- 根据当前 URL 路径，动态渲染匹配的页面组件 -->
    <router-view />
  </div>
</template>
