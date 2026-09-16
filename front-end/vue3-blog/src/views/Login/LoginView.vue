<template>
  <div class="login-page">
    <div class="login-bg"></div>
    <div class="login-overlay"></div>
    
    <div class="login-container">
      <div class="login-card animate-slide-up">
        <div class="login-header">
          <div class="login-logo">
            <Reading class="logo-icon" />
            <h1 class="logo-text">我的博客</h1>
          </div>
          <p class="login-subtitle">欢迎回来，开启创作之旅</p>
        </div>
        
        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group">
            <label class="form-label">
              <User class="label-icon" />
              用户名
            </label>
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              prefix-icon="User"
              size="large"
              class="form-input"
              :show-word-limit="false"
            />
          </div>
          
          <div class="form-group">
            <label class="form-label">
              <Lock class="label-icon" />
              密码
            </label>
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              size="large"
              class="form-input"
              :show-word-limit="false"
              @keyup.enter="handleLogin"
            />
          </div>
          
          <div class="form-options">
            <label class="checkbox-wrapper">
              <el-checkbox v-model="loginForm.remember" />
              <span>记住我</span>
            </label>
            <el-button type="text" class="captcha-link" @click="showCaptchaModal = true">
              <ChatDotRound class="link-icon" />
              使用验证码登录
            </el-button>
          </div>
          
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            @click="handleLogin"
            :loading="isLoading"
          >
            <Operation  class="btn-icon" />
            登录
          </el-button>
        </form>
        
        <div class="login-footer">
          <el-button type="text" class="footer-link" @click="goToForgotPassword">
            忘记密码？
          </el-button>
          <el-button type="text" class="footer-link primary" @click="goToRegister">
            立即注册
          </el-button>
        </div>
        
        <div class="divider">
          <span class="divider-text">或者</span>
        </div>
        
        <div class="social-login">
          <el-button type="default" class="social-btn google">
            <span class="social-icon">G</span>
            Google
          </el-button>
          <el-button type="default" class="social-btn github">
            <span class="social-icon">GH</span>
            GitHub
          </el-button>
        </div>
      </div>
    </div>
    
    <el-dialog
      v-model="showCaptchaModal"
      title="验证码登录"
      width="480px"
      :close-on-click-modal="true"
      class="captcha-dialog"
    >
      <div class="captcha-form">
        <div class="form-group">
          <label class="form-label">
            <Files class="label-icon" />
            邮箱
          </label>
          <el-input
            v-model="captchaForm.email"
            type="email"
            placeholder="请输入注册邮箱"
            prefix-icon="Mail"
            size="large"
            class="form-input"
          />
        </div>
        
        <div class="form-group">
          <label class="form-label">
            <Key class="label-icon" />
            验证码
          </label>
          <div class="code-input-wrapper">
            <el-input
              v-model="captchaForm.code"
              placeholder="请输入验证码"
              prefix-icon="Key"
              size="large"
              class="form-input code-input"
            />
            <el-button
              type="primary"
              :disabled="isSending"
              class="send-code-btn"
              @click="sendCaptcha"
            >
              {{ isSending ? '发送中...' : '发送验证码' }}
            </el-button>
          </div>
        </div>
        
        <div v-if="captchaMessage" class="error-message">{{ captchaMessage }}</div>
      </div>
      
      <template #footer>
        <el-button @click="showCaptchaModal = false">关闭</el-button>
        <el-button type="primary" @click="verifyCaptcha">验证登录</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { useUserInfoStore } from '@/stores/modules/userInfo.js'
import axiosAPI from '@/utils/api/axios.js'
import {
  ChatDotRound,
  Files,
  Key,
  Lock,
  Operation,
  Reading, User
} from '@element-plus/icons-vue'
import { ElButton, ElCheckbox, ElDialog, ElInput, ElMessage } from 'element-plus'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserInfoStore()

const loginForm = ref({
  username: '',
  password: '',
  remember: false
})

const captchaForm = ref({
  email: '',
  code: ''
})

const showCaptchaModal = ref(false)
const captchaMessage = ref('')
const isSending = ref(false)
const isLoading = ref(false)

const handleLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  
  isLoading.value = true
  try {
    const params = new URLSearchParams()
    params.append('username', loginForm.value.username)
    params.append('password', loginForm.value.password)
    params.append('remember', loginForm.value.remember)
    
    const response = await axiosAPI.post('/login', params, {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    })
    
    if (response.data.code === 1) {
      const userTempInfo = response.data.data.user
      const userToken = response.data.data.token
      userStore.setUserAndToken(userTempInfo, userToken)
      
      if (userTempInfo.userRole === 'admin') {
        ElMessage.success('管理员登录成功')
        await router.push('/newAdmin')
      } else {
        ElMessage.success('登录成功')
        await router.push('/home')
      }
    } else {
      ElMessage.error(response.data.msg || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error('登录失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}

const sendCaptcha = async () => {
  if (!captchaForm.value.email) {
    captchaMessage.value = '请输入邮箱'
    return
  }
  
  isSending.value = true
  try {
    const response = await axiosAPI.post('/auth/code2Email', {
      email: captchaForm.value.email
    })
    if (response.data.code === 1) {
      ElMessage.success('验证码发送成功')
      captchaMessage.value = ''
    } else {
      captchaMessage.value = response.data.msg || '发送失败'
    }
  } catch (error) {
    console.error('发送验证码失败:', error)
    captchaMessage.value = '发送验证码失败'
  } finally {
    isSending.value = false
  }
}

const verifyCaptcha = async () => {
  if (!captchaForm.value.email || !captchaForm.value.code) {
    captchaMessage.value = '请填写完整信息'
    return
  }
  
  try {
    const response = await axiosAPI.post('/auth/verifyCodeLogin', {
      email: captchaForm.value.email,
      code: captchaForm.value.code
    })
    
    if (response.data.code === 1) {
      userStore.setUserAndToken(response.data.data.user, response.data.data.token)
      showCaptchaModal.value = false
      ElMessage.success('登录成功')
      await router.push('/home')
    } else {
      captchaMessage.value = response.data.message || '验证失败'
    }
  } catch (error) {
    console.error('验证失败:', error)
    captchaMessage.value = '验证失败，请稍后重试'
  }
}

const goToRegister = () => {
  router.push('/register')
}

const goToForgotPassword = () => {
  router.push('/forgot-password')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: url('@/assets/backgrounds/bg1.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  z-index: 0;
}

.login-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(15, 23, 42, 0.7) 0%,
    rgba(71, 85, 105, 0.5) 50%,
    rgba(168, 85, 247, 0.3) 100%
  );
  z-index: 1;
}

.login-container {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 420px;
  padding: var(--spacing-lg);
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: var(--radius-2xl);
  padding: var(--spacing-2xl);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: var(--spacing-xl);
}

.login-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-md);
}

.logo-icon {
  width: 40px;
  height: 40px;
  color: var(--primary-500);
}

.logo-text {
  font-size: var(--text-2xl);
  font-weight: var(--font-extrabold);
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--secondary-500) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
}

.login-subtitle {
  color: var(--text-tertiary);
  font-size: var(--text-sm);
  margin: 0;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.form-label {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-secondary);
}

.label-icon {
  width: 16px;
  height: 16px;
}

.form-input {
  border-radius: var(--radius-lg);
}

:deep(.form-input .el-input__wrapper) {
  border-radius: var(--radius-lg);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

:deep(.form-input:focus-within .el-input__wrapper) {
  border-color: var(--primary-400);
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.15);
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.checkbox-wrapper {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  cursor: pointer;
}

:deep(.checkbox-wrapper .el-checkbox__label) {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

.captcha-link {
  font-size: var(--text-sm);
  color: var(--primary-500);
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.link-icon {
  width: 14px;
  height: 14px;
}

.login-btn {
  width: 100%;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--primary-600) 100%);
  font-weight: var(--font-semibold);
  padding: var(--spacing-md);
  transition: all var(--transition-fast);
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(249, 115, 22, 0.4);
}

.btn-icon {
  margin-right: var(--spacing-sm);
}

.login-footer {
  display: flex;
  justify-content: space-between;
  margin-top: var(--spacing-lg);
}

.footer-link {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

.footer-link.primary {
  color: var(--primary-500);
  font-weight: var(--font-medium);
}

.divider {
  display: flex;
  align-items: center;
  margin: var(--spacing-xl) 0;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: var(--border-color);
}

.divider-text {
  padding: 0 var(--spacing-md);
  color: var(--text-muted);
  font-size: var(--text-xs);
}

.social-login {
  display: flex;
  gap: var(--spacing-md);
}

.social-btn {
  flex: 1;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  transition: all var(--transition-fast);
}

.social-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.social-icon {
  width: 24px;
  height: 24px;
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: var(--font-bold);
}

.social-btn.google .social-icon {
  background: #4285f4;
  color: white;
}

.social-btn.github .social-icon {
  background: #333;
  color: white;
}

.captcha-dialog {
  border-radius: var(--radius-xl);
}

:deep(.captcha-dialog .el-dialog__header) {
  background: linear-gradient(135deg, var(--primary-50) 0%, var(--secondary-50) 100%);
  border-radius: var(--radius-xl) var(--radius-xl) 0 0;
  padding: var(--spacing-lg);
}

:deep(.captcha-dialog .el-dialog__title) {
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

:deep(.captcha-dialog .el-dialog__body) {
  padding: var(--spacing-xl);
}

.captcha-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.code-input-wrapper {
  display: flex;
  gap: var(--spacing-md);
}

.code-input {
  flex: 1;
}

.send-code-btn {
  white-space: nowrap;
}

.error-message {
  color: var(--danger);
  font-size: var(--text-sm);
  margin-top: var(--spacing-xs);
}

@media (max-width: 480px) {
  .login-card {
    padding: var(--spacing-xl);
  }
  
  .login-logo {
    flex-direction: column;
    gap: var(--spacing-xs);
  }
  
  .logo-text {
    font-size: var(--text-xl);
  }
  
  .social-login {
    flex-direction: column;
  }
}
</style>