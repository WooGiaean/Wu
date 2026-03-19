<template>
  <div class="register-container">
    <h2>用户注册</h2>
    <div class="form-group">
      <label for="username">用户名</label>
      <input type="text" id="username" v-model="registerForm.username" required placeholder="请输入用户名">
    </div>

    <div class="form-group">
      <label for="userNickName">昵称</label>
      <input type="text" id="userNickName" v-model="registerForm.userNickname" required placeholder="请输入昵称">
    </div>

    <div class="form-group">
      <label for="email">邮箱</label>
      <input type="email" id="email" v-model="registerForm.userEmail" required placeholder="请输入邮箱地址">
    </div>

    <div class="form-group">
      <label for="password">密码</label>
      <input type="password" id="password" v-model="registerForm.userPassword" required placeholder="请输入密码" @input="checkPasswordStrength">
      <div id="password-strength" class="mt-2" :style="{ color: passwordStrength.color }">{{ passwordStrength.message }}</div>
    </div>

    <div class="form-group">
      <label for="confirm-password">确认密码</label>
      <input type="password" id="confirm-password" v-model="registerForm.confirmPassword" required placeholder="请确认密码">
    </div>

    <div class="form-group">
      <div class="form-check">
        <input type="checkbox" id="agree-terms" v-model="registerForm.agreeTerms" class="form-check-input">
        <label for="agree-terms" class="form-check-label">我已阅读并同意<a href="#" style="color: #667eea;">服务条款</a>和<a href="#" style="color: #667eea;">隐私政策</a></label>
      </div>
    </div>

    <button type="submit" id="btn-register" @click="handleRegister">注册账号</button>
    
    <div class="alternative-register">
      <span>或</span>
    </div>
    
    <button type="button" id="btn-email-register" @click="showEmailRegisterModal = true">邮箱验证码注册</button>
    
    <p>已有账号？<a href="#" @click.prevent="goToLogin">请登录</a></p>
  </div>

  <!-- 邮箱验证码注册模态框 -->
  <div class="modal" v-if="showEmailRegisterModal" tabindex="-1" aria-labelledby="emailRegisterModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="emailRegisterModalLabel">邮箱验证码注册</h5>
          <button type="button" class="btn-close" @click="showEmailRegisterModal = false" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label for="email-modal">邮箱</label>
            <div class="input-group">
              <input type="email" id="email-modal" v-model="emailRegisterForm.email" required placeholder="请输入邮箱地址" class="form-control">
              <button type="button" class="btn btn-outline-secondary" id="send-captcha-modal" @click="sendCaptcha" :disabled="isSendingCaptcha">
                {{ sendCaptchaText }}
              </button>
            </div>
          </div>
          <div class="form-group">
            <label for="captchaCode-modal">验证码</label>
            <input type="text" id="captchaCode-modal" v-model="emailRegisterForm.captchaCode" required placeholder="请输入验证码" class="form-control">
          </div>
          <div class="form-group">
            <label for="username-modal">用户名</label>
            <input type="text" id="username-modal" v-model="emailRegisterForm.userName" required placeholder="请输入用户名" class="form-control">
          </div>
          <div class="form-group">
            <label for="userNickName-modal">昵称</label>
            <input type="text" id="userNickName-modal" v-model="emailRegisterForm.userNickname" required placeholder="请输入昵称" class="form-control">
          </div>
          <div class="form-group">
            <label for="password-modal">密码</label>
            <input type="password" id="password-modal" v-model="emailRegisterForm.userPassword" required placeholder="请输入密码" class="form-control" @input="checkEmailPasswordStrength">
            <div id="password-strength-modal" class="mt-2" :style="{ color: emailPasswordStrength.color }">{{ emailPasswordStrength.message }}</div>
          </div>
          <div class="form-group">
            <label for="confirm-password-modal">确认密码</label>
            <input type="password" id="confirm-password-modal" v-model="emailRegisterForm.confirmPassword" required placeholder="请确认密码" class="form-control">
          </div>
          <div class="form-group">
            <div class="form-check">
              <input type="checkbox" id="agree-terms-modal" v-model="emailRegisterForm.agreeTerms" class="form-check-input">
              <label for="agree-terms-modal" class="form-check-label">我已阅读并同意<a href="#" style="color: #667eea;">服务条款</a>和<a href="#" style="color: #667eea;">隐私政策</a></label>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" @click="showEmailRegisterModal = false">取消</button>
          <button type="button" class="btn btn-primary" id="btn-register-email" @click="handleEmailRegister">注册</button>
        </div>
      </div>
    </div>
  </div>

  <!-- 注册成功提示模态框 -->
  <div class="modal" v-if="showSuccessModal" tabindex="-1" aria-labelledby="registerSuccessModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="registerSuccessModalLabel">注册成功</h5>
          <button type="button" class="btn-close" @click="showSuccessModal = false" aria-label="Close"></button>
        </div>
        <div class="modal-body">
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
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" @click="showSuccessModal = false">稍后再说</button>
          <button type="button" class="btn btn-primary" id="go-login" @click="goToLogin">去登录</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

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
    alert('请输入邮箱地址')
    return
  }

  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    alert('请输入有效的邮箱地址')
    return
  }

  isSendingCaptcha.value = true
  sendCaptchaText.value = '发送中...'

  try {
    const response = await axios.post('/admin/code2Email', {
      email: email
    })

    if (response.data.code === 1) {
      alert('验证码已发送到您的邮箱')
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
      alert('发送验证码失败：' + response.data.message)
      isSendingCaptcha.value = false
      sendCaptchaText.value = '发送验证码'
    }
  } catch (error) {
    console.error(error)
    alert('发送验证码失败，请稍后重试')
    isSendingCaptcha.value = false
    sendCaptchaText.value = '发送验证码'
  }
}

