<template>
  <div class="login-container">
    <div class="login-form">
      <h2>Sign In</h2>
      <!-- 表单提交 -->
      <el-form :model="loginForm" @submit.prevent="handleLogin" class="login-form">
        <el-form-item label="Username" required>
          <el-input v-model="loginForm.username" :suffix-icon="User" type="text"
                    placeholder="Username" />
        </el-form-item>
        <el-form-item label="Password" required>
<!--          <el-icon><Lock /></el-icon>-->
          <el-input v-model="loginForm.password"   type="password"
                    placeholder="Password"
                    :suffix-icon="Lock"
                    show-password   />
        </el-form-item>
        <!-- “记住我” 复选框 -->
        <el-form-item>
          <el-checkbox v-model="loginForm.remember">Remember me</el-checkbox>
        </el-form-item>
        <!-- 登录按钮

         -->
        <el-form-item>
          <el-button type="primary" native-type="submit" @click="handleLogin" class="login-button">Login</el-button>
        </el-form-item>
      </el-form>
      <div class="form-footer">
        <el-button type="success"  @click="goToForgotPassword"  >Forget Password</el-button>
        <el-button type="success"  @click="goToRegister" >Sign up</el-button>
<!--        <a href="#" @click.prevent="goToForgotPassword">Forget Password</a>
        <a href="#" @click.prevent="goToRegister">Signup</a>-->
      </div>
    </div>
  </div>

  <!-- 邮箱验证码登录Modal -->
  <el-dialog
    v-model="showCaptchaModal"
    title="验证码登录"
    width="500px"
  >
    <el-form :model="captchaForm">
      <el-form-item label="邮箱" required>
        <el-input v-model="captchaForm.email" placeholder="请输入注册邮箱" />
      </el-form-item>
      <el-form-item label="验证码" required>
        <el-input v-model="captchaForm.code" placeholder="请输入验证码">
          <template #append>
            <el-button @click="sendCaptcha" :disabled="isSending">发送验证码</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item>
        <div class="text-danger">{{ captchaMessage }}</div>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showCaptchaModal = false">关闭</el-button>
        <el-button type="primary" @click="verifyCaptcha">验证</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import  router  from '@/router/index.js'
import axiosAPI  from "@/utils/api/axios.js"
import { useUserInfoStore } from '@/stores/modules/userInfo.js'
import { ElForm, ElFormItem, ElInput, ElButton
  , ElDialog, ElCheckbox, ElMessage,ElIcon} from 'element-plus'
import { Lock,User} from '@element-plus/icons-vue'
// 初始化用户信息存储
const userStore = useUserInfoStore()

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
const isSending = ref(false)

// 处理登录
const handleLogin = async () => {
  console.log('登录表单数据:', loginForm.value)
  try {
    // 使用URLSearchParams格式发送登录请求
    const params = new URLSearchParams()
    params.append('username', loginForm.value.username)
    params.append('password', loginForm.value.password)
    params.append('remember', loginForm.value.remember)

    //发送请求
    const response = await axiosAPI.post('/login', params, {
      //设置表单提交-》SpringSecurity的登录接口
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
    console.log('登录结果:', response.data)
    if (response.data.code === 1) {
      //userTempInfo用于存储用户角色
      const userTempInfo = response.data.data.user
      const userToken= response.data.data.token
      console.log('用户信息:{}，token:{}', userTempInfo,userToken)
      // 登录成功，存储用户信息和token到pinia
      userStore.setUserAndToken(userTempInfo, userToken)
      // 根据角色跳转
      const userRole = userTempInfo.userRole
      if (userRole === 'admin') {
        ElMessage.success({
          type: 'success',
          message: '管理员登录成功',
          duration: 1000
        })
        await router.push('/admin')
      } else {
        ElMessage.success({
          type: 'success',
          message: '用户登录成功',
          duration: 1000
        })
        await router.push('/home')
      }
      // 移除成功消息提示，避免可能的图标显示问题
    } else {
      ElMessage({
        type: 'error',
        message: response.data.msg,
        duration: 3000
      })
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage({
      type: 'error',
      message: '登录失败，请稍后重试',
      duration: 3000
    })
  }
}

// 发送验证码
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
      ElMessage({
        type: 'success',
        message: '验证码发送成功',
        duration: 3000
      })
    }
  } catch (error) {
    console.error('发送验证码失败:', error)
    captchaMessage.value = '发送验证码失败'
  } finally {
    isSending.value = false
  }
}

// 验证验证码
const verifyCaptcha = async () => {
  if (!captchaForm.value.code) {
    captchaMessage.value = '请输入验证码'
    return
  }

  try {
    const response = await axiosAPI.post('/auth/verifyCodeLogin', {
      email: captchaForm.value.email,
      code: captchaForm.value.code
    })

    if (response.data.code === 1) {
      // 登录成功，存储用户信息和token到pinia
      userStore.setUserAndToken(response.data.data.user, response.data.data.token)
      showCaptchaModal.value = false
      await router.push('/home')
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

/* Element Plus 表单样式调整 */
:deep(.el-form-item__label) {
  color: var(--text-primary);
}

:deep(.el-input__wrapper) {
  background: var(--bg-primary);
}

:deep(.el-input__inner) {
  color: var(--text-primary);
}

:deep(.el-input__suffix-inner) {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

:deep(.el-input__suffix-inner svg) {
  width: 16px;
  height: 16px;
}

:deep(.el-button--primary) {
  background-color: var(--button-bg);
  border-color: var(--button-bg);
}

:deep(.el-button--primary:hover) {
  background-color: var(--accent-color);
  border-color: var(--accent-color);
}

.login-button {
  width: 100%;
  padding: 12px 0;
  font-size: 16px;
}

.form-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
}

.form-footer a {
  color: var(--accent-color);
  text-decoration: none;
  font-size: 24px;
  transition: color 0.3s ease;
}

.form-footer .el-button--success :hover {
  color: var(--text-primary);
  text-decoration: underline;
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

  .login-button {
    padding: 10px 0;
    font-size: 14px;
  }
}
</style>
