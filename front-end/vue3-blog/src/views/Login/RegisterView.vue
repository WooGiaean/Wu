<!--
<template>
  <div class="register-container">
    <h2>用户注册</h2>
    <el-form :model="registerForm" class="register-form">
      <el-form-item label="用户名" required>
        <el-input v-model="registerForm.username" placeholder="请输入用户名" />
      </el-form-item>

      <el-form-item label="昵称" required>
        <el-input v-model="registerForm.userNickname" placeholder="请输入昵称" />
      </el-form-item>

      <el-form-item label="邮箱" required>
        <el-input v-model="registerForm.userEmail" type="email" placeholder="请输入邮箱地址" />
      </el-form-item>

      <el-form-item label="密码" required>
        <el-input v-model="registerForm.userPassword" type="password" placeholder="请输入密码" show-password @input="checkPasswordStrength" />
        <div class="mt-2" :style="{ color: passwordStrength.color }">{{ passwordStrength.message }}</div>
      </el-form-item>

      <el-form-item label="确认密码" required>
        <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码" show-password />
      </el-form-item>

      <el-form-item>
        <el-checkbox v-model="registerForm.agreeTerms">我已阅读并同意<a href="#" style="color: #667eea;">服务条款</a>和<a href="#" style="color: #667eea;">隐私政策</a></el-checkbox>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleRegister" class="register-button">注册账号</el-button>
      </el-form-item>
    </el-form>

    <div class="alternative-register">
      <span>或</span>
    </div>

    <el-button type="info" @click="showEmailRegisterModal = true" class="email-register-button">邮箱验证码注册</el-button>

    <p>已有账号？<a href="#" @click.prevent="goToLogin">请登录</a></p>
  </div>

  &lt;!&ndash; 邮箱验证码注册模态框 &ndash;&gt;
  <el-dialog
    v-model="showEmailRegisterModal"
    title="邮箱验证码注册"
    width="500px"
  >
    <el-form :model="emailRegisterForm">
      <el-form-item label="邮箱" required>
        <el-input v-model="emailRegisterForm.email" type="email" placeholder="请输入邮箱地址">
          <template #append>
            <el-button @click="sendCaptcha" :disabled="isSendingCaptcha">{{ sendCaptchaText }}</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="验证码" required>
        <el-input v-model="emailRegisterForm.captchaCode" placeholder="请输入验证码" />
      </el-form-item>
      <el-form-item label="用户名" required>
        <el-input v-model="emailRegisterForm.userName" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item label="昵称" required>
        <el-input v-model="emailRegisterForm.userNickname" placeholder="请输入昵称" />
      </el-form-item>
      <el-form-item label="密码" required>
        <el-input v-model="emailRegisterForm.userPassword" type="password" placeholder="请输入密码" show-password @input="checkEmailPasswordStrength" />
        <div class="mt-2" :style="{ color: emailPasswordStrength.color }">{{ emailPasswordStrength.message }}</div>
      </el-form-item>
      <el-form-item label="确认密码" required>
        <el-input v-model="emailRegisterForm.confirmPassword" type="password" placeholder="请确认密码" show-password />
      </el-form-item>
      <el-form-item>
        <el-checkbox v-model="emailRegisterForm.agreeTerms">我已阅读并同意
          <a href="#" style="color: #667eea;">服务条款</a>
          和<a href="#" style="color: #667eea;">隐私政策</a>
        </el-checkbox>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showEmailRegisterModal = false">取消</el-button>
        <el-button type="primary" @click="handleEmailRegister">注册</el-button>
      </span>
    </template>
  </el-dialog>

  &lt;!&ndash; 注册成功提示模态框 &ndash;&gt;
  <el-dialog
    v-model="showSuccessModal"
    title="注册成功"
    width="400px"
  >
    <div class="text-center">
      <div style="font-size: 48px; color: #28a745; margin-bottom: 20px;">🎉</div>
      <h4>欢迎加入我们！</h4>
      <p class="text-muted">您的账号已成功注册，现在可以开始使用我们的博客系统了。</p>
      <div class="mt-4">
        <h6>推荐下一步操作：</h6>
        <ul class="list-unstyled text-left mt-2">
          <li>• 完善个人资料</li>
          <li>• 发布第一篇博客</li>
          <li>• 关注其他博主</li>
        </ul>
      </div>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showSuccessModal = false">稍后再说</el-button>
        <el-button type="primary" @click="goToLogin">去登录</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
