<script setup>
import { defineProps } from 'vue'
const  props=defineProps({
  userInfo: {
    type: Object,
    default: () => ({})
  }
})
</script>

<template>
  <footer>
    <div class="footer-text">
      &copy; 2025 <span>{{ userInfo ? userInfo.userName : 'NoName' }}</span> | 个人博客 | 联系邮箱：{{ userInfo ? userInfo.userEmail : 'NoEmail' }}
    </div>
  </footer>
</template>

<style scoped>
/* --- 页脚 --- */
footer {
  background-color: var(--bg-secondary);
  padding: 30px;
  text-align: center;
  margin-top: 40px;
  border-top: 1px solid var(--border-color);
}

.footer-text {
  color: var(--text-secondary);
  font-size: 14px;
}

.footer-text span {
  color: var(--accent-color);
  font-weight: bold;
}

</style>