// 处理注册
const handleRegister = async () => {
  // 表单验证
  if (!registerForm.value.username || !registerForm.value.userNickname || !registerForm.value.userPassword || !registerForm.value.confirmPassword || !registerForm.value.userEmail) {
    alert('请填写完整信息')
    return
  }

  if (registerForm.value.userPassword !== registerForm.value.confirmPassword) {
    alert('两次密码不一致，请重新填写！')
    return
  }

  if (!registerForm.value.agreeTerms) {
    alert('请阅读并同意服务条款和隐私政策')
    return
  }

  try {
    const response = await axios.post('/admin/register', {
      userName: registerForm.value.username,
      userNickname: registerForm.value.userNickname,
      userPassword: registerForm.value.userPassword,
      userEmail: registerForm.value.userEmail
    })

    if (response.data.code === 1) {
      // 注册成功后显示模态框
      showSuccessModal.value = true
    } else {
      alert('注册失败，' + response.data.message)
    }
  } catch (error) {
    console.log(error)
    if (error.response) {
      alert('注册失败：' + error.response.data.message)
    } else {
      alert('注册失败，请稍后重试')
    }
  }
}

// 处理邮箱验证码注册
const handleEmailRegister = async () => {
  // 表单验证
  if (!emailRegisterForm.value.email || !emailRegisterForm.value.captchaCode || !emailRegisterForm.value.userName || !emailRegisterForm.value.userNickname || !emailRegisterForm.value.userPassword || !emailRegisterForm.value.confirmPassword) {
    alert('请填写完整信息')
    return
  }

  if (emailRegisterForm.value.userPassword !== emailRegisterForm.value.confirmPassword) {
    alert('两次密码不一致，请重新填写！')
    return
  }

  if (!emailRegisterForm.value.agreeTerms) {
    alert('请阅读并同意服务条款和隐私政策')
    return
  }

  try {
    const response = await axios.post('/admin/register', {
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
      alert('注册失败，' + response.data.message)
    }
  } catch (error) {
    console.log(error)
    if (error.response) {
      alert('注册失败：' + error.response.data.message)
    } else {
      alert('注册失败，请稍后重试')
    }
  }
}

// 跳转到登录页面
const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
body {
  font-family: 'Arial', sans-serif;
  background: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), url('https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=minimalist%20blog%20background%20with%20soft%20colors%20and%20subtle%20patterns&image_size=landscape_16_9');
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
  width: 320px;
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

.form-group {
  margin-bottom: 20px;
  text-align: left;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-size: 14px;
}

input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-sizing: border-box;
  font-size: 14px;
  transition: border 0.3s ease;
}

input:focus {
  outline: none;
  border-color: #d4b996;
}

button {
  background: linear-gradient(135deg, #d4b996 0%, #d4b996 100%);
  color: white;
  border: none;
  margin-bottom: 10px;
  padding: 12px 0;
  width: 100%;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  transition: transform 0.2s ease;
}

button:hover {
  transform: translateY(-2px);
}

.forgot-password {
  margin-top: 15px;
  font-size: 14px;
}

.forgot-password a {
  color: #667eea;
  text-decoration: none;
}

.forgot-password a:hover {
  text-decoration: underline;
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

#password-strength {
  font-size: 12px;
  margin-top: 5px;
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
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  overflow: hidden;
}

.modal-header {
  padding: 15px;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  padding: 15px;
  border-top: 1px solid #e9ecef;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-primary {
  background-color: #d4b996;
  color: white;
}

.btn-outline-secondary {
  background-color: transparent;
  border: 1px solid #6c757d;
  color: #6c757d;
}

.input-group {
  display: flex;
  gap: 10px;
}

.input-group input {
  flex: 1;
}
</style>