//import { useRouter } from 'vue-router'
import router from '@/router/index.js'
import axios from 'axios'
//import { useUserInfoStore } from '@/stores/modules/userInfo.js'
import {allStores} from "@/stores/index.js";
import { ElForm, ElFormItem, ElInput, ElButton, ElDialog, ElCheckbox, ElMessage } from 'element-plus'
import { User, EditPen, Message } from '@element-plus/icons-vue'
const userStore=allStores.useUserInfoStore();
//const router = useRouter()

// 主注册表单数据
const registerForm = ref({
  username: '',
  userNickname: '',
  userEmail: '',
  userPassword: '',
  confirmPassword: '',
  agreeTerms: false
})

// 邮箱验证码注册表单数据
const emailRegisterForm = ref({
  email: '',
  captchaCode: '',
  userName: '',
  userNickname: '',
  userPassword: '',
  confirmPassword: '',
  agreeTerms: false
})

// 模态框状态
const showEmailRegisterModal = ref(false)
const showSuccessModal = ref(false)

// 密码强度
const passwordStrength = ref({
  message: '请输入密码',
  color: '#999'
})

const emailPasswordStrength = ref({
  message: '请输入密码',
  color: '#999'
})

// 发送验证码状态
const isSendingCaptcha = ref(false)
const sendCaptchaText = ref('发送验证码')

// 密码强度检测
const checkPasswordStrength = (event) => {
  const password = event.target.value
  let strength = 0
  let message = ''
  let color = ''

  if (password.length >= 8) strength++
  if (/[A-Z]/.test(password)) strength++
  if (/[a-z]/.test(password)) strength++
  if (/[0-9]/.test(password)) strength++
  if (/[^A-Za-z0-9]/.test(password)) strength++

  switch(strength) {
    case 0:
      message = '请输入密码'
      color = '#999'
      break
    case 1:
    case 2:
      message = '密码强度：弱'
      color = '#dc3545'
      break
    case 3:
      message = '密码强度：中'
      color = '#ffc107'
      break
    case 4:
    case 5:
      message = '密码强度：强'
      color = '#28a745'
      break
  }

  passwordStrength.value = { message, color }
}

// 邮箱注册密码强度检测
const checkEmailPasswordStrength = (event) => {
  const password = event.target.value
  let strength = 0
  let message = ''
  let color = ''

  if (password.length >= 8) strength++
  if (/[A-Z]/.test(password)) strength++
  if (/[a-z]/.test(password)) strength++
  if (/[0-9]/.test(password)) strength++
  if (/[^A-Za-z0-9]/.test(password)) strength++

  switch(strength) {
    case 0:
      message = '请输入密码'
      color = '#999'
      break
    case 1:
    case 2:
      message = '密码强度：弱'
      color = '#dc3545'
      break
    case 3:
      message = '密码强度：中'
      color = '#ffc107'
      break
    case 4:
    case 5:
      message = '密码强度：强'
      color = '#28a745'
      break
  }

  emailPasswordStrength.value = { message, color }
}

// 发送验证码
const sendCaptcha = async () => {
  const email = emailRegisterForm.value.email
  if (!email) {
    ElMessage.error('请输入邮箱地址')
    return
  }

  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    ElMessage.error('请输入有效的邮箱地址')
    return
  }

  isSendingCaptcha.value = true
  sendCaptchaText.value = '发送中...'

  try {
    const response = await axios.post('/auth/code2Email', {
      email: email
    })

    if (response.data.code === 1) {
      ElMessage.success('验证码已发送到您的邮箱')
      // 60秒后恢复发送按钮
      let countdown = 60
      sendCaptchaText.value = `${countdown}秒后重试`
      const timer = setInterval(() => {
        countdown&#45;&#45;
        sendCaptchaText.value = `${countdown}秒后重试`
        if (countdown <= 0) {
          clearInterval(timer)
          isSendingCaptcha.value = false
          sendCaptchaText.value = '发送验证码'
        }
      }, 1000)
    } else {
      ElMessage.error('发送验证码失败：' + response.data.message)
      isSendingCaptcha.value = false
      sendCaptchaText.value = '发送验证码'
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('发送验证码失败，请稍后重试')
    isSendingCaptcha.value = false
    sendCaptchaText.value = '发送验证码'
  }
}

