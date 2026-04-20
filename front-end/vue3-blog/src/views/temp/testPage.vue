<script setup>

</script>

<template>
  <!-- 顶部导航 -->
  <div class="header">
    <div class="nav">
      <div class="logo">我的博客</div>
      <div class="nav-menu">
        <a href="#">首页</a>
        <a href="#">博客</a>
        <a href="#">分类</a>
        <a href="#">关于我</a>
      </div>
    </div>
  </div>

  <!-- 主体内容 -->
  <div class="container">
    <!-- 左侧边栏 -->
    <div class="sidebar">
      <!-- 个人信息 -->
      <div class="card user-info">
        <div class="user-avatar"></div>
        <div class="user-name">你的昵称</div>
        <div class="user-desc">专注编程、学习、分享</div>
      </div>

      <!-- 文章分类 -->
      <div class="card">
        <div class="sidebar-title">文章分类</div>
        <ul class="category-list">
          <li>Java 编程</li>
          <li>SpringCloud</li>
          <li>前端开发</li>
          <li>数据库</li>
        </ul>
      </div>

      <!-- 标签云 -->
      <div class="card">
        <div class="sidebar-title">热门标签</div>
        <div class="tags">
          <span class="tag">Java</span>
          <span class="tag">微服务</span>
          <span class="tag">SpringBoot</span>
          <span class="tag">前端</span>
          <span class="tag">MySQL</span>
        </div>
      </div>
    </div>

    <!-- 右侧主内容 -->
    <div class="main">
      <div class="article-card">
        <div class="article-title">微服务架构学习笔记（一）：核心概念</div>
        <div class="article-meta">发布时间：2026-04-07 | 阅读：100+</div>
        <div class="article-content">
          微服务是一种架构风格，将一个大型应用拆分为多个小型、自治、可独立部署的服务。每个服务运行在自己的进程中，服务之间通过轻量级机制通信...
        </div>
      </div>

      <div class="article-card">
        <div class="article-title">SpringCloud Alibaba 最全学习路线</div>
        <div class="article-meta">发布时间：2026-04-06 | 阅读：200+</div>
        <div class="article-content">
          SpringCloud Alibaba 是国内最主流的微服务解决方案，包含 Nacos、Sentinel、Seata 等核心组件，适合企业级开发使用...
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: "Microsoft YaHei", sans-serif;
}

body {
  background-color: #f5f7fa;
  color: #333;
  line-height: 1.6;
}

/* ========== 顶部导航栏 ========== */
.header {
  background-color: #2d3748;
  color: white;
  padding: 16px 5%;
  position: sticky;
  top: 0;
  z-index: 99;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.nav {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 22px;
  font-weight: bold;
}

.nav-menu a {
  color: white;
  text-decoration: none;
  margin-left: 24px;
  font-size: 16px;
}

.nav-menu a:hover {
  color: #63b3ed;
}

/* ========== 主体布局 ========== */
.container {
  max-width: 1400px;
  margin: 30px auto;
  padding: 0 5%;
  display: flex;
  gap: 25px;
}

/* ========== 左侧边栏 ========== */
.sidebar {
  width: 280px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}

.user-info {
  text-align: center;
}

.user-avatar {
  width: 90px;
  height: 90px;
  background: #63b3ed;
  border-radius: 50%;
  margin: 0 auto 12px;
}

.user-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 6px;
}

.user-desc {
  font-size: 14px;
  color: #666;
}

/* 分类 / 标签 */
.sidebar-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}

.category-list {
  list-style: none;
}

.category-list li {
  padding: 6px 0;
  font-size: 15px;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  background: #e2e8f0;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 13px;
}

/* ========== 右侧主内容区 ========== */
.main {
  flex: 1;
}

.article-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}

.article-title {
  font-size: 20px;
  font-weight: bold;
  color: #2d3748;
  margin-bottom: 10px;
}

.article-meta {
  font-size: 13px;
  color: #718096;
  margin-bottom: 12px;
}

.article-content {
  font-size: 15px;
  color: #4a5568;
  line-height: 1.8;
}

/* ========== 响应式（手机适配） ========== */
@media (max-width: 768px) {
  .container {
    flex-direction: column;
  }
  .sidebar {
    width: 100%;
  }
}
</style>
