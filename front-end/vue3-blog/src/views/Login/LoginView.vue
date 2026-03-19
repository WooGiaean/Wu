<template>
  <div class="login-container">
    <div class="login-form">
      <h2>Sign In</h2>
      <!-- 表单提交 -->
      <form id="loginForm" @submit.prevent="handleLogin">
        <div class="form-group">
          <input type="text" id="username" v-model="loginForm.username" placeholder="Username" required>
        </div>
        <div class="form-group">
          <input type="password" id="password" v-model="loginForm.password" placeholder="Password" required>
        </div>
        <!-- “记住我” 复选框 -->
        <div class="form-group remember-me">
          <div class="form-check">
            <input type="checkbox" id="remember" v-model="loginForm.remember" class="form-check-input">
            <label for="remember" class="form-check-label">Remember me</label>
          </div>
        </div>
        <!-- 登录按钮 -->
        <button type="submit" id="submit_btn">Login</button>
      </form>
      <div class="form-footer">
        <a href="#" @click.prevent="goToForgotPassword">Forget Password</a>
        <a href="#" @click.prevent="goToRegister">Signup</a>
      </div>
    </div>
  </div>

  <!-- 邮箱验证码登录Modal -->
  <div class="modal" v-if="showCaptchaModal" tabindex="-1" aria-labelledby="captchaModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <!-- modal标题 -->
        <div class="modal-header">
          <h5 class="modal-title" id="captchaModalLabel">验证码登录</h5>
          <button type="button" class="btn-close" @click="showCaptchaModal = false" aria-label="Close"></button>
        </div>

        <!-- modal主体 -->
        <div class="modal-body">
          <div class="mb-3">
            <label for="captchaEmail" class="form-label">邮箱</label>
            <input type="email" id="captchaEmail" v-model="captchaForm.email" placeholder="请输入注册邮箱">
          </div>
          <div class="mb-3">
            <label for="captchaCode" class="form-label">验证码</label>
            <div class="input-group">
              <input type="text" id="captchaCode" v-model="captchaForm.code" placeholder="请输入验证码">
              <button class="btn btn-outline-secondary" type="button" id="sendCaptchaBtn" @click="sendCaptcha">发送验证码</button>
            </div>
          </div>

          <div id="captchaMessage" class="text-danger">{{ captchaMessage }}</div>
        </div>

        <!-- modal底部组件footer -->
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" @click="showCaptchaModal = false">关闭</button>
          <button type="button" class="btn btn-primary" id="verifyCaptchaBtn" @click="verifyCaptcha">验证</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axiosAPI  from "@/utils/api/axios.js";

const router = useRouter()

// 登录表单数据
const loginForm = ref({
  username: '',
  password: '',
  remember: false
})

// 验证码登录表单数据
const captchaForm = ref({
  email: '',
  code: ''
})

const showCaptchaModal = ref(false)
const captchaMessage = ref('')

// 处理登录
const handleLogin = async () => {
  try {
    const response = await axiosAPI.post('/login', {
      username: loginForm.value.username,
      password: loginForm.value.password,
      remember: loginForm.value.remember
    })

    if (response.data.code === 1) {
      // 登录成功，根据角色跳转
      const userRole = response.data.data.userRole
      if (userRole === 'admin') {
        router.push('/admin')
      } else {
        router.push('/home')
      }
    } else {
      alert(response.data.message)
    }
  } catch (error) {
    console.error('登录失败:', error)
    alert('登录失败，请稍后重试')
  }
}

// 发送验证码
const sendCaptcha = async () => {
  if (!captchaForm.value.email) {
    captchaMessage.value = '请输入邮箱'
    return
  }

  try {
    const response = await axios.post('/admin/code2Email', {
      email: captchaForm.value.email
    })
    if (response.data.code === 1) {
      alert('验证码发送成功')
    }
  } catch (error) {
    console.error('发送验证码失败:', error)
    captchaMessage.value = '发送验证码失败'
  }
}