// 处理注册
const handleRegister = async () => {
  // 表单验证
  if (!registerForm.value.username || !registerForm.value.userNickname || !registerForm.value.userPassword || !registerForm.value.confirmPassword || !registerForm.value.userEmail) {
    ElMessage.error('请填写完整信息')
    return
  }

  if (registerForm.value.userPassword !== registerForm.value.confirmPassword) {
    ElMessage.error('两次密码不一致，请重新填写！')
    return
  }

  if (!registerForm.value.agreeTerms) {
    ElMessage.error('请阅读并同意服务条款和隐私政策')
    return
  }

  try {
    const response = await axios.post('/auth/register', {
      userName: registerForm.value.username,
      userNickname: registerForm.value.userNickname,
      userPassword: registerForm.value.userPassword,
      userEmail: registerForm.value.userEmail
    })

    if (response.data.code === 1) {
      // 注册成功后显示模态框
      showSuccessModal.value = true
    } else {
      ElMessage.error('注册失败，' + response.data.message)
    }
  } catch (error) {
    console.log(error)
    if (error.response) {
      ElMessage.error('注册失败：' + error.response.data.message)
    } else {
      ElMessage.error('注册失败，请稍后重试')
    }
  }
}

// 处理邮箱验证码注册
const handleEmailRegister = async () => {
  // 表单验证
  if (!emailRegisterForm.value.email || !emailRegisterForm.value.captchaCode || !emailRegisterForm.value.userName || !emailRegisterForm.value.userNickname || !emailRegisterForm.value.userPassword || !emailRegisterForm.value.confirmPassword) {
    ElMessage.error('请填写完整信息')
    return
  }

  if (emailRegisterForm.value.userPassword !== emailRegisterForm.value.confirmPassword) {
    ElMessage.error('两次密码不一致，请重新填写！')
    return
  }

  if (!emailRegisterForm.value.agreeTerms) {
    ElMessage.error('请阅读并同意服务条款和隐私政策')
    return
  }

  try {
    const response = await axios.post('/auth/register', {
      email: emailRegisterForm.value.email,
      captchaCode: emailRegisterForm.value.captchaCode,
      userName: emailRegisterForm.value.userName,
      userNickname: emailRegisterForm.value.userNickname,
      userPassword: emailRegisterForm.value.userPassword
    })

    if (response.data.code === 1) {
      // 注册成功后显示模态框
      showSuccessModal.value = true
      // 关闭邮箱注册模态框
      showEmailRegisterModal.value = false
    } else {
      ElMessage.error('注册失败，' + response.data.message)
    }
  } catch (error) {
    console.log(error)
    if (error.response) {
      ElMessage.error('注册失败：' + error.response.data.message)
    } else {
      ElMessage.error('注册失败，请稍后重试')
    }
  }
}

// 跳转到登录页面
const goToLogin = () => {
  router.push('/login')
}
</script>

