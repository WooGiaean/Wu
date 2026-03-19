<script setup>
import { RouterView } from 'vue-router'
import './assets/css/global.css'
</script>

<template>
  <RouterView />
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', 'Microsoft YaHei', Tahoma, Geneva, Verdana, sans-serif;
  background: var(--bg-gradient);
  color: var(--text-primary);
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
  transition: background-color 0.3s ease, color 0.3s ease;
}

/* 星空背景效果 */
body::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
    radial-gradient(2px 2px at 20px 30px, rgba(255, 255, 255, 0.2), transparent),
    radial-gradient(2px 2px at 40px 70px, rgba(255, 255, 255, 0.15), transparent),
    radial-gradient(2px 2px at 50px 160px, rgba(255, 255, 255, 0.2), transparent),
    radial-gradient(2px 2px at 90px 40px, rgba(255, 255, 255, 0.15), transparent),
    radial-gradient(2px 2px at 130px 80px, rgba(255, 255, 255, 0.1), transparent),
    radial-gradient(2px 2px at 160px 120px, rgba(255, 255, 255, 0.15), transparent);
  background-repeat: repeat;
  background-size: 200px 200px;
  z-index: -1;
  animation: twinkle 10s infinite linear;
}

@keyframes twinkle {
  0% {
    opacity: 0.3;
  }
  50% {
    opacity: 0.8;
  }
  100% {
    opacity: 0.3;
  }
}
</style>
