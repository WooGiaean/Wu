<script setup>

</script>

<template>
  <footer>
    <div class="footer-text">
      &copy; 2025 <span>{{ userInfo ? userInfo.userName : 'JiaYn' }}</span> | 个人博客 | 联系邮箱：{{ userInfo ? userInfo.userEmail : '1317192941@qq.com' }}
    </div>
  </footer>
</template>

<style scoped>

</style>