&lt;!&ndash;<style scoped>
body {
  font-family: 'Arial', sans-serif;
  background: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)),
  url('https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=minimalist%20blog%20background%20with%20soft%20colors%20and%20subtle%20patterns&image_size=landscape_16_9');
  background-size: cover;
  background-position: center;
  margin: 0;
  padding: 0;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.register-container {
  background: white;
  padding: 30px 40px;
  border-radius: 15px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
  width: 100%;
  max-width: 400px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.register-container::before {
  content: '';
  position: absolute;
  top: -5px;
  left: -5px;
  right: -5px;
  bottom: -5px;
  background: linear-gradient(135deg, #d4b996, #f2e9dc, #d4b996);
  border-radius: 20px;
  z-index: -1;
}

h2 {
  color: #333;
  margin-bottom: 25px;
  font-weight: 500;
}

/* Element Plus 表单样式调整 */
:deep(.el-form-item__label) {
  color: #555;
}

:deep(.el-button&#45;&#45;primary) {
  background: linear-gradient(135deg, #d4b996 0%, #d4b996 100%);
  border-color: #d4b996;
}

:deep(.el-button&#45;&#45;info) {
  background: linear-gradient(135deg, #6c757d 0%, #6c757d 100%);
  border-color: #6c757d;
}

.register-button,
.email-register-button {
  width: 100%;
  padding: 12px 0;
  font-size: 16px;
  margin-bottom: 10px;
}

.alternative-register {
  margin: 15px 0;
  position: relative;
}

.alternative-register::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: #ddd;
  z-index: 0;
}

.alternative-register span {
  background: white;
  padding: 0 10px;
  position: relative;
  z-index: 1;
  color: #999;
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .register-container {
    padding: 20px;
    margin: 0 20px;
  }

  h2 {
    font-size: 24px;
  }

  .register-button,
  .email-register-button {
    padding: 10px 0;
    font-size: 14px;
  }
}
</style>&ndash;&gt;
<style scoped>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', 'Microsoft YaHei', Tahoma, Geneva, Verdana, sans-serif;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin: 0;
  padding: 0;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

/* 注册容器 */
.register-container {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

/* 注册表单 */
.register-form {
  background: #ffffff;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 420px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.register-form:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
}

/* 表单头部 */
.form-header {
  text-align: center;
  margin-bottom: 30px;
}

.form-header h2 {
  color: #333;
  margin-bottom: 10px;
  font-size: 28px;
  font-weight: 700;
}

.form-header p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

/* 表单内容 */
.register-form-content {
  padding: 0;
}

/* 表单组 */
:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-form-item__label) {
  color: #333;
  font-size: 14px;
  font-weight: 500;
  text-align: left;
}

/* 输入框容器 */
.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 15px;
  color: #999;
  pointer-events: none;
  z-index: 1;
}

.input-icon svg {
  width: 18px;
  height: 18px;
}

/* Element Plus 输入框样式调整 */
:deep(.el-input__wrapper) {
  padding: 12px 15px 12px 40px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
  box-shadow: none !important;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  background-color: #fff;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1) !important;
}

:deep(.el-input__inner) {
  font-size: 14px;
}

/* 密码强度提示 */
.password-strength {
  margin-top: 8px;
  font-size: 12px;
  text-align: left;
}

/* 服务条款组 */
.terms-group {
  margin-bottom: 25px;
}

.terms-group :deep(.el-checkbox) {
  color: #666;
  font-size: 14px;
}

/* 注册按钮 */
.register-button {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #E1BE97 0%, #664401 100%);
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

/* Element Plus 按钮样式调整 */
:deep(.el-button&#45;&#45;primary) {
  background: linear-gradient(135deg, #E1BE97 0%, #664401 100%);
  border-color: #E1BE97;
}

:deep(.el-button&#45;&#45;primary:hover) {
  background: linear-gradient(135deg, #E1BE97 0%, #664401 100%);
  border-color: #E1BE97;
  opacity: 0.9;
}

:deep(.el-button&#45;&#45;info) {
  background: #f9f9f9;
  color: #333;
  border-color: #ddd;
}

:deep(.el-button&#45;&#45;info:hover) {
  background: #f0f0f0;
  border-color: #ddd;
}

/* 表单底部 */
.form-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 20px;
}

.footer-text {
  color: #666;
  font-size: 14px;
}

.footer-link {
  background: none;
  border: none;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  padding: 0;
  transition: color 0.3s ease;
}

.footer-link:hover {
  color: #333;
}

.footer-link.primary {
  color: #667eea;
  font-weight: 500;
}

.footer-link.primary:hover {
  color: #5a6fd8;
  text-decoration: underline;
}

/* 分隔线 */
.divider {
  display: flex;
  align-items: center;
  margin: 30px 0;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background-color: #eee;
}

.divider span {
  padding: 0 15px;
  color: #999;
  font-size: 14px;
}

/* 其他注册方式 */
.alternative-register {
  width: 100%;
}

.email-register-button {
  width: 100%;
  padding: 14px;
  font-size: 16px;
  font-weight: 500;
}

/* Element Plus 对话框样式调整 */
:deep(.el-dialog) {
  border-radius: 12px;
}

:deep(.el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid #eee;
}

:deep(.el-dialog__title) {
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

:deep(.el-dialog__footer) {
  padding: 20px 24px;
  border-top: 1px solid #eee;
}

.custom-dialog :deep(.el-button&#45;&#45;primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
}

.custom-dialog :deep(.el-button&#45;&#45;primary:hover) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .register-form {
    padding: 30px 20px;
  }

  .form-header h2 {
    font-size: 24px;
  }

  .register-button {
    padding: 12px;
    font-size: 15px;
  }

  .custom-dialog :deep(.el-dialog) {
    width: 95% !important;
  }

  .custom-dialog :deep(.el-dialog__header),
  .custom-dialog :deep(.el-dialog__body),
  .custom-dialog :deep(.el-dialog__footer) {
    padding: 16px;
  }
}
</style>
-->
<template>
  <div class="register-container">
    <div class="register-form">
      <div class="form-header">
        <h2>注册</h2>
        <p>创建新账号，开启您的博客之旅！</p>
      </div>

      <!-- 表单提交 -->
      <form @submit.prevent="handleRegister" class="register-form-content">
        <div class="form-group">
          <label for="username">用户名</label>
          <div class="input-wrapper">
            <User class="input-icon" />
            <input
              type="text"
              id="username"
              v-model="registerForm.username"
              placeholder="请输入用户名"
              class="form-input"
              required
            >
          </div>
        </div>

        <div class="form-group">
          <label for="nickname">昵称</label>
          <div class="input-wrapper">
            <EditPen class="input-icon" />
            <input
              type="text"
              id="nickname"
              v-model="registerForm.userNickname"
              placeholder="请输入昵称"
              class="form-input"
              required
            >
          </div>
        </div>

        <div class="form-group">
          <label for="email">邮箱</label>
          <div class="input-wrapper">
            <Message class="input-icon" />
            <input
              type="email"
              id="email"
              v-model="registerForm.userEmail"
              placeholder="请输入邮箱地址"
              class="form-input"
              required
            >
          </div>
        </div>

        <div class="form-group">
          <label for="password">密码</label>
          <div class="input-wrapper">
            <span class="input-icon">🔒</span>
            <input
              type="password"
              id="password"
              v-model="registerForm.userPassword"
              placeholder="请输入密码"
              class="form-input"
              required
              @input="checkPasswordStrength"
            >
          </div>
          <div v-if="registerForm.userPassword" class="password-strength" :style="{ color: passwordStrength.color }">{{ passwordStrength.message }}</div>
        </div>

        <div class="form-group">
          <label for="confirmPassword">确认密码</label>
          <div class="input-wrapper">
            <span class="input-icon">🔐</span>
            <input
              type="password"
              id="confirmPassword"
              v-model="registerForm.confirmPassword"
              placeholder="请确认密码"
              class="form-input"
              required
            >
          </div>
        </div>

        <!-- 服务条款 -->
        <div class="form-group terms-group">
          <input
            type="checkbox"
            id="agreeTerms"
            v-model="registerForm.agreeTerms"
            class="form-checkbox"
          >
          <label for="agreeTerms" class="checkbox-label">
            我已阅读并同意<a href="#" style="color: #667eea;">服务条款</a>和<a href="#" style="color: #667eea;">隐私政策</a>
          </label>
        </div>

        <!-- 注册按钮 -->
        <div class="form-group">
          <button type="submit" @click="handleRegister" class="register-button">注册账号</button>
        </div>
      </form>

      <div class="form-footer">
        <span class="footer-text">已有账号？</span>
        <button type="button" class="footer-link primary" @click="goToLogin">去登录</button>
      </div>

      <div class="divider">
        <span>或</span>
      </div>

      <div class="alternative-register">
        <button type="button" class="email-register-button" @click="showEmailRegisterModal = true">邮箱验证码注册</button>
      </div>

    </div>
  </div>

  <!-- 邮箱验证码注册Modal -->
  <div v-if="showEmailRegisterModal" class="modal-overlay" @click="showEmailRegisterModal = false">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>邮箱验证码注册</h3>
        <button type="button" class="modal-close" @click="showEmailRegisterModal = false">×</button>
      </div>
      <div class="modal-body">
        <form :model="emailRegisterForm">
          <div class="form-group">
            <label for="modal-email">邮箱</label>
            <div class="code-input-wrapper">
              <input
                type="email"
                id="modal-email"
                v-model="emailRegisterForm.email"
                placeholder="请输入邮箱地址"
                class="form-input"
                required
              >
              <button
                type="button"
                @click="sendCaptcha"
                :disabled="isSendingCaptcha"
                class="send-code-button"
              >
                {{ sendCaptchaText }}
              </button>
            </div>
          </div>

          <div class="form-group">
            <label for="modal-captcha">验证码</label>
            <input
              type="text"
              id="modal-captcha"
              v-model="emailRegisterForm.captchaCode"
              placeholder="请输入验证码"
              class="form-input"
              required
            >
          </div>

          <div class="form-group">
            <label for="modal-username">用户名</label>
            <input
              type="text"
              id="modal-username"
              v-model="emailRegisterForm.userName"
              placeholder="请输入用户名"
              class="form-input"
              required
            >
          </div>

          <div class="form-group">
            <label for="modal-nickname">昵称</label>
            <input
              type="text"
              id="modal-nickname"
              v-model="emailRegisterForm.userNickname"
              placeholder="请输入昵称"
              class="form-input"
              required
            >
          </div>

          <div class="form-group">
            <label for="modal-password">密码</label>
            <input
              type="password"
              id="modal-password"
              v-model="emailRegisterForm.userPassword"
              placeholder="请输入密码"
              class="form-input"
              required
              @input="checkEmailPasswordStrength"
            >
            <div v-if="emailRegisterForm.userPassword" class="password-strength" :style="{ color: emailPasswordStrength.color }">{{ emailPasswordStrength.message }}</div>
          </div>

          <div class="form-group">
            <label for="modal-confirmPassword">确认密码</label>
            <input
              type="password"
              id="modal-confirmPassword"
              v-model="emailRegisterForm.confirmPassword"
              placeholder="请确认密码"
              class="form-input"
              required
            >
          </div>

          <div class="form-group terms-group">
            <input
              type="checkbox"
              id="modal-agreeTerms"
              v-model="emailRegisterForm.agreeTerms"
              class="form-checkbox"
            >
            <label for="modal-agreeTerms" class="checkbox-label">
              我已阅读并同意<a href="#" style="color: #667eea;">服务条款</a>和<a href="#" style="color: #667eea;">隐私政策</a>
            </label>
          </div>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="modal-button secondary" @click="showEmailRegisterModal = false">取消</button>
        <button type="button" class="modal-button primary" @click="handleEmailRegister">注册</button>
      </div>
    </div>
  </div>

  <!-- 注册成功提示Modal -->
  <div v-if="showSuccessModal" class="modal-overlay" @click="showSuccessModal = false">
    <div class="modal-content success-modal" @click.stop>
      <div class="modal-header">
        <h3>注册成功</h3>
        <button type="button" class="modal-close" @click="showSuccessModal = false">×</button>
      </div>
      <div class="modal-body success-body">
        <div style="font-size: 48px; color: #28a745; margin-bottom: 20px;">🎉</div>
        <h4>欢迎加入我们！</h4>
        <p class="text-muted">您的账号已成功注册，现在可以开始使用我们的博客系统了。</p>
        <div class="mt-4">
          <h6>推荐下一步操作：</h6>
          <ul class="list-unstyled text-left mt-2">
            <li>• 完善个人资料</li>
            <li>• 发布第一篇博客</li>
            <li>• 关注其他博主</li>
          </ul>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="modal-button secondary" @click="showSuccessModal = false">稍后再说</button>
        <button type="button" class="modal-button primary" @click="goToLogin">去登录</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import router from '@/router/index.js'
//import axiosAPI from 'axios'
import { useUserInfoStore } from '@/stores/modules/userInfo.js'
import { ElMessage } from 'element-plus'
import axiosAPI from "@/utils/api/axios.js";
const userStore=useUserInfoStore();

// 主注册表单数据
const registerForm = ref({
  username: '',
  userNickname: '',
  userEmail: '',
  userPassword: '',
  confirmPassword: '',
  agreeTerms: false
})

// 邮箱验证码注册表单数据
const emailRegisterForm = ref({
  email: '',
  captchaCode: '',
  userName: '',
  userNickname: '',
  userPassword: '',
  confirmPassword: '',
  agreeTerms: false
})

// 模态框状态
const showEmailRegisterModal = ref(false)
const showSuccessModal = ref(false)

// 密码强度
const passwordStrength = ref({
  message: '请输入密码',
  color: '#999'
})

const emailPasswordStrength = ref({
  message: '请输入密码',
  color: '#999'
})

// 发送验证码状态
const isSendingCaptcha = ref(false)
const sendCaptchaText = ref('发送验证码')

// 密码强度检测
const checkPasswordStrength = (event) => {
  const password = event.target.value
  let strength = 0
  let message = ''
  let color = ''

  if (password.length >= 8) strength++
  if (/[A-Z]/.test(password)) strength++
  if (/[a-z]/.test(password)) strength++
  if (/[0-9]/.test(password)) strength++
  if (/[^A-Za-z0-9]/.test(password)) strength++

  switch(strength) {
    case 0:
      message = '请输入密码'
      color = '#999'
      break
    case 1:
    case 2:
      message = '密码强度：弱'
      color = '#dc3545'
      break
    case 3:
      message = '密码强度：中'
      color = '#ffc107'
      break
    case 4:
    case 5:
      message = '密码强度：强'
      color = '#28a745'
      break
  }

  passwordStrength.value = { message, color }
}

// 邮箱注册密码强度检测
const checkEmailPasswordStrength = (event) => {
  const password = event.target.value
  let strength = 0
  let message = ''
  let color = ''

  if (password.length >= 8) strength++
  if (/[A-Z]/.test(password)) strength++
  if (/[a-z]/.test(password)) strength++
  if (/[0-9]/.test(password)) strength++
  if (/[^A-Za-z0-9]/.test(password)) strength++

  switch(strength) {
    case 0:
      message = '请输入密码'
      color = '#999'
      break
    case 1:
    case 2:
      message = '密码强度：弱'
      color = '#dc3545'
      break
    case 3:
      message = '密码强度：中'
      color = '#ffc107'
      break
    case 4:
    case 5:
      message = '密码强度：强'
      color = '#28a745'
      break
  }

  emailPasswordStrength.value = { message, color }
}

// 发送验证码
const sendCaptcha = async () => {
  const email = emailRegisterForm.value.email
  if (!email) {
    ElMessage.error('请输入邮箱地址')
    return
  }

  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    ElMessage.error('请输入有效的邮箱地址')
    return
  }

  isSendingCaptcha.value = true
  sendCaptchaText.value = '发送中...'

  try {
    const response = await axiosAPI.post('/auth/code2Email', {
      email: email
    })

    if (response.data.code === 1) {
      ElMessage.success('验证码已发送到您的邮箱')
      // 60秒后恢复发送按钮
      let countdown = 60
      sendCaptchaText.value = `${countdown}秒后重试`
      const timer = setInterval(() => {
        countdown--
        sendCaptchaText.value = `${countdown}秒后重试`
        if (countdown <= 0) {
          clearInterval(timer)
          isSendingCaptcha.value = false
          sendCaptchaText.value = '发送验证码'
        }
      }, 1000)
    } else {
      ElMessage.error('发送验证码失败：' + response.data.message)
      isSendingCaptcha.value = false
      sendCaptchaText.value = '发送验证码'
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('发送验证码失败，请稍后重试')
    isSendingCaptcha.value = false
    sendCaptchaText.value = '发送验证码'
  }
}

// 处理注册
const handleRegister = async () => {
  // 表单验证
  if (!registerForm.value.username || !registerForm.value.userNickname || !registerForm.value.userPassword || !registerForm.value.confirmPassword || !registerForm.value.userEmail) {
    ElMessage.error('请填写完整信息')
    return
  }

  if (registerForm.value.userPassword !== registerForm.value.confirmPassword) {
    ElMessage.error('两次密码不一致，请重新填写！')
    return
  }

  if (!registerForm.value.agreeTerms) {
    ElMessage.error('请阅读并同意服务条款和隐私政策')
    return
  }

  try {
    const response = await axiosAPI.post('/auth/register', {
      userName: registerForm.value.username,
      userNickname: registerForm.value.userNickname,
      userPassword: registerForm.value.userPassword,
      userEmail: registerForm.value.userEmail
    })

    if (response.data.code === 1) {
      // 注册成功后显示模态框
      showSuccessModal.value = true
    } else {
      ElMessage.error('注册失败，' + response.data.message)
    }
  } catch (error) {
    console.log(error)
    if (error.response) {
      ElMessage.error('注册失败：' + error.response.data.message)
    } else {
      ElMessage.error('注册失败，请稍后重试')
    }
  }
}

// 处理邮箱验证码注册
const handleEmailRegister = async () => {
  // 表单验证
  if (!emailRegisterForm.value.email || !emailRegisterForm.value.captchaCode || !emailRegisterForm.value.userName || !emailRegisterForm.value.userNickname || !emailRegisterForm.value.userPassword || !emailRegisterForm.value.confirmPassword) {
    ElMessage.error('请填写完整信息')
    return
  }

  if (emailRegisterForm.value.userPassword !== emailRegisterForm.value.confirmPassword) {
    ElMessage.error('两次密码不一致，请重新填写！')
    return
  }

  if (!emailRegisterForm.value.agreeTerms) {
    ElMessage.error('请阅读并同意服务条款和隐私政策')
    return
  }

  try {
    const response = await axiosAPI.post('/auth/register', {
      email: emailRegisterForm.value.email,
      captchaCode: emailRegisterForm.value.captchaCode,
      userName: emailRegisterForm.value.userName,
      userNickname: emailRegisterForm.value.userNickname,
      userPassword: emailRegisterForm.value.userPassword
    })

    if (response.data.code === 1) {
      // 注册成功后显示模态框
      showSuccessModal.value = true
      // 关闭邮箱注册模态框
      showEmailRegisterModal.value = false
    } else {
      ElMessage.error('注册失败，' + response.data.message)
    }
  } catch (error) {
    console.log(error)
    if (error.response) {
      ElMessage.error('注册失败：' + error.response.data.message)
    } else {
      ElMessage.error('注册失败，请稍后重试')
    }
  }
}

// 跳转到登录页面
const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', 'Microsoft YaHei', Tahoma, Geneva, Verdana, sans-serif;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin: 0;
  padding: 0;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

/* 注册容器 */
.register-container {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

/* 注册表单 */
.register-form {
  background: #ffffff;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 420px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.register-form:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
}

/* 表单头部 */
.form-header {
  text-align: center;
  margin-bottom: 30px;
}

.form-header h2 {
  color: #333;
  margin-bottom: 10px;
  font-size: 28px;
  font-weight: 700;
}

.form-header p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

/* 表单组 */
.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  color: #333;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
  text-align: left;
}

/* 输入框容器 */
.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 15px;
  color: #999;
  pointer-events: none;
}

.input-icon svg {
  width: 18px;
  height: 18px;
}

.form-input {
  width: 100%;
  padding: 12px 15px 12px 40px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
  background-color: #f9f9f9;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  background-color: #fff;
}

/* 密码强度提示 */
.password-strength {
  margin-top: 8px;
  font-size: 12px;
  text-align: left;
}

/* 服务条款组 */
.terms-group {
  display: flex;
  align-items: center;
  margin-bottom: 25px;
}

.form-checkbox {
  margin-right: 8px;
  accent-color: #667eea;
}

.checkbox-label {
  color: #666;
  font-size: 14px;
  cursor: pointer;
}

/* 注册按钮 */
.register-button {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #E1BE97 0%, #664401 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

/* 表单底部 */
.form-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 20px;
}

.footer-text {
  color: #666;
  font-size: 14px;
}

.footer-link {
  background: none;
  border: none;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  padding: 0;
  transition: color 0.3s ease;
}

.footer-link:hover {
  color: #333;
}

.footer-link.primary {
  color: #667eea;
  font-weight: 500;
}

.footer-link.primary:hover {
  color: #5a6fd8;
  text-decoration: underline;
}

/* 分隔线 */
.divider {
  display: flex;
  align-items: center;
  margin: 30px 0;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background-color: #eee;
}

.divider span {
  padding: 0 15px;
  color: #999;
  font-size: 14px;
}

/* 其他注册方式 */
.alternative-register {
  width: 100%;
}

.email-register-button {
  width: 100%;
  padding: 14px;
  background-color: #f9f9f9;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.email-register-button:hover {
  background-color: #f0f0f0;
  transform: translateY(-2px);
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  animation: modalFadeIn 0.3s ease;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  color: #333;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  color: #999;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.modal-close:hover {
  background-color: #f0f0f0;
  color: #333;
}

.modal-body {
  padding: 24px;
}

.success-body {
  text-align: center;
}

.success-body h4 {
  color: #333;
  margin-bottom: 15px;
}

.success-body .text-muted {
  color: #666;
  margin-bottom: 20px;
}

.success-body .mt-4 {
  margin-top: 20px;
}

.success-body h6 {
  color: #333;
  margin-bottom: 10px;
}

.success-body .list-unstyled {
  list-style: none;
  padding: 0;
}

.success-body .text-left {
  text-align: left;
}

.success-body li {
  color: #666;
  margin-bottom: 8px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px 24px;
  border-top: 1px solid #eee;
}

.modal-button {
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #ddd;
}

.modal-button.secondary {
  background-color: white;
  color: #333;
}

.modal-button.secondary:hover {
  background-color: #f0f0f0;
}

.modal-button.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
}

.modal-button.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* 验证码输入 */
.code-input-wrapper {
  display: flex;
  gap: 10px;
}

.send-code-button {
  padding: 0 16px;
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.send-code-button:hover:not(:disabled) {
  background-color: #e0e0e0;
}

.send-code-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .register-form {
    padding: 30px 20px;
  }

  .form-header h2 {
    font-size: 24px;
  }

  .register-button {
    padding: 12px;
    font-size: 15px;
  }

  .modal-content {
    width: 95%;
  }

  .modal-header,
  .modal-body,
  .modal-footer {
    padding: 16px;
  }
}
</style>
