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

  <!-- 邮箱验证码注册模态框 -->
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
        <el-checkbox v-model="emailRegisterForm.agreeTerms">我已阅读并同意<a href="#" style="color: #667eea;">服务条款</a>和<a href="#" style="color: #667eea;">隐私政策</a></el-checkbox>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showEmailRegisterModal = false">取消</el-button>
        <el-button type="primary" @click="handleEmailRegister">注册</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 注册成功提示模态框 -->
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
import { useUserInfoStore } from '@/stores/modules/userInfo.js'
import { ElForm, ElFormItem, ElInput, ElButton, ElDialog, ElCheckbox, ElMessage } from 'element-plus'
const userStore=useUserInfoStore();
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

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #d4b996 0%, #d4b996 100%);
  border-color: #d4b996;
}

:deep(.el-button--info) {
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
</style>