// 验证验证码
const verifyCaptcha = async () => {
  if (!captchaForm.value.code) {
    captchaMessage.value = '请输入验证码'
    return
  }

  try {
    const response = await axios.post('/admin/verifyCodeLogin', {
      email: captchaForm.value.email,
      code: captchaForm.value.code
    })

    if (response.data.code === 1) {
      alert('验证成功')
      showCaptchaModal.value = false
      router.push('/home')
    } else {
      captchaMessage.value = response.data.message
    }
  } catch (error) {
    console.error('验证失败:', error)
    captchaMessage.value = '验证失败，请稍后重试'
  }
}

// 跳转到注册页面
const goToRegister = () => {
  router.push('/register')
}

// 跳转到忘记密码页面
const goToForgotPassword = () => {
  router.push('/forgot-password')
}
</script>

<style scoped>
body {
  font-family: 'Segoe UI', 'Microsoft YaHei', Tahoma, Geneva, Verdana, sans-serif;
  background: var(--bg-gradient);
  margin: 0;
  padding: 0;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.login-container {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-form {
  background: var(--bg-secondary);
  padding: 40px;
  border-radius: 15px;
  box-shadow: var(--card-shadow);
  width: 100%;
  max-width: 400px;
  text-align: center;
  border: 1px solid var(--border-color);
}

h2 {
  color: var(--text-primary);
  margin-bottom: 30px;
  font-size: 28px;
  font-weight: bold;
  border-left: 5px solid var(--accent-color);
  padding-left: 15px;
  text-align: left;
}

.form-group {
  margin-bottom: 20px;
  text-align: left;
}

input {
  width: 100%;
  padding: 15px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  box-sizing: border-box;
  font-size: 16px;
  transition: all 0.3s ease;
  background: var(--bg-primary);
  color: var(--text-primary);
}

input:focus {
  outline: none;
  border-color: var(--accent-color);
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
}

.remember-me {
  display: flex;
  align-items: center;
  margin-bottom: 25px;
}

.form-check {
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-check-input {
  width: auto;
  margin: 0;
}

.form-check-label {
  color: var(--text-secondary);
  font-size: 14px;
}

button {
  background: var(--button-bg);
  color: var(--button-text);
  border: none;
  padding: 15px 0;
  width: 100%;
  border-radius: 8px;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 20px;
}

button:hover {
  background: var(--accent-color);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.4);
}

.form-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
}

.form-footer a {
  color: var(--accent-color);
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;
}

.form-footer a:hover {
  color: var(--text-primary);
  text-decoration: underline;
}

/* 模态框样式 */
.modal {
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1000;
}

.modal-dialog {
  background: var(--bg-secondary);
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  overflow: hidden;
  border: 1px solid var(--border-color);
}

.modal-header {
  padding: 15px;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--bg-primary);
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  padding: 15px;
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  background: var(--bg-primary);
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-secondary {
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

.btn-secondary:hover {
  background-color: var(--bg-primary);
  border-color: var(--accent-color);
}

.btn-primary {
  background-color: var(--button-bg);
  color: var(--button-text);
}

.btn-primary:hover {
  background-color: var(--accent-color);
}

.btn-outline-secondary {
  background-color: transparent;
  border: 1px solid var(--border-color);
  color: var(--text-secondary);
}

.btn-outline-secondary:hover {
  border-color: var(--accent-color);
  color: var(--text-primary);
}

.input-group {
  display: flex;
  gap: 10px;
}

.input-group input {
  flex: 1;
}

.text-danger {
  color: #dc3545;
  font-size: 14px;
  margin-top: 10px;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-form {
    padding: 30px 20px;
    margin: 0 20px;
  }

  h2 {
    font-size: 24px;
  }

  input {
    padding: 12px;
  }

  button {
    padding: 12px 0;
    font-size: 16px;
  }
}
</style>
